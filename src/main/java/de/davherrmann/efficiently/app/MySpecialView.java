package de.davherrmann.efficiently.app;

import static de.davherrmann.efficiently.view.Assistant.ASSISTANT;
import static de.davherrmann.efficiently.view.Button.BUTTON;
import static de.davherrmann.efficiently.view.Dialog.DIALOG;
import static de.davherrmann.efficiently.view.Input.INPUT;
import static de.davherrmann.efficiently.view.Panel.PANEL;
import static de.davherrmann.efficiently.view.Refresher.REFRESHER;
import static de.davherrmann.efficiently.view.States.STATES;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;

import javax.inject.Named;

import de.davherrmann.efficiently.view.Assistant.AssistantProperties;
import de.davherrmann.efficiently.view.Components;
import de.davherrmann.efficiently.view.Dialog.DialogProperties;
import de.davherrmann.efficiently.view.Element;
import de.davherrmann.efficiently.view.Element.ElementProperties;
import de.davherrmann.efficiently.view.Refresher.RefresherProperties;
import de.davherrmann.efficiently.view.View;

@Named
public class MySpecialView implements View
{
    @Override
    public Element create()
    {
        final Components components = new Components(MySpecialState.class);
        final MySpecialState path = pathInstanceFor(MySpecialState.class);

        // TODO just do:
        // components.create(ASSISTANT).bindProperties(path.assistant());

        // TODO when binding, we have:
        // - properties (DialogProperties)
        // - actions (DialogActions?) --> no! actions are properties!
        // - content

        // TODO still allow binding without AssistantProperties
        //.bindProperties((bind, properties) ->  //
        //    bind(properties::title).to(path.ewb()::title)) //

        // TODO we don't need a bindActions! actions are props as well!
        // .bindActions(AssistantActions.class, path::assistantActions) //

        return components.create(PANEL) //
            .bindProperties(ElementProperties.class, path::rootElementProperties) //
            .content( //
                components.create(REFRESHER) //
                    .bindProperties(RefresherProperties.class, path::refresherProperties), //
                components.create(STATES) //
                    .states(path::possibleStates), //
                components.create(ASSISTANT) //
                    .bindProperties(AssistantProperties.class, path::assistantProperties) //
                    .content( //
                        components.create(BUTTON) //
                            .onClick(path.actions()::loginUser) //
                            .content(path.form()::firstname), //
                        components.create(INPUT) //
                            .placeholder(path.form()::firstname)), //
                components.create(DIALOG) //
                    .bindProperties(DialogProperties.class, path::dialogProperties) //
                    .content(path::dialogMessage));//
    }
}