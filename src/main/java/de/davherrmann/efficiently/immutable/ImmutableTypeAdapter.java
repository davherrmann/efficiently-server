package de.davherrmann.efficiently.immutable;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableTypeAdapter extends TypeAdapter<Immutable<?>>
{
    @Override
    public void write(JsonWriter out, Immutable<?> value) throws IOException
    {
        new Gson().getAdapter(Map.class).write(out, value.values());
    }

    @Override
    public Immutable<?> read(JsonReader in) throws IOException
    {
        return null;
    }
}
