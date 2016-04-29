package de.davherrmann.efficiently.immutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

public class ImmutableTypeAdapterTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();
    private final Gson gson = new Gson();

    @Test
    public void write_withEmptyImmutable_writesEmptyObject() throws Exception
    {
        // when / then
        assertThat(gson.toJson(immutable), is("{}"));
    }
    @Test
    public void write_setInCustomType_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name().firstname()).set("Foo");

        // then
        assertThat(gson.toJson(newImmutable), is("{\"name\":{\"firstname\":\"Foo\"}}"));
    }

    @Test
    public void write_setCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name()).set(name("Foo", "Bar"));

        // then
        assertThat(gson.toJson(newImmutable), is("{\"name\":{\"firstname\":\"Foo\",\"lastname\":\"Bar\"}}"));
    }

    @Test
    public void write_setInCustomType_withPreviousCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name()).set(name("Foo", "Bar")) //
            .in(path.name().lastname()).set("B");

        // then
        assertThat(gson.toJson(newImmutable), is("{\"name\":{\"firstname\":\"Foo\",\"lastname\":\"B\"}}"));
    }

    private Immutable<POJO.Name> name(String firstname, String lastname)
    {
        final Immutable<POJO.Name> immutable = new Immutable<>(POJO.Name.class);
        final POJO.Name path = immutable.path();

        return immutable //
            .in(path.firstname()).set(firstname) //
            .in(path.lastname()).set(lastname);
    }

    private interface POJO
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