package de.davherrmann.efficiently.view;

import java.util.List;

public interface Dialog extends HasContent<Dialog>, Bindable<Dialog, Dialog.DialogState>
{
    Class<Dialog> DIALOG = Dialog.class;

    interface Action
    {
        String type();

        String actionName();
    }

    public interface DialogState
    {
        String title();

        boolean hidden();

        List<Action> actions();
    }
}
