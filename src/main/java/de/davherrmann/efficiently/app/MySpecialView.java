package de.davherrmann.efficiently.app;

import static de.davherrmann.efficiently.view.Assistant.ASSISTANT;
import static de.davherrmann.efficiently.view.Button.BUTTON;
import static de.davherrmann.efficiently.view.Dialog.DIALOG;
import static de.davherrmann.efficiently.view.Input.INPUT;
import static de.davherrmann.efficiently.view.Panel.PANEL;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;

import javax.inject.Named;

import de.davherrmann.efficiently.view.Assistant;
import de.davherrmann.efficiently.view.Components;
import de.davherrmann.efficiently.view.Dialog;
import de.davherrmann.efficiently.view.Element;
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

        return components.create(PANEL) //
            .content( //
                components.create(ASSISTANT) //
                    // TODO still allow binding without AssistantProperties
                    //.bindProperties((bind, properties) ->  //
                    //    bind(properties::title).to(path.ewb()::title)) //
                    .bindProperties(Assistant.AssistantProperties.class, path::assistantProperties) //
                    // TODO we don't need a bindActions! actions are props as well!
                    // .bindActions(AssistantActions.class, path::assistantActions) //
                    .content( //
                        components.create(BUTTON) //
                            .onClick(path.actions()::loginUser) //
                            .content(path.form()::firstname), //
                        components.create(INPUT) //
                            .placeholder(path.form()::firstname)), //
                components.create(DIALOG) //
                    .bindProperties(Dialog.DialogProperties.class, path::dialogProperties) //
                    .content(path::dialogMessage));//
    }
}
