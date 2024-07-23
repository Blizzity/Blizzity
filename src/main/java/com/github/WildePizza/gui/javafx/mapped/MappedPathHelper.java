package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.sg.prism.MappedNGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.paint.Paint;


public class MappedPathHelper extends MappedShapeHelper {

    private static final MappedPathHelper theInstance;
    private static PathAccessor pathAccessor;

    static {
        theInstance = new MappedPathHelper();
        Utils.forceInit(MappedPath.class);
    }

    private static MappedPathHelper getInstance() {
        return theInstance;
    }

    public static void initHelper(MappedPath path) {
        setHelper(path, getInstance());
    }

    @Override
    protected MappedNGNode createPeerImpl(MappedNode node) {
        return pathAccessor.doCreatePeer(node);
    }

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        pathAccessor.doUpdatePeer(node);
    }

    @Override
    protected Bounds computeLayoutBoundsImpl(MappedNode node) {
        Bounds bounds = pathAccessor.doComputeLayoutBounds(node);
        if (bounds != null) {
            return bounds;
        }
        return super.computeLayoutBoundsImpl(node);
    }

    @Override
    protected Paint cssGetFillInitialValueImpl(MappedShape shape) {
        return pathAccessor.doCssGetFillInitialValue(shape);
    }

    @Override
    protected Paint cssGetStrokeInitialValueImpl(MappedShape shape) {
        return pathAccessor.doCssGetStrokeInitialValue(shape);
    }

    @Override
    protected  Shape configShapeImpl(MappedShape shape) {
        return pathAccessor.doConfigShape(shape);
    }

    public static void setPathAccessor(final PathAccessor newAccessor) {
        if (pathAccessor != null) {
            throw new IllegalStateException();
        }

        pathAccessor = newAccessor;
    }

    public interface PathAccessor {
        MappedNGNode doCreatePeer(MappedNode node);
        void doUpdatePeer(MappedNode node);
        Bounds doComputeLayoutBounds(MappedNode node);
        Paint doCssGetFillInitialValue(MappedShape shape);
        Paint doCssGetStrokeInitialValue(MappedShape shape);
        Shape doConfigShape(MappedShape shape);
    }

}

