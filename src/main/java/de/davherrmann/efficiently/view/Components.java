package de.davherrmann.efficiently.view;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.reflect.Reflection.newProxy;
import static de.davherrmann.immutable.PathRecorder.pathRecorderInstanceFor;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        private final Method BINDTOSTATE_METHOD;
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
                BINDTOSTATE_METHOD = Bindable.class.getDeclaredMethod("bindProperties", Class.class, Supplier.class);
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
                final Element[] elements = (Element[]) args[0];
                final List<Map<String, Object>> elementTemplates = stream(elements) //
                    .map(Element::template) //
                    .collect(toList());
                template.put("content", elementTemplates);
                return proxy;
            }

            if (method.equals(BINDTOSTATE_METHOD))
            {
                final Class<?> innerStateType = (Class<?>) args[0];
                final Supplier<?> innerState = (Supplier<?>) args[1];
                final List<String> innerStatePath = pathRecorderInstanceFor(stateType).pathFor(innerState);
                final Map<String, String> innerStateBindings = stream(innerStateType.getMethods()) //
                    .map(m -> new SimpleEntry<>(m.getName(), on(".").join(innerStatePath) + "." + m.getName())) //
                    .collect(toMap(Entry::getKey, Entry::getValue));
                template.putAll(innerStateBindings);
                return proxy;
            }

            if (acceptsOneSupplier(method))
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

        private boolean acceptsOneSupplier(final Method method)
        {
            return method.getParameterCount() == 1 && Supplier.class.isAssignableFrom(method.getParameterTypes()[0]);
        }
    }
}