package de.davherrmann.efficiently.app;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static de.davherrmann.efficiently.server.Actions.waitingForAsyncAction;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.inject.Named;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.AsyncDispatcher;
import de.davherrmann.efficiently.server.Dispatcher;

@Named
public class MyAsyncDispatcher implements AsyncDispatcher
{
    // TODO use async dispatcher to change input values via an explicit event?
    // TODO question: why not change input value state in reducer?
    // advantage: we know when to really set the value on client side
    // can we achieve this with server and client side versioned state and a merge strategy?
    // client dispatches ACTION_RESET
    // -> async dispatcher dispatches ACTION_SET_FORM_VALUE("form_X.user.name", "Foo")

    // TODO api: return true if waiting for async action, return false if not? or dispatch: "waiting for async"?
    @Override
    public void dispatch(Dispatcher syncDispatcher, Action<?> action)
    {
        // TODO show best practice for testing synchronously (possibly with futures?)
        if (action.type().equals("assistantAction/print"))
        {
            new Thread(() -> {
                sleepUninterruptibly(2, SECONDS);
                syncDispatcher.dispatch(new MySpecialAction());
                syncDispatcher.dispatch(waitingForAsyncAction(false));
            });
            // TODO no boolean, but counter (several threads) -> use update(x -> x + 1) in reducer
            syncDispatcher.dispatch(waitingForAsyncAction(true));
        }
    }
}
