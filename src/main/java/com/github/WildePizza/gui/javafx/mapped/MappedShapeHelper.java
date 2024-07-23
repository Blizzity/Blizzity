package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.prism.NGShape;
import com.sun.javafx.util.Utils;
import javafx.scene.paint.Paint;

public abstract class MappedShapeHelper extends MappedNodeHelper {
    private static ShapeAccessor shapeAccessor;

    static {
        Utils.forceInit(MappedShape.class);
    }

    /*
     * Static helper methods for cases where the implementation is done in an
     * instance method that is overridden by subclasses.
     * These methods exist in the base class only.
     */

    public static Paint cssGetFillInitialValue(MappedShape shape) {
        return ((MappedShapeHelper) getHelper(shape)).cssGetFillInitialValueImpl(shape);
    }

    public static Paint cssGetStrokeInitialValue(MappedShape shape) {
        return ((MappedShapeHelper) getHelper(shape)).cssGetStrokeInitialValueImpl(shape);
    }

    public static Shape configShape(MappedShape shape) {
        return ((MappedShapeHelper) getHelper(shape)).configShapeImpl(shape);
    }

    /*
     * Methods that will be overridden by subclasses
     */

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        shapeAccessor.doUpdatePeer(node);
    }

    @Override
    protected void markDirtyImpl(MappedNode node, DirtyBits dirtyBit) {
        shapeAccessor.doMarkDirty(node, dirtyBit);
        super.markDirtyImpl(node, dirtyBit);
    }

    @Override
    protected BaseBounds computeGeomBoundsImpl(MappedNode node, BaseBounds bounds,
                                               BaseTransform tx) {
        return shapeAccessor.doComputeGeomBounds(node, bounds, tx);
    }

    @Override
    protected boolean computeContainsImpl(MappedNode node, double localX, double localY) {
        return shapeAccessor.doComputeContains(node, localX, localY);
    }

    protected Paint cssGetFillInitialValueImpl(MappedShape shape) {
        return shapeAccessor.doCssGetFillInitialValue(shape);
    }

    protected Paint cssGetStrokeInitialValueImpl(MappedShape shape) {
        return shapeAccessor.doCssGetStrokeInitialValue(shape);
    }

    protected abstract Shape configShapeImpl(MappedShape shape);

    /*
     * Methods used by MappedShape (base) class only
     */

    public static NGShape.Mode getMode(MappedShape shape) {
        return shapeAccessor.getMode(shape);
    }

    public static void setMode(MappedShape shape, NGShape.Mode mode) {
        shapeAccessor.setMode(shape, mode);
    }

    public static void setShapeChangeListener(MappedShape shape, Runnable listener) {
        shapeAccessor.setShapeChangeListener(shape, listener);
    }

    public static void setShapeAccessor(final ShapeAccessor newAccessor) {
        if (shapeAccessor != null) {
            throw new IllegalStateException();
        }

        shapeAccessor = newAccessor;
    }

    public interface ShapeAccessor {
        void doUpdatePeer(MappedNode node);
        void doMarkDirty(MappedNode node, DirtyBits dirtyBit);
        BaseBounds doComputeGeomBounds(MappedNode node, BaseBounds bounds, BaseTransform tx);
        boolean doComputeContains(MappedNode node, double localX, double localY);
        Paint doCssGetFillInitialValue(MappedShape shape);
        Paint doCssGetStrokeInitialValue(MappedShape shape);
        NGShape.Mode getMode(MappedShape shape);
        void setMode(MappedShape shape, NGShape.Mode mode);
        void setShapeChangeListener(MappedShape shape, Runnable listener);
    }

}
