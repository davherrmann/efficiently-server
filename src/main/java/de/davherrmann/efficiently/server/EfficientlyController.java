package de.davherrmann.efficiently.server;

import static de.davherrmann.efficiently.view.Assistant.ASSISTANT;
import static de.davherrmann.efficiently.view.Button.BUTTON;
import static de.davherrmann.efficiently.view.Dialog.DIALOG;
import static de.davherrmann.efficiently.view.Input.INPUT;
import static de.davherrmann.efficiently.view.Panel.PANEL;
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
import de.davherrmann.efficiently.view.Components;
import de.davherrmann.efficiently.view.Panel;
import de.davherrmann.immutable.Immutable;
import de.davherrmann.immutable.PathRecorder;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class EfficientlyController
{
    private static Logger log = LoggerFactory.getLogger(EfficientlyServer.class);

    private final Gson gson = new Gson();
    private final Efficiently efficiently;

    @Inject
    public EfficientlyController(Efficiently efficiently)
    {
        this.efficiently = efficiently;
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
        final Components components = new Components(MySpecialState.class);
        final MySpecialState path = PathRecorder.pathInstanceFor(MySpecialState.class);

        // TODO extract into a View
        final Panel view = components.create(PANEL) //
            .content( //
                components.create(ASSISTANT) //
                    .title(path.assistant()::title) //
                    .actions(path.assistant()::actions) //
                    .currentPage(path.assistant()::currentPage) //
                    .content( //
                        components.create(BUTTON) //
                            .onClick(path.actions()::loginUser) //
                            .content(path.form()::firstname), //
                        components.create(INPUT) //
                            .placeholder(path.form()::firstname)), //
                components.create(DIALOG) //
                    .title(path.notification()::title) //
                    // TODO something like a "not(path)"?!
                    .hidden(path.notification()::hidden) //
                    .actions(path.notification()::actions) //
                    .content(path.notification()::message));//

        return new Gson().toJson(view.template());
    }
}
