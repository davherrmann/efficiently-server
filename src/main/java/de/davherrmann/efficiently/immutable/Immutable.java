package de.davherrmann.efficiently.immutable;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ClassUtils.isAssignable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.base.Defaults;
import com.google.common.collect.ImmutableMap;

@com.google.gson.annotations.JsonAdapter(ImmutableTypeAdapter.class)
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
        // TODO can we rely on method as defaultValue?
        return new In<>(getAndCheckLastPath(), method);
    }

    public <LT, T extends List<LT>> InList<LT> in(T method)
    {
        return new InList<>(getAndCheckLastPath(), method);
    }

    public Immutable<I> diff(Immutable<I> immutable)
    {
        return new Immutable<>(type, nextImmutable.diff(values, immutable.values()), pathRecorder);
    }

    public Immutable<I> clear()
    {
        return new Immutable<>(type, ImmutableMap.of(), pathRecorder);
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
            return new Immutable<>(type, nextImmutable.setIn(values, path, immutableNodeOr(value)), pathRecorder);
        }

        public Immutable<I> set(Immutable<T> immutableValue)
        {
            return set(immutableValue.asObject());
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

        private Object immutableNodeOr(T value)
        {
            return value instanceof ImmutableNode
                ? ((ImmutableNode) value).values()
                : value;
        }
    }

    public class InList<LT>
    {
        private final List<String> path;
        private final List<LT> defaultValue;

        public InList(List<String> path, List<LT> defaultValue)
        {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        public Immutable<I> set(List<LT> value)
        {
            return new Immutable<>(type, nextImmutable.setIn(values, path, value), pathRecorder);
        }

        public Immutable<I> set(ImmutableList<LT> value)
        {
            return set(value.asList());
        }

        @SuppressWarnings("unchecked")
        public Immutable<I> update(Function<ImmutableList<LT>, ImmutableList<LT>> updater)
        {
            return new Immutable<>( //
                type, //
                nextImmutable.updateIn( //
                    values, //
                    path, //
                    value -> updater.apply(value == null
                        ? new ImmutableList<>()
                        : new ImmutableList<LT>() //
                            .addAll((List<LT>) value)) //
                        .asList()),  //
                pathRecorder);
        }

        // TODO check for redundant code!
        @SuppressWarnings("unchecked")
        public Immutable<I> updateList(Function<List<LT>, List<LT>> updater)
        {
            return new Immutable<>( //
                type, //
                nextImmutable.updateIn( //
                    values, //
                    path, //
                    value -> updater.apply(value == null
                        ? defaultValue
                        : (List<LT>) value)), //
                pathRecorder);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Immutable<?> immutable = (Immutable<?>) o;
        return Objects.equals(type, immutable.type) && Objects.equals(values, immutable.values);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, values);
    }

    Map<String, Object> values()
    {
        return values;
    }

    @SuppressWarnings("unchecked")
    private <T> T immutableFor(Class<T> type, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            ImmutableNode.class.getClassLoader(), //
            new Class[]{type, ImmutableNode.class}, //
            new ImmutableObjectInvocationHandler(nestedPath) //
        );
    }

    private List<String> getAndCheckLastPath()
    {
        final List<String> path = pathRecorder.lastCalledPath();

        if (path.isEmpty())
        {
            throw new IllegalStateException("No path was recorded. Did you use the correct Immutable#path()?");
        }

        return path;
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
            if (method.getName().equals("values"))
            {
                return path.isEmpty()
                    ? values
                    : nextImmutable.getInPath(values, path);
            }

            final List<String> pathWithMethod = pathWith(method);
            final Class<?> returnType = method.getReturnType();

            final Object value = nextImmutable.getInPath(values, pathWithMethod);
            final Object returnValue = value == null
                ? Defaults.defaultValue(returnType)
                : value;
            final boolean isCastable = returnValue == null || isAssignable(returnValue.getClass(), returnType);

            return isCastable
                ? returnValue
                : immutableFor(returnType, pathWithMethod);
        }
    }

    interface ImmutableNode
    {
        Map<String, Object> values();
    }
}
