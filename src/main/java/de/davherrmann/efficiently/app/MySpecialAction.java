package de.davherrmann.efficiently.app;

import de.davherrmann.efficiently.server.Action;

public class MySpecialAction implements Action<String>
{
    @Override
    public String type()
    {
        return "assistantAction/reallyPrint";
    }

    @Override
    public String meta()
    {
        return "myMetaData!";
    }
}
