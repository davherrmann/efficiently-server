package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

public class Derivation
{
    private final String name;
    private final Supplier<?> sourceValue;

    public Derivation(final String name, final Supplier<?> sourceValue)
    {
        this.name = name;
        this.sourceValue = sourceValue;
    }

    public String name()
    {
        return name;
    }

    public Supplier<?> sourceValue()
    {
        return sourceValue;
    }
}
