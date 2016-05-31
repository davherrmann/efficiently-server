package de.davherrmann.efficiently.app;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.inject.Named;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.AsyncDispatcher;
import de.davherrmann.efficiently.server.Dispatcher;

@Named
public class MyAsyncDispatcher extends AsyncDispatcher
{
    // TODO use async dispatcher to change input values via an explicit event?
    // TODO question: why not change input value state in reducer?
    // advantage: we know when to really set the value on client side
    // can we achieve this with server and client side versioned state and a merge strategy?
    // client dispatches ACTION_RESET
    // -> async dispatcher dispatches ACTION_SET_FORM_VALUE("form_X.user.name", "Foo")

    // TODO api: return true if waiting for async action, return false if not? or dispatch: "waiting for async"?
    @Override
    public void dispatch(final Dispatcher syncDispatcher, final Action action)
    {
        // TODO show best practice for testing synchronously (possibly with futures?)
        if (action.type().equals("assistantAction/print"))
        {
            // TODO always call execute? or can the framework call dispatch asynchronously?
            // disadvantage: we could not dispatch a synchronous action here!
            execute(syncDispatcher, () -> {
                // simulate long lasting service call
                sleepUninterruptibly(2, SECONDS);
                syncDispatcher.dispatch(new Action("assistantAction/reallyPrint"));
            });
        }
    }
}
