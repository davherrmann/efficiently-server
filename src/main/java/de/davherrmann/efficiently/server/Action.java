package de.davherrmann.efficiently.server;

public interface Action<T>
{
    String type();

    // TODO allow typed Action? with immutable data? returning from asynchronous service call?
    T meta();
}
