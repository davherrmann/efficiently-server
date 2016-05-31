package de.davherrmann.efficiently.server;

@FunctionalInterface
public interface Dispatcher
{
    void dispatch(Action action);
}
