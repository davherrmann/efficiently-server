package de.davherrmann.efficiently.components;

import java.util.function.Supplier;

public interface Input extends Element
{
    Class<Input> INPUT = Input.class;

    Input placeholder(Supplier<String> placeholder);
}
