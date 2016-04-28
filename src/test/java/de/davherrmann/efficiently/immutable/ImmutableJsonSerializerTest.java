package de.davherrmann.efficiently.immutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class ImmutableJsonSerializerTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();
    private final POJO pojo = immutable.asObject();//

    private final Gson gson = new Gson();

    @Test
    public void gsonCompatibility() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable  //
            .in(path.wantToClose()).set(true) //
            .in(path.currentPage()).set(2) //
            .in(path.pojo().title()).set("Foo") //
            .in(path.pojo().pojo().currentPage()).set(9) //
            .in(path.pojo().pojo().pojo().title()).set("Bar");

        // when / then
        assertThat(gson.toJson(newImmutable),
            is("{\"pojo\":{\"pojo\":{\"pojo\":{\"title\":\"Bar\"},\"currentPage\":9},\"title\":\"Foo\"},\"wantToClose\":true,\"currentPage\":2}"));
    }

    @Test
    public void toJson_canHandleMaps_withStandardTypes() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.myMap()).set(
            ImmutableMap.<String, String>builder().put("My", "Map").build());

        // then
        assertThat(gson.toJson(newImmutable), is("{\"myMap\":{\"My\":\"Map\"}}"));
    }

    @Test
    public void toJson_canHandleArrays_withCustomTypes() throws Exception
    {
        // given / when
        final POJO.Name name = new POJO.Name()
        {
            @Override
            public String firstname()
            {
                return "Foo";
            }

            @Override
            public String lastname()
            {
                return "Bar";
            }
        };
        final Immutable<POJO> newImmutable = immutable //
            .in(path.names()).set(new POJO.Name[]{name}) //
            .in(path.name()).set(name) //
            .in(path.currentPage()).set(9);

        System.out.println(gson.toJson(newImmutable));

        // then
        assertThat(gson.toJson(newImmutable), is("{\"names\":[{\"firstname\":\"Foo\",\"lastname\":\"Bar\"}]}"));
    }

    public interface POJO
    {
        String title();

        boolean wantToClose();

        int currentPage();

        Map<String, String> myMap();

        POJO pojo();

        Name[] names();

        Name name();

        interface Name
        {
            String firstname();

            String lastname();
        }
    }
}