package de.davherrmann.efficiently.server;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.regex.Pattern;

import de.davherrmann.efficiently.immutable.Immutable;

public class Reducers<S> implements Reducer<S>
{
    private List<ReducerMapping> reducers = newArrayList();

    public void add(String actionTypeRegEx, Reducer<S> reducer)
    {
        reducers.add(new ReducerMapping(Pattern.compile(actionTypeRegEx), reducer));
    }

    @Override
    public Immutable<S> reduce(Immutable<S> state, S path, Action<?> action)
    {
        return reducers.stream() //
            .filter(r -> r.pattern().matcher(action.type()).matches()) //
            .map(r -> r.reducer().reduce(state, path, action)) //
            .findFirst() //
            .orElse(state);
    }

    private class ReducerMapping
    {
        private final Pattern pattern;
        private final Reducer<S> reducer;

        public ReducerMapping(Pattern pattern, Reducer<S> reducer)
        {
            this.pattern = pattern;
            this.reducer = reducer;
        }

        public Pattern pattern()
        {
            return pattern;
        }

        public Reducer<S> reducer()
        {
            return reducer;
        }
    }
}
