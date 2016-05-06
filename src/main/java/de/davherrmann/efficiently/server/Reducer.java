package de.davherrmann.efficiently.server;

import de.davherrmann.efficiently.immutable.Immutable;

public interface Reducer<T>
{
    Immutable<T> reduce(Immutable<T> state, T path, Action<?> action);
}
