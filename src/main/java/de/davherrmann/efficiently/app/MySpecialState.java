package de.davherrmann.efficiently.app;

import java.util.List;

import de.davherrmann.efficiently.view.Assistant.AssistantProperties;
import de.davherrmann.efficiently.view.Dialog.DialogState;

public interface MySpecialState
{
    List<PossibleState> possibleStates();

    EWB ewb();

    AssistantProperties assistantProperties();

    DialogState dialogState();

    // TODO let the user extend DialogState? Or keep content in DialogState as well?
    // content in state is not ok, has to be defined in view
    // TODO it should not be DialogState, but rather DialogProperties/DialogAttributes!
    String dialogMessage();

    boolean wantToClose();

    List<Item> items();

    boolean waitingForAsync();

    Form form();

    Item user();

    Actions actions();

    interface EWB
    {
        String[] actions();

        String title();
    }

    interface Item
    {
        String firstname();

        String lastname();

        String thumbnail();

        String email();

        String additional();
    }

    interface Form
    {
        // TODO explicitly mark versioned/client side/controlled input state?
        // @Versioned
        String firstnameLabel();

        String firstname();

        String lastname();

        String email();
    }

    interface PossibleState
    {
        String name();

        String title();
    }

    interface Actions
    {
        String loginUser();
    }
}
