package com.github.WildePizza.gui.javafx.mapped;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.MappedCamera;
import javafx.scene.ParallelCamera;
import javafx.scene.MappedPerspectiveCamera;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import com.sun.javafx.logging.PlatformLogger;

/**
 * Parameters used to specify the rendering attributes for MappedNode snapshot.
 * @since JavaFX 2.2
 */
public class MappedSnapshotParameters {

    private boolean depthBuffer;
    private MappedCamera camera;
    private Transform transform;
    private Paint fill;
    private Rectangle2D viewport;

    /**
     * Constructs a new MappedSnapshotParameters object with default values for
     * all rendering attributes.
     */
    public MappedSnapshotParameters() {
    }

    /**
     * Gets the current depthBuffer flag.
     *
     * @return the depthBuffer flag
     */
    public boolean isDepthBuffer() {
        return depthBuffer;
    }

    boolean isDepthBufferInternal() {
        if(!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            return false;
        }
        return depthBuffer;
    }

    /**
     * Sets the depthBuffer flag to the specified value.
     * The default value is false.
     *
     * Note that this is a conditional feature. See
     * {@link javafx.application.ConditionalFeature#SCENE3D ConditionalFeature.SCENE3D}
     *
     * @param depthBuffer the depthBuffer to set
     */
    public void setDepthBuffer(boolean depthBuffer) {
        if (depthBuffer && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
            String logname = MappedSnapshotParameters.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "ConditionalFeature.SCENE3D");
        }
        this.depthBuffer = depthBuffer;
    }

    /**
     * Gets the current camera.
     *
     * @return the camera
     */
    public MappedCamera getCamera() {
        return camera;
    }

    MappedCamera defaultCamera;

    MappedCamera getEffectiveCamera() {
        if (camera instanceof MappedPerspectiveCamera
                && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
            if (defaultCamera == null) {
                // According to MappedScene.doSnapshot, temporarily, it adjusts camera
                // viewport to the snapshot size. So, its viewport doesn't matter.
                defaultCamera = new ParallelCamera();
            }
            return defaultCamera;
        }
        return camera;
    }

    /**
     * Sets the camera to the specified value.
     * The default value is null, which means a ParallelCamera will be used.
     *
     * @param camera the camera to set
     */
    public void setCamera(MappedCamera camera) {
        if (camera instanceof MappedPerspectiveCamera
                && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
            String logname = MappedSnapshotParameters.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "ConditionalFeature.SCENE3D");
        }
        this.camera = camera;
    }

    /**
     * Gets the current transform.
     *
     * @return the transform
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Sets the transform to the specified value. This transform is applied to
     * the node being rendered before any local transforms are applied.
     * A value of null indicates that the identity transform should be used.
     * The default value is null.
     *
     * @param transform the transform to set
     */
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    /**
     * Gets the current fill.
     *
     * @return the fill
     */
    public Paint getFill() {
        return fill;
    }

    /**
     * Sets the fill to the specified value. This is used to fill the entire
     * image being rendered prior to rendering the node. A value of null
     * indicates that the color white should be used for the fill.
     * The default value is null.
     *
     * @param fill the fill to set
     */
    public void setFill(Paint fill) {
        this.fill = fill;
    }

    /**
     * Gets the current viewport
     *
     * @return the viewport
     */
    public Rectangle2D getViewport() {
        return viewport;
    }

    /**
     * Sets the viewport used for rendering.
     * The viewport is specified in the parent coordinate system of the
     * node being rendered. It is not transformed by the transform
     * of this MappedSnapshotParameters.
     * If this viewport is non-null it is used instead of the bounds of the
     * node being rendered and specifies the source rectangle that will be
     * rendered into the image.
     * In this case, the upper-left pixel of the viewport will map to
     * the upper-left pixel (0,0)
     * in the rendered image.
     * If the viewport is null, then the entire area of the node defined
     * by its boundsInParent, after first applying the
     * transform of this MappedSnapshotParameters, will be rendered.
     * The default value is null.
     *
     * @param viewport the viewport to set
     */
    public void setViewport(Rectangle2D viewport) {
        this.viewport = viewport;
    }

    /**
     * Returns a deep clone of this MappedSnapshotParameters
     *
     * @return a clone
     */
    MappedSnapshotParameters copy() {
        MappedSnapshotParameters params = new MappedSnapshotParameters();
        params.camera = camera == null ? null : camera.copy();
        params.depthBuffer = depthBuffer;
        params.fill = fill;
        params.viewport = viewport;
        params.transform = transform == null ? null : transform.clone();
        return params;
    }
}
