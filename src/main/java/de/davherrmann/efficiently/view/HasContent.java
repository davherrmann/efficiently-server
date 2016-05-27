package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

public interface HasContent<T> extends Element
{
    T content(Supplier<String> text);

    T content(Element... element);
}
