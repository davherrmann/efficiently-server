package de.davherrmann.efficiently.immutable;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImmutableTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();
    private final POJO pojo = immutable.asObject();

    @Test
    public void immutableWorks() throws Exception
    {
        immutable //
            .in(path.title()).set("Test") //
            .in(path.wantToClose()).set(true) //

            .in(path.pojo().wantToClose()).update(wantToClose -> !wantToClose) //
            .in(path.title()).update(title -> title + "!");

        immutable.in(path.currentPage()).update(page -> page + 1);

        // TODO offer a map set function?
        // immutable.in(path.myMap(), "key").set("value");

        // TODO offer a merge function?

        // then
        final POJO changedPOJO = immutable.asObject();
    }

    @Test
    public void asObject_returnsObject() throws Exception
    {
        // given / then
        assertNotNull(pojo);
    }

    @Test
    public void get_returnsDefaultBooleanValue() throws Exception
    {
        // given / then
        assertThat(immutable.asObject().wantToClose(), is(false));
    }

    @Test
    public void set_changesBooleanValue() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(true);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void update_changesBooleanValue() throws Exception
    {
        // TODO is this necessary? Or should the object only be immutable when returned from asObject()?
        // TODO -> yes! this is what we need -> service call with immutable as arg, service should not be allowed
        // TODO to change state!
        // given /  when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).update(value -> !value);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void set_doesNotChangeCurrentObject() throws Exception
    {
        // given / when
        immutable.in(path.wantToClose()).set(true);

        // then
        assertThat(pojo.wantToClose(), is(false));
    }

    @Test
    public void set_returnsNewImmutable() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(false);

        // then
        assertThat(newImmutable, is(not(immutable)));
    }

    @Test
    public void set_returnsImmutable_withSamePath() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(false);

        // then
        assertThat(immutable.path(), is(newImmutable.path()));
    }

    @Test
    public void set_inNestedObject_changesBooleanValue() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.pojo().wantToClose()).set(true);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(false));
        assertThat(newImmutable.asObject().pojo().wantToClose(), is(true));
    }

    @Test
    public void gsonCompatibility() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable  //
            .in(path.wantToClose()).set(true) //
            .in(path.currentPage()).set(2) //
            .in(path.pojo().title()).set("Foo") //
            .in(path.pojo().pojo().currentPage()).set(9) //
            .in(path.pojo().pojo().pojo().title()).set("Bar");

        final Gson gson = new GsonBuilder() //
            .registerTypeAdapter(Immutable.class, new ImmutableJsonSerializer()) //
            .registerTypeAdapter(ImmutableNode.class, new ImmutableNodeJsonSerializer()) //
            .create();

        // when / then
        assertThat(gson.toJson(newImmutable),
            is("{\"pojo\":{\"pojo\":{\"pojo\":{\"title\":\"Bar\"},\"currentPage\":9},\"title\":\"Foo\"},\"currentPage\":2,\"wantToClose\":true}"));
    }

    @Test
    public void immutableDataStructure() throws Exception
    {
        // given
        final Map<String, Object> immutableDataStructure = ImmutableMap.<String, Object>builder() //
            .put("B", ImmutableMap.<String, Object>builder() //
                .put("C1", "C1Foo") //
                .put("C2", "C2Foo") //
                .put("C3", "C3Foo") //
                .build()) //
            .put("D", "EFoo") //
            .build();

        // when
        // TODO change content of D to "FFoo"
        final ImmutableMap<String, Object> immutableDataStructure1 = ImmutableMap.<String, Object>builder() //
            .put("B", immutableDataStructure.get("B")) //
            .put("D", "FFoo") //
            .build();

        // TODO change content of C2 to "C2Bar"
        final ImmutableMap<String, Object> immutableDataStructure2 = ImmutableMap.<String, Object>builder() //
            .put("B", ImmutableMap.<String, Object>builder() //
                .put("C1", ((Map<String, Object>) immutableDataStructure1.get("B")).get("C1")) //
                .put("C2", "C2Bar") //
                .put("C3", ((Map<String, Object>) immutableDataStructure1.get("B")).get("C3")) //
                .build()) //
            .put("D", immutableDataStructure1.get("D")) //
            .build();

        // TODO new top level node, copy all other nodes and values -> automate
        final ImmutableMap<String, Object> immutableDataStructure3 = ImmutableMap.<String, Object>builder() //
            .put("B", immutableDataStructure2.get("B")) //
            .put("D", immutableDataStructure2.get("D")) //
            .build();

        // then
        System.out.println(Arrays.toString(immutableDataStructure.entrySet().toArray()));
        System.out.println(Arrays.toString(immutableDataStructure1.entrySet().toArray()));
        System.out.println(Arrays.toString(immutableDataStructure2.entrySet().toArray()));
        System.out.println(Arrays.toString(immutableDataStructure3.entrySet().toArray()));
    }

    private interface POJO
    {
        String title();

        boolean wantToClose();

        int currentPage();

        Map<String, String> myMap();

        POJO pojo();
    }

}