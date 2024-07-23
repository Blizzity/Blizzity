package com.github.WildePizza.gui.javafx.mapped;

import java.util.Collection;

import com.sun.javafx.scene.shape.MappedPathElementHelper;
import javafx.scene.shape.MappedPathElement;

import com.sun.javafx.geom.MappedPath2D;

public final class MappedPathUtils {
    private MappedPathUtils() {
    }

    public static MappedPath2D configShape(
            final Collection<MappedPathElement> pathElements,
            final boolean evenOddFillRule) {

        MappedPath2D path = new MappedPath2D(
                evenOddFillRule ? MappedPath2D.WIND_EVEN_ODD : MappedPath2D.WIND_NON_ZERO,
                pathElements.size());
        for (final MappedPathElement el: pathElements) {
            MappedPathElementHelper.addTo(el, path);
        }
        return path;
    }
}
