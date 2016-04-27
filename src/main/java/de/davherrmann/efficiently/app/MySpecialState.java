package de.davherrmann.efficiently.app;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MySpecialState
{

    ImmutableEWB ewb();

    ImmutableAssistant assistant();

    boolean wantToClose();

    ImmutableItem[] items();

    @Value.Immutable
    interface EWB
    {
        String[] actions();

        String title();
    }

    @Value.Immutable
    interface Assistant
    {
        String[] actions();

        String title();

        int currentPage();
    }

    @Value.Immutable
    interface Item
    {
        String firstname();

        String lastname();

        String thumbnail();

        String email();

        String additional();
    }
}
