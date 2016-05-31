package de.davherrmann.efficiently.view;

import static com.google.common.collect.Lists.newArrayList;
import static de.davherrmann.efficiently.view.ComponentsTest.TestElement.TESTELEMENT;
import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.util.Map;
import java.util.function.Supplier;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;

import de.davherrmann.efficiently.view.ComponentsTest.TestElement.TestElementProperties;
import de.davherrmann.immutable.PathRecorder;

public class ComponentsTest
{
    @Rule
    public ExpectedException thrown = none();

    private final Components components = new Components(State.class);
    private final State path = PathRecorder.pathInstanceFor(State.class);

    @Test
    public void create_addsElementTypeInformation() throws Exception
    {
        // given
        final Element element = create(TESTELEMENT);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .build()));
    }
    @Test
    public void bindingDirectProperty_worksWithNonNestedBinding() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.title(path::specialTitle);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", noDerivation("specialTitle")) //
            .build()));
    }

    @Test
    public void bindingDirectProperty_worksWithNestedBinding() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.title(path.nested()::specialTitle);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", noDerivation("nested.specialTitle")) //
            .build()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void content_addsNestedElement() throws Exception
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
    public void bindAll_createsAllBindings_whenElementIsABindable() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.bindAll(path::testElementState);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", noDerivation("testElementState.title")) //
            .build()));
    }

    @Test
    public void bindAll_createsAllBindings_whenSuperElementIsABindable() throws Exception
    {
        // given
        final ExtendedTestElement element = create(ExtendedTestElement.class);

        // when
        // TODO we should forbid extending elements:
        // reason: ExtendedTestElement.bindAll(...) returns TestElement!
        element.bindAll(path::testElementState);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "ExtendedTestElement") //
            .put("title", noDerivation("testElementState.title")) //
            .build()));
    }

    @Test
    public void bind_createsSpecificBinding() throws Exception
    {
        // given
        final TestElement element = create(TESTELEMENT);

        // when
        element.bind(properties -> properties::title).to(path::specialTitle);

        // then
        assertThat(element.template(), is(ImmutableMap.builder() //
            .put("type", "TestElement") //
            .put("title", noDerivation("specialTitle")) //
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

    private Map<String, String> noDerivation(final String value)
    {
        return ImmutableMap.<String, String>builder() //
            .put("name", "none") //
            .put("sourceValue", value) //
            .build();
    }

    public interface TestElement
        extends HasContent<TestElement>, Bindable<TestElement, TestElement.TestElementProperties>
    {
        Class<TestElement> TESTELEMENT = TestElement.class;

        TestElement title(Supplier<String> title);

        TestElement unsupportedMethod(String unsupported);

        interface TestElementProperties
        {
            String title();
        }
    }

    public interface ExtendedTestElement extends TestElement
    {
    }

    private interface State
    {
        String specialTitle();

        State nested();

        TestElementProperties testElementState();
    }

    private <T extends Element> T create(Class<T> type)
    {
        return components.create(type);
    }
}