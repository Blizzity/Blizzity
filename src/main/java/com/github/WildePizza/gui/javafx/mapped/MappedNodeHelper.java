package com.github.WildePizza.gui.javafx.mapped;

import com.sun.glass.ui.Accessible;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.util.Utils;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.Style;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.scene.text.Font;


public abstract class MappedNodeHelper {
    private static NodeAccessor nodeAccessor;

    static {
        Utils.forceInit(MappedNode.class);
    }

    protected MappedNodeHelper() {
    }

    protected static MappedNodeHelper getHelper(MappedNode node) {

        MappedNodeHelper helper = nodeAccessor.getHelper(node);
        if (helper == null) {
            String nodeType;
            if (node instanceof MappedShape) {
                nodeType = "Shape";
            } else if (node instanceof MappedShape3D) {
                nodeType = "Shape3D";
            } else {
                nodeType = "MappedNode";
            }

            throw new UnsupportedOperationException(
                    "Applications should not extend the "
                            + nodeType + " class directly.");
        }
        return helper;
    }

    protected static void setHelper(MappedNode node, MappedNodeHelper nodeHelper) {
        nodeAccessor.setHelper(node, nodeHelper);
    }

    /*
     * Static helper methods for cases where the implementation is done in an
     * instance method that is overridden by subclasses.
     * These methods exist in the base class only.
     */

    public static MappedNGNode createPeer(MappedNode node) {
        return getHelper(node).createPeerImpl(node);
    }

    public static void markDirty(MappedNode node, DirtyBits dirtyBit) {
        getHelper(node).markDirtyImpl(node, dirtyBit);
    }

    public static void updatePeer(MappedNode node) {
        getHelper(node).updatePeerImpl(node);
    }

    public static Bounds computeLayoutBounds(MappedNode node) {
        return getHelper(node).computeLayoutBoundsImpl(node);
    }

    /*
     * Computes the geometric bounds for this MappedNode. This method is abstract
     * and must be implemented by each MappedNode subclass.
     */
    public static BaseBounds computeGeomBounds(MappedNode node,
                                               BaseBounds bounds, BaseTransform tx) {
        return getHelper(node).computeGeomBoundsImpl(node, bounds, tx);
    }

    public static void transformsChanged(MappedNode node) {
        getHelper(node).transformsChangedImpl(node);
    }

    public static boolean computeContains(MappedNode node, double localX, double localY) {
        return getHelper(node).computeContainsImpl(node, localX, localY);
    }

    public static void pickNodeLocal(MappedNode node, PickRay localPickRay,
                                     PickResultChooser result) {
        getHelper(node).pickNodeLocalImpl(node, localPickRay, result);
    }

    public static boolean computeIntersects(MappedNode node, PickRay pickRay,
                                            PickResultChooser pickResult) {
        return getHelper(node).computeIntersectsImpl(node, pickRay, pickResult);
    }

    public static void geomChanged(MappedNode node) {
        getHelper(node).geomChangedImpl(node);
    }

    public static void notifyLayoutBoundsChanged(MappedNode node) {
        getHelper(node).notifyLayoutBoundsChangedImpl(node);
    }

    public static void processCSS(MappedNode node) {
        getHelper(node).processCSSImpl(node);
    }

    /*
     * Methods that will be overridden by subclasses
     */

    protected abstract MappedNGNode createPeerImpl(MappedNode node);
    protected abstract boolean computeContainsImpl(MappedNode node, double localX, double localY);
    protected abstract BaseBounds computeGeomBoundsImpl(MappedNode node,
                                                        BaseBounds bounds, BaseTransform tx);

    protected void markDirtyImpl(MappedNode node, DirtyBits dirtyBit) {
        nodeAccessor.doMarkDirty(node, dirtyBit);
    }

    protected void updatePeerImpl(MappedNode node) {
        nodeAccessor.doUpdatePeer(node);
    }

    protected Bounds computeLayoutBoundsImpl(MappedNode node) {
        return nodeAccessor.doComputeLayoutBounds(node);
    }

    protected void transformsChangedImpl(MappedNode node) {
        nodeAccessor.doTransformsChanged(node);
    }

    protected void pickNodeLocalImpl(MappedNode node, PickRay localPickRay,
                                     PickResultChooser result) {
        nodeAccessor.doPickNodeLocal(node, localPickRay, result);
    }

    protected boolean computeIntersectsImpl(MappedNode node, PickRay pickRay,
                                            PickResultChooser pickResult) {
        return nodeAccessor.doComputeIntersects(node, pickRay, pickResult);
    }

    protected void geomChangedImpl(MappedNode node) {
        nodeAccessor.doGeomChanged(node);
    }

    protected void notifyLayoutBoundsChangedImpl(MappedNode node) {
        nodeAccessor.doNotifyLayoutBoundsChanged(node);
    }

    protected void processCSSImpl(MappedNode node) {
        nodeAccessor.doProcessCSS(node);
    }

    /*
     * Methods used by MappedNode (base) class only
     */

    public static boolean isDirty(MappedNode node, DirtyBits dirtyBit) {
        return nodeAccessor.isDirty(node, dirtyBit);
    }

    public static boolean isDirtyEmpty(MappedNode node) {
        return nodeAccessor.isDirtyEmpty(node);
    }

    public static void syncPeer(MappedNode node) {
        nodeAccessor.syncPeer(node);
    }

    public static <P extends MappedNGNode> P getPeer(MappedNode node) {
        return nodeAccessor.getPeer(node);
    }

    public static BaseTransform getLeafTransform(MappedNode node) {
        return nodeAccessor.getLeafTransform(node);
    }

    public static void layoutBoundsChanged(MappedNode node) {
        nodeAccessor.layoutBoundsChanged(node);
    }

    public static void setShowMnemonics(MappedNode node, boolean value) {
        nodeAccessor.setShowMnemonics(node, value);
    }

    public static boolean isShowMnemonics(MappedNode node) {
        return nodeAccessor.isShowMnemonics(node);
    }

    public static BooleanProperty showMnemonicsProperty(MappedNode node) {
        return nodeAccessor.showMnemonicsProperty(node);
    }

    public static boolean traverse(MappedNode node, Direction direction) {
        return nodeAccessor.traverse(node, direction);
    }

    public static double getPivotX(MappedNode node) {
        return nodeAccessor.getPivotX(node);
    }

    public static double getPivotY(MappedNode node) {
        return nodeAccessor.getPivotY(node);
    }

    public static double getPivotZ(MappedNode node) {
        return nodeAccessor.getPivotZ(node);
    }

    public static void pickNode(MappedNode node, PickRay pickRay,
                                PickResultChooser result) {
        nodeAccessor.pickNode(node, pickRay, result);
    }

    public static boolean intersects(MappedNode node, PickRay pickRay,
                                     PickResultChooser pickResult) {
        return nodeAccessor.intersects(node, pickRay, pickResult);
    }

    public static double intersectsBounds(MappedNode node, PickRay pickRay) {
        return nodeAccessor.intersectsBounds(node, pickRay);
    }

    public static void layoutNodeForPrinting(MappedNode node) {
        nodeAccessor.layoutNodeForPrinting(node);
    }

    public static boolean isDerivedDepthTest(MappedNode node) {
        return nodeAccessor.isDerivedDepthTest(node);
    }

    public static MappedSubScene getSubScene(MappedNode node) {
        return nodeAccessor.getSubScene(node);
    }

    public static Accessible getAccessible(MappedNode node) {
        return nodeAccessor.getAccessible(node);
    }

    public static void reapplyCSS(MappedNode node) {
        nodeAccessor.reapplyCSS(node);
    }

    public static void recalculateRelativeSizeProperties(MappedNode node, Font fontForRelativeSizes) {
        nodeAccessor.recalculateRelativeSizeProperties(node, fontForRelativeSizes);
    }

    public static boolean isTreeVisible(MappedNode node) {
        return nodeAccessor.isTreeVisible(node);
    }

    public static BooleanExpression treeVisibleProperty(MappedNode node) {
        return nodeAccessor.treeVisibleProperty(node);
    }

    public static boolean isTreeShowing(MappedNode node) {
        return nodeAccessor.isTreeShowing(node);
    }

    public static List<Style> getMatchingStyles(CssMetaData cssMetaData, MappedStyleable styleable) {
        return nodeAccessor.getMatchingStyles(cssMetaData, styleable);
    }

    public static Map<StyleableProperty<?>,List<Style>> findStyles(MappedNode node, Map<StyleableProperty<?>,List<Style>> styleMap) {
        return nodeAccessor.findStyles(node, styleMap);
    }

    public static void setNodeAccessor(final NodeAccessor newAccessor) {
        if (nodeAccessor != null) {
            throw new IllegalStateException();
        }

        nodeAccessor = newAccessor;
    }

    public static NodeAccessor getNodeAccessor() {
        if (nodeAccessor == null) {
            throw new IllegalStateException();
        }

        return nodeAccessor;
    }

    public interface NodeAccessor {
        MappedNodeHelper getHelper(MappedNode node);
        void setHelper(MappedNode node, MappedNodeHelper nodeHelper);
        void doMarkDirty(MappedNode node, DirtyBits dirtyBit);
        void doUpdatePeer(MappedNode node);
        BaseTransform getLeafTransform(MappedNode node);
        Bounds doComputeLayoutBounds(MappedNode node);
        void doTransformsChanged(MappedNode node);
        void doPickNodeLocal(MappedNode node, PickRay localPickRay,
                             PickResultChooser result);
        boolean doComputeIntersects(MappedNode node, PickRay pickRay,
                                    PickResultChooser pickResult);
        void doGeomChanged(MappedNode node);
        void doNotifyLayoutBoundsChanged(MappedNode node);
        void doProcessCSS(MappedNode node);
        boolean isDirty(MappedNode node, DirtyBits dirtyBit);
        boolean isDirtyEmpty(MappedNode node);
        void syncPeer(MappedNode node);
        <P extends MappedNGNode> P getPeer(MappedNode node);
        void layoutBoundsChanged(MappedNode node);
        void setShowMnemonics(MappedNode node, boolean value);
        boolean isShowMnemonics(MappedNode node);
        BooleanProperty showMnemonicsProperty(MappedNode node);
        boolean traverse(MappedNode node, Direction direction);
        double getPivotX(MappedNode node);
        double getPivotY(MappedNode node);
        double getPivotZ(MappedNode node);
        void pickNode(MappedNode node, PickRay pickRay, PickResultChooser result);
        boolean intersects(MappedNode node, PickRay pickRay, PickResultChooser pickResult);
        double intersectsBounds(MappedNode node, PickRay pickRay);
        void layoutNodeForPrinting(MappedNode node);
        boolean isDerivedDepthTest(MappedNode node);
        MappedSubScene getSubScene(MappedNode node);
        void setLabeledBy(MappedNode node, MappedNode labeledBy);
        Accessible getAccessible(MappedNode node);
        void reapplyCSS(MappedNode node);
        void recalculateRelativeSizeProperties(MappedNode node, Font fontForRelativeSizes);
        boolean isTreeVisible(MappedNode node);
        BooleanExpression treeVisibleProperty(MappedNode node);
        boolean isTreeShowing(MappedNode node);
        List<Style> getMatchingStyles(CssMetaData cssMetaData, MappedStyleable styleable);
        Map<StyleableProperty<?>,List<Style>> findStyles(MappedNode node,
                                                         Map<StyleableProperty<?>,List<Style>> styleMap);
    }

}
