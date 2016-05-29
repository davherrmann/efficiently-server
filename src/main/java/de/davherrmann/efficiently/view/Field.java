package de.davherrmann.efficiently.view;

public interface Field extends Element, Bindable<Field, Field.FieldProperties>
{
    Class<Field> FIELD = Field.class;

    interface FieldProperties extends Element.ElementProperties
    {
        String value();

        // type is ambivalent at the moment! create map with: type, properties (group them in here), content
        // String type();

        String cols();

        String label();

        String placeHolder();

        String model();

        String form();

        boolean disabled();

        boolean readonly();
    }
}
