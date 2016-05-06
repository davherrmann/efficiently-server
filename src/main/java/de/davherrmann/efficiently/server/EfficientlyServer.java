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
    // TODO dependency injection
    private final MySpecialReducer reducer = new MySpecialReducer();
    private AsyncDispatcher asyncDispatcher = new AsyncDispatcher();

    private final Gson gson = new Gson();

    // TODO Optionals?
    private Immutable<MySpecialState> state = new Immutable<>(MySpecialState.class);

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    String reduce(@RequestBody final String json)
    {
        dispatch(gson.fromJson(json, StandardAction.class));
        return gson.toJson(state);
    }

    @Override
    public void dispatch(Action<?> action)
    {
        System.out.println("dispatching action: " + action.type());

        asyncDispatcher.dispatch(this, action);

        final Immutable<MySpecialState> newState = reducer.reduce(state, state.path(), action);

        System.out.println("state diff: " + gson.toJson(state.diff(newState)));

        state = newState;
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(EfficientlyServer.class, args);
    }
}
