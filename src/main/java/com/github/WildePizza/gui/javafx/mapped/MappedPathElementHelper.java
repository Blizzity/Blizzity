package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.util.Utils;
import javafx.scene.shape.MappedPathElement;

public abstract class MappedPathElementHelper {
    private static PathElementAccessor pathElementAccessor;

    static {
        Utils.forceInit(MappedPathElement.class);
    }

    protected MappedPathElementHelper() {
    }

    private static MappedPathElementHelper getHelper(MappedPathElement pathElement) {
        return pathElementAccessor.getHelper(pathElement);
    }

    protected static void setHelper(MappedPathElement pathElement, MappedPathElementHelper pathElementHelper) {
        pathElementAccessor.setHelper(pathElement, pathElementHelper);
    }

    /*
     * Static helper methods for cases where the implementation is done in an
     * instance method that is overridden by subclasses.
     * These methods exist in the base class only.
     */

    public static void addTo(MappedPathElement pathElement, MappedPath2D path) {
        getHelper(pathElement).addToImpl(pathElement, path);
    }

    /*
     * Methods that will be overridden by subclasses
     */

    protected abstract void addToImpl(MappedPathElement pathElement, MappedPath2D path);

    /*
     * Methods used by MappedPathElement (base) class only
     */

    public static void setPathElementAccessor(final PathElementAccessor newAccessor) {
        if (pathElementAccessor != null) {
            throw new IllegalStateException();
        }

        pathElementAccessor = newAccessor;
    }

    public interface PathElementAccessor {
        MappedPathElementHelper getHelper(MappedPathElement pathElement);
        void setHelper(MappedPathElement pathElement, MappedPathElementHelper pathElementHelper);
    }

}

