package de.davherrmann.efficiently.immutable;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImmutableTest
{
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

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
    public void set_returnsNewImmutable2() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(false);

        // then
        assertThat(newImmutable, is(not(immutable)));
    }

    @Test
    public void set_returnsImmutable2_withSamePath() throws Exception
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
            .create();

        // when / then
        assertThat(gson.toJson(newImmutable),
            is("{\"pojo\":{\"pojo\":{\"pojo\":{\"title\":\"Bar\"},\"currentPage\":9},\"title\":\"Foo\"},\"wantToClose\":true,\"currentPage\":2}"));
    }

    @Test
    public void equals_returnsTrue_forEqualImmutables() throws Exception
    {
        // given / then
        assertThat(new Immutable<>(POJO.class), is(new Immutable<>(POJO.class)));
    }

    @Test
    public void diff_returnsChanges() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable.in(path.pojo().wantToClose()).set(true);

        // TODO should you be able to pass a PathRecorder into constructor?
        // TODO -> one "unnecessary" variable: initialDiffImmutable
        final Immutable<POJO> initialDiffImmutable = new Immutable<>(POJO.class);
        final POJO diffPath = initialDiffImmutable.path();
        final Immutable<POJO> diffImmutable = initialDiffImmutable.in(diffPath.pojo().wantToClose()).set(true);

        // when
        final Immutable<POJO> diff = immutable.diff(newImmutable);

        // then
        assertThat(diff, is(diffImmutable));
    }

    @Test
    public void changeWithOwnUnusedPath_usingWrongPath_throwsMeaningfulException() throws Exception
    {
        // then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No path was recorded. Did you use the correct Immutable#path()?");

        // when
        new Immutable<>(POJO.class).in(path.wantToClose()).set(true);
    }

    @Test
    public void clear_returnsEmptyState() throws Exception
    {
        // when
        final Immutable<POJO> clearedImmutable = immutable.clear();

        // then
        assertThat(immutable.values().isEmpty(), is(true));
    }

    @Test
    public void setIn_canHandleMaps() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.myMap()).set(
            ImmutableMap.<String, String>builder().put("My", "Map").build());

        // then
        assertThat(newImmutable.asObject().myMap().get("My"), is("Map"));
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