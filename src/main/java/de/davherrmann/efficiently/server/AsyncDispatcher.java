package de.davherrmann.efficiently.server;

import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsyncDispatcher
{
    private final ScheduledExecutorService executorService = newScheduledThreadPool(1);

    public void dispatch(Dispatcher syncDispatcher, Action action)
    {
        // TODO show best practice for testing synchronously
        if (action.type().equals("assistantAction/print"))
        {
            executorService.schedule( //
                () -> syncDispatcher.dispatch(action("assistantAction/reallyPrint")), //
                2, //
                TimeUnit.SECONDS);
        }
    }

    private Action action(final String type)
    {
        return new Action()
        {
            @Override
            public String type()
            {
                return type;
            }
        };
    }
}
