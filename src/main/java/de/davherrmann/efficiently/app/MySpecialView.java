package de.davherrmann.efficiently.app;

import static de.davherrmann.efficiently.view.Assistant.ASSISTANT;
import static de.davherrmann.efficiently.view.Button.BUTTON;
import static de.davherrmann.efficiently.view.Dialog.DIALOG;
import static de.davherrmann.efficiently.view.Field.FIELD;
import static de.davherrmann.efficiently.view.Form.FORM;
import static de.davherrmann.efficiently.view.FormGroup.FORMGROUP;
import static de.davherrmann.efficiently.view.Panel.PANEL;
import static de.davherrmann.efficiently.view.Refresher.REFRESHER;
import static de.davherrmann.efficiently.view.States.STATES;
import static de.davherrmann.efficiently.view.Table.TABLE;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;

import javax.inject.Named;

import de.davherrmann.efficiently.view.Components;
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

        // TODO when binding, we have:
        // - properties (DialogProperties)
        // - actions (DialogActions?) --> no! actions are properties!
        // - content

        // TODO we don't need a bindActions! actions are props as well!
        // .bindActions(AssistantActions.class, path::assistantActions) //

        return components.create(PANEL) //
            .bind(properties -> properties::style).to(path::rootElementStyle) //
            .content( //
                components.create(REFRESHER) //
                    .bindAll(path::refresherProperties), //
                components.create(STATES) //
                    .states(path::possibleStates), //
                components.create(ASSISTANT) //
                    .bindAll(path::assistantProperties) //
                    .content( //
                        components.create(FORM).content( //
                            components.create(FORMGROUP).content( //
                                components.create(FIELD) //
                                    .bindAll(path.pageUserLogin()::userFirstName), //
                                components.create(FIELD) //
                                    .bindAll(path.pageUserLogin()::userLastName)), //
                            components.create(FORMGROUP).content( //
                                components.create(FIELD) //
                                    .bindAll(path.pageUserLogin()::userEmail), //
                                components.create(BUTTON) //
                                    .onClick(path.actions()::loginUser) //
                                    .content(path.pageUserLogin().userFirstName()::value))), //
                        components.create(TABLE) //
                            .bindAll(path::tableProperties)), //
                components.create(DIALOG) //
                    .bindAll(path::dialogProperties) //
                    .content(path::dialogMessage));//
    }
}
