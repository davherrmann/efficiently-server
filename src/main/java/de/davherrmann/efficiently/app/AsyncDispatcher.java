package de.davherrmann.efficiently.app;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Dispatcher;
import de.davherrmann.efficiently.server.StandardAction;

public class AsyncDispatcher
{
    private final ScheduledExecutorService executorService = newScheduledThreadPool(1);

    // TODO api: return true if waiting for async action, return false if not? or dispatch: "waiting for async"?
    public void dispatch(Dispatcher syncDispatcher, Action<?> action)
    {
        // TODO show best practice for testing synchronously (possibly with futures?)
        if (action.type().equals("assistantAction/print"))
        {
            executorService.schedule( //
                () -> {
                    syncDispatcher.dispatch(new MySpecialAction());
                    syncDispatcher.dispatch(waitingForAsyncAction(false));
                }, //
                2, //
                TimeUnit.SECONDS);
            // TODO no boolean, but counter (several threads)
            syncDispatcher.dispatch(waitingForAsyncAction(true));
        }
    }

    private StandardAction waitingForAsyncAction(final boolean waiting)
    {
        return new StandardAction()
        {
            @Override
            public String type()
            {
                return waiting
                    ? "startWaitingForAsync"
                    : "stopWaitingForAsync";
            }
        };
    }
}
