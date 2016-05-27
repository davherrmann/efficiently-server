package de.davherrmann.efficiently.view;

import static com.google.common.collect.Lists.newArrayList;
import static de.davherrmann.efficiently.view.ComponentsTest.TestElement.TESTELEMENT;
import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.util.function.Supplier;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;

import de.davherrmann.efficiently.view.ComponentsTest.TestElement.TestElementState;
import de.davherrmann.immutable.PathRecorder;

public class ComponentsTest
{
    @Rule
    public ExpectedException thrown = none();

    private final Components components = new Components(State.class);
    private final State path = PathRecorder.pathInstanceFor(State.class);

    @Test
    public void template_returnsMapContainingType_withInitialElement() throws Exception
    {
        // given
        final Element element = create(TESTELEMENT);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .build()));
    }
    @Test
    public void template_returnsMapWithPathToTitle_whenTitleIsSet() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.title(path::specialTitle);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", "specialTitle") //
            .build()));
    }

    @Test
    public void template_returnsMapWithPathToNestedTitle_whenTitleIsSet() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.title(path.nested()::specialTitle);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", "nested.specialTitle") //
            .build()));
    }

    @Test
    public void template_returnsMapWithArrayContainingNestedElementMap_whenElementIsSetAsContent() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.content(create(TESTELEMENT));

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("content", newArrayList( //
                ImmutableMap.builder() //
                    .put("type", "TestElement") //
                    .build())) //
            .build()));
    }

    @Test
    public void template_returnsMapWithAllStateBindings_whenElementIsBoundToState() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.bindAll(path::testElementState);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", "testElementState.title") //
            .build()));
    }

    @Test
    public void usingUnsupportedMethod_throwsMeaningfulException() throws Exception
    {
        // TODO add documentation to Element
        // then
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage(format("Method '%s#%s' is not supported. Please see %s for more information.", //
            TestElement.class.getName(), //
            "unsupportedMethod", //
            Element.class.getName()));

        // when
        create(TESTELEMENT).unsupportedMethod("Foo");
    }

    public interface TestElement extends HasContent, Bindable<TestElement, TestElement.TestElementState>
    {
        Class<TestElement> TESTELEMENT = TestElement.class;

        TestElement title(Supplier<String> title);

        TestElement unsupportedMethod(String unsupported);

        interface TestElementState
        {
            String title();
        }
    }

    private interface State
    {
        String specialTitle();

        State nested();

        TestElementState testElementState();
    }

    private <T extends Element> T create(Class<T> type)
    {
        return components.create(type);
    }
}