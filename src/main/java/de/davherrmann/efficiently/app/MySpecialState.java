package de.davherrmann.efficiently.app;

import java.util.List;
import java.util.Map;

import de.davherrmann.efficiently.view.Assistant.AssistantProperties;
import de.davherrmann.efficiently.view.Dialog.DialogProperties;
import de.davherrmann.efficiently.view.Field.FieldProperties;
import de.davherrmann.efficiently.view.Refresher.RefresherProperties;
import de.davherrmann.efficiently.view.States.PossibleState;

public interface MySpecialState
{
    List<PossibleState> possibleStates();

    EWB ewb();

    Map<String, Object> rootElementStyle();

    AssistantProperties assistantProperties();

    DialogProperties dialogProperties();

    // TODO let the user extend DialogState? Or keep content in DialogState as well?
    // content in state is not ok, has to be defined in view
    // TODO it should not be DialogState, but rather DialogProperties/DialogAttributes!
    String dialogMessage();

    RefresherProperties refresherProperties();

    boolean wantToClose();

    List<Item> items();

    Form form();

    Item user();

    Actions actions();

    PageUserLogin pageUserLogin();

    interface PageUserLogin
    {
        FieldProperties userFirstName();

        FieldProperties userLastName();

        FieldProperties userEmail();
    }

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

    interface Actions
    {
        String loginUser();
    }
}
