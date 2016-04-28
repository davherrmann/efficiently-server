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
import com.google.gson.GsonBuilder;

import de.davherrmann.efficiently.app.MySpecialReducer;
import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.efficiently.immutable.Immutable;
import de.davherrmann.efficiently.immutable.ImmutableJsonSerializer;

@RestController
@ComponentScan
@EnableAutoConfiguration
public class EfficientlyServer
{
    // TODO dependency injection
    private final Gson gson = new GsonBuilder() //
        .registerTypeAdapter(Immutable.class, new ImmutableJsonSerializer()) //
        .create();
    private final MySpecialReducer reducer = new MySpecialReducer();

    // TODO Optionals?
    private Immutable<MySpecialState> currentState = new Immutable<>(MySpecialState.class);

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    String reduce(@RequestBody final String json)
    {
        System.out.println("got raw data: " + json);

        final Action action = gson.fromJson(json, Action.class);
        System.out.println("action: " + action);

        final Immutable<MySpecialState> newState = reducer.reduce(currentState, action);

        System.out.println("state diff: " + gson.toJson(currentState.diff(newState)));

        currentState = newState;

        return gson.toJson(newState);
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(EfficientlyServer.class, args);
    }
}
