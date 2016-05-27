package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

public interface Input extends Element
{
    Class<Input> INPUT = Input.class;

    Input placeholder(Supplier<String> placeholder);
}
