package de.davherrmann.efficiently.app;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void testArrayToJson() throws Exception
    {
        // given
        MySpecialState.Item[] items = MySpecialReducer.persons();
        List<MySpecialState.Item> itemList = Arrays.asList(items);

        final MySpecialState.Item person = MySpecialReducer.person("firstname", "lastname", "thumbnail", "email");

        // when
        System.out.println(new Gson().toJson(person));
        System.out.println(new Gson().toJson(items));
        System.out.println(new Gson().toJson(itemList));

        // then

    }
}