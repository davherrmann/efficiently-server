package de.davherrmann.efficiently.app;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import com.google.common.collect.ImmutableMap;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;
import de.davherrmann.efficiently.server.Reducers;
import de.davherrmann.efficiently.view.Dialog;
import de.davherrmann.efficiently.view.Field;
import de.davherrmann.efficiently.view.States.PossibleState;
import de.davherrmann.efficiently.view.Table;
import de.davherrmann.immutable.Immutable;

@Named
public class MySpecialReducer implements Reducer<MySpecialState>
{
    private final Reducers<MySpecialState> reducers = new Reducers<>();
    private final MySpecialState path = pathInstanceFor(MySpecialState.class);

    public MySpecialReducer()
    {
        // TODO custom action types?
        // reducers.add("assistantAction/reallyPrint", MySpecialAction.class, (state, path, action) -> state);

        // initialise state
        reducers.add("initState", (state, action) -> resetState(state));

        reducers.add("changeLanguage/German", (state, action) -> addCaptionsTo(state, "German"));
        reducers.add("changeLanguage/English", (state, action) -> addCaptionsTo(state, "English"));

        final MySpecialState.GlobalState global = path.global();

        // possible states
        reducers.add("setState/firstPageEmpty", (state, action) -> resetState(state) //
            .in(global.assistantProperties()::currentPage).set(0));
        reducers.add("setState/firstPageEmptyWaiting", (state, action) -> resetState(state) //
            .in(global.assistantProperties()::currentPage).set(0) //
            .in(global.refresherProperties()::refresh).set(true));
        reducers.add("setState/secondPageEmpty", (state, action) -> resetState(state) //
            .in(global.assistantProperties()::currentPage).set(1));
        reducers.add("setState/thirdPageWithDialog", (state, action) -> resetState(state) //
            .in(global.assistantProperties()::currentPage).set(2) //
            .in(global.dialogProperties()::hidden).set(false));
        reducers.add("setState/thirdPageWithDialogWaiting", (state, action) -> resetState(state) //
            .in(global.assistantProperties()::currentPage).set(2) //
            .in(global.dialogProperties()::hidden).set(false) //
            .in(global.refresherProperties()::refresh).set(true));
        reducers.add("setState/English", (state, action) -> addCaptionsTo(state, "English"));
        reducers.add("setState/German", (state, action) -> addCaptionsTo(state, "German"));

        // async start/stop
        // TODO should/can the framework do this?
        reducers.add("startWaitingForAsync", (state, action) -> state //
            .in(global.refresherProperties()::refresh).set(true));
        reducers.add("stopWaitingForAsync", (state, action) -> state //
            .in(global.refresherProperties()::refresh).set(false));

        // assistant actions
        reducers.add("assistantAction/next", (state, action) -> state //
            .in(global.assistantProperties()::currentPage).update(page -> page + 1));
        reducers.add("assistantAction/previous", (state, action) -> state //
            .in(global.assistantProperties()::currentPage).update(page -> page - 1));
        reducers.add("assistantAction/close", (state, action) -> state //
            .in(global.dialogProperties()::hidden).set(false));
        // TODO casting is not really safe here, could be any action
        reducers.add("assistantAction/reallyPrint", (state, action) -> state //
            .in(global.assistantProperties()::title).set(action.meta().toString()));

        // dialog actions
        reducers.add("dialogAction/reallyClose", (state, action) -> state //
            .in(global.dialogProperties()::hidden).set(true) //
            .in(global.assistantProperties()::title).update(title -> title + " closed...") //
            .in(global.assistantProperties()::title) //
            .set(state.get(path.pageUserLogin().userFirstName()::value) + " was selected.") //
            .in(path.pageUserLogin().userFirstName()::value).set("FooUser"));

        // table actions
        reducers.add("requestNewItems", (state, action) -> state //
            .inList(path.pageUserList()::items).update(items -> items.size() > 100
                ? items
                : items.addAll(PersonService.persons())));

        // validation
        reducers.add("validate/pageUserLogin.userFirstName", (state, action) -> state //
            .in(path.pageUserLogin().userFirstName()::valid) //
            .set(state.get(path.pageUserLogin().userFirstName()::value).length() % 2 == 0));
        reducers.add("validate/pageUserLogin.userLastName", (state, action) -> {
            final String currentValue = state.get(path.pageUserLogin().userLastName()::value);
            final boolean isValid = currentValue.length() % 2 == 1;
            return state //
                .in(path.pageUserLogin().userLastName()::valid).set(isValid) //
                .in(path.pageUserLogin().userLastName()::error).set(isValid
                    ? ""
                    : "Length must be an odd number!");
        });

        // default action
        // TODO use this as default action?
        // TODO pass action as parameter as well!
        // TODO findFirst vs all?
        reducers.add(".*", (state, action) -> {
            System.out.println("NO REDUCER FOR: " + action.type());
            return state;
        });

        // TODO allow state subset?
        // reducers.add("assistantAction/next", this::assistantNavigateNext, path -> path.assistant());

        // TODO maybe something like (receiving only a part of the state!)
        //actionReducers.add("assistantAction/next", this::navigateToNextPage, state -> state.assistant())

        // Allow function annotations?
        // @Action("assistantAction/next")
        //actionReducerMap.putAll(AnnotatedActionReducers.from(this));
    }

    private Immutable<MySpecialState> resetState(Immutable<MySpecialState> state)
    {
        final MySpecialState path = pathInstanceFor(MySpecialState.class);
        final MySpecialState.GlobalState global = path.global();

        final Immutable<MySpecialState> initialState = state.clear() //
            .in(global.refresherProperties()::refresh).set(false) //

            .in(global.assistantProperties()::actions).set(newArrayList("print", "close", "save")) //
            .in(global.assistantProperties()::title).set("MyAssistant") //
            .in(global.assistantProperties()::currentPage).set(0) //

            .in(global.refresherProperties()::refresh).set(false) //
            .inList(path.pageUserList()::items).set(PersonService.persons()) //

            .in(path.actions()::loginUser).set("assistantAction/close") //

            // TODO styles in here?
            .in(global::rootElementStyle).set(ImmutableMap.<String, Object>builder() //
                .put("display", "flex") //
                .build()) //
            .in(global.assistantProperties()::style).set(ImmutableMap.<String, Object>builder() //
                .put("flexGrow", "1") //
                .build()) //

            .in(global.dialogProperties()::title).set("Super major feedback question...") //
            .in(global::dialogMessage).set("Do you really want to close?") //
            .in(global.dialogProperties()::hidden).set(true) //
            .in(global.dialogProperties()::actions).set(newArrayList( //
                dialogAction("dialogAction/reallyClose", "Really close!"),
                dialogAction("dialogAction/cancelClose", "Cancel!"))) //

            // TODO this throws an error
            // .in(path.user()::firstname).set((String) null) //

            .in(path.pageUserLogin().userFirstName()::cols).set("2,3") //
            .in(path.pageUserLogin().userFirstName()::value).set("Test") //
            .in(path.pageUserLogin().userFirstName()::label).set("First Name") //
            .in(path.pageUserLogin().userFirstName()::validateOn).set(Field.ValidateOn.BLUR) //

            .in(path.pageUserLogin().userLastName()::cols).set("2,3") //
            .in(path.pageUserLogin().userLastName()::label).set("Last Name") //
            .in(path.pageUserLogin().userLastName()::validateOn).set(Field.ValidateOn.CHANGE) //

            .in(path.pageUserLogin().userEmail()::value).set("email@email.com") //
            .in(path.pageUserLogin().userEmail()::label).set("Email") //

            .in(path.pageUserList().tableProperties()::items).set(persons()) //
            .in(path.pageUserList().tableProperties()::columns).set(newArrayList( //
                column("thumbnail", 100), //
                column("firstname"), //
                column("lastname"), //
                column("email"))) //
            .in(path.pageUserList().tableProperties()::onRequestNewItems).set("requestNewItems") //

            .inList(global::possibleStates).update(list -> list //
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

    @SuppressWarnings("unchecked")
    private List<Object> persons()
    {
        return (List<Object>) (List<?>) PersonService.persons();
    }

    private Table.Column column(String columnName)
    {
        return column(columnName, -1);
    }

    private Table.Column column(String columnName, int width)
    {
        return new Immutable<>(Table.Column.class) //
            .in(p -> p::name).set(columnName) //
            .in(p -> p::width).set(width) //
            .asObject();
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
            .in(p -> p.global().assistantProperties()::title).set(captionFor("Deutscher Titel", language)) //
            .in(p -> p.pageUserLogin().userFirstName()::label).set(captionFor("Vorname", language));
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
    public Immutable<MySpecialState> reduce(Immutable<MySpecialState> state, Action action)
    {
        // TODO is this a good pattern? explicit call vs. implicit call?
        // TODO advantage: middleware - logging & co
        return reducers.reduce(state, action);
    }
}
