package de.davherrmann.efficiently.components;

import java.util.List;

public interface Table extends Element, Bindable<Table, Table.TableProperties>
{
    Class<Table> TABLE = Table.class;

    interface Column
    {
        String name();

        int width();

        String renderer();
    }

    interface TableProperties extends Element.ElementProperties
    {
        List<Object> items();

        List<Column> columns();

        String onRequestNewItems();
    }
}
