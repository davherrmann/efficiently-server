package de.davherrmann.efficiently.app;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.inject.Named;

import com.google.common.collect.ImmutableMap;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.AsyncDispatcher;
import de.davherrmann.efficiently.server.Dispatcher;

@Named
public class MyAsyncDispatcher extends AsyncDispatcher
{
    // TODO pass state, so we can read it!
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
                syncDispatcher.dispatch(
                    new Action("assistantAction/reallyPrint", ImmutableMap.<String, String>builder() //
                        .put("title", "No worries, we printed it out!") //
                        .build()));
            });
        }
    }
}
