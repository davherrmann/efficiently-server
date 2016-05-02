package de.davherrmann.efficiently.immutable;

import static com.google.common.base.Defaults.defaultValue;
import static java.util.Collections.emptyList;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.common.collect.ImmutableList;

public class PathRecorder<I>
{
    private final I path;

    private ThreadLocal<List<String>> lastPath = new ThreadLocal<List<String>>()
    {
        @Override
        protected List<String> initialValue()
        {
            return ImmutableList.of();
        }
    };

    public PathRecorder(Class<I> type)
    {
        this.path = pathFor(type, emptyList());
    }

    public I path()
    {
        return path;
    }

    @NotNull
    public List<String> lastCalledPath()
    {
        return this.lastPath.get();
    }

    @SuppressWarnings("unchecked")
    private <T> T pathFor(Class<T> type, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            type.getClassLoader(), //
            new Class[]{type}, //
            new PathInvocationHandler(nestedPath));
    }

    private class PathInvocationHandler extends AbstractPathInvocationHandler
    {
        public PathInvocationHandler(final List<String> nestedPath)
        {
            super(nestedPath);
        }

        @Override
        protected Object handleInvocation(List<String> path, Method method) throws Throwable
        {
            lastPath.set(pathWith(method));

            final Class<?> returnType = method.getReturnType();
            final Object defaultValue = defaultValue(returnType);

            return defaultValue != null || !returnType.isInterface()
                ? defaultValue
                : pathFor(returnType, lastPath.get());
        }
    }

}

