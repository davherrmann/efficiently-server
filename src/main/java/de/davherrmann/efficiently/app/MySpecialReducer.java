package de.davherrmann.efficiently.app;

import de.davherrmann.efficiently.immutable.Immutable;
import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;
import de.davherrmann.efficiently.server.Reducers;

public class MySpecialReducer implements Reducer<MySpecialState>
{
    private final Reducers<MySpecialState> reducers = new Reducers<>();

    public MySpecialReducer()
    {
        // TODO show asynchronous service call

        // TODO custom action types?
        // reducers.add("assistantAction/reallyPrint", MySpecialAction.class, (state, path, action) -> state);

        // TODO could we use method references in path?
        // careful: type erasure -> Supplier == Supplier -> no difference between List<T> and T
        // reducers.add("", state -> path -> action -> state.in(path.assistant()::title).set(""));

        // initialise state
        reducers.add("initState", (state, path, action) -> state.clear() //
            .in(path.ewb()::actions).set(new String[]{"print", "close", "save"}) //
            .in(path.ewb()::title).set("MyEWB") //

            .in(path.assistant()::actions).set(new String[]{"print", "close", "save"}) //
            .in(path.assistant()::title).set("MyAssistant") //
            .in(path.assistant()::currentPage).set(2) //

            .in(path::wantToClose).set(false) //
            .inList(path::items).set(PersonService.persons()) //

            .in(path.user()::firstname).set("Foo") //
            .in(path.user()::lastname).set("Bar"));

        // TODO should/can the framework do this?
        reducers.add("startWaitingForAsync", (state, path, action) -> state //
            .in(path::waitingForAsync).set(true));
        reducers.add("stopWaitingForAsync", (state, path, action) -> state //
            .in(path::waitingForAsync).set(false));

        // assistant actions
        reducers.add("assistantAction/next", state -> path -> action -> state //
            .in(path.assistant()::currentPage).update(page -> page + 1));
        reducers.add("assistantAction/previous", state -> path -> action -> state //
            .in(path.assistant()::currentPage).update(page -> page - 1));
        reducers.add("assistantAction/close", state -> path -> action -> state //
            .in(path::wantToClose).set(true));
        // TODO casting is not really safe here, could be any action
        reducers.add("assistantAction/reallyPrint", (state, path, action) -> state //
            .in(path.assistant()::title).set(((MySpecialAction) action).meta()));

        // dialog actions
        reducers.add("dialogAction/reallyClose", (state, path, action) -> state //
            .in(path::wantToClose).set(false) //
            .in(path.assistant()::title).update(title -> title + " closed..."));

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

    @Override
    public Immutable<MySpecialState> reduce(Immutable<MySpecialState> state, MySpecialState path, Action<?> action)
    {
        // TODO is this a good pattern? explicit call vs. implicit call?
        // TODO advantage: middleware - logging & co
        return reducers.reduce(state, path, action);
    }
}
