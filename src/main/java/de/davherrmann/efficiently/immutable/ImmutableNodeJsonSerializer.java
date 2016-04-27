package de.davherrmann.efficiently.immutable;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class ImmutableNodeJsonSerializer implements JsonSerializer<ImmutableNode>
{
    @Override
    public JsonElement serialize(ImmutableNode src, Type typeOfSrc, JsonSerializationContext context)
    {
        return context.serialize(src.values());
    }
}
