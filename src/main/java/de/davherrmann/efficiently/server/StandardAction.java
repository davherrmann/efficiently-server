package de.davherrmann.efficiently.server;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class StandardAction implements Action<Map<String, String>>
{
    private String type;
    private Map<String, String> meta = ImmutableMap.of();

    public StandardAction()
    {
        this("");
    }

    public StandardAction(final String type)
    {
        this.type = type;
    }

    public String type()
    {
        return type;
    }

    // TODO allow typed Action? with immutable data? returning from asynchronous service call?
    public Map<String, String> meta()
    {
        return meta;
    }
}
