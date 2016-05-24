package de.davherrmann.efficiently.server;

public class Actions
{
    private Actions()
    {
    }

    public static StandardAction waitingForAsyncAction(final boolean waiting)
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
