package de.davherrmann.efficiently.immutable;

import java.util.Map;

public class ImmutableNode
{
    private final Map<String, Object> values;

    public ImmutableNode(Map<String, Object> values)
    {
        this.values = values;
    }

    public Map<String, Object> values()
    {
        return values;
    }
}
