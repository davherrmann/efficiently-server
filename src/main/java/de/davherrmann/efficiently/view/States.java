package de.davherrmann.efficiently.view;

import java.util.List;
import java.util.function.Supplier;

public interface States extends Element
{
    Class<States> STATES = States.class;

    States states(Supplier<List<PossibleState>> states);

    interface PossibleState
    {
        String name();

        String title();
    }
}
