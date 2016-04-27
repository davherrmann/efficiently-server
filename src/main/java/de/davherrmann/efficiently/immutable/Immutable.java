package de.davherrmann.efficiently.immutable;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Defaults;
import com.google.common.collect.Lists;
import com.google.common.reflect.AbstractInvocationHandler;

public class Immutable<I>
{
    private final Class<I> type;
    private final PathRecorder<I> pathRecorder;
    private final Map<String, Object> values;

    public Immutable(Class<I> type)
    {
        this(type, newHashMap(), new PathRecorder<>(type));
    }

    private Immutable(Class<I> type, Map<String, Object> initialValues, PathRecorder<I> pathRecorder)
    {
        this.values = initialValues;
        this.type = type;
        this.pathRecorder = pathRecorder;
    }

    public I asObject()
    {
        return immutableFor(type, values, emptyList());
    }

    Map<String, Object> values()
    {
        return values;
    }

    @SuppressWarnings("unchecked")
    private <T> T immutableFor(Class<T> type, Map<String, Object> values, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            type.getClassLoader(), //
            new Class[]{type}, //
            new ImmutableObjectInvocationHandler(values, nestedPath) //
        );
    }

    public I path()
    {
        return pathRecorder.path();
    }

    public <T> In<T> in(T method)
    {
        return new In<>(pathRecorder.lastCalledNestedPath(), pathRecorder.lastCalledMethod());
    }

    public class In<T>
    {
        private final List<String> nestedPath;
        private final Method method;

        public In(List<String> nestedPath, Method method)
        {
            this.nestedPath = nestedPath;
            this.method = method;
        }

        public Immutable<I> set(T value)
        {
            return setIn(nestedPath, method, value);
        }

        public Immutable<I> update(MappingFunction<T> mappingFunction)
        {
            return updateIn(nestedPath, method, mappingFunction);
        }
    }

    private <T> Immutable<I> setIn(List<String> nestedPath, Method method, T value)
    {
        final Map<String, Object> newValues = newHashMap(values);
        getOrCreate(newValues, nestedPath).put(method.getName(), value);

        return new Immutable<>(type, newValues, pathRecorder);
    }

    private <T> Immutable<I> updateIn(List<String> nestedPath, Method method, MappingFunction<T> mappingFunction)
    {
        @SuppressWarnings("unchecked")
        final T currentValue = (T) getOrDefaultValue(values, nestedPath, method);
        final T value = mappingFunction.map(currentValue);

        return setIn(nestedPath, method, value);
    }

    private Map<String, Object> getOrCreate(Map<String, Object> values, List<String> nestedPath)
    {
        if (nestedPath.isEmpty())
        {
            return values;
        }

        final String key = nestedPath.get(0);
        values.putIfAbsent(key, new ImmutableNode(newHashMap()));

        return getOrCreate(((ImmutableNode) values.get(key)).values(), nestedPath.subList(1, nestedPath.size()));
    }

    private class ImmutableObjectInvocationHandler extends AbstractInvocationHandler
    {
        private final Map<String, Object> values;
        private final List<String> nestedPath;

        public ImmutableObjectInvocationHandler(Map<String, Object> values, List<String> nestedPath)
        {
            this.values = values;
            this.nestedPath = nestedPath;
        }

        @Override
        protected Object handleInvocation(final Object proxy, final Method method, final Object[] args) throws Throwable
        {
            return getOrDefaultValue(values, nestedPath, method);
        }
    }

    private Object getOrDefaultValue(Map<String, Object> values, List<String> nestedPath, Method method)
    {
        final Object value = values.get(method.getName());
        final Class<?> returnType = method.getReturnType();
        final Object returnValue = value != null
            ? value
            : Defaults.defaultValue(returnType);

        return returnValue != null && ImmutableNode.class.isAssignableFrom(returnValue.getClass())
            ? immutableFor(returnType, ((ImmutableNode) returnValue).values(), nestedPathWith(nestedPath, method))
            : returnValue;
    }

    private List<String> nestedPathWith(List<String> nestedPath, Method method)
    {
        final ArrayList<String> newNestedPath = Lists.newArrayList(nestedPath);
        newNestedPath.add(method.getName());
        return newNestedPath;
    }

    public interface MappingFunction<T>
    {
        T map(T value);
    }
}
