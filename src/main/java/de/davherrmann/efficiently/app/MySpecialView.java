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

import java.util.function.Supplier;

import javax.inject.Named;

import de.davherrmann.efficiently.view.Components;
import de.davherrmann.efficiently.view.Derivation;
import de.davherrmann.efficiently.view.Element;
import de.davherrmann.efficiently.view.View;

@Named
public class MySpecialView implements View
{
    private final Components components = new Components(MySpecialState.class);
    private final MySpecialState path = pathInstanceFor(MySpecialState.class);

    @Override
    public Element create()
    {
        // TODO when binding, we have:
        // - properties (DialogProperties)
        // - actions (DialogActions?) --> no! actions are properties!
        // - content

        // TODO we don't need a bindActions! actions are props as well!
        // .bindActions(AssistantActions.class, path::assistantActions) //

        // TODO allow computed/derived values? or should derivations rather be declared in the reducer?
        // .bind(p -> p::disabled).to(isEmpty(path.pageUserLogin()::userLastName)), //

        // TODO allow setting static props directly
        // .in(p -> p::validateOn).set(Field.ValidateOn.BLUR)

        // TODO dynamic content? list of fields?
        //create(DynamicList.class) //
        //    .template((index))

        final MySpecialState.PageUserLoginState pageUserLogin = path.pageUserLogin();

        return create(PANEL) //
            .bind(properties -> properties::style).to(path.global()::rootElementStyle) //
            .content( //
                create(REFRESHER) //
                    .bindAll(path.global()::refresherProperties), //
                create(STATES) //
                    .states(path.global()::possibleStates), //
                create(ASSISTANT) //
                    .bindAll(path.global()::assistantProperties) //
                    .content( //
                        create(FORM).content( //
                            create(FORMGROUP).content( //
                                create(FIELD) //
                                    .bindAll(pageUserLogin::userFirstName), //
                                create(FIELD) //
                                    .bindAll(pageUserLogin::userLastName) //
                                    .bind(p -> p::disabled).to(isEmpty(pageUserLogin.userFirstName()::value))), //
                            create(FORMGROUP).content( //
                                create(FIELD) //
                                    .bindAll(pageUserLogin::userEmail) //
                                    .bind(p -> p::disabled).to(
                                    lengthGreaterThanFour(pageUserLogin.userFirstName()::value)), //
                                create(BUTTON) //
                                    .onClick(path.actions()::loginUser) //
                                    .content(pageUserLogin.userFirstName()::value))), //
                        create(Page1.class), //
                        create(BUTTON) //
                            .content(pageUserLogin.userFirstName()::value), //
                        create(TABLE) //
                            .bindAll(path.pageUserList()::tableProperties)), //
                create(DIALOG) //
                    .bindAll(path.global()::dialogProperties) //
                    .content(path.global()::dialogMessage));//
    }

    private <T extends Element> T create(Class<T> elementType)
    {
        return components.create(elementType);
    }

    private Derivation lengthGreaterThanFour(Supplier<String> string)
    {
        return new Derivation("lengthGreaterThanFour", string);
    }

    private Derivation isEmpty(Supplier<String> string)
    {
        return new Derivation("isEmpty", string);
    }

    private interface Page1 extends Element
    {
    }

}
