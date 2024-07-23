package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.util.WeakReferenceQueue;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;

import java.util.Iterator;

public abstract class MappedPathElement {
    /*
     * Store the singleton instance of the MappedPathElementHelper subclass corresponding
     * to the subclass of this instance of MappedPathElement
     */
    private MappedPathElementHelper pathElementHelper = null;

    static {
        // This is used by classes in different packages to get access to
        // private and package private methods.
        MappedPathElementHelper.setPathElementAccessor(new MappedPathElementHelper.PathElementAccessor() {
            @Override
            public MappedPathElementHelper getHelper(MappedPathElement pathElement) {
                return pathElement.pathElementHelper;
            }

            @Override
            public void setHelper(MappedPathElement pathElement, MappedPathElementHelper pathElementHelper) {
                pathElement.pathElementHelper = pathElementHelper;
            }
        });
    }

    
    WeakReferenceQueue nodes = new WeakReferenceQueue();

    
    public MappedPathElement() {
    }

    void addNode(final MappedNode n) {
        nodes.add(n);
    }

    void removeNode(final MappedNode n) {
        nodes.remove(n);
    }

    void u() {
        final Iterator iterator = nodes.iterator();
        while (iterator.hasNext()) {
            ((MappedPath) iterator.next()).markPathDirty();
        }
    }

    abstract void addTo(NGPath pgPath);

    
    private BooleanProperty absolute;


    public final void setAbsolute(boolean value) {
        absoluteProperty().set(value);
    }

    public final boolean isAbsolute() {
        return absolute == null || absolute.get();
    }

    public final BooleanProperty absoluteProperty() {
        if (absolute == null) {
            absolute = new BooleanPropertyBase(true) {
                @Override protected void invalidated() {
                    u();
                }

                @Override
                public Object getBean() {
                    return MappedPathElement.this;
                }

                @Override
                public String getName() {
                    return "absolute";
                }
            };
        }
        return absolute;
    }
}

