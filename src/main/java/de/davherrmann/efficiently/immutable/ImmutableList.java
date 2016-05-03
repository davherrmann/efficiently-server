package de.davherrmann.efficiently.immutable;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(ImmutableListTypeAdapter.class)
public class ImmutableList<I>
{
    private final Class<I> type;
    private final List<I> values;

    public ImmutableList(Class<I> type)
    {
        this(type, com.google.common.collect.ImmutableList.of());
    }

    private ImmutableList(Class<I> type, List<I> initialValues)
    {
        this.type = type;
        this.values = initialValues;
    }

    // TODO add(Immutable<I> immutableItem) & addAll(ImmutableList<I> immutableItems)?

    ImmutableList<I> add(I item)
    {
        return new ImmutableList<>(type, com.google.common.collect.ImmutableList.<I>builder() //
            .addAll(values) //
            .add(item) //
            .build());
    }

    public ImmutableList<I> addAll(List<I> items)
    {
        return new ImmutableList<>(type, com.google.common.collect.ImmutableList.<I>builder() //
            .addAll(values) //
            .addAll(items) //
            .build());
    }

    public List<I> asList()
    {
        return values;
    }

    public int size()
    {
        return values.size();
    }
}
