package de.davherrmann.efficiently.server;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import de.davherrmann.efficiently.app.MySpecialState;
import de.davherrmann.efficiently.view.View;
import de.davherrmann.immutable.Immutable;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class EfficientlyController
{
    private static Logger log = LoggerFactory.getLogger(EfficientlyServer.class);

    private final Gson gson = new Gson();
    private final Efficiently efficiently;
    private final View view;

    @Inject
    public EfficientlyController(Efficiently efficiently, View view)
    {
        this.efficiently = efficiently;
        this.view = view;
    }

    @RequestMapping(value = "/", method = {POST})
    public String reduce(@RequestBody final String json)
    {
        log.debug("received raw data from client: ", json);

        final ClientData clientData = gson.fromJson(json, ClientData.class);
        final Immutable<MySpecialState> diff = efficiently.reduce(clientData);

        return gson.toJson(diff);
    }

    @RequestMapping(value = "/reset", method = {POST})
    public String reset()
    {
        log.debug("reset");
        efficiently.reset();
        return "";
    }

    @RequestMapping(value = "/view", method = {GET})
    public String view()
    {
        return new Gson().toJson(view.create().template());
    }
}
