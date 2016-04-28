package de.davherrmann.efficiently.immutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.junit.Test;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableTypeAdapterTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();

    private final ImmutableTypeAdapter typeAdapter = new ImmutableTypeAdapter();
    private final StringWriter out = new StringWriter();
    private final JsonWriter jsonWriter = new JsonWriter(out);

    @Test
    public void write_withEmptyImmutable_writesEmptyObject() throws Exception
    {
        // when
        write(immutable);

        // then
        assertThat(out.toString(), is("{}"));
    }
    @Test
    public void write_setInCustomType_works() throws Exception
    {
        // when
        write(immutable.in(path.name().firstname()).set("Foo"));

        // then
        assertThat(out.toString(), is("{\"name\":{\"firstname\":\"Foo\"}}"));
    }

    @Test
    public void write_overwriteCustomType_works() throws Exception
    {
        // when
        write(immutable.in(path.name()).set(name("Foo", "Bar")));

        // then
        assertThat(out.toString(), is("{\"name\":{\"firstname\":\"Foo\",\"lastname\":\"Bar\"}}"));
    }

    private Immutable<POJO.Name> name(String firstname, String lastname)
    {
        final Immutable<POJO.Name> immutable = new Immutable<>(POJO.Name.class);
        final POJO.Name path = immutable.path();

        return immutable //
            .in(path.firstname()).set(firstname) //
            .in(path.lastname()).set(lastname);
    }

    private void write(Immutable<POJO> immutable) throws IOException
    {
        typeAdapter.write(jsonWriter, immutable);
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

    private static class NameTypeAdapter extends TypeAdapter<POJO.Name>
    {
        @Override
        public void write(JsonWriter out, POJO.Name value) throws IOException
        {
            throw new RuntimeException("called...");
        }

        @Override
        public POJO.Name read(JsonReader in) throws IOException
        {
            return null;
        }
    }
}