package de.davherrmann.efficiently.server;

public interface Action<T>
{
    String type();

    // TODO allow typed Action? with immutable data? returning from asynchronous service call?
    // TODO just use a Map<String, String>, extended Actions should use custom data-providing methods
    T meta();
}
