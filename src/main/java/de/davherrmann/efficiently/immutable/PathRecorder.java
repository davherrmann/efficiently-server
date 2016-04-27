package de.davherrmann.efficiently.immutable;

import static com.google.common.base.Defaults.defaultValue;
import static java.util.Collections.emptyList;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.reflect.AbstractInvocationHandler;

public class PathRecorder<I>
{
    private final I path;

    private Method lastCalledMethod;
    private List<String> lastCalledNestedPath;

    public PathRecorder(Class<I> type)
    {
        this.path = pathFor(type, emptyList());
    }

    public I path()
    {
        return path;
    }

    public Method lastCalledMethod()
    {
        return lastCalledMethod;
    }

    public List<String> lastCalledNestedPath()
    {
        return lastCalledNestedPath;
    }

    @SuppressWarnings("unchecked")
    private <T> T pathFor(Class<T> type, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            type.getClassLoader(), //
            new Class[]{type}, //
            new PathInvocationHandler(nestedPath));
    }

    private class PathInvocationHandler extends AbstractInvocationHandler
    {
        private final List<String> nestedPath;

        public PathInvocationHandler(final List<String> nestedPath)
        {
            this.nestedPath = nestedPath;
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable
        {
            lastCalledMethod = method;
            lastCalledNestedPath = nestedPath;

            final Class<?> returnType = method.getReturnType();
            final Object defaultValue = defaultValue(returnType);

            return defaultValue != null || !returnType.isInterface()
                ? defaultValue
                : pathFor(returnType, nestedPathWith(method));
        }

        private List<String> nestedPathWith(Method method)
        {
            final ArrayList<String> newNestedPath = Lists.newArrayList(nestedPath);
            newNestedPath.add(method.getName());
            return newNestedPath;
        }
    }

}
