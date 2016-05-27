package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

// TODO naming of parameters!
public interface Bindable<TYPE, STATE>
{
    TYPE bindAll(Supplier<STATE> state);
}
