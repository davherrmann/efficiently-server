package de.davherrmann.efficiently.server;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import de.davherrmann.efficiently.app.AsyncDispatcher;
import de.davherrmann.efficiently.app.MySpecialReducer;
import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.immutable.Immutable;

@Component
@Scope(value = SCOPE_SESSION, proxyMode = TARGET_CLASS)
public class Efficiently implements Dispatcher
{
    // TODO Optionals?
    private Immutable<MySpecialState> lastSentState = new Immutable<>(MySpecialState.class);
    private Immutable<MySpecialState> state = new Immutable<>(MySpecialState.class);

    public Immutable<MySpecialState> reduce(ClientData clientData)
    {
        // 1. merge client side state
        state = state.merge(clientData.clientStateDiff());

        // TODO find cleaner way of doing this!
        // problem: open page, initState, refresh page, initState
        if ("initState".equals(clientData.action().type()))
        {
            lastSentState = new Immutable<>(MySpecialState.class);
        }

        // 2. possible asynchronous actions
        // 3. reduce
        dispatch(clientData.action());

        // 4. calc diff
        final Immutable<MySpecialState> diff = lastSentState.diff(state);
        System.out.println("state diff: " + new Gson().toJson(diff));

        // 5. save last sent state
        lastSentState = state;

        return diff;
    }

    @Override
    public void dispatch(Action<?> action)
    {
        System.out.println("dispatching action: " + action.type());

        // TODO dependency injection
        // TODO should an asynchronous action callback be able to call asynchronous actions again?!?
        new AsyncDispatcher().dispatch(this, action);
        state = new MySpecialReducer().reduce(state, action);
    }
}
