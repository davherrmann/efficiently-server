package de.davherrmann.efficiently.immutable;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Defaults;

public class Immutable<I>
{
    private final Class<I> type;
    private final NextImmutable nextImmutable = new NextImmutable();
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

    public I path()
    {
        return pathRecorder.path();
    }

    public I asObject()
    {
        return immutableFor(type, emptyList());
    }

    public <T> In<T> in(T method)
    {
        return new In<>(pathRecorder.lastCalledPath(), method);
    }

    public class In<T>
    {
        private final List<String> path;
        private final Object defaultValue;

        public In(List<String> path, Object defaultValue)
        {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        public Immutable<I> set(T value)
        {
            return new Immutable<>(type, nextImmutable.setIn(values, path, value), pathRecorder);
        }

        @SuppressWarnings("unchecked")
        public Immutable<I> update(Function<T, T> updater)
        {
            return new Immutable<>( //
                type, //
                nextImmutable.updateIn( //
                    values, //
                    path, //
                    value -> updater.apply((T) (value == null
                        ? defaultValue
                        : value))),  //
                pathRecorder);
        }
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Value
    {
    }

    Map<String, Object> values()
    {
        return values;
    }

    @SuppressWarnings("unchecked")
    private <T> T immutableFor(Class<T> type, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            type.getClassLoader(), //
            new Class[]{type}, //
            new ImmutableObjectInvocationHandler(nestedPath) //
        );
    }

    private class ImmutableObjectInvocationHandler extends AbstractPathInvocationHandler
    {
        public ImmutableObjectInvocationHandler(List<String> path)
        {
            super(path);
        }

        @Override
        protected Object handleInvocation(List<String> path, Method method) throws Throwable
        {
            final List<String> pathWithMethod = pathWith(method);
            final Class<?> returnType = method.getReturnType();

            final Object value = nextImmutable.getInPath(values, pathWithMethod);
            final Object returnValue = value == null
                ? Defaults.defaultValue(returnType)
                : value;

            return returnValue != null && method.isAnnotationPresent(Value.class)
                ? immutableFor(returnType, pathWithMethod)
                : returnValue;
        }
    }
}
