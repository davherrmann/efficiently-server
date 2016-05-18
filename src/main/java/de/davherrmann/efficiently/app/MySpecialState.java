package de.davherrmann.efficiently.app;

import java.util.List;

public interface MySpecialState
{
    List<PossibleState> possibleStates();

    EWB ewb();

    Assistant assistant();

    boolean wantToClose();

    List<Item> items();

    boolean waitingForAsync();

    Form form();

    interface EWB
    {
        String[] actions();

        String title();
    }

    interface Assistant
    {
        String[] actions();

        String title();

        int currentPage();
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
}
