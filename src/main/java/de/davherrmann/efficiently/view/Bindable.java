package de.davherrmann.efficiently.view;

import java.util.function.Function;
import java.util.function.Supplier;

// TODO naming of parameters!
public interface Bindable<TYPE, STATE>
{
    TYPE bindAll(Supplier<STATE> state);

    <PROP> Binder<TYPE, PROP> bind(Function<STATE, Supplier<PROP>> mapping);

    interface Binder<TYPE, PROP>
    {
        TYPE to(Supplier<PROP> state);
    }
}
