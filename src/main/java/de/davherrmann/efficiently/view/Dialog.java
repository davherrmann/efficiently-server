package de.davherrmann.efficiently.view;

import java.util.List;
import java.util.function.Supplier;

public interface Dialog extends HasContent<Dialog>
{
    Class<Dialog> DIALOG = Dialog.class;

    Dialog title(Supplier<String> title);

    Dialog hidden(Supplier<Boolean> hidden);

    Dialog actions(Supplier<List<Action>> actions);

    interface Action
    {
        String type();

        String actionName();
    }
}
