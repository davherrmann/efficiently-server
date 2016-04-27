package de.davherrmann.efficiently.server;

public interface Reducer<T>
{
    T reduce(T state, Action action);
}
