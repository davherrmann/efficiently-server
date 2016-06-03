package de.davherrmann.efficiently.components;

import java.util.Map;

public interface Element
{
    Class<Element> ELEMENT = Element.class;

    Map<String, Object> template();

    interface ElementProperties
    {
        Map<String, Object> style();
    }
}
