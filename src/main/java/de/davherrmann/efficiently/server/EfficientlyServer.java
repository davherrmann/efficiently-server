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
import de.davherrmann.efficiently.immutable.Immutable;

@RestController
@ComponentScan
@EnableAutoConfiguration
public class EfficientlyServer implements Dispatcher
{
    private final Gson gson = new Gson();

    // TODO Optionals?
    private Immutable<MySpecialState> lastSentState = new Immutable<>(MySpecialState.class);
    private Immutable<MySpecialState> state = new Immutable<>(MySpecialState.class);

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    String reduce(@RequestBody final String json)
    {
        dispatch(gson.fromJson(json, StandardAction.class));
        final Immutable<MySpecialState> diff = lastSentState.diff(state);
        System.out.println("state diff: " + gson.toJson(diff));
        lastSentState = state;
        // TODO we could send the diff here! client side handling!
        return gson.toJson(lastSentState);
    }

    @Override
    public void dispatch(Action<?> action)
    {
        System.out.println("dispatching action: " + action.type());

        // TODO dependency injection
        new AsyncDispatcher().dispatch(this, action);
        state = new MySpecialReducer().reduce(state, state.path(), action);
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(EfficientlyServer.class, args);
    }
}
