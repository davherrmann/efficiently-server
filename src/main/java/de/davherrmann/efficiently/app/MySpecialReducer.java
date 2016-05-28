package de.davherrmann.efficiently.app;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;

import java.util.Map;

import javax.inject.Named;

import com.google.common.collect.ImmutableMap;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;
import de.davherrmann.efficiently.server.Reducers;
import de.davherrmann.efficiently.view.Dialog;
import de.davherrmann.efficiently.view.States.PossibleState;
import de.davherrmann.immutable.Immutable;

@Named
public class MySpecialReducer implements Reducer<MySpecialState>
{
    private final Reducers<MySpecialState> reducers = new Reducers<>();

    public MySpecialReducer()
    {
        // TODO custom action types?
        // reducers.add("assistantAction/reallyPrint", MySpecialAction.class, (state, path, action) -> state);

        // initialise state
        reducers.add("initState", (state, action) -> resetState(state));

        reducers.add("changeLanguage/German", (state, action) -> addCaptionsTo(state, "German"));
        reducers.add("changeLanguage/English", (state, action) -> addCaptionsTo(state, "English"));

        // possible states
        reducers.add("setState/firstPageEmpty", (state, action) -> resetState(state) //
            .in(path().assistantProperties()::currentPage).set(0));
        reducers.add("setState/firstPageEmptyWaiting", (state, action) -> resetState(state) //
            .in(path().assistantProperties()::currentPage).set(0) //
            .in(path().refresherProperties()::refresh).set(true));
        reducers.add("setState/secondPageEmpty", (state, action) -> resetState(state) //
            .in(path().assistantProperties()::currentPage).set(1));
        reducers.add("setState/thirdPageWithDialog", (state, action) -> resetState(state) //
            .in(path().assistantProperties()::currentPage).set(2) //
            .in(path()::wantToClose).set(true));
        reducers.add("setState/thirdPageWithDialogWaiting", (state, action) -> resetState(state) //
            .in(path().assistantProperties()::currentPage).set(2) //
            .in(path()::wantToClose).set(true) //
            .in(path().refresherProperties()::refresh).set(true));
        reducers.add("setState/English", (state, action) -> addCaptionsTo(state, "English"));
        reducers.add("setState/German", (state, action) -> addCaptionsTo(state, "German"));

        // async start/stop
        // TODO should/can the framework do this?
        reducers.add("startWaitingForAsync", (state, action) -> state //
            .in(path().refresherProperties()::refresh).set(true));
        reducers.add("stopWaitingForAsync", (state, action) -> state //
            .in(path().refresherProperties()::refresh).set(false));

        // assistant actions
        reducers.add("assistantAction/next", (state, action) -> state //
            .in(path().assistantProperties()::currentPage).update(page -> page + 1));
        reducers.add("assistantAction/previous", (state, action) -> state //
            .in(path().assistantProperties()::currentPage).update(page -> page - 1));
        reducers.add("assistantAction/close", (state, action) -> state //
            .in(path()::wantToClose).set(true) //
            .in(path().dialogProperties()::hidden).set(false));
        // TODO casting is not really safe here, could be any action
        reducers.add("assistantAction/reallyPrint", (state, action) -> state //
            .in(path().assistantProperties()::title).set(action.meta().toString()));

        // dialog actions
        reducers.add("dialogAction/reallyClose", (state, action) -> state //
            .in(path()::wantToClose).set(false) //
            .in(path().dialogProperties()::hidden).set(true) //
            .in(path().assistantProperties()::title).update(title -> title + " closed...") //
            .in(path().assistantProperties()::title).set(state.get(path().user()::firstname) + " was selected.") //
            .in(path().user()::firstname).set("FooUser"));

        // table actions
        reducers.add("requestNewItems", (state, action) -> state //
            .inList(path()::items).update(items -> items.size() > 100
                ? items
                : items.addAll(PersonService.persons())));

        // default action
        // TODO use this as default action?
        // TODO pass action as parameter as well!
        // TODO findFirst vs all?
        reducers.add(".*", (state, action) -> {
            System.out.println("we have no reducer for action: " + action.type());
            return state  //
                .in(path().assistantProperties()::title).update(title -> title + "!");
        });

        // TODO allow state subset?
        // reducers.add("assistantAction/next", this::assistantNavigateNext, path -> path.assistant());

        // TODO maybe something like (receiving only a part of the state!)
        //actionReducers.add("assistantAction/next", this::navigateToNextPage, state -> state.assistant())

        // Allow function annotations?
        // @Action("assistantAction/next")
        //actionReducerMap.putAll(AnnotatedActionReducers.from(this));
    }

    // TODO threadLocal problematic? constructor is called in different Thread than reducers!
    // maybe we should provide a path in reducers (state, path, action) -> ...
    private MySpecialState path()
    {
        return pathInstanceFor(MySpecialState.class);
    }

    private Immutable<MySpecialState> resetState(Immutable<MySpecialState> state)
    {
        final MySpecialState path = pathInstanceFor(MySpecialState.class);

        final Immutable<MySpecialState> initialState = state.clear() //
            .in(path.refresherProperties()::refresh).set(false) //

            .in(path.ewb()::actions).set(new String[]{"print", "close", "save"}) //
            .in(path.ewb()::title).set("MyEWB") //

            .in(path.assistantProperties()::actions).set(newArrayList("print", "close", "save")) //
            .in(path.assistantProperties()::title).set("MyAssistant") //
            .in(path.assistantProperties()::currentPage).set(0) //

            .in(path::wantToClose).set(false) //
            .in(path.refresherProperties()::refresh).set(false) //
            .inList(path::items).set(PersonService.persons()) //

            .in(path.form()::firstname).set("Foo") //
            .in(path.form()::lastname).set("Bar") //
            .in(path.form()::firstnameLabel).set("First Name FOo") //

            .in(path.actions()::loginUser).set("assistantAction/close") //

            // TODO styles in here?
            .in(path::rootElementStyle).set(ImmutableMap.<String, Object>builder() //
                .put("display", "flex") //
                .build()) //
            .in(path.assistantProperties()::style).set(ImmutableMap.<String, Object>builder() //
                .put("flexGrow", "1") //
                .build()) //

            .in(path.dialogProperties()::title).set("Super major feedback question...") //
            .in(path::dialogMessage).set("Do you really want to close?") //
            .in(path.dialogProperties()::hidden).set(true) //
            .in(path.dialogProperties()::actions).set(newArrayList( //
                dialogAction("dialogAction/reallyClose", "Really close!"),
                dialogAction("dialogAction/cancelClose", "Cancel!"))) //

            // TODO this throws an error
            // .in(path.user()::firstname).set((String) null) //
            .in(path.user()::firstname).set("") //
            .in(path.user()::lastname).set("") //
            .in(path.user()::email).set("") //

            .inList(path::possibleStates).update(list -> list //
                .add(possibleState("firstPageEmpty", "Seite 1 leer")) //
                .add(possibleState("firstPageEmptyWaiting", "Seite 1 leer wartend")) //
                .add(possibleState("secondPageEmpty", "Seite 2 leer")) //
                .add(possibleState("thirdPageWithDialog", "Seite 3 mit Dialog")) //
                .add(possibleState("thirdPageWithDialogWaiting", "Seite 3 mit Dialog wartend")) //
                .add(possibleState("German", "Deutsch")) //
                .add(possibleState("English", "Englisch")) //
            );
        return addCaptionsTo(initialState, "German");
    }

    private Dialog.Action dialogAction(final String type, final String name)
    {
        return new Immutable<>(Dialog.Action.class) //
            .in(p -> p::type).set(type) //
            .in(p -> p::actionName).set(name) //
            .asObject();
    }

    private Immutable<MySpecialState> addCaptionsTo(Immutable<MySpecialState> state, String language)
    {
        return state //
            .in(p -> p.assistantProperties()::title).set(captionFor("Deutscher Titel", language)) //
            .in(p -> p.form()::firstnameLabel).set(captionFor("Vorname", language));
    }

    private String captionFor(String fromString, String language)
    {
        if ("German".equals(language))
        {
            return fromString;
        }

        Map<String, String> translation = newHashMap();
        translation.put("Deutscher Titel", "English Title");
        translation.put("Vorname", "First Name");

        return translation.getOrDefault(fromString, "NO TRANSLATION");
    }

    private PossibleState possibleState(final String name, String title)
    {
        return new Immutable<>(PossibleState.class) //
            .in(path -> path::name).set(name) //
            .in(path -> path::title).set(title) //
            .asObject();
    }

    @Override
    public Immutable<MySpecialState> reduce(Immutable<MySpecialState> state, Action<?> action)
    {
        // TODO is this a good pattern? explicit call vs. implicit call?
        // TODO advantage: middleware - logging & co
        return reducers.reduce(state, action);
    }
}
