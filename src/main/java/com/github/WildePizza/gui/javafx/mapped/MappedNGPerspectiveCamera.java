package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.PickRay;

/**
 * Specifies a Swing-coordinates camera, suitable for mixing with Swing and
 * the 2D scene graph. The coordinate system defined by this camera has its
 * origin in the upper left corner of the panel with the Y-axis pointing
 * down and the Z axis pointing away from the viewer (into the screen). The
 * units are in pixel coordinates at the projection plane (Z=0),
 * regardless of the size of the panel.
 * The viewing transform is defined by specifying the zero point, the viewing
 * direction, and the up vector.
 * This resulting transform is used as the view portion of the ModelView matrix.
 * The projection transform a fixed perspective transform. The 3D viewport is
 * set to the bounds of the panel.
 */
public class MappedNGPerspectiveCamera extends MappedNGCamera {
    private final boolean fixedEyeAtCameraZero;
    private double fovrad;
    private boolean verticalFieldOfView;

    public MappedNGPerspectiveCamera(boolean fixedEyeAtCameraZero) {
        this.fixedEyeAtCameraZero = fixedEyeAtCameraZero;
    }

    public void setFieldOfView(float fieldOfViewDegrees) {
        this.fovrad = Math.toRadians(fieldOfViewDegrees);
    }

    public void setVerticalFieldOfView(boolean verticalFieldOfView) {
        this.verticalFieldOfView = verticalFieldOfView;
    }

    @Override
    public PickRay computePickRay(float x, float y, PickRay pickRay) {
        return PickRay.computePerspectivePickRay(x, y, fixedEyeAtCameraZero,
                viewWidth, viewHeight, fovrad, verticalFieldOfView, worldTransform,
                zNear, zFar,
                pickRay);
    }
}
