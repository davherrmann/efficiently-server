package de.davherrmann.efficiently.view;

import de.davherrmann.efficiently.view.Element.ElementProperties;

public interface Panel extends HasContent<Panel>, Bindable<Panel, ElementProperties>
{
    Class<Panel> PANEL = Panel.class;
}
