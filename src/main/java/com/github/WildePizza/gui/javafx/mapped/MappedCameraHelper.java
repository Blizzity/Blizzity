package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.util.Utils;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class MappedCameraHelper extends MappedNodeHelper {

    private static final MappedCameraHelper theInstance;
    private static CameraAccessor cameraAccessor;

    static {
        theInstance = new MappedCameraHelper();
        Utils.forceInit(MappedCamera.class);
    }

    private static MappedCameraHelper getInstance() {
        return theInstance;
    }

    public static void initHelper(MappedCamera camera) {
        setHelper(camera, getInstance());
    }

    @Override
    protected MappedNGNode createPeerImpl(MappedNode node) {
        throw new UnsupportedOperationException("Applications should not extend the MappedCamera class directly.");
    }

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        cameraAccessor.doUpdatePeer(node);
    }

    @Override
    protected void markDirtyImpl(MappedNode node, DirtyBits dirtyBit) {
        super.markDirtyImpl(node, dirtyBit);
        cameraAccessor.doMarkDirty(node, dirtyBit);
    }

    @Override
    protected BaseBounds computeGeomBoundsImpl(MappedNode node, BaseBounds bounds,
                                               BaseTransform tx) {
        return cameraAccessor.doComputeGeomBounds(node, bounds, tx);
    }

    @Override
    protected boolean computeContainsImpl(MappedNode node, double localX, double localY) {
        return cameraAccessor.doComputeContains(node, localX, localY);
    }

    public static Point2D project(MappedCamera camera, Point3D p) {
        return cameraAccessor.project(camera, p);
    }

    public static Point2D pickNodeXYPlane(MappedCamera camera, MappedNode node, double x, double y) {
        return cameraAccessor.pickNodeXYPlane(camera, node, x, y);
    }

    public static Point3D pickProjectPlane(MappedCamera camera, double x, double y) {
        return cameraAccessor.pickProjectPlane(camera, x, y);
    }

    public static void setCameraAccessor(final CameraAccessor newAccessor) {
        if (cameraAccessor != null) {
            throw new IllegalStateException();
        }

        cameraAccessor = newAccessor;
    }

    public interface CameraAccessor {
        void doMarkDirty(MappedNode node, DirtyBits dirtyBit);
        void doUpdatePeer(MappedNode node);
        BaseBounds doComputeGeomBounds(MappedNode node, BaseBounds bounds, BaseTransform tx);
        boolean doComputeContains(MappedNode node, double localX, double localY);
        Point2D project(MappedCamera camera, Point3D p);
        Point2D pickNodeXYPlane(MappedCamera camera, MappedNode node, double x, double y);
        Point3D pickProjectPlane(MappedCamera camera, double x, double y);
    }

}
