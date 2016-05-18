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

    // TODO use async dispatcher to change input values via an explicit event?
    // TODO question: why not change input value state in reducer?
    // advantage: we know when to really set the value on client side
    // can we achieve this with server and client side versioned state and a merge strategy?
    // client dispatches ACTION_RESET
    // -> async dispatcher dispatches ACTION_SET_FORM_VALUE("form_X.user.name", "Foo")

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
            // TODO no boolean, but counter (several threads) -> use update(x -> x + 1) in reducer
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
