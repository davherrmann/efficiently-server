package de.davherrmann.efficiently.immutable;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class ImmutableJsonSerializer implements JsonSerializer<Immutable>
{
    @Override
    public JsonElement serialize(Immutable src, Type typeOfSrc, JsonSerializationContext context)
    {
        return context.serialize(src.values());
    }
}
