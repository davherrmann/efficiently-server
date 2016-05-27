package de.davherrmann.efficiently.view;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.reflect.Reflection.newProxy;
import static de.davherrmann.immutable.PathRecorder.pathRecorderInstanceFor;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.reflect.AbstractInvocationHandler;

public class Components
{
    private final Class<?> stateType;

    public Components(Class<?> stateType)
    {
        this.stateType = stateType;
    }

    public <T extends Element> T create(Class<T> type)
    {
        return newProxy(type, new ComponentInvocationHandler(type));
    }

    private class ComponentInvocationHandler extends AbstractInvocationHandler
    {
        private final Method TEMPLATE_METHOD;
        private final Method CONTENT_METHOD;
        private final Class<?> componentType;

        private HashMap<Object, Object> template = newHashMap();

        private ComponentInvocationHandler(Class<?> componentType)
        {
            this.componentType = componentType;
            template.put("type", componentType.getSimpleName());

            try
            {
                TEMPLATE_METHOD = Element.class.getDeclaredMethod("template");
                CONTENT_METHOD = HasContent.class.getDeclaredMethod("content", Element[].class);
            }
            catch (NoSuchMethodException e)
            {
                throw new IllegalStateException("could not find method", e);
            }
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable
        {
            if (method.equals(TEMPLATE_METHOD))
            {
                return template;
            }

            if (method.equals(CONTENT_METHOD))
            {
                template.put("content", stream((Element[]) args[0]) //
                    .map(Element::template) //
                    .collect(toList()));
                return proxy;
            }

            if (method.getParameterCount() == 1 && Supplier.class.isAssignableFrom(method.getParameterTypes()[0]))
            {
                final List<String> path = pathRecorderInstanceFor(stateType).pathFor((Supplier<?>) args[0]);
                template.put(method.getName(), on(".").join(path));
                return proxy;
            }

            throw new UnsupportedOperationException(
                format("Method '%s#%s' is not supported. Please see %s for more information.", //
                    componentType.getName(), //
                    method.getName(), //
                    Element.class.getName()));
        }
    }
}
