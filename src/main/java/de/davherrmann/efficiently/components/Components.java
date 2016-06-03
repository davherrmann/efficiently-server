package de.davherrmann.efficiently.components;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.reflect.Reflection.newProxy;
import static de.davherrmann.immutable.PathRecorder.pathRecorderInstanceFor;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.AbstractInvocationHandler;

import de.davherrmann.immutable.PathRecorder;

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
        private final Method CONTENT_STRING_METHOD;
        private final Method BINDALL_METHOD;
        private final Method BIND_METHOD;
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
                CONTENT_STRING_METHOD = HasContent.class.getDeclaredMethod("content", Supplier.class);
                BINDALL_METHOD = Bindable.class.getDeclaredMethod("bindAll", Supplier.class);
                BIND_METHOD = Bindable.class.getDeclaredMethod("bind", Function.class);
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

            if (method.equals(CONTENT_STRING_METHOD))
            {
                final String path = on(".").join(pathRecorderInstanceFor(stateType).pathFor((Supplier<?>) args[0]));
                template.put(method.getName(), path);
                return proxy;
            }

            if (method.equals(BINDALL_METHOD))
            {
                final PathRecorder<?> pathRecorder = pathRecorderInstanceFor(stateType);
                final Supplier<?> innerState = (Supplier<?>) args[0];
                final Class<?> innerStateType = pathRecorder.methodFor(innerState).getReturnType();
                final List<String> innerStatePath = pathRecorder.pathFor(innerState);

                final Map<String, Object> innerStateBindings = stream(innerStateType.getMethods()) //
                    .map(m -> new SimpleEntry<>( //
                        m.getName(), //
                        derivation("none", on(".").join(innerStatePath) + "." + m.getName()))) //
                    .collect(toMap(Entry::getKey, Entry::getValue));
                template.putAll(innerStateBindings);
                return proxy;
            }

            if (method.equals(BIND_METHOD))
            {
                final Class<?> propertiesType = (Class<?>) stream(componentType.getGenericInterfaces()) //
                    .filter(i -> i instanceof ParameterizedType) //
                    .map(i -> (ParameterizedType) i) //
                    .filter(i -> Bindable.class.equals(i.getRawType())) //
                    .findFirst() //
                    .map(ParameterizedType::getActualTypeArguments) //
                    .get()[1];

                final PathRecorder<?> propertyTypePathRecorder = pathRecorderInstanceFor(propertiesType);

                @SuppressWarnings("unchecked")
                final Function<Object, Supplier<Object>> mapping = (Function<Object, Supplier<Object>>) args[0];
                final Supplier<Object> nestedProperty = mapping.apply(propertyTypePathRecorder.path());
                final List<String> nestedPropertyPath = propertyTypePathRecorder.pathFor(nestedProperty);
                return new BinderImpl(nestedPropertyPath, proxy);
            }

            if (acceptsOneSupplier(method))
            {
                final String path = on(".").join(pathRecorderInstanceFor(stateType).pathFor((Supplier<?>) args[0]));
                template.put( //
                    method.getName(), //
                    derivation("none", path));
                return proxy;
            }

            throw new UnsupportedOperationException(
                format("Method '%s#%s' is not supported. Please see %s for more information.", //
                    componentType.getName(), //
                    method.getName(), //
                    Element.class.getName()));
        }

        private class BinderImpl implements Bindable.Binder<Object, Object>
        {
            private final List<String> nestedPropertyPath;
            private final Object proxy;
            private final PathRecorder<?> pathRecorder;

            public BinderImpl(List<String> nestedPropertyPath, Object proxy)
            {
                this.nestedPropertyPath = nestedPropertyPath;
                this.proxy = proxy;
                pathRecorder = pathRecorderInstanceFor(stateType);
            }

            @Override
            public Object to(Supplier<Object> state)
            {
                return to(new Derivation("none", state));
            }

            @Override
            public Object to(Derivation derivation)
            {
                template.put( //
                    on(".").join(nestedPropertyPath), //
                    derivation( //
                        derivation.name(), //
                        on(".").join(pathRecorder.pathFor(derivation.sourceValue()))));
                return proxy;
            }
        }

        private Map<Object, Object> derivation(String name, String sourcePath)
        {
            return ImmutableMap.builder() //
                .put("name", name) //
                .put("sourceValue", sourcePath) //
                .build();
        }

        private boolean acceptsOneSupplier(final Method method)
        {
            return method.getParameterCount() == 1 && Supplier.class.isAssignableFrom(method.getParameterTypes()[0]);
        }
    }
}
