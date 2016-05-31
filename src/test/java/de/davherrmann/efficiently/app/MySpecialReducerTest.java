package de.davherrmann.efficiently.app;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.immutable.Immutable;

public class MySpecialReducerTest
{
    private final Immutable<MySpecialState> emptyState = new Immutable<>(MySpecialState.class);
    private final MySpecialReducer reducer = new MySpecialReducer();
    private Immutable<MySpecialState> initialState = reducer.reduce(emptyState, new Action("initState"));
    @Test
    public void initialState() throws Exception
    {
        // given / then
        assertThat(initialState.asObject().global().assistantProperties().currentPage(), is(0));
    }

    @Test
    public void nextPage() throws Exception
    {
        // given / when
        final Immutable<MySpecialState> state = reducer.reduce(initialState, new Action("assistantAction/next"));

        // then
        assertThat(state.asObject().global().assistantProperties().currentPage(),
            is(initialState.asObject().global().assistantProperties().currentPage() + 1));
    }

}