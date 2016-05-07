package de.davherrmann.efficiently.app;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import de.davherrmann.efficiently.immutable.Immutable;

public class PersonService
{
    public static List<MySpecialState.Item> persons()
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

    public static MySpecialState.Item person(String firstName, String lastName, String thumbnail, String email)
    {
        final Immutable<MySpecialState.Item> immutable = new Immutable<>(MySpecialState.Item.class);
        final MySpecialState.Item path = immutable.path();
        return immutable //
            .in(path::firstname).set(firstName) //
            .in(path::lastname).set(lastName) //
            .in(path::thumbnail).set(thumbnail) //
            .in(path::email).set(email) //
            .asObject();
    }
}
