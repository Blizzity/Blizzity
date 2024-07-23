package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.MappedNGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.MappedCamera;


public class MappedSubSceneHelper extends MappedNodeHelper {

    private static final MappedSubSceneHelper theInstance;
    private static SubSceneAccessor subSceneAccessor;

    static {
        theInstance = new MappedSubSceneHelper();
        Utils.forceInit(MappedSubScene.class);
    }

    private static MappedSubSceneHelper getInstance() {
        return theInstance;
    }

    public static void initHelper(MappedSubScene subScene) {
        setHelper(subScene, getInstance());
    }

    public static void superProcessCSS(MappedNode node) {
        ((MappedSubSceneHelper) getHelper(node)).superProcessCSSImpl(node);
    }

    @Override
    protected MappedNGNode createPeerImpl(MappedNode node) {
        return subSceneAccessor.doCreatePeer(node);
    }

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        subSceneAccessor.doUpdatePeer(node);
    }

    @Override
    protected BaseBounds computeGeomBoundsImpl(MappedNode node, BaseBounds bounds,
                                               BaseTransform tx) {
        return subSceneAccessor.doComputeGeomBounds(node, bounds, tx);
    }

    @Override
    protected boolean computeContainsImpl(MappedNode node, double localX, double localY) {
        return subSceneAccessor.doComputeContains(node, localX, localY);
    }

    void superProcessCSSImpl(MappedNode node) {
        super.processCSSImpl(node);
    }

    protected void processCSSImpl(MappedNode node) {
        subSceneAccessor.doProcessCSS(node);
    }

    @Override
    protected void pickNodeLocalImpl(MappedNode node, PickRay localPickRay,
                                     PickResultChooser result) {
        subSceneAccessor.doPickNodeLocal(node, localPickRay, result);
    }

    public static boolean isDepthBuffer(MappedSubScene subScene) {
        return subSceneAccessor.isDepthBuffer(subScene);
    }

    public static MappedCamera getEffectiveCamera(MappedSubScene subScene) {
        return subSceneAccessor.getEffectiveCamera(subScene);
    }

    public static void setSubSceneAccessor(final SubSceneAccessor newAccessor) {
        if (subSceneAccessor != null) {
            throw new IllegalStateException();
        }

        subSceneAccessor = newAccessor;
    }

    public interface SubSceneAccessor {
        MappedNGNode doCreatePeer(MappedNode node);
        void doUpdatePeer(MappedNode node);
        BaseBounds doComputeGeomBounds(MappedNode node, BaseBounds bounds, BaseTransform tx);
        boolean doComputeContains(MappedNode node, double localX, double localY);
        void doProcessCSS(MappedNode node);
        void doPickNodeLocal(MappedNode node, PickRay localPickRay,
                             PickResultChooser result);
        boolean isDepthBuffer(MappedSubScene subScene);
        MappedCamera getEffectiveCamera(MappedSubScene subScene);
    }

}
