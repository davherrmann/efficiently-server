package de.davherrmann.efficiently.components;

import de.davherrmann.efficiently.components.Element.ElementProperties;

public interface Panel extends HasContent<Panel>, Bindable<Panel, ElementProperties>
{
    Class<Panel> PANEL = Panel.class;
}
