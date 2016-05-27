package de.davherrmann.efficiently.view;

import java.util.List;

public interface Dialog extends HasContent<Dialog>, Bindable<Dialog, Dialog.DialogProperties>
{
    Class<Dialog> DIALOG = Dialog.class;

    interface Action
    {
        String type();

        String actionName();
    }

    interface DialogProperties
    {
        String title();

        boolean hidden();

        List<Action> actions();
    }
}
