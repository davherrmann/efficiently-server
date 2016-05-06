package de.davherrmann.efficiently.server;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Action
{
    private String type = "";
    private Map<String, String> meta = ImmutableMap.of();

    public String type()
    {
        return type;
    }

    public Map<String, String> meta()
    {
        return meta;
    }
}
