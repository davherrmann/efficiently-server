package de.davherrmann.efficiently.server;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import de.davherrmann.efficiently.immutable.Immutable;

public class Reducers<S> implements Reducer<S>
{
    private List<ReducerMapping> reducers = newArrayList();

    // TODO do we need this monad arg?
    public void add(String actionRegEx, Function<Immutable<S>, Function<S, Function<Action<?>, Immutable<S>>>> reducer)
    {
        reducers.add(new ReducerMapping(Pattern.compile(actionRegEx), reducer));
    }

    public void add(String actionTypeRegEx, Reducer<S> reducer)
    {
        reducers.add(new ReducerMapping(Pattern.compile(actionTypeRegEx),
            state -> path -> action -> reducer.reduce(state, path, action)));
    }

    @Override
    public Immutable<S> reduce(Immutable<S> state, S path, Action<?> action)
    {
        return reducers.stream() //
            .filter(r -> r.pattern().matcher(action.type()).matches()) //
            .map(r -> r.reducer() //
                .apply(state) //
                .apply(path) //
                .apply(action)) //
            .findFirst() //
            .orElse(state);
    }

    private class ReducerMapping
    {
        private final Pattern pattern;
        private final Function<Immutable<S>, Function<S, Function<Action<?>, Immutable<S>>>> reducer;

        public ReducerMapping(Pattern pattern,
            Function<Immutable<S>, Function<S, Function<Action<?>, Immutable<S>>>> reducer)
        {
            this.pattern = pattern;
            this.reducer = reducer;
        }

        public Pattern pattern()
        {
            return pattern;
        }

        public Function<Immutable<S>, Function<S, Function<Action<?>, Immutable<S>>>> reducer()
        {
            return reducer;
        }
    }
}
