package de.davherrmann.efficiently.server;

public class Actions
{
    private Actions()
    {
    }

    public static Action waitingForAsyncAction(final boolean waiting)
    {
        return new Action(waiting
            ? "startWaitingForAsync"
            : "stopWaitingForAsync");
    }
}
