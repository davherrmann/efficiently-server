package de.davherrmann.efficiently.server;

import static de.davherrmann.efficiently.server.Actions.waitingForAsyncAction;

public abstract class AsyncDispatcher
{
    public abstract void dispatch(Dispatcher syncDispatcher, Action<?> action);

    protected void execute(Dispatcher syncDispatcher, Runnable target)
    {
        new Thread(() -> {
            target.run();
            syncDispatcher.dispatch(waitingForAsyncAction(false));
        }).start();
        // TODO no boolean, but counter (several threads) -> use update(x -> x + 1) in reducer
        syncDispatcher.dispatch(waitingForAsyncAction(true));
    }
}
