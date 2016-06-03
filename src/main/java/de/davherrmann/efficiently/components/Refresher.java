package de.davherrmann.efficiently.components;

// TODO when should I use Bindable, when should I add the props directly
public interface Refresher extends Element, Bindable<Refresher, Refresher.RefresherProperties>
{
    Class<Refresher> REFRESHER = Refresher.class;

    interface RefresherProperties
    {
        int delay();

        boolean refresh();
    }
}
