package de.davherrmann.efficiently.app;

import org.junit.Test;

import com.google.gson.Gson;

public class MySpecialReducerTest
{
    private final Gson gson = new Gson();

    @Test
    public void testImmutablesWithGson() throws Exception
    {
        // given
        ImmutableMySpecialState state = ImmutableMySpecialState.builder() //
            .wantToClose(false) //
            .build();

        // when / then
        String json = gson.toJson(state);
        System.out.println(json);
        System.out.println(gson.fromJson(json, ImmutableMySpecialState.class));
    }
}