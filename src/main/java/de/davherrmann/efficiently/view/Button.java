package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

public interface Button extends HasContent<Button>
{
    Class<Button> BUTTON = Button.class;

    Button onClick(Supplier<String> actionName);
}
