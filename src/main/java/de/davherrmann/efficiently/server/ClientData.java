package de.davherrmann.efficiently.server;

import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.immutable.Immutable;

public class ClientData
{
    private Immutable<MySpecialState> clientStateDiff;
    private StandardAction action;

    public ClientData()
    {
    }

    public ClientData(Immutable<MySpecialState> clientStateDiff, StandardAction action)
    {
        this.clientStateDiff = clientStateDiff;
        this.action = action;
    }

    public Immutable<MySpecialState> clientStateDiff()
    {
        return clientStateDiff;
    }

    public StandardAction action()
    {
        return action;
    }
}
