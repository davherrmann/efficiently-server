package de.davherrmann.efficiently.app;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import de.davherrmann.efficiently.app.MySpecialState.Item;
import de.davherrmann.efficiently.immutable.Immutable;
import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;

public class MySpecialReducer implements Reducer<Immutable<MySpecialState>>
{
    @Override
    public Immutable<MySpecialState> reduce(Immutable<MySpecialState> state, Action action)
    {
        final MySpecialState path = state.path();
        final String actionId = action.actionId();

        // TODO show asynchronous service call! waiting for service, dispatching action, service answer

        switch (action.type())
        {
            case "initState":
                return state.clear() //
                    .in(path.ewb().actions()).set(new String[]{"print", "close", "save"}) //
                    .in(path.ewb().title()).set("MyEWB") //

                    .in(path.assistant().actions()).set(new String[]{"print", "close", "save"}) //
                    .in(path.assistant().title()).set("MyAssistant") //
                    .in(path.assistant().currentPage()).set(2) //

                    .in(path.wantToClose()).set(false) //
                    .in(path.items()).set(persons());

            case "assistantAction":
                if (actionId.equals("previous"))
                    return state.in(path.assistant().currentPage()).update(page -> page - 1);
                if (actionId.equals("next"))
                    return state.in(path.assistant().currentPage()).update(page -> page + 1);
                if (actionId.equals("print"))
                    return state.in(path.assistant().title()).update(title -> title + "!");
                if (actionId.equals("save"))
                    return state.in(path.assistant().actions()).set(new String[]{"print", "close"});
                if (actionId.equals("close"))
                    return state.in(path.wantToClose()).set(true);
                break;

            case "dialogAction":
                if (actionId.equals("reallyClose"))
                    return state.in(path.wantToClose()).set(false);

            case "requestNewItems":
                return state.in(path.items()).update(items -> items.size() > 100
                    ? items
                    : items.addAll(persons()));
        }

        return state;
    }

    static List<Item> persons()
    {
        return newArrayList(person("hilla", "sakala", "https://randomuser.me/api/portraits/thumb/women/32.jpg",
            "hilla.sakala@example.com"),
            person("samuel", "mitchell", "https://randomuser.me/api/portraits/thumb/men/2.jpg",
                "samuel.mitchell@example.com"),
            person("helmi", "ranta", "https://randomuser.me/api/portraits/thumb/women/18.jpg",
                "helmi.ranta@example.com"),
            person("ellen", "silva", "https://randomuser.me/api/portraits/thumb/women/3.jpg",
                "ellen.silva@example.com"),
            person("یاسمین", "گلشن", "https://randomuser.me/api/portraits/thumb/women/74.jpg",
                "یاسمین.گلشن@example.com"),
            person("martin", "gautier", "https://randomuser.me/api/portraits/thumb/men/19.jpg",
                "martin.gautier@example.com"),
            person("brooke", "miles", "https://randomuser.me/api/portraits/thumb/women/4.jpg",
                "brooke.miles@example.com"),
            person("elif", "aydan", "https://randomuser.me/api/portraits/thumb/women/59.jpg", "elif.aydan@example.com"),
            person("alfred", "craig", "https://randomuser.me/api/portraits/thumb/men/97.jpg",
                "alfred.craig@example.com"),
            person("حامد", "کریمی", "https://randomuser.me/api/portraits/thumb/men/99.jpg", "حامد.کریمی@example.com"),
            person("danny", "torres", "https://randomuser.me/api/portraits/thumb/men/61.jpg",
                "danny.torres@example.com"),
            person("hayley", "walker", "https://randomuser.me/api/portraits/thumb/women/18.jpg",
                "hayley.walker@example.com"),
            person("konrad", "zimmer", "https://randomuser.me/api/portraits/thumb/men/51.jpg",
                "konrad.zimmer@example.com"),
            person("lily", "ross", "https://randomuser.me/api/portraits/thumb/women/16.jpg", "lily.ross@example.com"),
            person("fernando", "watts", "https://randomuser.me/api/portraits/thumb/men/48.jpg",
                "fernando.watts@example.com"),
            person("alexandre", "ambrose", "https://randomuser.me/api/portraits/thumb/men/15.jpg",
                "alexandre.ambrose@example.com"),
            person("mario", "bravo", "https://randomuser.me/api/portraits/thumb/men/44.jpg", "mario.bravo@example.com"),
            person("roméo", "morin", "https://randomuser.me/api/portraits/thumb/men/93.jpg", "roméo.morin@example.com"),
            person("leo", "li", "https://randomuser.me/api/portraits/thumb/men/95.jpg", "leo.li@example.com"),
            person("ayşe", "tuğlu", "https://randomuser.me/api/portraits/thumb/women/12.jpg",
                "ayşe.tuğlu@example.com"));
    }

    static Item person(String firstName, String lastName, String thumbnail, String email)
    {
        final Immutable<Item> immutable = new Immutable<>(Item.class);
        final Item path = immutable.path();
        return immutable //
            .in(path.firstname()).set(firstName) //
            .in(path.lastname()).set(lastName) //
            .in(path.thumbnail()).set(thumbnail) //
            .in(path.email()).set(email) //
            .asObject();
    }
}
