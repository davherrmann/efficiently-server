package de.davherrmann.efficiently.components;

import java.util.function.Supplier;

public interface HasContent<T> extends Element
{
    T content(Supplier<String> text);

    T content(Element... element);
}
