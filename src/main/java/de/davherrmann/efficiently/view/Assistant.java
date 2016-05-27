package de.davherrmann.efficiently.view;

import java.util.function.Supplier;

// TODO specify client side tag name via annotation?
// @Name("SpecialAssistant")
// @ClientSideFQName("de.davherrmann.special.Assistant")
public interface Assistant extends HasContent<Assistant>
{
    Class<Assistant> ASSISTANT = Assistant.class;

    // TODO specifiy client side attribute name via annotation?
    // @Name("specialActions")
    Assistant actions(Supplier<String[]> actions);

    Assistant title(Supplier<String> title);

    Assistant currentPage(Supplier<Integer> currentPage);
}
