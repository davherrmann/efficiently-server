package de.davherrmann.efficiently.server;

public interface AsyncDispatcher
{
    void dispatch(Dispatcher syncDispatcher, Action<?> action);
}
