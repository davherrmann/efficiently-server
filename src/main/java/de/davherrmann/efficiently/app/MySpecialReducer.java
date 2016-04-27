package de.davherrmann.efficiently.app;

import de.davherrmann.efficiently.server.Action;
import de.davherrmann.efficiently.server.Reducer;

public class MySpecialReducer implements Reducer<ImmutableMySpecialState>
{
    @Override
    public ImmutableMySpecialState reduce(ImmutableMySpecialState state, Action action)
    {
        switch (action.type())
        {
            case "initState":
                return ImmutableMySpecialState.builder() //
                    .ewb(ImmutableEWB.builder() //
                        .actions("print", "close", "save") //
                        .title("MyEWB") //
                        .build()) //
                    .assistant(ImmutableAssistant.builder() //
                        .actions("print", "close", "save") //
                        .title("MyAssistant") //
                        .currentPage(2) //
                        .build()) //
                    .wantToClose(false) //
                    .items(persons()) //
                    .build();

            case "assistantAction":
                final ImmutableAssistant assistant = state.assistant();
                if (action.actionId().equals("previous"))
                    return state.withAssistant(assistant.withCurrentPage(assistant.currentPage() - 1));
                if (action.actionId().equals("next"))
                    return state.withAssistant(assistant.withCurrentPage(assistant.currentPage() + 1));
                if (action.actionId().equals("print"))
                    return state.withAssistant(assistant.withTitle(assistant.title() + "!"));
                if (action.actionId().equals("save"))
                    return state.withAssistant(assistant.withActions("print", "close"));
                if (action.actionId().equals("close"))
                    return state.withWantToClose(true);
                break;

            case "dialogAction":
                if (action.actionId().equals("reallyClose"))
                    return state.withWantToClose(false);


        }

        return state;
    }

    private ImmutableItem[] persons()
    {
        return new ImmutableItem[]{person("hilla", "sakala", "https://randomuser.me/api/portraits/thumb/women/32.jpg",
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
                "ayşe.tuğlu@example.com")};
    }

    private ImmutableItem person(String firstName, String lastName, String thumbnail, String email)
    {
        return ImmutableItem.builder() //
            .additional(Double.toString(Math.random())) //
            .firstname(firstName) //
            .lastname(lastName) //
            .thumbnail(thumbnail) //
            .email(email) //
            .build();
    }
}
