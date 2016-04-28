package de.davherrmann.efficiently.app;

public interface MySpecialState
{

    EWB ewb();

    Assistant assistant();

    boolean wantToClose();

    Item[] items();

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
}
