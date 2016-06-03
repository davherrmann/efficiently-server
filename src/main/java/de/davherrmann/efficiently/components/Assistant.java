package de.davherrmann.efficiently.components;

import java.util.List;

// TODO specify client side tag name via annotation?
// @Name("SpecialAssistant")
// @ClientSideFQName("de.davherrmann.special.Assistant")
public interface Assistant extends HasContent<Assistant>, Bindable<Assistant, Assistant.AssistantProperties>
{
    Class<Assistant> ASSISTANT = Assistant.class;

    // TODO do we even need those explicit bindings?
    // TODO specifiy client side attribute name via annotation?
    // @Name("specialActions")
    // Assistant actions(Supplier<String[]> actions);
    // Assistant title(Supplier<String> title);
    // Assistant currentPage(Supplier<Integer> currentPage);

    interface AssistantProperties extends Element.ElementProperties
    {
        String title();

        List<String> actions();

        Integer currentPage();
    }
}
