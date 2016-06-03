package de.davherrmann.efficiently.app;

import java.util.List;
import java.util.Map;

import de.davherrmann.efficiently.components.Assistant.AssistantProperties;
import de.davherrmann.efficiently.components.Dialog.DialogProperties;
import de.davherrmann.efficiently.components.Field.FieldProperties;
import de.davherrmann.efficiently.components.Refresher.RefresherProperties;
import de.davherrmann.efficiently.components.States.PossibleState;
import de.davherrmann.efficiently.components.Table.TableProperties;

public interface MySpecialState
{
    Actions actions();

    GlobalState global();

    PageUserLoginState pageUserLogin();

    PageUserListState pageUserList();

    interface GlobalState
    {
        List<PossibleState> possibleStates();

        Map<String, Object> rootElementStyle();

        AssistantProperties assistantProperties();

        DialogProperties dialogProperties();

        RefresherProperties refresherProperties();

        // TODO Keep content in DialogProperties as well?
        // content in state is not ok, has to be defined in view
        String dialogMessage();
    }

    interface PageUserLoginState
    {
        // TODO explicitly mark as controlled/uncontrolled?
        FieldProperties userFirstName();

        FieldProperties userLastName();

        FieldProperties userEmail();
    }

    interface PageUserListState
    {
        TableProperties tableProperties();

        List<Item> items();
    }

    interface Item
    {
        String firstname();

        String lastname();

        String thumbnail();

        String email();

        String additional();
    }

    interface Actions
    {
        String loginUser();
    }
}
