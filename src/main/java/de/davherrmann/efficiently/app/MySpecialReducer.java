package de.davherrmann.efficiently.app;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;
import de.davherrmann.efficiently.server.Reducers;
import de.davherrmann.immutable.Immutable;

public class MySpecialReducer implements Reducer<MySpecialState>
{
    private final Reducers<MySpecialState> reducers = new Reducers<>();

    // TODO one central path recorder per type?
    // private final MySpecialState path = PathRecorder.instanceFor(MySpecialState.class);

    public MySpecialReducer()
    {
        // TODO custom action types?
        // reducers.add("assistantAction/reallyPrint", MySpecialAction.class, (state, path, action) -> state);

        // initialise state
        reducers.add("initState", (state, path, action) -> resetState(state));

        reducers.add("changeLanguage/German", (state, path, action) -> addCaptionsTo(state, "German"));
        reducers.add("changeLanguage/English", (state, path, action) -> addCaptionsTo(state, "English"));

        // possible states
        reducers.add("setState/firstPageEmpty", (state, path, action) -> resetState(state) //
            .in(path.assistant()::currentPage).set(0));
        reducers.add("setState/firstPageEmptyWaiting", (state, path, action) -> resetState(state) //
            .in(path.assistant()::currentPage).set(0) //
            .in(path::waitingForAsync).set(true));
        reducers.add("setState/secondPageEmpty", (state, path, action) -> resetState(state) //
            .in(path.assistant()::currentPage).set(1));
        reducers.add("setState/thirdPageWithDialog", (state, path, action) -> resetState(state) //
            .in(path.assistant()::currentPage).set(2) //
            .in(path::wantToClose).set(true));
        reducers.add("setState/thirdPageWithDialogWaiting", (state, path, action) -> resetState(state) //
            .in(path.assistant()::currentPage).set(2) //
            .in(path::wantToClose).set(true) //
            .in(path::waitingForAsync).set(true));
        reducers.add("setState/English", (state, path, action) -> addCaptionsTo(state, "English"));
        reducers.add("setState/German", (state, path, action) -> addCaptionsTo(state, "German"));

        // async start/stop
        // TODO should/can the framework do this?
        reducers.add("startWaitingForAsync", (state, path, action) -> state //
            .in(path::waitingForAsync).set(true));
        reducers.add("stopWaitingForAsync", (state, path, action) -> state //
            .in(path::waitingForAsync).set(false));

        // assistant actions
        reducers.add("assistantAction/next", (state, path, action) -> state //
            .in(path.assistant()::currentPage).update(page -> page + 1));
        reducers.add("assistantAction/previous", (state, path, action) -> state //
            .in(path.assistant()::currentPage).update(page -> page - 1));
        reducers.add("assistantAction/close", (state, path, action) -> state //
            .in(path::wantToClose).set(true));
        // TODO casting is not really safe here, could be any action
        reducers.add("assistantAction/reallyPrint", (state, path, action) -> state //
            .in(path.assistant()::title).set(((MySpecialAction) action).meta()));

        // dialog actions
        reducers.add("dialogAction/reallyClose", (state, path, action) -> state //
            .in(path::wantToClose).set(false) //
            .in(path.assistant()::title).update(title -> title + " closed...") //
            .in(path.assistant()::title).set(state.get(path.user()::firstname) + " was selected.") //
            .in(path.user()::firstname).set("FooUser"));

        // table actions
        reducers.add("requestNewItems", (state, path, action) -> state //
            .inList(path::items).update(items -> items.size() > 100
                ? items
                : items.addAll(PersonService.persons())));

        // default action
        // TODO use this as default action?
        // TODO pass action as parameter as well!
        // TODO findFirst vs all?
        reducers.add(".*", (state, path, action) -> {
            System.out.println("we have no reducer for action: " + action.type());
            return state  //
                .in(path.assistant()::title).update(title -> title + "!");
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
        final MySpecialState path = state.path();
        final Immutable<MySpecialState> initialState = state.clear() //
            .in(path::waitingForAsync).set(false) //

            .in(path.ewb()::actions).set(new String[]{"print", "close", "save"}) //
            .in(path.ewb()::title).set("MyEWB") //

            .in(path.assistant()::actions).set(new String[]{"print", "close", "save"}) //
            .in(path.assistant()::title).set("MyAssistant") //
            .in(path.assistant()::currentPage).set(0) //

            .in(path::wantToClose).set(false) //
            .inList(path::items).set(PersonService.persons()) //

            .in(path.form()::firstname).set("Foo") //
            .in(path.form()::lastname).set("Bar") //
            .in(path.form()::firstnameLabel).set("First Name FOo") //

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

    private Immutable<MySpecialState> addCaptionsTo(Immutable<MySpecialState> state, String language)
    {
        return state //
            .in(p -> p.assistant()::title).set(captionFor("Deutscher Titel", language)) //
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

    private MySpecialState.PossibleState possibleState(final String name, String title)
    {
        return new Immutable<>(MySpecialState.PossibleState.class) //
            .in(path -> path::name).set(name) //
            .in(path -> path::title).set(title) //
            .asObject();
    }

    @Override
    public Immutable<MySpecialState> reduce(Immutable<MySpecialState> state, MySpecialState path, Action<?> action)
    {
        // TODO is this a good pattern? explicit call vs. implicit call?
        // TODO advantage: middleware - logging & co
        return reducers.reduce(state, path, action);
    }
}
