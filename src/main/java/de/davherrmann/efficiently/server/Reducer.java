package de.davherrmann.efficiently.server;

import de.davherrmann.immutable.Immutable;

public interface Reducer<T>
{
    Immutable<T> reduce(Immutable<T> state, Action action);
}
