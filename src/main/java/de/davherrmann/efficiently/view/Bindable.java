package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

public interface Bindable<TYPE, STATE>
{
    TYPE bindProperties(Class<STATE> stateType, Supplier<STATE> state);
}
