package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.PickRay;

public class MappedNGParallelCamera extends MappedNGCamera {

    public MappedNGParallelCamera() { }

    @Override
    public PickRay computePickRay(float x, float y, PickRay pickRay) {
        return PickRay.computeParallelPickRay(x, y, viewHeight, worldTransform,
                zNear, zFar, pickRay);
    }
}
