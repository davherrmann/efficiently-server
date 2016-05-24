package de.davherrmann.efficiently.app;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.StandardAction;
import de.davherrmann.immutable.Immutable;

public class MySpecialReducerTest
{
    private final Immutable<MySpecialState> emptyState = new Immutable<>(MySpecialState.class);
    private final MySpecialReducer reducer = new MySpecialReducer();
    private Immutable<MySpecialState> initialState = reducer.reduce(emptyState, action("initState"));
    @Test
    public void initialState() throws Exception
    {
        // given / then
        assertThat(initialState.asObject().assistant().currentPage(), is(0));
    }

    @Test
    public void nextPage() throws Exception
    {
        // given / when
        final Immutable<MySpecialState> state = reducer.reduce(initialState, action("assistantAction/next"));

        // then
        assertThat(state.asObject().assistant().currentPage(),
            is(initialState.asObject().assistant().currentPage() + 1));
    }

    private Action action(final String type)
    {
        return new StandardAction()
        {
            @Override
            public String type()
            {
                return type;
            }
        };
    }
}