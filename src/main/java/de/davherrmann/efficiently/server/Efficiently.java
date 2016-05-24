package de.davherrmann.efficiently.server;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.immutable.Immutable;

@Component
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class Efficiently implements Dispatcher
{
    private final List<AsyncDispatcher> asyncDispatchers;
    private final List<Reducer<MySpecialState>> reducers;

    // TODO Optionals?
    private Immutable<MySpecialState> lastSentState = new Immutable<>(MySpecialState.class);
    private Immutable<MySpecialState> state = new Immutable<>(MySpecialState.class);

    @Inject
    public Efficiently(final List<AsyncDispatcher> asyncDispatchers, final List<Reducer<MySpecialState>> reducers)
    {
        this.asyncDispatchers = asyncDispatchers;
        this.reducers = reducers;
    }

    public Immutable<MySpecialState> reduce(ClientData clientData)
    {
        // 1. merge client side state
        state = state.merge(clientData.clientStateDiff());

        // 2. possible asynchronous actions
        asyncDispatchers.forEach(asyncDispatcher -> asyncDispatcher.dispatch(this, clientData.action()));
        // 3. reduce
        dispatch(clientData.action());

        // 4. calc diff
        final Immutable<MySpecialState> diff = lastSentState.diff(state);
        System.out.println("state diff: " + new Gson().toJson(diff));

        // 5. save last sent state
        lastSentState = state;

        return diff;
    }

    public void reset()
    {
        lastSentState = new Immutable<>(MySpecialState.class);
    }

    @Override
    public void dispatch(Action<?> action)
    {
        System.out.println("dispatching action: " + action.type());

        state = reducers.stream() //
            .reduce( //
                state, //
                (s, reducer) -> reducer.reduce(s, action), //
                Immutable::merge);
    }
}
