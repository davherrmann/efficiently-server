package de.davherrmann.efficiently.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import de.davherrmann.efficiently.app.AsyncDispatcher;
import de.davherrmann.efficiently.app.MySpecialReducer;
import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.immutable.Immutable;

@RestController
@ComponentScan
@EnableAutoConfiguration
public class EfficientlyServer implements Dispatcher
{
    // TODO Optionals?
    private Immutable<MySpecialState> lastSentState = new Immutable<>(MySpecialState.class);
    private Immutable<MySpecialState> state = new Immutable<>(MySpecialState.class);

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    String reduce(@RequestBody final String json)
    {
        System.out.println("raw data: " + json);

        final Gson gson = new Gson();
        final ClientData clientData = gson.fromJson(json, ClientData.class);

        // 1. merge client side state
        state = state.merge(clientData.clientStateDiff());

        // TODO find cleaner way of doing this!
        if ("initState".equals(clientData.action().type()))
        {
            lastSentState = new Immutable<>(MySpecialState.class);
        }

        // 2. possible asynchronous actions
        // 3. reduce
        dispatch(clientData.action());

        // 4. calc diff
        final Immutable<MySpecialState> diff = lastSentState.diff(state);
        System.out.println("state diff: " + gson.toJson(diff));

        // 5. save last sent state
        lastSentState = state;

        // 6. send diff to client
        return gson.toJson(diff);
    }

    @Override
    public void dispatch(Action<?> action)
    {
        System.out.println("dispatching action: " + action.type());

        // TODO dependency injection
        // TODO should an asynchronous action callback be able to call asynchronous actions again?!?
        new AsyncDispatcher().dispatch(this, action);
        state = new MySpecialReducer().reduce(state, state.path(), action);
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(EfficientlyServer.class, args);
    }
}
