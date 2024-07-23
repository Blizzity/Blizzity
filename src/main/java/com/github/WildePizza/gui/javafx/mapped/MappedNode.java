package com.github.WildePizza.gui.javafx.mapped;


import com.sun.javafx.geometry.BoundsUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ZoomEvent;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Callback;
import java.security.AccessControlContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.Application;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.TempState;
import com.sun.javafx.util.Utils;
import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.UnmodifiableListSet;
import com.sun.javafx.css.PseudoClassState;
import javafx.css.Style;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.CursorConverter;
import javafx.css.converter.EffectConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.EventHandlerProperties;
import com.sun.javafx.scene.LayoutFlags;
import com.sun.javafx.scene.NodeEventDispatcher;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.SceneUtils;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.scene.transform.TransformUtils;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.impl.PrismSettings;
import com.sun.scenario.effect.EffectHelper;

import javafx.scene.shape.Shape3D;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;

@IDProperty("id")
public abstract class MappedNode implements EventTarget, MappedStyleable {

    /*
     * Store the singleton instance of the MappedNodeHelper subclass corresponding
     * to the subclass of this instance of MappedNode
     */
    private MappedNodeHelper nodeHelper = null;

    static {
        MappedPerformanceTracker.logEvent("MappedNode class loaded");

        // This is used by classes in different packages to get access to
        // private and package private methods.
        MappedNodeHelper.setNodeAccessor(new MappedNodeHelper.NodeAccessor() {
            @Override
            public MappedNodeHelper getHelper(MappedNode node) {
                return node.nodeHelper;
            }

            @Override
            public void setHelper(MappedNode node, MappedNodeHelper nodeHelper) {
                node.nodeHelper = nodeHelper;
            }

            @Override
            public void doMarkDirty(MappedNode node, DirtyBits dirtyBit) {
                node.doMarkDirty(dirtyBit);
            }

            @Override
            public void doUpdatePeer(MappedNode node) {
                node.doUpdatePeer();
            }

            @Override
            public BaseTransform getLeafTransform(MappedNode node) {
                return node.getLeafTransform();
            }

            @Override
            public Bounds doComputeLayoutBounds(MappedNode node) {
                return node.doComputeLayoutBounds();
            }

            @Override
            public void doTransformsChanged(MappedNode node) {
                node.doTransformsChanged();
            }

            @Override
            public void doPickNodeLocal(MappedNode node, PickRay localPickRay,
                                        PickResultChooser result) {
                node.doPickNodeLocal(localPickRay, result);
            }

            @Override
            public boolean doComputeIntersects(MappedNode node, PickRay pickRay,
                                               PickResultChooser pickResult) {
                return node.doComputeIntersects(pickRay, pickResult);
            }

            @Override
            public void doGeomChanged(MappedNode node) {
                node.doGeomChanged();
            }

            @Override
            public void doNotifyLayoutBoundsChanged(MappedNode node) {
                node.doNotifyLayoutBoundsChanged();
            }

            @Override
            public void doProcessCSS(MappedNode node) {
                node.doProcessCSS();
            }

            @Override
            public boolean isDirty(MappedNode node, DirtyBits dirtyBit) {
                return node.isDirty(dirtyBit);
            }

            @Override
            public boolean isDirtyEmpty(MappedNode node) {
                return node.isDirtyEmpty();
            }

            @Override
            public void syncPeer(MappedNode node) {
                node.syncPeer();
            }

            @Override
            public void layoutBoundsChanged(MappedNode node) {
                node.layoutBoundsChanged();
            }

            @Override
            public <P extends MappedNGNode> P getPeer(MappedNode node) {
                return node.getPeer();
            }

            @Override
            public void setShowMnemonics(MappedNode node, boolean value) {
                node.setShowMnemonics(value);
            }

            @Override
            public boolean isShowMnemonics(MappedNode node) {
                return node.isShowMnemonics();
            }

            @Override
            public BooleanProperty showMnemonicsProperty(MappedNode node) {
                return node.showMnemonicsProperty();
            }

            @Override
            public boolean traverse(MappedNode node, Direction direction) {
                return node.traverse(direction);
            }

            @Override
            public double getPivotX(MappedNode node) {
                return node.getPivotX();
            }

            @Override
            public double getPivotY(MappedNode node) {
                return node.getPivotY();
            }

            @Override
            public double getPivotZ(MappedNode node) {
                return node.getPivotZ();
            }

            @Override
            public void pickNode(MappedNode node,PickRay pickRay,
                                 PickResultChooser result) {
                node.pickNode(pickRay, result);
            }

            @Override
            public boolean intersects(MappedNode node, PickRay pickRay,
                                      PickResultChooser pickResult) {
                return node.intersects(pickRay, pickResult);
            }

            @Override
            public double intersectsBounds(MappedNode node, PickRay pickRay) {
                return node.intersectsBounds(pickRay);
            }

            @Override
            public void layoutNodeForPrinting(MappedNode node) {
                node.doCSSLayoutSyncForSnapshot();
            }

            @Override
            public boolean isDerivedDepthTest(MappedNode node) {
                return node.isDerivedDepthTest();
            }

            @Override
            public MappedSubScene getSubScene(MappedNode node) {
                return node.getSubScene();
            }

            @Override
            public void setLabeledBy(MappedNode node, MappedNode labeledBy) {
                node.labeledBy = labeledBy;
            }

            @Override
            public Accessible getAccessible(MappedNode node) {
                return node.getAccessible();
            }

            @Override
            public void reapplyCSS(MappedNode node) {
                node.reapplyCSS();
            }

            @Override
            public void recalculateRelativeSizeProperties(MappedNode node, Font fontForRelativeSizes) {
                node.recalculateRelativeSizeProperties(fontForRelativeSizes);
            }

            @Override
            public boolean isTreeVisible(MappedNode node) {
                return node.isTreeVisible();
            }

            @Override
            public BooleanExpression treeVisibleProperty(MappedNode node) {
                return node.treeVisibleProperty();
            }

            @Override
            public boolean isTreeShowing(MappedNode node) {
                return node.isTreeShowing();
            }

            @Override
            public List<Style> getMatchingStyles(CssMetaData cssMetaData,
                                                 MappedStyleable styleable) {
                return MappedNode.getMatchingStyles(cssMetaData, styleable);
            }

            @Override
            public Map<StyleableProperty<?>, List<Style>> findStyles(MappedNode node,
                                                                     Map<StyleableProperty<?>, List<Style>> styleMap) {
                return node.findStyles(styleMap);
            }
        });
    }

    /* ************************************************************************
     *                                                                        *
     * Methods and state for managing the dirty bits of a MappedNode. The dirty     *
     * bits are flags used to keep track of what things are dirty on the      *
     * node and therefore need processing on the next pulse. Since the pulse  *
     * happens asynchronously to the change that made the node dirty (for     *
     * performance reasons), we need to keep track of what things have        *
     * changed.                                                               *
     *                                                                        *
     *************************************************************************/

    
    private Set<DirtyBits> dirtyBits = EnumSet.allOf(DirtyBits.class);

    
    private void doMarkDirty(DirtyBits dirtyBit) {
        if (isDirtyEmpty()) {
            addToSceneDirtyList();
        }

        dirtyBits.add(dirtyBit);
    }

    private void addToSceneDirtyList() {
        MappedScene s = getScene();
        if (s != null) {
            s.addToDirtyList(this);
            if (getSubScene() != null) {
                getSubScene().setDirty(this);
            }
        }
    }

    
    final boolean isDirty(DirtyBits dirtyBit) {
        return dirtyBits.contains(dirtyBit);
    }

    
    final void clearDirty(DirtyBits dirtyBit) {
        dirtyBits.remove(dirtyBit);
    }

    
    private void clearDirty() {
        dirtyBits.clear();
    }

    
    private boolean isDirtyEmpty() {
        return dirtyBits.isEmpty();
    }

    /* ************************************************************************
     *                                                                        *
     * Methods for synchronizing state from this MappedNode to its PG peer. This    *
     * should only *ever* be called during synchronization initialized as a   *
     * result of a pulse. Any attempt to synchronize at any other time may    *
     * cause rendering artifacts.                                             *
     *                                                                        *
     *************************************************************************/

    
    final void syncPeer() {
        // Do not synchronize invisible nodes unless their visibility has changed
        // or they have requested a forced synchronization
        if (!isDirtyEmpty() && (treeVisible
                || isDirty(DirtyBits.NODE_VISIBLE)
                || isDirty(DirtyBits.NODE_FORCE_SYNC)))
        {
            MappedNodeHelper.updatePeer(this);
            clearDirty();
        }
    }

    
    private BaseBounds _geomBounds = new RectBounds(0, 0, -1, -1);
    private BaseBounds _txBounds = new RectBounds(0, 0, -1, -1);

    private boolean pendingUpdateBounds = false;

    // Happens before we hold the sync lock
    void updateBounds() {
        // Note: the clip must be handled before the visibility is checked. This is because the visiblity might be
        // changing in the clip and it is going to be synchronized, so it needs to recompute the bounds.
        MappedNode n = getClip();
        if (n != null) {
            n.updateBounds();
        }

        // See syncPeer()
        if (!treeVisible && !isDirty(DirtyBits.NODE_VISIBLE)) {

            // Need to save the dirty bits since they will be cleared even for the
            // case of short circuiting dirty bit processing.
            if (isDirty(DirtyBits.NODE_TRANSFORM)
                    || isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS)
                    || isDirty(DirtyBits.NODE_BOUNDS)) {
                pendingUpdateBounds = true;
            }

            return;
        }

        // Set transform and bounds dirty bits when this node becomes visible
        if (pendingUpdateBounds) {
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORM);
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORMED_BOUNDS);
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_BOUNDS);

            pendingUpdateBounds = false;
        }

        if (isDirty(DirtyBits.NODE_TRANSFORM) || isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS)) {
            if (isDirty(DirtyBits.NODE_TRANSFORM)) {
                updateLocalToParentTransform();
            }
            _txBounds = getTransformedBounds(_txBounds,
                    BaseTransform.IDENTITY_TRANSFORM);
        }

        if (isDirty(DirtyBits.NODE_BOUNDS)) {
            _geomBounds = getGeomBounds(_geomBounds,
                    BaseTransform.IDENTITY_TRANSFORM);
        }

    }

    /*
     * This function is called during synchronization to update the state of the
     * NG MappedNode from the FX MappedNode. Subclasses of MappedNode should override this method
     * and must call MappedNodeHelper.updatePeer(this)
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        final MappedNGNode peer = getPeer();

        // For debug / diagnostic purposes, we will copy across a name for this node down to
        // the NG layer, where we can use the name to figure out what the MappedNGNode represents.
        // An alternative would be to have a back-reference from the MappedNGNode back to the MappedNode it
        // is a peer to, however it was felt that this would make it too easy to communicate back
        // to the MappedNode and possibly violate thread invariants. But of course, we only need to do this
        // if we're going to print the render graph (otherwise all the work we'd do to keep the name
        // properly updated would be a waste).
        if (PrismSettings.printRenderGraph && isDirty(DirtyBits.DEBUG)) {
            final String id = getId();
            String className = getClass().getSimpleName();
            if (className.isEmpty()) {
                className = getClass().getName();
            }
            peer.setName(id == null ? className : id + "(" + className + ")");
        }

        if (isDirty(DirtyBits.NODE_TRANSFORM)) {
            peer.setTransformMatrix(localToParentTx);
        }

        if (isDirty(DirtyBits.NODE_VIEW_ORDER)) {
            peer.setViewOrder(getViewOrder());
        }

        if (isDirty(DirtyBits.NODE_BOUNDS)) {
            peer.setContentBounds(_geomBounds);
        }

        if (isDirty(DirtyBits.NODE_TRANSFORMED_BOUNDS)) {
            peer.setTransformedBounds(_txBounds, !isDirty(DirtyBits.NODE_BOUNDS));
        }

        if (isDirty(DirtyBits.NODE_OPACITY)) {
            peer.setOpacity((float)Utils.clamp(0, getOpacity(), 1));
        }

        if (isDirty(DirtyBits.NODE_CACHE)) {
            peer.setCachedAsBitmap(isCache(), getCacheHint());
        }

        if (isDirty(DirtyBits.NODE_CLIP)) {
            peer.setClipNode(getClip() != null ? getClip().getPeer() : null);
        }

        if (isDirty(DirtyBits.EFFECT_EFFECT)) {
            if (getEffect() != null) {
                EffectHelper.sync(getEffect());
                peer.effectChanged();
            }
        }

        if (isDirty(DirtyBits.NODE_EFFECT)) {
            peer.setEffect(getEffect() != null ? EffectHelper.getPeer(getEffect()) : null);
        }

        if (isDirty(DirtyBits.NODE_VISIBLE)) {
            peer.setVisible(isVisible());
        }

        if (isDirty(DirtyBits.NODE_DEPTH_TEST)) {
            peer.setDepthTest(isDerivedDepthTest());
        }

        if (isDirty(DirtyBits.NODE_BLENDMODE)) {
            BlendMode mode = getBlendMode();
            peer.setNodeBlendMode((mode == null)
                    ? null
                    : EffectHelper.getToolkitBlendMode(mode));
        }
    }

    /* ***********************************************************************
     *                                                                        *
     *                                                                        *
     *                                                                        *
     *************************************************************************/

    private static final Object USER_DATA_KEY = new Object();
    // A map containing a set of properties for this node
    private ObservableMap<Object, Object> properties;

    
    public final ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableMap(new HashMap<Object, Object>());
        }
        return properties;
    }

    
    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    
    public void setUserData(Object value) {
        getProperties().put(USER_DATA_KEY, value);
    }

    
    public Object getUserData() {
        return getProperties().get(USER_DATA_KEY);
    }

    /* ************************************************************************
     *                                                                        *
     *
     *                                                                        *
     *************************************************************************/

    
    private ReadOnlyObjectWrapper<MappedParent> parent;

    final void setParent(MappedParent value) {
        parentPropertyImpl().set(value);
    }

    public final MappedParent getParent() {
        return parent == null ? null : parent.get();
    }

    public final ReadOnlyObjectProperty<MappedParent> parentProperty() {
        return parentPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<MappedParent> parentPropertyImpl() {
        if (parent == null) {
            parent = new ReadOnlyObjectWrapper<MappedParent>() {
                private MappedParent oldParent;

                @Override
                protected void invalidated() {
                    if (oldParent != null) {
                        oldParent.disabledProperty().removeListener(parentDisabledChangedListener);
                        oldParent.treeVisibleProperty().removeListener(parentTreeVisibleChangedListener);
                        if (nodeTransformation != null && nodeTransformation.listenerReasons > 0) {
                            ((MappedNode) oldParent).localToSceneTransformProperty().removeListener(
                                    nodeTransformation.getLocalToSceneInvalidationListener());
                        }
                    }
                    updateDisabled();
                    computeDerivedDepthTest();
                    final MappedParent newParent = get();
                    if (newParent != null) {
                        newParent.disabledProperty().addListener(parentDisabledChangedListener);
                        newParent.treeVisibleProperty().addListener(parentTreeVisibleChangedListener);
                        if (nodeTransformation != null && nodeTransformation.listenerReasons > 0) {
                            ((MappedNode) newParent).localToSceneTransformProperty().addListener(
                                    nodeTransformation.getLocalToSceneInvalidationListener());
                        }
                        //
                        // if parent changed, then CSS needs to be reapplied so
                        // that this node will get the right styles. This used
                        // to be done from MappedParent.children's onChanged method.
                        // See the comments there, also.
                        //
                        reapplyCSS();
                    } else {
                        // RT-31168: reset CssFlag to clean so css will be reapplied if the node is added back later.
                        // If flag is REAPPLY, then reapplyCSS() will just return and the call to
                        // notifyParentsOfInvalidatedCSS() will be skipped thus leaving the node un-styled.
                        cssFlag = CssFlags.CLEAN;
                    }
                    updateTreeVisible(true);
                    oldParent = newParent;
                    invalidateLocalToSceneTransform();
                    parentResolvedOrientationInvalidated();
                    notifyAccessibleAttributeChanged(AccessibleAttribute.PARENT);
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "parent";
                }
            };
        }
        return parent;
    }

    private final InvalidationListener parentDisabledChangedListener = valueModel -> updateDisabled();

    private final InvalidationListener parentTreeVisibleChangedListener = valueModel -> updateTreeVisible(true);

    private MappedSubScene subScene = null;

    
    private ReadOnlyObjectWrapperManualFire<MappedScene> scene = new ReadOnlyObjectWrapperManualFire<MappedScene>();

    private class ReadOnlyObjectWrapperManualFire<T> extends ReadOnlyObjectWrapper<T> {
        @Override
        public Object getBean() {
            return MappedNode.this;
        }

        @Override
        public String getName() {
            return "scene";
        }

        @Override
        protected void fireValueChangedEvent() {
            /*
             * Note: This method has been intentionally made into a no-op. In
             * order to override the default set behavior. By default calling
             * set(...) on a different scene will trigger:
             * - invalidated();
             * - fireValueChangedEvent();
             * Both of the above are no-ops, but are handled manually via
             * - MappedNode.this.setScenes(...)
             * - MappedNode.this.invalidatedScenes(...)
             * - forceValueChangedEvent()
             */
        }

        public void fireSuperValueChangedEvent() {
            super.fireValueChangedEvent();
        }
    }

    private void invalidatedScenes(MappedScene oldScene, MappedSubScene oldSubScene) {
        MappedScene newScene = sceneProperty().get();
        boolean sceneChanged = oldScene != newScene;
        MappedSubScene newSubScene = subScene;

        if (getClip() != null) {
            getClip().setScenes(newScene, newSubScene);
        }
        if (sceneChanged) {
            updateCanReceiveFocus();
            if (isFocusTraversable()) {
                if (newScene != null) {
                    newScene.initializeInternalEventDispatcher();
                }
            }
            focusSetDirty(oldScene);
            focusSetDirty(newScene);
        }
        scenesChanged(newScene, newSubScene, oldScene, oldSubScene);

        if (sceneChanged) reapplyCSS();

        if (sceneChanged && !isDirtyEmpty()) {
            //Note: no need to remove from scene's dirty list
            //MappedScene's is checking if the node's scene is correct
            /* TODO: looks like an existing bug when a node is moved from one
             * location to another, setScenes will be called twice by
             * MappedParent.VetoableListDecorator onProposedChange and onChanged
             * respectively. Removing the node and setting setScense(null,null)
             * then adding it back to potentially the same scene. Causing the
             * same node to being added twice to the same scene.
             */
            addToSceneDirtyList();
        }

        if (newScene == null && peer != null) {
            peer.release();
        }

        if (oldScene != null) {
            oldScene.clearNodeMnemonics(this);
        }
        if (getParent() == null) {
            // if we are the root we need to handle scene change
            parentResolvedOrientationInvalidated();
        }

        if (sceneChanged) { scene.fireSuperValueChangedEvent(); }

        /* Dispose the accessible peer, if any. If AT ever needs this node again
         * a new accessible peer is created. */
        if (accessible != null) {
            /* Generally accessibility does not retain any state, therefore deleting objects
             * generally does not cause problems (AT just asks everything back).
             * The exception to this rule is when the object sends a notifications to the AT,
             * in which case it is expected to be around to answer request for the new values.
             * It is possible that a object is reparented (within the scene) in the middle of
             * this process. For example, when a tree item is expanded, the notification is
             * sent to the AT by the cell. But when the TreeView relayouts the cell can be
             * reparented before AT can query the relevant information about the expand event.
             * If the accessible was disposed, AT can't properly report the event.
             *
             * The fix is to defer the disposal of the accessible to the next pulse.
             * If at that time the node is placed back to the scene, then the accessible is hooked
             * to MappedNode and AT requests are processed. Otherwise the accessible is disposed.
             */
            if (oldScene != null && oldScene != newScene && newScene == null) {
                // Strictly speaking we need some type of accessible.thaw() at this point.
                oldScene.addAccessible(MappedNode.this, accessible);
            } else {
                accessible.dispose();
            }
            /* Always set to null to ensure this accessible is never on more than one
             * MappedScene#accMap at the same time (At lest not with the same accessible).
             */
            accessible = null;
        }
    }

    final void setScenes(MappedScene newScene, MappedSubScene newSubScene) {
        MappedScene oldScene = sceneProperty().get();
        if (newScene != oldScene || newSubScene != subScene) {
            scene.set(newScene);
            MappedSubScene oldSubScene = subScene;
            subScene = newSubScene;
            invalidatedScenes(oldScene, oldSubScene);
            if (this instanceof MappedSubScene) { // TODO: find better solution
                MappedSubScene thisSubScene = (MappedSubScene)this;
                thisSubScene.getRoot().setScenes(newScene, thisSubScene);
            }
        }
    }

    final MappedSubScene getSubScene() {
        return subScene;
    }

    public final MappedScene getScene() {
        return scene.get();
    }

    public final ReadOnlyObjectProperty<MappedScene> sceneProperty() {
        return scene.getReadOnlyProperty();
    }

    
    void scenesChanged(final MappedScene newScene, final MappedSubScene newSubScene,
                       final MappedScene oldScene, final MappedSubScene oldSubScene) { }


    
    private StringProperty id;

    public final void setId(String value) {
        idProperty().set(value);
    }

    //TODO: this is copied from the property in order to add the @return statement.
    //      We should have a better, general solution without the need to copy it.
    
    public final String getId() {
        return id == null ? null : id.get();
    }

    public final StringProperty idProperty() {
        if (id == null) {
            id = new StringPropertyBase() {

                @Override
                protected void invalidated() {
                    reapplyCSS();
                    if (PrismSettings.printRenderGraph) {
                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.DEBUG);
                    }
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "id";
                }
            };
        }
        return id;
    }

    
    private ObservableList<String> styleClass = new TrackableObservableList<String>() {
        @Override
        protected void onChanged(Change<String> c) {
            reapplyCSS();
        }

        @Override
        public String toString() {
            if (size() == 0) {
                return "";
            } else if (size() == 1) {
                return get(0);
            } else {
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < size(); i++) {
                    buf.append(get(i));
                    if (i + 1 < size()) {
                        buf.append(' ');
                    }
                }
                return buf.toString();
            }
        }
    };

    @Override
    public final ObservableList<String> getStyleClass() {
        return styleClass;
    }

    
    private StringProperty style;

    
    public final void setStyle(String value) {
        styleProperty().set(value);
    }

    // TODO: javadoc copied from property for the sole purpose of providing a return tag
    
    public final String getStyle() {
        return style == null ? "" : style.get();
    }

    public final StringProperty styleProperty() {
        if (style == null) {
            style = new StringPropertyBase("") {

                @Override public void set(String value) {
                    // getStyle returns an empty string if the style property
                    // is null. To be consistent, getStyle should also return
                    // an empty string when the style property's value is null.
                    super.set((value != null) ? value : "");
                }

                @Override
                protected void invalidated() {
                    // If the style has changed, then styles of this node
                    // and child nodes might be affected.
                    reapplyCSS();
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "style";
                }
            };
        }
        return style;
    }

    
    private BooleanProperty visible;

    public final void setVisible(boolean value) {
        visibleProperty().set(value);
    }

    public final boolean isVisible() {
        return visible == null ? true : visible.get();
    }

    public final BooleanProperty visibleProperty() {
        if (visible == null) {
            visible = new StyleableBooleanProperty(true) {
                boolean oldValue = true;
                @Override
                protected void invalidated() {
                    if (oldValue != get()) {
                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_VISIBLE);
                        MappedNodeHelper.geomChanged(MappedNode.this);
                        updateTreeVisible(false);
                        if (getParent() != null) {
                            // notify the parent of the potential change in visibility
                            // of this node, since visibility affects bounds of the
                            // parent node
                            getParent().childVisibilityChanged(MappedNode.this);
                        }
                        oldValue = get();
                    }
                }

                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.VISIBILITY;
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "visible";
                }
            };
        }
        return visible;
    }

    public final void setCursor(Cursor value) {
        cursorProperty().set(value);
    }

    public final Cursor getCursor() {
        return (miscProperties == null) ? DEFAULT_CURSOR
                : miscProperties.getCursor();
    }

    
    public final ObjectProperty<Cursor> cursorProperty() {
        return getMiscProperties().cursorProperty();
    }

    
    private DoubleProperty opacity;

    public final void setOpacity(double value) {
        opacityProperty().set(value);
    }
    public final double getOpacity() {
        return opacity == null ? 1 : opacity.get();
    }

    public final DoubleProperty opacityProperty() {
        if (opacity == null) {
            opacity = new StyleableDoubleProperty(1) {

                @Override
                public void invalidated() {
                    MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_OPACITY);
                }

                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.OPACITY;
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "opacity";
                }
            };
        }
        return opacity;
    }

    
    private javafx.beans.property.ObjectProperty<BlendMode> blendMode;

    public final void setBlendMode(BlendMode value) {
        blendModeProperty().set(value);
    }
    public final BlendMode getBlendMode() {
        return blendMode == null ? null : blendMode.get();
    }

    public final ObjectProperty<BlendMode> blendModeProperty() {
        if (blendMode == null) {
            blendMode = new StyleableObjectProperty<BlendMode>(null) {
                @Override public void invalidated() {
                    MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_BLENDMODE);
                }

                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.BLEND_MODE;
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "blendMode";
                }
            };
        }
        return blendMode;
    }

    public final void setClip(MappedNode value) {
        clipProperty().set(value);
    }

    public final MappedNode getClip() {
        return (miscProperties == null) ? DEFAULT_CLIP
                : miscProperties.getClip();
    }

    
    public final ObjectProperty<MappedNode> clipProperty() {
        return getMiscProperties().clipProperty();
    }

    public final void setCache(boolean value) {
        cacheProperty().set(value);
    }

    public final boolean isCache() {
        return (miscProperties == null) ? DEFAULT_CACHE
                : miscProperties.isCache();
    }

    
    public final BooleanProperty cacheProperty() {
        return getMiscProperties().cacheProperty();
    }

    public final void setCacheHint(CacheHint value) {
        cacheHintProperty().set(value);
    }

    public final CacheHint getCacheHint() {
        return (miscProperties == null) ? DEFAULT_CACHE_HINT
                : miscProperties.getCacheHint();
    }

    
    public final ObjectProperty<CacheHint> cacheHintProperty() {
        return getMiscProperties().cacheHintProperty();
    }

    public final void setEffect(Effect value) {
        effectProperty().set(value);
    }

    public final Effect getEffect() {
        return (miscProperties == null) ? DEFAULT_EFFECT
                : miscProperties.getEffect();
    }

    
    public final ObjectProperty<Effect> effectProperty() {
        return getMiscProperties().effectProperty();
    }

    public final void setDepthTest(DepthTest value) {
        depthTestProperty().set(value);
    }

    public final DepthTest getDepthTest() {
        return (miscProperties == null) ? DEFAULT_DEPTH_TEST
                : miscProperties.getDepthTest();
    }
    public final ObjectProperty<DepthTest> depthTestProperty() {
        return getMiscProperties().depthTestProperty();
    }

    
    void computeDerivedDepthTest() {
        boolean newDDT;
        if (getDepthTest() == DepthTest.INHERIT) {
            if (getParent() != null) {
                newDDT = getParent().isDerivedDepthTest();
            } else {
                newDDT = true;
            }
        } else if (getDepthTest() == DepthTest.ENABLE) {
            newDDT = true;
        } else {
            newDDT = false;
        }

        if (isDerivedDepthTest() != newDDT) {
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_DEPTH_TEST);
            setDerivedDepthTest(newDDT);
        }
    }

    // This is the derived depthTest value to pass to PG level
    private boolean derivedDepthTest = true;

    void setDerivedDepthTest(boolean value) {
        derivedDepthTest = value;
    }

    boolean isDerivedDepthTest() {
        return derivedDepthTest;
    }

    public final void setDisable(boolean value) {
        disableProperty().set(value);
    }

    public final boolean isDisable() {
        return (miscProperties == null) ? DEFAULT_DISABLE
                : miscProperties.isDisable();
    }

    
    public final BooleanProperty disableProperty() {
        return getMiscProperties().disableProperty();
    }


//    
//    public final ObjectProperty<InputMap<?>> inputMapProperty() {
//        if (inputMap == null) {
//            inputMap = new SimpleObjectProperty<InputMap<?>>(this, "inputMap") {
//                private InputMap<?> currentMap = get();
//                @Override protected void invalidated() {
//                    if (currentMap != null) {
//                        currentMap.dispose();
//                    }
//                    currentMap = get();
//                }
//            };
//        }
//        return inputMap;
//    }
//    public final void setInputMap(InputMap<?> value) { inputMapProperty().set(value); }
//    public final InputMap<?> getInputMap() { return inputMapProperty().getValue(); }
//    private ObjectProperty<InputMap<?>> inputMap;


    /* ************************************************************************
     *                                                                        *
     *
     *                                                                        *
     *************************************************************************/
    
    private BooleanProperty pickOnBounds;

    public final void setPickOnBounds(boolean value) {
        pickOnBoundsProperty().set(value);
    }

    public final boolean isPickOnBounds() {
        return pickOnBounds == null ? false : pickOnBounds.get();
    }

    public final BooleanProperty pickOnBoundsProperty() {
        if (pickOnBounds == null) {
            pickOnBounds = new SimpleBooleanProperty(this, "pickOnBounds");
        }
        return pickOnBounds;
    }

    
    private ReadOnlyBooleanWrapper disabled;

    protected final void setDisabled(boolean value) {
        disabledPropertyImpl().set(value);
    }

    public final boolean isDisabled() {
        return disabled == null ? false : disabled.get();
    }

    public final ReadOnlyBooleanProperty disabledProperty() {
        return disabledPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper disabledPropertyImpl() {
        if (disabled == null) {
            disabled = new ReadOnlyBooleanWrapper() {

                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, get());
                    updateCanReceiveFocus();
                    focusSetDirty(getScene());
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "disabled";
                }
            };
        }
        return disabled;
    }

    private void updateDisabled() {
        boolean isDisabled = isDisable();
        if (!isDisabled) {
            isDisabled = getParent() != null ? getParent().isDisabled() :
                    getSubScene() != null && getSubScene().isDisabled();
        }
        setDisabled(isDisabled);
        if (this instanceof MappedSubScene) {
            ((MappedSubScene)this).getRoot().setDisabled(isDisabled);
        }
    }

    
    public MappedNode lookup(String selector) {
        if (selector == null) return null;
        MappedSelector s = MappedSelector.createSelector(selector);
        return s != null && s.applies(this) ? this : null;
    }

    
    public Set<MappedNode> lookupAll(String selector) {
        final MappedSelector s = MappedSelector.createSelector(selector);
        final Set<MappedNode> empty = Collections.emptySet();
        if (s == null) return empty;
        List<MappedNode> results = lookupAll(s, null);
        return results == null ? empty : new UnmodifiableListSet<MappedNode>(results);
    }

    
    List<MappedNode> lookupAll(MappedSelector selector, List<MappedNode> results) {
        if (selector.applies(this)) {
            // Lazily create the set to reduce some trash.
            if (results == null) {
                results = new LinkedList<MappedNode>();
            }
            results.add(this);
        }
        return results;
    }

    
    public void toBack() {
        if (getParent() != null) {
            getParent().toBack(this);
        }
    }

    
    public void toFront() {
        if (getParent() != null) {
            getParent().toFront(this);
        }
    }

    // TODO: need to verify whether this is OK to do starting from a node in
    // the scene graph other than the root.
    private void doCSSPass() {
        if (this.cssFlag != CssFlags.CLEAN) {
            // The dirty bit isn't checked but we must ensure it is cleared.
            // The cssFlag is set to clean in either MappedNode.processCSS or
            // MappedNodeHelper.processCSS

            // Don't clear the dirty bit in case it will cause problems
            // with a full CSS pass on the scene.
            // TODO: is this the right thing to do?
            // this.clearDirty(com.sun.javafx.scene.DirtyBits.NODE_CSS);

            this.processCSS();
        }
    }

    
    private static void syncAll(MappedNode node) {
        node.syncPeer();
        if (node instanceof MappedParent) {
            MappedParent p = (MappedParent) node;
            final int childrenCount = p.getChildren().size();

            for (int i = 0; i < childrenCount; i++) {
                MappedNode n = p.getChildren().get(i);
                if (n != null) {
                    syncAll(n);
                }
            }
        }
        if (node.getClip() != null) {
            syncAll(node.getClip());
        }
    }

    private void doLayoutPass() {
        if (this instanceof MappedParent) {
            // TODO: As an optimization we only need to layout those dirty
            // roots that are descendants of this node
            MappedParent p = (MappedParent)this;
            for (int i = 0; i < 3; i++) {
                p.layout();
            }
        }
    }

    private void doCSSLayoutSyncForSnapshot() {
        doCSSPass();
        doLayoutPass();
        updateBounds();
        MappedScene.setAllowPGAccess(true);
        syncAll(this);
        MappedScene.setAllowPGAccess(false);
    }

    private WritableImage doSnapshot(MappedSnapshotParameters params, WritableImage img) {
        if (getScene() != null) {
            getScene().doCSSLayoutSyncForSnapshot(this);
        } else {
            doCSSLayoutSyncForSnapshot();
        }

        BaseTransform transform = BaseTransform.IDENTITY_TRANSFORM;
        if (params.getTransform() != null) {
            Affine3D tempTx = new Affine3D();
            TransformHelper.apply(params.getTransform(), tempTx);
            transform = tempTx;
        }
        double x;
        double y;
        double w;
        double h;
        Rectangle2D viewport = params.getViewport();
        if (viewport != null) {
            // Use the specified viewport
            x = viewport.getMinX();
            y = viewport.getMinY();
            w = viewport.getWidth();
            h = viewport.getHeight();
        } else {
            // Get the bounds in parent of this node, transformed by the
            // specified transform.
            BaseBounds tempBounds = TempState.getInstance().bounds;
            tempBounds = getTransformedBounds(tempBounds, transform);
            x = tempBounds.getMinX();
            y = tempBounds.getMinY();
            w = tempBounds.getWidth();
            h = tempBounds.getHeight();
        }
        WritableImage result = MappedScene.doSnapshot(getScene(), x, y, w, h,
                this, transform, params.isDepthBufferInternal(),
                params.getFill(), params.getEffectiveCamera(), img);

        return result;
    }

    
    public WritableImage snapshot(MappedSnapshotParameters params, WritableImage image) {
        Toolkit.getToolkit().checkFxUserThread();

        if (params == null) {
            params = new MappedSnapshotParameters();
            MappedScene s = getScene();
            if (s != null) {
                params.setCamera(s.getEffectiveCamera());
                params.setDepthBuffer(s.isDepthBufferInternal());
                params.setFill(s.getFill());
            }
        }

        return doSnapshot(params, image);
    }

    
    public void snapshot(Callback<SnapshotResult, Void> callback,
                         MappedSnapshotParameters params, WritableImage image) {

        Toolkit.getToolkit().checkFxUserThread();
        if (callback == null) {
            throw new NullPointerException("The callback must not be null");
        }

        if (params == null) {
            params = new MappedSnapshotParameters();
            MappedScene s = getScene();
            if (s != null) {
                params.setCamera(s.getEffectiveCamera());
                params.setDepthBuffer(s.isDepthBufferInternal());
                params.setFill(s.getFill());
            }
        } else {
            params = params.copy();
        }

        final MappedSnapshotParameters theParams = params;
        final Callback<SnapshotResult, Void> theCallback = callback;
        final WritableImage theImage = image;

        // Create a deferred runnable that will be run from a pulse listener
        // that is called after all of the scenes have been synced but before
        // any of them have been rendered.
        final Runnable snapshotRunnable = () -> {
            WritableImage img = doSnapshot(theParams, theImage);
            SnapshotResult result = new SnapshotResult(img, MappedNode.this, theParams);
//                System.err.println("Calling snapshot callback");
            try {
                Void v = theCallback.call(result);
            } catch (Throwable th) {
                System.err.println("Exception in snapshot callback");
                th.printStackTrace(System.err);
            }
        };

//        System.err.println("Schedule a snapshot in the future");
        MappedScene.addSnapshotRunnable(snapshotRunnable);
    }

    /* ************************************************************************
     *                                                                        *
     *
     *                                                                        *
     *************************************************************************/

    public final void setOnDragEntered(
            EventHandler<? super DragEvent> value) {
        onDragEnteredProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragEntered() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragEntered();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>>
    onDragEnteredProperty() {
        return getEventHandlerProperties().onDragEnteredProperty();
    }

    public final void setOnDragExited(
            EventHandler<? super DragEvent> value) {
        onDragExitedProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragExited() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragExited();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>>
    onDragExitedProperty() {
        return getEventHandlerProperties().onDragExitedProperty();
    }

    public final void setOnDragOver(
            EventHandler<? super DragEvent> value) {
        onDragOverProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragOver() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragOver();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>>
    onDragOverProperty() {
        return getEventHandlerProperties().onDragOverProperty();
    }

    // Do we want DRAG_TRANSFER_MODE_CHANGED event?
//    public final void setOnDragTransferModeChanged(
//            EventHandler<? super DragEvent> value) {
//        onDragTransferModeChangedProperty().set(value);
//    }
//
//    public final EventHandler<? super DragEvent> getOnDragTransferModeChanged() {
//        return (eventHandlerProperties == null)
//                ? null : eventHandlerProperties.getOnDragTransferModeChanged();
//    }
//
//    
//    public final ObjectProperty<EventHandler<? super DragEvent>>
//            onDragTransferModeChangedProperty() {
//        return getEventHandlerProperties().onDragTransferModeChangedProperty();
//    }

    public final void setOnDragDropped(
            EventHandler<? super DragEvent> value) {
        onDragDroppedProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragDropped() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragDropped();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>>
    onDragDroppedProperty() {
        return getEventHandlerProperties().onDragDroppedProperty();
    }

    public final void setOnDragDone(
            EventHandler<? super DragEvent> value) {
        onDragDoneProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragDone() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragDone();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>>
    onDragDoneProperty() {
        return getEventHandlerProperties().onDragDoneProperty();
    }

    
    public Dragboard startDragAndDrop(TransferMode... transferModes) {
        if (getScene() != null) {
            return getScene().startDragAndDrop(this, transferModes);
        }

        throw new IllegalStateException("Cannot start drag and drop on node "
                + "that is not in scene");
    }

    
    public void startFullDrag() {
        if (getScene() != null) {
            getScene().startFullDrag(this);
            return;
        }

        throw new IllegalStateException("Cannot start full drag on node "
                + "that is not in scene");
    }

    ////////////////////////////
    //  Private Implementation
    ////////////////////////////

    
    private MappedNode clipParent;
    // Use a getter function instead of giving clipParent package access,
    // so that clipParent doesn't get turned into a Location.
    final MappedNode getClipParent() {
        return clipParent;
    }

    
    boolean isConnected() {
        // don't need to check scene, because if scene is non-null
        // parent must also be non-null
        return getParent() != null || clipParent != null;
    }

    
    boolean wouldCreateCycle(MappedNode parent, MappedNode child) {
        if (child != null && child.getClip() == null && (!(child instanceof MappedParent))) {
            return false;
        }

        MappedNode n = parent;
        while (n != child) {
            if (n.getParent() != null) {
                n = n.getParent();
            } else if (n.getSubScene() != null) {
                n = n.getSubScene();
            } else if (n.clipParent != null) {
                n = n.clipParent;
            } else {
                return false;
            }
        }
        return true;
    }

    
    private MappedNGNode peer;

    @SuppressWarnings("CallToPrintStackTrace")
    <P extends MappedNGNode> P getPeer() {
        if (Utils.assertionEnabled()) {
            // Assertion checking code
            if (getScene() != null && !MappedScene.isPGAccessAllowed()) {
                java.lang.System.err.println();
                java.lang.System.err.println("*** unexpected PG access");
                java.lang.Thread.dumpStack();
            }
        }

        if (peer == null) {
            //if (MappedPerformanceTracker.isLoggingEnabled()) {
            //    MappedPerformanceTracker.logEvent("Creating MappedNGNode for [{this}, id=\"{id}\"]");
            //}
            peer = MappedNodeHelper.createPeer(this);
            //if (MappedPerformanceTracker.isLoggingEnabled()) {
            //    MappedPerformanceTracker.logEvent("MappedNGNode created");
            //}
        }
        return (P) peer;
    }

    /* *************************************************************************
     *                                                                         *
     *                              Initialization                             *
     *                                                                         *
     *  To Note limit the number of bounds computations and improve startup    *
     *  performance.                                                           *
     *                                                                         *
     **************************************************************************/

    
    protected MappedNode() {
        //if (MappedPerformanceTracker.isLoggingEnabled()) {
        //    MappedPerformanceTracker.logEvent("MappedNode.init for [{this}, id=\"{id}\"]");
        //}
        updateTreeVisible(false);
        //if (MappedPerformanceTracker.isLoggingEnabled()) {
        //    MappedPerformanceTracker.logEvent("MappedNode.postinit " +
        //                                "for [{this}, id=\"{id}\"] finished");
        //}
    }
    private BooleanProperty managed;

    public final void setManaged(boolean value) {
        managedProperty().set(value);
    }

    public final boolean isManaged() {
        return managed == null ? true : managed.get();
    }

    public final BooleanProperty managedProperty() {
        if (managed == null) {
            managed = new BooleanPropertyBase(true) {

                @Override
                protected void invalidated() {
                    final MappedParent parent = getParent();
                    if (parent != null) {
                        parent.managedChildChanged();
                    }
                    notifyManagedChanged();
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "managed";
                }

            };
        }
        return managed;
    }

    
    void notifyManagedChanged() { }

    
    private DoubleProperty layoutX;

    public final void setLayoutX(double value) {
        layoutXProperty().set(value);
    }

    public final double getLayoutX() {
        return layoutX == null ? 0.0 : layoutX.get();
    }

    public final DoubleProperty layoutXProperty() {
        if (layoutX == null) {
            layoutX = new DoublePropertyBase(0.0) {

                @Override
                protected void invalidated() {
                    MappedNodeHelper.transformsChanged(MappedNode.this);
                    final MappedParent p = getParent();

                    // Propagate layout if this change isn't triggered by its parent
                    if (p != null && !p.isCurrentLayoutChild(MappedNode.this)) {
                        if (isManaged()) {
                            // Force its parent to fix the layout since it is a managed child.
                            p.requestLayout(true);
                        } else {
                            // MappedParent size changed, parent's parent might need to re-layout
                            p.clearSizeCache();
                            p.requestParentLayout();
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "layoutX";
                }
            };
        }
        return layoutX;
    }

    
    private DoubleProperty layoutY;

    public final void setLayoutY(double value) {
        layoutYProperty().set(value);
    }

    public final double getLayoutY() {
        return layoutY == null ? 0.0 : layoutY.get();
    }

    public final DoubleProperty layoutYProperty() {
        if (layoutY == null) {
            layoutY = new DoublePropertyBase(0.0) {

                @Override
                protected void invalidated() {
                    MappedNodeHelper.transformsChanged(MappedNode.this);
                    final MappedParent p = getParent();

                    // Propagate layout if this change isn't triggered by its parent
                    if (p != null && !p.isCurrentLayoutChild(MappedNode.this)) {
                        if (isManaged()) {
                            // Force its parent to fix the layout since it is a managed child.
                            p.requestLayout(true);
                        } else {
                            // MappedParent size changed, parent's parent might need to re-layout
                            p.clearSizeCache();
                            p.requestParentLayout();
                        }
                    }
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "layoutY";
                }

            };
        }
        return layoutY;
    }

    
    public void relocate(double x, double y) {
        setLayoutX(x - getLayoutBounds().getMinX());
        setLayoutY(y - getLayoutBounds().getMinY());

        PlatformLogger logger = Logging.getLayoutLogger();
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString()+" moved to ("+x+","+y+")");
        }
    }

    
    public boolean isResizable() {
        return false;
    }

    
    public Orientation getContentBias() {
        return null;
    }

    
    public double minWidth(double height) {
        return prefWidth(height);
    }

    
    public double minHeight(double width) {
        return prefHeight(width);
    }

    
    public double prefWidth(double height) {
        final double result = getLayoutBounds().getWidth();
        return Double.isNaN(result) || result < 0 ? 0 : result;
    }

    
    public double prefHeight(double width) {
        final double result = getLayoutBounds().getHeight();
        return Double.isNaN(result) || result < 0 ? 0 : result;
    }

    
    public double maxWidth(double height) {
        return prefWidth(height);
    }

    
    public double maxHeight(double width) {
        return prefHeight(width);
    }

    
    public void resize(double width, double height) {
    }

    
    public final void autosize() {
        if (isResizable()) {
            Orientation contentBias = getContentBias();
            double w, h;
            if (contentBias == null) {
                w = boundedSize(prefWidth(-1), minWidth(-1), maxWidth(-1));
                h = boundedSize(prefHeight(-1), minHeight(-1), maxHeight(-1));
            } else if (contentBias == Orientation.HORIZONTAL) {
                w = boundedSize(prefWidth(-1), minWidth(-1), maxWidth(-1));
                h = boundedSize(prefHeight(w), minHeight(w), maxHeight(w));
            } else { // bias == VERTICAL
                h = boundedSize(prefHeight(-1), minHeight(-1), maxHeight(-1));
                w = boundedSize(prefWidth(h), minWidth(h), maxWidth(h));
            }
            resize(w,h);
        }
    }

    double boundedSize(double value, double min, double max) {
        // if max < value, return max
        // if min > value, return min
        // if min > max, return min
        return Math.min(Math.max(value, min), Math.max(min,max));
    }

    
    public void resizeRelocate(double x, double y, double width, double height) {
        resize(width, height);
        relocate(x,y);
    }

    
    public static final double BASELINE_OFFSET_SAME_AS_HEIGHT = Double.NEGATIVE_INFINITY;

    
    public double getBaselineOffset() {
        if (isResizable()) {
            return BASELINE_OFFSET_SAME_AS_HEIGHT;
        } else {
            return getLayoutBounds().getHeight();
        }
    }

    
    public double computeAreaInScreen() {
        return doComputeAreaInScreen();
    }

    /*
     * Help application or utility to implement LOD support by returning the
     * projected area of a MappedNode in pixel unit. The projected area is not clipped.
     *
     * For perspective camera, this method first exams node's bounds against
     * camera's clipping plane to cut off those out of viewing frustrum. After
     * computing areaInScreen, it applies a tight viewing frustrum check using
     * canonical view volume.
     *
     * The result of areaInScreen comes from the product of
     * (projViewTx x localToSceneTransform x localBounds).
     *
     * Returns 0 for those fall outside viewing frustrum.
     */
    private double doComputeAreaInScreen() {
        MappedScene tmpScene = getScene();
        if (tmpScene != null) {
            Bounds bounds = getBoundsInLocal();
            MappedCamera camera = tmpScene.getEffectiveCamera();
            boolean isPerspective = camera instanceof MappedPerspectiveCamera ? true : false;
            Transform localToSceneTx = getLocalToSceneTransform();
            Affine3D tempTx = TempState.getInstance().tempTx;
            BaseBounds localBounds = new BoxBounds((float) bounds.getMinX(),
                    (float) bounds.getMinY(),
                    (float) bounds.getMinZ(),
                    (float) bounds.getMaxX(),
                    (float) bounds.getMaxY(),
                    (float) bounds.getMaxZ());

            // NOTE: Viewing frustrum check on camera's clipping plane is now only
            // for perspective camera.
            // TODO: Need to hook up parallel camera's nearClip and farClip.
            if (isPerspective) {
                Transform cameraL2STx = camera.getLocalToSceneTransform();

                // If camera transform only contains translate, compare in scene
                // coordinate. Otherwise, compare in camera coordinate.
                if (cameraL2STx.getMxx() == 1.0
                        && cameraL2STx.getMxy() == 0.0
                        && cameraL2STx.getMxz() == 0.0
                        && cameraL2STx.getMyx() == 0.0
                        && cameraL2STx.getMyy() == 1.0
                        && cameraL2STx.getMyz() == 0.0
                        && cameraL2STx.getMzx() == 0.0
                        && cameraL2STx.getMzy() == 0.0
                        && cameraL2STx.getMzz() == 1.0) {

                    double minZ, maxZ;

                    // If node transform only contains translate, only convert
                    // minZ and maxZ to scene coordinate. Otherwise, convert
                    // node bounds to scene coordinate.
                    if (localToSceneTx.getMxx() == 1.0
                            && localToSceneTx.getMxy() == 0.0
                            && localToSceneTx.getMxz() == 0.0
                            && localToSceneTx.getMyx() == 0.0
                            && localToSceneTx.getMyy() == 1.0
                            && localToSceneTx.getMyz() == 0.0
                            && localToSceneTx.getMzx() == 0.0
                            && localToSceneTx.getMzy() == 0.0
                            && localToSceneTx.getMzz() == 1.0) {

                        Vec3d tempV3D = TempState.getInstance().vec3d;
                        tempV3D.set(0, 0, bounds.getMinZ());
                        localToScene(tempV3D);
                        minZ = tempV3D.z;

                        tempV3D.set(0, 0, bounds.getMaxZ());
                        localToScene(tempV3D);
                        maxZ = tempV3D.z;
                    } else {
                        Bounds nodeInSceneBounds = localToScene(bounds);
                        minZ = nodeInSceneBounds.getMinZ();
                        maxZ = nodeInSceneBounds.getMaxZ();
                    }

                    if (minZ > camera.getFarClipInScene()
                            || maxZ < camera.getNearClipInScene()) {
                        return 0;
                    }

                } else {
                    BaseBounds nodeInCameraBounds = new BoxBounds();

                    // We need to set tempTx to identity since it is a recycled transform.
                    // This is because TransformHelper.apply() is a matrix concatenation operation.
                    tempTx.setToIdentity();
                    TransformHelper.apply(localToSceneTx, tempTx);

                    // Convert node from local coordinate to camera coordinate
                    tempTx.preConcatenate(camera.getSceneToLocalTransform());
                    tempTx.transform(localBounds, nodeInCameraBounds);

                    // Compare in camera coordinate
                    if (nodeInCameraBounds.getMinZ() > camera.getFarClip()
                            || nodeInCameraBounds.getMaxZ() < camera.getNearClip()) {
                        return 0;
                    }
                }
            }

            GeneralTransform3D projViewTx = TempState.getInstance().projViewTx;
            projViewTx.set(camera.getProjViewTransform());

            // We need to set tempTx to identity since it is a recycled transform.
            // This is because TransformHelper.apply() is a matrix concatenation operation.
            tempTx.setToIdentity();
            TransformHelper.apply(localToSceneTx, tempTx);

            // The product of projViewTx * localToSceneTransform
            GeneralTransform3D tx = projViewTx.mul(tempTx);

            // Transform localBounds to projected bounds
            localBounds = tx.transform(localBounds, localBounds);
            double area = localBounds.getWidth() * localBounds.getHeight();

            // Use canonical view volume to check whether object is outside the
            // viewing frustrum
            if (isPerspective) {
                localBounds.intersectWith(-1, -1, 0, 1, 1, 1);
                area = (localBounds.getWidth() < 0 || localBounds.getHeight() < 0) ? 0 : area;
            }
            return area * (camera.getViewWidth() / 2 * camera.getViewHeight() / 2);
        }
        return 0;
    }

    /* *************************************************************************
     *                                                                         *
     * Bounds related APIs                                                     *
     *                                                                         *
     **************************************************************************/

    public final Bounds getBoundsInParent() {
        return boundsInParentProperty().get();
    }

    
    public final ReadOnlyObjectProperty<Bounds> boundsInParentProperty() {
        return getMiscProperties().boundsInParentProperty();
    }

    private void invalidateBoundsInParent() {
        if (miscProperties != null) {
            miscProperties.invalidateBoundsInParent();
        }
    }

    public final Bounds getBoundsInLocal() {
        return boundsInLocalProperty().get();
    }

    
    public final ReadOnlyObjectProperty<Bounds> boundsInLocalProperty() {
        return getMiscProperties().boundsInLocalProperty();
    }

    private void invalidateBoundsInLocal() {
        if (miscProperties != null) {
            miscProperties.invalidateBoundsInLocal();
        }
    }

    
    private LazyBoundsProperty layoutBounds = new LazyBoundsProperty() {
        @Override
        protected Bounds computeBounds() {
            return MappedNodeHelper.computeLayoutBounds(MappedNode.this);
        }

        @Override
        public Object getBean() {
            return MappedNode.this;
        }

        @Override
        public String getName() {
            return "layoutBounds";
        }
    };

    public final Bounds getLayoutBounds() {
        return layoutBoundsProperty().get();
    }

    public final ReadOnlyObjectProperty<Bounds> layoutBoundsProperty() {
        return layoutBounds;
    }

    /*
     *                  Bounds And Transforms Computation
     *
     *  This section of the code is responsible for computing and caching
     *  various bounds and transforms. For optimal performance and minimal
     *  recomputation of bounds (which can be quite expensive), we cache
     *  values on two different levels. We expose two public immutable
     *  Bounds boundsInParent objects and boundsInLocal. Because they are
     *  immutable and because they may change quite frequently (especially
     *  in the case of a MappedParent whose children are animated), it is
     *  important that the system does not rely on these variables, because
     *  doing so would produce a large amount of garbage. Rather, these
     *  variables are provided solely for the convenience of application
     *  developers and, being lazily bound, should generally be created at
     *  most once per frame.
     *
     *  The second level of caching are within local Bounds2D variables.
     *  These variables, txBounds and geomBounds, are mutable and as such
     *  can be cached and updated as frequently as necessary without creating
     *  excessive garbage. However, since the computation of bounds is still
     *  expensive, it is desirable to cache both the geometric bounds and
     *  the "complete" transformed bounds (essentially, boundsInParent).
     *  Cached txBounds is particularly useful when computing the geometric
     *  bounds of a MappedParent since it would not require complete or partial
     *  recomputation of each child.
     *
     *  Finally, we cache the complete transform for this node which converts
     *  its coord system from local to parent coords. This is useful both for
     *  minimizing bounds recomputations in the case of the geometry having
     *  changed but the transform not having changed, and also because the tx
     *  is required for several different computations (for example, it must
     *  be computed once during state synchronization with the PG peer, and
     *  must also be computed when the pivot point changes, and also when
     *  deriving the txBounds of the MappedNode).
     *
     *  As with any caching system, a subtle and non-trivial amount of code
     *  is devoted to invalidating the bounds / transforms at appropriate
     *  times and in appropriate places to make sure bounds / transforms
     *  are recomputed at all necessary times.
     *
     *  There are three computeXXX functions. One is for computing the
     *  boundsInParent, the second for computing boundsInLocal, and the
     *  third for computing the default layout bounds (which, by default,
     *  is based on the geometric bounds). These functions are all prefixed
     *  with "compute" because they create and return new immutable
     *  Bounds objects.
     *
     *  There are three getXXXBounds functions. One is for returning the
     *  complete transformed bounds. The second is for returning the
     *  local bounds. The last is for returning the geometric bounds. These
     *  functions are all prefixed with "get" because they may well return
     *  a cached value, or may actually compute the bounds if necessary. These
     *  functions all have the same signature. They take a Bounds2D and
     *  BaseTransform, and return a Bounds2D (the same as they took). These
     *  functions essentially populate the supplied bounds2D with the
     *  appropriate bounds information, leveraging cached bounds if possible.
     *
     *  There is a single MappedNodeHelper.computeGeomBoundsImpl function which is abstract.
     *  This must be implemented in each subclass, and is responsible for
     *  computing the actual geometric bounds for the MappedNode. For example, MappedParent
     *  is written such that this function is the union of the transformed
     *  bounds of each child. Rectangle is written such that this takes into
     *  account the size and stroke. Text is written such that it is computed
     *  based on the actual glyphs.
     *
     *  There are two updateXXX functions, updateGeomBounds and updateTxBounds.
     *  These functions are for ensuring that geomBounds and txBounds are
     *  valid. They only execute in the case of the cached value being invalid,
     *  so the function call is very cheap in cases where the cached bounds
     *  values are still valid.
     */

    
    private BaseTransform localToParentTx = BaseTransform.IDENTITY_TRANSFORM;

    
    private boolean transformDirty = true;

    
    private BaseBounds txBounds = new RectBounds();

    
    private BaseBounds geomBounds = new RectBounds();

    
    private BaseBounds localBounds = null;

    
    boolean boundsChanged;

    /*
     * Returns geometric bounds, but may be over-ridden by a subclass.
     */
    private Bounds doComputeLayoutBounds() {
        BaseBounds tempBounds = TempState.getInstance().bounds;
        tempBounds = getGeomBounds(tempBounds,
                BaseTransform.IDENTITY_TRANSFORM);
        return new BoundingBox(tempBounds.getMinX(),
                tempBounds.getMinY(),
                tempBounds.getMinZ(),
                tempBounds.getWidth(),
                tempBounds.getHeight(),
                tempBounds.getDepth());
    }

    /*
     * Subclasses may customize the layoutBounds by means of overriding the
     * MappedNodeHelper.computeLayoutBoundsImpl method. If the layout bounds need to be
     * recomputed, the subclass must notify the MappedNode implementation of this
     * fact so that appropriate notifications and internal state can be
     * kept in sync. Subclasses must call MappedNodeHelper.layoutBoundsChanged to
     * let MappedNode know that the layout bounds are invalid and need to be
     * recomputed.
     */
    final void layoutBoundsChanged() {
        if (!layoutBounds.valid) {
            return;
        }
        layoutBounds.invalidate();
        if ((nodeTransformation != null && nodeTransformation.hasScaleOrRotate()) || hasMirroring()) {
            // if either the scale or rotate convenience variables are used,
            // then we need a valid pivot point. Since the layoutBounds
            // affects the pivot we need to invalidate the transform
            MappedNodeHelper.transformsChanged(this);
        }
    }

    
    BaseBounds getTransformedBounds(BaseBounds bounds, BaseTransform tx) {
        updateLocalToParentTransform();
        if (tx.isTranslateOrIdentity()) {
            updateTxBounds();
            bounds = bounds.deriveWithNewBounds(txBounds);
            if (!tx.isIdentity()) {
                final double translateX = tx.getMxt();
                final double translateY = tx.getMyt();
                final double translateZ = tx.getMzt();
                bounds = bounds.deriveWithNewBounds(
                        (float) (bounds.getMinX() + translateX),
                        (float) (bounds.getMinY() + translateY),
                        (float) (bounds.getMinZ() + translateZ),
                        (float) (bounds.getMaxX() + translateX),
                        (float) (bounds.getMaxY() + translateY),
                        (float) (bounds.getMaxZ() + translateZ));
            }
            return bounds;
        } else if (localToParentTx.isIdentity()) {
            return getLocalBounds(bounds, tx);
        } else {
            double mxx = tx.getMxx();
            double mxy = tx.getMxy();
            double mxz = tx.getMxz();
            double mxt = tx.getMxt();
            double myx = tx.getMyx();
            double myy = tx.getMyy();
            double myz = tx.getMyz();
            double myt = tx.getMyt();
            double mzx = tx.getMzx();
            double mzy = tx.getMzy();
            double mzz = tx.getMzz();
            double mzt = tx.getMzt();
            BaseTransform boundsTx = tx.deriveWithConcatenation(localToParentTx);
            bounds = getLocalBounds(bounds, boundsTx);
            if (boundsTx == tx) {
                tx.restoreTransform(mxx, mxy, mxz, mxt,
                        myx, myy, myz, myt,
                        mzx, mzy, mzz, mzt);
            }
            return bounds;
        }
    }

    
    BaseBounds getLocalBounds(BaseBounds bounds, BaseTransform tx) {
        if (getEffect() == null && getClip() == null) {
            return getGeomBounds(bounds, tx);
        }

        if (tx.isTranslateOrIdentity()) {
            // we can take a fast path since we know tx is either a simple
            // translation or is identity
            updateLocalBounds();
            bounds = bounds.deriveWithNewBounds(localBounds);
            if (!tx.isIdentity()) {
                double translateX = tx.getMxt();
                double translateY = tx.getMyt();
                double translateZ = tx.getMzt();
                bounds = bounds.deriveWithNewBounds((float) (bounds.getMinX() + translateX),
                        (float) (bounds.getMinY() + translateY),
                        (float) (bounds.getMinZ() + translateZ),
                        (float) (bounds.getMaxX() + translateX),
                        (float) (bounds.getMaxY() + translateY),
                        (float) (bounds.getMaxZ() + translateZ));
            }
            return bounds;
        } else if (tx.is2D()
                && (tx.getType()
                & ~(BaseTransform.TYPE_UNIFORM_SCALE | BaseTransform.TYPE_TRANSLATION
                | BaseTransform.TYPE_FLIP | BaseTransform.TYPE_QUADRANT_ROTATION)) != 0) {
            // this is a non-uniform scale / non-quadrant rotate / skew transform
            return computeLocalBounds(bounds, tx);
        } else {
            // 3D transformations and
            // selected 2D transformations (uniform transform, flip, quadrant rotation).
            // These 2D transformation will yield tight bounds when applied on the pre-computed
            // geomBounds
            // Note: Transforming the local bounds into a 3D space will yield a bounds
            // that isn't as tight as transforming its geometry and compute it bounds.
            updateLocalBounds();
            return tx.transform(localBounds, bounds);
        }
    }

    
    BaseBounds getGeomBounds(BaseBounds bounds, BaseTransform tx) {
        if (tx.isTranslateOrIdentity()) {
            // we can take a fast path since we know tx is either a simple
            // translation or is identity
            updateGeomBounds();
            bounds = bounds.deriveWithNewBounds(geomBounds);
            if (!tx.isIdentity()) {
                double translateX = tx.getMxt();
                double translateY = tx.getMyt();
                double translateZ = tx.getMzt();
                bounds = bounds.deriveWithNewBounds((float) (bounds.getMinX() + translateX),
                        (float) (bounds.getMinY() + translateY),
                        (float) (bounds.getMinZ() + translateZ),
                        (float) (bounds.getMaxX() + translateX),
                        (float) (bounds.getMaxY() + translateY),
                        (float) (bounds.getMaxZ() + translateZ));
            }
            return bounds;
        } else if (tx.is2D()
                && (tx.getType()
                & ~(BaseTransform.TYPE_UNIFORM_SCALE | BaseTransform.TYPE_TRANSLATION
                | BaseTransform.TYPE_FLIP | BaseTransform.TYPE_QUADRANT_ROTATION)) != 0) {
            // this is a non-uniform scale / non-quadrant rotate / skew transform
            return MappedNodeHelper.computeGeomBounds(this, bounds, tx);
        } else {
            // 3D transformations and
            // selected 2D transformations (unifrom transform, flip, quadrant rotation).
            // These 2D transformation will yield tight bounds when applied on the pre-computed
            // geomBounds
            // Note: Transforming the local geomBounds into a 3D space will yield a bounds
            // that isn't as tight as transforming its geometry and compute it bounds.
            updateGeomBounds();
            return tx.transform(geomBounds, bounds);
        }
    }

    
    void updateGeomBounds() {
        if (geomBoundsInvalid) {
            geomBounds = MappedNodeHelper.computeGeomBounds(this, geomBounds, BaseTransform.IDENTITY_TRANSFORM);
            geomBoundsInvalid = false;
        }
    }

    
    private BaseBounds computeLocalBounds(BaseBounds bounds, BaseTransform tx) {
        // We either get the bounds of the effect (if it isn't null)
        // or we get the geom bounds (if effect is null). We will then
        // intersect this with the clip.
        if (getEffect() != null) {
            BaseBounds b = EffectHelper.getBounds(getEffect(), bounds, tx, this, boundsAccessor);
            bounds = bounds.deriveWithNewBounds(b);
        } else {
            bounds = getGeomBounds(bounds, tx);
        }
        // intersect with the clip. Take care with "bounds" as it may
        // actually be TEMP_BOUNDS, so we save off state
        if (getClip() != null
                // FIXME: All 3D picking is currently ignored by rendering.
                // Until this is fixed or defined differently (RT-28510),
                // we follow this behavior.
                && !(this instanceof Shape3D) && !(getClip() instanceof Shape3D)) {
            double x1 = bounds.getMinX();
            double y1 = bounds.getMinY();
            double x2 = bounds.getMaxX();
            double y2 = bounds.getMaxY();
            double z1 = bounds.getMinZ();
            double z2 = bounds.getMaxZ();
            bounds = getClip().getTransformedBounds(bounds, tx);
            bounds.intersectWith((float)x1, (float)y1, (float)z1,
                    (float)x2, (float)y2, (float)z2);
        }
        return bounds;
    }


    
    private void updateLocalBounds() {
        if (localBoundsInvalid) {
            if (getClip() != null || getEffect() != null) {
                localBounds = computeLocalBounds(
                        localBounds == null ? new RectBounds() : localBounds,
                        BaseTransform.IDENTITY_TRANSFORM);
            } else {
                localBounds = null;
            }
            localBoundsInvalid = false;
        }
    }

    
    void updateTxBounds() {
        if (txBoundsInvalid) {
            updateLocalToParentTransform();
            txBounds = getLocalBounds(txBounds, localToParentTx);
            txBoundsInvalid = false;
        }
    }

    /*
     *                   Bounds Invalidation And Notification
     *
     *  The goal of this section is to efficiently propagate bounds
     *  invalidation through the scenegraph while also being semantically
     *  correct.
     *
     *  The code path for invalidation of layout bounds is somewhat confusing
     *  primarily due to performance enhancements and the desire to reduce the
     *  number of requestLayout() calls that are performed when layout bounds
     *  change. Before diving into layout bounds, I will first describe how
     *  normal bounds invalidation occurs.
     *
     *  When a node's geometry changes (for example, if the width of a
     *  Rectangle is changed) then the MappedNode must call MappedNodeHelper.geomChanged().
     *  Invoking this function will eventually clear all cached bounds and
     *  notify to each parent up the tree that their bounds may have changed.
     *
     *  After invalidating geomBounds (and after kicking off layout bounds
     *  notification), MappedNodeHelper.geomChanged calls localBoundsChanged(). It should
     *  be noted that MappedNodeHelper.geomChanged should only be called when the geometry
     *  of the node has changed such that it may result in the geom bounds
     *  actually changing.
     *
     *  localBoundsChanged() simply invalidates boundsInLocal and then calls
     *  transformedBoundsChanged().
     *
     *  transformedBoundsChanged() is responsible for invalidating
     *  boundsInParent and txBounds. If the MappedNode is not visible, then there is
     *  no need to notify the parent of the bounds change because the parent's
     *  bounds do not include invisible nodes. If the node is visible, then
     *  it must tell the parent that this child node's bounds have changed.
     *  It is up to the parent to eventually invoke its own MappedNodeHelper.geomChanged
     *  function. If instead of a parent this node has a clipParent, then the
     *  clipParent's localBoundsChanged() is called instead.
     *
     *  There are a few other ways in which we enter the invalidate steps
     *  beyond just the geometry changes. If the visibility of a MappedNode changes,
     *  its own bounds are not affected but its parent's bounds are. So a
     *  special call to parent.childVisibilityChanged is made so the parent
     *  can react accordingly.
     *
     *  If a transform is changed (layoutX, layoutY, rotate, transforms, etc)
     *  then the transform must be invalidated. When a transform is invalidated,
     *  it must also invalidate the txBounds by invoking
     *  transformedBoundsChanged, which will in turn notify the parent as
     *  before.
     *
     *  If an effect is changed or replaced then the local bounds must be
     *  invalidated, as well as the transformedBounds and the parent notified
     *  of the change in bounds.
     *
     *  layoutBound is somewhat unique in that it can be redefined in
     *  subclasses. By default, the layoutBounds is the geomBounds, and so
     *  whenever the geomBounds() function is called the layoutBounds
     *  must be invalidated. However in subclasses, especially Resizables,
     *  the layout bounds may not be defined to be the same as the geometric
     *  bounds. This is both useful and provides a very nice performance
     *  optimization for regions and controls. In this case, subclasses
     *  need some way to interpose themselves such that a call to
     *  MappedNodeHelper.geomChanged() *does not* invalidate the layout bounds.
     *
     *  This interposition happens by providing the
     *  MappedNodeHelper.notifyLayoutBoundsChanged function. The default implementation
     *  simply invalidates boundsInLocal. Subclasses (such as Region and
     *  Control) can override this function so that it does not invalidate
     *  the layout bounds.
     *
     *  An on invalidate trigger on layoutBounds handles kicking off the rest
     *  of the invalidate process for layoutBounds. Because the layout bounds
     *  define the pivot point, if scaleX, scaleY, or rotate contain
     *  non-identity values then whenever the layoutBounds change the
     *  transformed bounds also change. Finally, if this node's parent is
     *  a Region and if the MappedNode is being managed by the Region, then
     *  we must call requestLayout on the Region whenever the layout bounds
     *  have changed.
     */

    /*
     * Invoked by subclasses whenever their geometric bounds have changed.
     * Because the default layout bounds is based on the node geometry, this
     * function will invoke MappedNodeHelper.notifyLayoutBoundsChanged. The default
     * implementation of MappedNodeHelper.notifyLayoutBoundsChanged() will simply invalidate
     * layoutBounds. Resizable subclasses will want to override this function
     * in most cases to be a no-op.
     *
     * This function will also invalidate the cached geom bounds, and then
     * invoke localBoundsChanged() which will eventually end up invoking a
     * chain of functions up the tree to ensure that each parent of this
     * MappedNode is notified that its bounds may have also changed.
     *
     * This function should be treated as though it were final. It is not
     * intended to be overridden by subclasses.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doGeomChanged() {
        if (geomBoundsInvalid) {
            // GeomBoundsInvalid is false when node geometry changed and
            // the untransformed node bounds haven't been recalculated yet.
            // Most of the time, the recalculation of layout and transformed
            // node bounds don't require validation of untransformed bounds
            // and so we can not skip the following notifications.
            MappedNodeHelper.notifyLayoutBoundsChanged(this);
            transformedBoundsChanged();
            return;
        }
        geomBounds.makeEmpty();
        geomBoundsInvalid = true;
        MappedNodeHelper.markDirty(this, DirtyBits.NODE_BOUNDS);
        MappedNodeHelper.notifyLayoutBoundsChanged(this);
        localBoundsChanged();
    }

    private boolean geomBoundsInvalid = true;
    private boolean localBoundsInvalid = true;
    private boolean txBoundsInvalid = true;

    
    void localBoundsChanged() {
        localBoundsInvalid = true;
        invalidateBoundsInLocal();
        transformedBoundsChanged();
    }

    
    void transformedBoundsChanged() {
        if (!txBoundsInvalid) {
            txBounds.makeEmpty();
            txBoundsInvalid = true;
            invalidateBoundsInParent();
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORMED_BOUNDS);
        }
        if (isVisible()) {
            notifyParentOfBoundsChange();
        }
    }

    /*
     * Invoked by geomChanged(). Since layoutBounds is by default based
     * on the geometric bounds, the default implementation of this function will
     * invalidate the layoutBounds. Resizable MappedNode subclasses generally base
     * layoutBounds on the width/height instead of the geometric bounds, and so
     * will generally want to override this function to be a no-op.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doNotifyLayoutBoundsChanged() {
        layoutBoundsChanged();
        // notify the parent
        // MappedGroup instanceof check a little hoaky, but it allows us to disable
        // unnecessary layout for the case of a non-resizable within a group
        MappedParent p = getParent();

        // Need to propagate layout if parent isn't part of performing layout
        if (isManaged() && (p != null) && !(p instanceof MappedGroup && !isResizable())
                && !p.isPerformingLayout()) {
            // Force its parent to fix the layout since it is a managed child.
            p.requestLayout(true);
        }
    }

    
    void notifyParentOfBoundsChange() {
        // let the parent know which node has changed and the parent will
        // deal with marking itself invalid correctly
        MappedParent p = getParent();
        if (p != null) {
            p.childBoundsChanged(this);
        }
        // since the clip is used to compute the local bounds (and not the
        // geom bounds), we just need to notify that local bounds on the
        // clip parent have changed
        if (clipParent != null) {
            clipParent.localBoundsChanged();
        }
    }

    /* *************************************************************************
     *                                                                         *
     * Geometry and coordinate system related APIs. For example, methods       *
     * related to containment, intersection, coordinate space conversion, etc. *
     *                                                                         *
     **************************************************************************/

    
    public boolean contains(double localX, double localY) {
        if (containsBounds(localX, localY)) {
            return (isPickOnBounds() || MappedNodeHelper.computeContains(this, localX, localY));
        }
        return false;
    }

    /*
     * This method only does the contains check based on the bounds, clip and
     * effect of this node, excluding its shape (or geometry).
     *
     * Returns true if the given point (specified in the local
     * coordinate space of this {@code MappedNode}) is contained within the bounds,
     * clip and effect of this node.
     */
    private boolean containsBounds(double localX, double localY) {
        final TempState tempState = TempState.getInstance();
        BaseBounds tempBounds = tempState.bounds;

        // first, we do a quick test to see if the point is contained in
        // our local bounds. If so, then we will go the next step and check
        // the clip, effect, and geometry for containment.
        tempBounds = getLocalBounds(tempBounds,
                BaseTransform.IDENTITY_TRANSFORM);
        if (tempBounds.contains((float)localX, (float)localY)) {
            // if the clip is defined, then check it for containment, being
            // sure to convert from this node's local coordinate system
            // to the local coordinate system of the clip node
            if (getClip() != null) {
                tempState.point.x = (float)localX;
                tempState.point.y = (float)localY;
                try {
                    getClip().parentToLocal(tempState.point);
                } catch (NoninvertibleTransformException e) {
                    return false;
                }
                if (!getClip().contains(tempState.point.x, tempState.point.y)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    
    public boolean contains(Point2D localPoint) {
        return contains(localPoint.getX(), localPoint.getY());
    }

    
    public boolean intersects(double localX, double localY, double localWidth, double localHeight) {
        BaseBounds tempBounds = TempState.getInstance().bounds;
        tempBounds = getLocalBounds(tempBounds,
                BaseTransform.IDENTITY_TRANSFORM);
        return tempBounds.intersects((float)localX,
                (float)localY,
                (float)localWidth,
                (float)localHeight);
    }

    
    public boolean intersects(Bounds localBounds) {
        return intersects(localBounds.getMinX(), localBounds.getMinY(), localBounds.getWidth(), localBounds.getHeight());
    }

    
    public Point2D screenToLocal(double screenX, double screenY) {
        MappedScene scene = getScene();
        if (scene == null) return null;
        MappedWindow window = scene.getWindow();
        if (window == null) return null;

        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;

        tempPt.setLocation((float)(screenX - scene.getX() - window.getX()),
                (float)(screenY - scene.getY() - window.getY()));

        final MappedSubScene subScene = getSubScene();
        if (subScene != null) {
            final Point2D ssCoord = SceneUtils.sceneToSubScenePlane(subScene,
                    new Point2D(tempPt.x, tempPt.y));
            if (ssCoord == null) {
                return null;
            }
            tempPt.setLocation((float) ssCoord.getX(), (float) ssCoord.getY());
        }

        final Point3D ppIntersect =
                scene.getEffectiveCamera().pickProjectPlane(tempPt.x, tempPt.y);
        tempPt.setLocation((float) ppIntersect.getX(), (float) ppIntersect.getY());

        try {
            sceneToLocal(tempPt);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
        return new Point2D(tempPt.x, tempPt.y);
    }

    
    public Point2D screenToLocal(Point2D screenPoint) {
        return screenToLocal(screenPoint.getX(), screenPoint.getY());
    }

    
    public Bounds screenToLocal(Bounds screenBounds) {
        final Point2D p1 = screenToLocal(screenBounds.getMinX(), screenBounds.getMinY());
        final Point2D p2 = screenToLocal(screenBounds.getMinX(), screenBounds.getMaxY());
        final Point2D p3 = screenToLocal(screenBounds.getMaxX(), screenBounds.getMinY());
        final Point2D p4 = screenToLocal(screenBounds.getMaxX(), screenBounds.getMaxY());

        return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
    }


    
    public Point2D sceneToLocal(double x, double y, boolean rootScene) {
        if (!rootScene) {
            return sceneToLocal(x, y);
        }
        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;

        tempPt.setLocation((float)(x), (float)y);

        final MappedSubScene subScene = getSubScene();
        if (subScene != null) {
            final Point2D ssCoord = SceneUtils.sceneToSubScenePlane(subScene,
                    new Point2D(tempPt.x, tempPt.y));
            if (ssCoord == null) {
                return null;
            }
            tempPt.setLocation((float) ssCoord.getX(), (float) ssCoord.getY());
        }

        try {
            sceneToLocal(tempPt);
            return new Point2D(tempPt.x, tempPt.y);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    
    public Point2D sceneToLocal(Point2D point, boolean rootScene) {
        return sceneToLocal(point.getX(), point.getY(), rootScene);
    }

    
    public Bounds sceneToLocal(Bounds bounds, boolean rootScene) {
        if (!rootScene) {
            return sceneToLocal(bounds);
        }
        if (bounds.getMinZ() != 0 || bounds.getMaxZ() != 0) {
            return null;
        }
        final Point2D p1 = sceneToLocal(bounds.getMinX(), bounds.getMinY(), true);
        final Point2D p2 = sceneToLocal(bounds.getMinX(), bounds.getMaxY(), true);
        final Point2D p3 = sceneToLocal(bounds.getMaxX(), bounds.getMinY(), true);
        final Point2D p4 = sceneToLocal(bounds.getMaxX(), bounds.getMaxY(), true);

        return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
    }

    
    public Point2D sceneToLocal(double sceneX, double sceneY) {
        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;
        tempPt.setLocation((float)sceneX, (float)sceneY);
        try {
            sceneToLocal(tempPt);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
        return new Point2D(tempPt.x, tempPt.y);
    }

    
    public Point2D sceneToLocal(Point2D scenePoint) {
        return sceneToLocal(scenePoint.getX(), scenePoint.getY());
    }

    
    public Point3D sceneToLocal(Point3D scenePoint) {
        return sceneToLocal(scenePoint.getX(), scenePoint.getY(), scenePoint.getZ());
    }

    
    public Point3D sceneToLocal(double sceneX, double sceneY, double sceneZ) {
        try {
            return sceneToLocal0(sceneX, sceneY, sceneZ);
        } catch (NoninvertibleTransformException ex) {
            return null;
        }
    }

    
    private Point3D sceneToLocal0(double x, double y, double z) throws NoninvertibleTransformException {
        final com.sun.javafx.geom.Vec3d tempV3D =
                TempState.getInstance().vec3d;
        tempV3D.set(x, y, z);
        sceneToLocal(tempV3D);
        return new Point3D(tempV3D.x, tempV3D.y, tempV3D.z);
    }

    
    public Bounds sceneToLocal(Bounds sceneBounds) {
        // Do a quick update of localToParentTransform so that we can determine
        // if this tx is 2D transform
        updateLocalToParentTransform();
        if (localToParentTx.is2D() && (sceneBounds.getMinZ() == 0) && (sceneBounds.getMaxZ() == 0)) {
            Point2D p1 = sceneToLocal(sceneBounds.getMinX(), sceneBounds.getMinY());
            Point2D p2 = sceneToLocal(sceneBounds.getMaxX(), sceneBounds.getMinY());
            Point2D p3 = sceneToLocal(sceneBounds.getMaxX(), sceneBounds.getMaxY());
            Point2D p4 = sceneToLocal(sceneBounds.getMinX(), sceneBounds.getMaxY());

            return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
        }
        try {
            Point3D p1 = sceneToLocal0(sceneBounds.getMinX(), sceneBounds.getMinY(), sceneBounds.getMinZ());
            Point3D p2 = sceneToLocal0(sceneBounds.getMinX(), sceneBounds.getMinY(), sceneBounds.getMaxZ());
            Point3D p3 = sceneToLocal0(sceneBounds.getMinX(), sceneBounds.getMaxY(), sceneBounds.getMinZ());
            Point3D p4 = sceneToLocal0(sceneBounds.getMinX(), sceneBounds.getMaxY(), sceneBounds.getMaxZ());
            Point3D p5 = sceneToLocal0(sceneBounds.getMaxX(), sceneBounds.getMaxY(), sceneBounds.getMinZ());
            Point3D p6 = sceneToLocal0(sceneBounds.getMaxX(), sceneBounds.getMaxY(), sceneBounds.getMaxZ());
            Point3D p7 = sceneToLocal0(sceneBounds.getMaxX(), sceneBounds.getMinY(), sceneBounds.getMinZ());
            Point3D p8 = sceneToLocal0(sceneBounds.getMaxX(), sceneBounds.getMinY(), sceneBounds.getMaxZ());
            return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    
    public Point2D localToScreen(double localX, double localY) {
        return localToScreen(localX, localY, 0.0);
    }

    
    public Point2D localToScreen(Point2D localPoint) {
        return localToScreen(localPoint.getX(), localPoint.getY());
    }

    
    public Point2D localToScreen(double localX, double localY, double localZ) {
        MappedScene scene = getScene();
        if (scene == null) return null;
        MappedWindow window = scene.getWindow();
        if (window == null) return null;

        Point3D pt = localToScene(localX, localY, localZ);
        final MappedSubScene subScene = getSubScene();
        if (subScene != null) {
            pt = SceneUtils.subSceneToScene(subScene, pt);
        }
        final Point2D projection = MappedCameraHelper.project(
                SceneHelper.getEffectiveCamera(getScene()), pt);

        return new Point2D(projection.getX() + scene.getX() + window.getX(),
                projection.getY() + scene.getY() + window.getY());
    }

    
    public Point2D localToScreen(Point3D localPoint) {
        return localToScreen(localPoint.getX(), localPoint.getY(), localPoint.getZ());
    }

    
    public Bounds localToScreen(Bounds localBounds) {
        final Point2D p1 = localToScreen(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMinZ());
        final Point2D p2 = localToScreen(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMaxZ());
        final Point2D p3 = localToScreen(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMinZ());
        final Point2D p4 = localToScreen(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMaxZ());
        final Point2D p5 = localToScreen(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMinZ());
        final Point2D p6 = localToScreen(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMaxZ());
        final Point2D p7 = localToScreen(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMinZ());
        final Point2D p8 = localToScreen(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMaxZ());

        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    
    public Point2D localToScene(double localX, double localY) {
        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;
        tempPt.setLocation((float)localX, (float)localY);
        localToScene(tempPt);
        return new Point2D(tempPt.x, tempPt.y);
    }

    
    public Point2D localToScene(Point2D localPoint) {
        return localToScene(localPoint.getX(), localPoint.getY());
    }

    
    public Point3D localToScene(Point3D localPoint) {
        return localToScene(localPoint.getX(), localPoint.getY(), localPoint.getZ());
    }

    
    public Point3D localToScene(double x, double y, double z) {
        final com.sun.javafx.geom.Vec3d tempV3D =
                TempState.getInstance().vec3d;
        tempV3D.set(x, y, z);
        localToScene(tempV3D);
        return new Point3D(tempV3D.x, tempV3D.y, tempV3D.z);
    }

    
    public Point3D localToScene(Point3D localPoint, boolean rootScene) {
        Point3D pt = localToScene(localPoint);
        if (rootScene) {
            final MappedSubScene subScene = getSubScene();
            if (subScene != null) {
                pt = SceneUtils.subSceneToScene(subScene, pt);
            }
        }
        return pt;
    }

    
    public Point3D localToScene(double x, double y, double z, boolean rootScene) {
        return localToScene(new Point3D(x, y, z), rootScene);
    }

    
    public Point2D localToScene(Point2D localPoint, boolean rootScene) {
        if (!rootScene) {
            return localToScene(localPoint);
        }
        Point3D pt = localToScene(localPoint.getX(), localPoint.getY(), 0, rootScene);
        return new Point2D(pt.getX(), pt.getY());
    }

    
    public Point2D localToScene(double x, double y, boolean rootScene) {
        return localToScene(new Point2D(x, y), rootScene);
    }

    
    public Bounds localToScene(Bounds localBounds, boolean rootScene) {
        if (!rootScene) {
            return localToScene(localBounds);
        }
        Point3D p1 = localToScene(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMinZ(), true);
        Point3D p2 = localToScene(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMaxZ(), true);
        Point3D p3 = localToScene(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMinZ(), true);
        Point3D p4 = localToScene(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMaxZ(), true);
        Point3D p5 = localToScene(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMinZ(), true);
        Point3D p6 = localToScene(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMaxZ(), true);
        Point3D p7 = localToScene(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMinZ(), true);
        Point3D p8 = localToScene(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMaxZ(), true);
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    
    public Bounds localToScene(Bounds localBounds) {
        // Do a quick update of localToParentTransform so that we can determine
        // if this tx is 2D transform
        updateLocalToParentTransform();
        if (localToParentTx.is2D() && (localBounds.getMinZ() == 0) && (localBounds.getMaxZ() == 0)) {
            Point2D p1 = localToScene(localBounds.getMinX(), localBounds.getMinY());
            Point2D p2 = localToScene(localBounds.getMaxX(), localBounds.getMinY());
            Point2D p3 = localToScene(localBounds.getMaxX(), localBounds.getMaxY());
            Point2D p4 = localToScene(localBounds.getMinX(), localBounds.getMaxY());

            return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
        }
        Point3D p1 = localToScene(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMinZ());
        Point3D p2 = localToScene(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMaxZ());
        Point3D p3 = localToScene(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMinZ());
        Point3D p4 = localToScene(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMaxZ());
        Point3D p5 = localToScene(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMinZ());
        Point3D p6 = localToScene(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMaxZ());
        Point3D p7 = localToScene(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMinZ());
        Point3D p8 = localToScene(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMaxZ());
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);

    }

    
    public Point2D parentToLocal(double parentX, double parentY) {
        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;
        tempPt.setLocation((float)parentX, (float)parentY);
        try {
            parentToLocal(tempPt);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
        return new Point2D(tempPt.x, tempPt.y);
    }

    
    public Point2D parentToLocal(Point2D parentPoint) {
        return parentToLocal(parentPoint.getX(), parentPoint.getY());
    }

    
    public Point3D parentToLocal(Point3D parentPoint) {
        return parentToLocal(parentPoint.getX(), parentPoint.getY(), parentPoint.getZ());
    }

    
    public Point3D parentToLocal(double parentX, double parentY, double parentZ) {
        final com.sun.javafx.geom.Vec3d tempV3D =
                TempState.getInstance().vec3d;
        tempV3D.set(parentX, parentY, parentZ);
        try {
            parentToLocal(tempV3D);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
        return new Point3D(tempV3D.x, tempV3D.y, tempV3D.z);
    }

    
    public Bounds parentToLocal(Bounds parentBounds) {
        // Do a quick update of localToParentTransform so that we can determine
        // if this tx is 2D transform
        updateLocalToParentTransform();
        if (localToParentTx.is2D() && (parentBounds.getMinZ() == 0) && (parentBounds.getMaxZ() == 0)) {
            Point2D p1 = parentToLocal(parentBounds.getMinX(), parentBounds.getMinY());
            Point2D p2 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMinY());
            Point2D p3 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMaxY());
            Point2D p4 = parentToLocal(parentBounds.getMinX(), parentBounds.getMaxY());

            return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
        }
        Point3D p1 = parentToLocal(parentBounds.getMinX(), parentBounds.getMinY(), parentBounds.getMinZ());
        Point3D p2 = parentToLocal(parentBounds.getMinX(), parentBounds.getMinY(), parentBounds.getMaxZ());
        Point3D p3 = parentToLocal(parentBounds.getMinX(), parentBounds.getMaxY(), parentBounds.getMinZ());
        Point3D p4 = parentToLocal(parentBounds.getMinX(), parentBounds.getMaxY(), parentBounds.getMaxZ());
        Point3D p5 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMaxY(), parentBounds.getMinZ());
        Point3D p6 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMaxY(), parentBounds.getMaxZ());
        Point3D p7 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMinY(), parentBounds.getMinZ());
        Point3D p8 = parentToLocal(parentBounds.getMaxX(), parentBounds.getMinY(), parentBounds.getMaxZ());
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    
    public Point2D localToParent(double localX, double localY) {
        final com.sun.javafx.geom.Point2D tempPt =
                TempState.getInstance().point;
        tempPt.setLocation((float)localX, (float)localY);
        localToParent(tempPt);
        return new Point2D(tempPt.x, tempPt.y);
    }

    
    public Point2D localToParent(Point2D localPoint) {
        return localToParent(localPoint.getX(), localPoint.getY());
    }

    
    public Point3D localToParent(Point3D localPoint) {
        return localToParent(localPoint.getX(), localPoint.getY(), localPoint.getZ());
    }

    
    public Point3D localToParent(double x, double y, double z) {
        final com.sun.javafx.geom.Vec3d tempV3D =
                TempState.getInstance().vec3d;
        tempV3D.set(x, y, z);
        localToParent(tempV3D);
        return new Point3D(tempV3D.x, tempV3D.y, tempV3D.z);
    }

    
    public Bounds localToParent(Bounds localBounds) {
        // Do a quick update of localToParentTransform so that we can determine
        // if this tx is 2D transform
        updateLocalToParentTransform();
        if (localToParentTx.is2D() && (localBounds.getMinZ() == 0) && (localBounds.getMaxZ() == 0)) {
            Point2D p1 = localToParent(localBounds.getMinX(), localBounds.getMinY());
            Point2D p2 = localToParent(localBounds.getMaxX(), localBounds.getMinY());
            Point2D p3 = localToParent(localBounds.getMaxX(), localBounds.getMaxY());
            Point2D p4 = localToParent(localBounds.getMinX(), localBounds.getMaxY());

            return BoundsUtils.createBoundingBox(p1, p2, p3, p4);
        }
        Point3D p1 = localToParent(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMinZ());
        Point3D p2 = localToParent(localBounds.getMinX(), localBounds.getMinY(), localBounds.getMaxZ());
        Point3D p3 = localToParent(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMinZ());
        Point3D p4 = localToParent(localBounds.getMinX(), localBounds.getMaxY(), localBounds.getMaxZ());
        Point3D p5 = localToParent(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMinZ());
        Point3D p6 = localToParent(localBounds.getMaxX(), localBounds.getMaxY(), localBounds.getMaxZ());
        Point3D p7 = localToParent(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMinZ());
        Point3D p8 = localToParent(localBounds.getMaxX(), localBounds.getMinY(), localBounds.getMaxZ());
        return BoundsUtils.createBoundingBox(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    
    BaseTransform getLocalToParentTransform(BaseTransform tx) {
        updateLocalToParentTransform();
        tx.setTransform(localToParentTx);
        return tx;
    }

    /*
     * Currently used only by PathTransition
     */
    final BaseTransform getLeafTransform() {
        return getLocalToParentTransform(TempState.getInstance().leafTx);
    }

    /*
     * Invoked whenever the transforms[] ObservableList changes, or by the transforms
     * in that ObservableList whenever they are changed.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doTransformsChanged() {
        if (!transformDirty) {
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_TRANSFORM);
            transformDirty = true;
            transformedBoundsChanged();
        }
        invalidateLocalToParentTransform();
        invalidateLocalToSceneTransform();
    }

    final double getPivotX() {
        final Bounds bounds = getLayoutBounds();
        return bounds.getMinX() + bounds.getWidth()/2;
    }

    final double getPivotY() {
        final Bounds bounds = getLayoutBounds();
        return bounds.getMinY() + bounds.getHeight()/2;
    }

    final double getPivotZ() {
        final Bounds bounds = getLayoutBounds();
        return bounds.getMinZ() + bounds.getDepth()/2;
    }

    
    void updateLocalToParentTransform() {
        if (transformDirty) {
            localToParentTx.setToIdentity();

            boolean mirror = false;
            double mirroringCenter = 0;
            if (hasMirroring()) {
                final MappedScene sceneValue = getScene();
                if ((sceneValue != null) && (sceneValue.getRoot() == this)) {
                    // handle scene mirroring in this branch
                    // (must be the last transformation)
                    mirroringCenter = sceneValue.getWidth() / 2;
                    if (mirroringCenter == 0.0) {
                        mirroringCenter = getPivotX();
                    }

                    localToParentTx = localToParentTx.deriveWithTranslation(
                            mirroringCenter, 0.0);
                    localToParentTx = localToParentTx.deriveWithScale(
                            -1.0, 1.0, 1.0);
                    localToParentTx = localToParentTx.deriveWithTranslation(
                            -mirroringCenter, 0.0);
                } else {
                    // mirror later
                    mirror = true;
                    mirroringCenter = getPivotX();
                }
            }

            if (getScaleX() != 1 || getScaleY() != 1 || getScaleZ() != 1 || getRotate() != 0) {
                // recompute pivotX, pivotY and pivotZ
                double pivotX = getPivotX();
                double pivotY = getPivotY();
                double pivotZ = getPivotZ();

                localToParentTx = localToParentTx.deriveWithTranslation(
                        getTranslateX() + getLayoutX() + pivotX,
                        getTranslateY() + getLayoutY() + pivotY,
                        getTranslateZ() + pivotZ);
                localToParentTx = localToParentTx.deriveWithRotation(
                        Math.toRadians(getRotate()), getRotationAxis().getX(),
                        getRotationAxis().getY(), getRotationAxis().getZ());
                localToParentTx = localToParentTx.deriveWithScale(
                        getScaleX(), getScaleY(), getScaleZ());
                localToParentTx = localToParentTx.deriveWithTranslation(
                        -pivotX, -pivotY, -pivotZ);
            } else {
                localToParentTx = localToParentTx.deriveWithTranslation(
                        getTranslateX() + getLayoutX(),
                        getTranslateY() + getLayoutY(),
                        getTranslateZ());
            }

            if (hasTransforms()) {
                for (Transform t : getTransforms()) {
                    localToParentTx = TransformHelper.derive(t, localToParentTx);
                }
            }

            // Check to see whether the node requires mirroring
            if (mirror) {
                localToParentTx = localToParentTx.deriveWithTranslation(
                        mirroringCenter, 0);
                localToParentTx = localToParentTx.deriveWithScale(
                        -1.0, 1.0, 1.0);
                localToParentTx = localToParentTx.deriveWithTranslation(
                        -mirroringCenter, 0);
            }

            transformDirty = false;
        }
    }

    
    void parentToLocal(com.sun.javafx.geom.Point2D pt) throws NoninvertibleTransformException {
        updateLocalToParentTransform();
        localToParentTx.inverseTransform(pt, pt);
    }

    void parentToLocal(com.sun.javafx.geom.Vec3d pt) throws NoninvertibleTransformException {
        updateLocalToParentTransform();
        localToParentTx.inverseTransform(pt, pt);
    }

    void sceneToLocal(com.sun.javafx.geom.Point2D pt) throws NoninvertibleTransformException {
        if (getParent() != null) {
            getParent().sceneToLocal(pt);
        }
        parentToLocal(pt);
    }

    void sceneToLocal(com.sun.javafx.geom.Vec3d pt) throws NoninvertibleTransformException {
        if (getParent() != null) {
            getParent().sceneToLocal(pt);
        }
        parentToLocal(pt);
    }

    void localToScene(com.sun.javafx.geom.Point2D pt) {
        localToParent(pt);
        if (getParent() != null) {
            getParent().localToScene(pt);
        }
    }

    void localToScene(com.sun.javafx.geom.Vec3d pt) {
        localToParent(pt);
        if (getParent() != null) {
            getParent().localToScene(pt);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * Mouse event related APIs                                                *
     *                                                                         *
     **************************************************************************/

    
    void localToParent(com.sun.javafx.geom.Point2D pt) {
        updateLocalToParentTransform();
        localToParentTx.transform(pt, pt);
    }

    void localToParent(com.sun.javafx.geom.Vec3d pt) {
        updateLocalToParentTransform();
        localToParentTx.transform(pt, pt);
    }

    /*
     * Finds a top-most child node that contains the given local coordinates.
     *
     * The result argument is used for storing the picking result.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doPickNodeLocal(PickRay localPickRay, PickResultChooser result) {
        intersects(localPickRay, result);
    }

    /*
     * Finds a top-most child node that intersects the given ray.
     *
     * The result argument is used for storing the picking result.
     */
    final void pickNode(PickRay pickRay, PickResultChooser result) {

        // In some conditions we can omit picking this node or subgraph
        if (!isVisible() || isDisable() || isMouseTransparent()) {
            return;
        }

        final Vec3d o = pickRay.getOriginNoClone();
        final double ox = o.x;
        final double oy = o.y;
        final double oz = o.z;
        final Vec3d d = pickRay.getDirectionNoClone();
        final double dx = d.x;
        final double dy = d.y;
        final double dz = d.z;

        updateLocalToParentTransform();
        try {
            localToParentTx.inverseTransform(o, o);
            localToParentTx.inverseDeltaTransform(d, d);

            // Delegate to a function which can be overridden by subclasses which
            // actually does the pick. The implementation is markedly different
            // for leaf nodes vs. parent nodes vs. region nodes.
            MappedNodeHelper.pickNodeLocal(this, pickRay, result);
        } catch (NoninvertibleTransformException e) {
            // in this case we just don't pick anything
        }

        pickRay.setOrigin(ox, oy, oz);
        pickRay.setDirection(dx, dy, dz);
    }

    /*
     * Returns {@code true} if the given ray (start, dir), specified in the
     * local coordinate space of this {@code MappedNode}, intersects the
     * shape of this {@code MappedNode}. Note that this method does not take visibility
     * into account; the test is based on the geometry of this {@code MappedNode} only.
     * <p>
     * The pickResult is updated if the found intersection is closer than
     * the currently held one.
     * <p>
     * Note that this is a conditional feature. See
     * {@link javafx.application.ConditionalFeature#SCENE3D ConditionalFeature.SCENE3D}
     * for more information.
     */
    final boolean intersects(PickRay pickRay, PickResultChooser pickResult) {
        double boundsDistance = intersectsBounds(pickRay);
        if (!Double.isNaN(boundsDistance)) {
            if (isPickOnBounds()) {
                if (pickResult != null) {
                    pickResult.offer(this, boundsDistance, PickResultChooser.computePoint(pickRay, boundsDistance));
                }
                return true;
            } else {
                return MappedNodeHelper.computeIntersects(this, pickRay, pickResult);
            }
        }
        return false;
    }

    /*
     * Computes the intersection of the pickRay with this node.
     * The pickResult argument is updated if the found intersection
     * is closer than the passed one. On the other hand, the return value
     * specifies whether the intersection exists, regardless of its comparison
     * with the given pickResult.
     */
    private boolean doComputeIntersects(PickRay pickRay, PickResultChooser pickResult) {
        double origZ = pickRay.getOriginNoClone().z;
        double dirZ = pickRay.getDirectionNoClone().z;
        // Handle the case where pickRay is almost parallel to the Z-plane
        if (almostZero(dirZ)) {
            return false;
        }
        double t = -origZ / dirZ;
        if (t < pickRay.getNearClip() || t > pickRay.getFarClip()) {
            return false;
        }
        double x = pickRay.getOriginNoClone().x + (pickRay.getDirectionNoClone().x * t);
        double y = pickRay.getOriginNoClone().y + (pickRay.getDirectionNoClone().y * t);

        if (contains((float) x, (float) y)) {
            if (pickResult != null) {
                pickResult.offer(this, t, PickResultChooser.computePoint(pickRay, t));
            }
            return true;
        }
        return false;
    }

    /*
     * Computes the intersection of the pickRay with the bounds of this node.
     * The return value is the distance between the camera and the intersection
     * point, measured in pickRay direction magnitudes. If there is
     * no intersection, it returns NaN.
     *
     * @param pickRay The pick ray
     * @return Distance of the intersection point, a NaN if there
     *         is no intersection
     */
    final double intersectsBounds(PickRay pickRay) {

        final Vec3d dir = pickRay.getDirectionNoClone();
        double tmin, tmax;

        final Vec3d origin = pickRay.getOriginNoClone();
        final double originX = origin.x;
        final double originY = origin.y;
        final double originZ = origin.z;

        final TempState tempState = TempState.getInstance();
        BaseBounds tempBounds = tempState.bounds;

        tempBounds = getLocalBounds(tempBounds,
                BaseTransform.IDENTITY_TRANSFORM);

        if (dir.x == 0.0 && dir.y == 0.0) {
            // fast path for the usual 2D picking

            if (dir.z == 0.0) {
                return Double.NaN;
            }

            if (originX < tempBounds.getMinX() ||
                    originX > tempBounds.getMaxX() ||
                    originY < tempBounds.getMinY() ||
                    originY > tempBounds.getMaxY()) {
                return Double.NaN;
            }

            final double invDirZ = 1.0 / dir.z;
            final boolean signZ = invDirZ < 0.0;

            final double minZ = tempBounds.getMinZ();
            final double maxZ = tempBounds.getMaxZ();
            tmin = ((signZ ? maxZ : minZ) - originZ) * invDirZ;
            tmax = ((signZ ? minZ : maxZ) - originZ) * invDirZ;

        } else if (tempBounds.getDepth() == 0.0) {
            // fast path for 3D picking of 2D bounds

            if (almostZero(dir.z)) {
                return Double.NaN;
            }

            final double t = (tempBounds.getMinZ() - originZ) / dir.z;
            final double x = originX + (dir.x * t);
            final double y = originY + (dir.y * t);

            if (x < tempBounds.getMinX() ||
                    x > tempBounds.getMaxX() ||
                    y < tempBounds.getMinY() ||
                    y > tempBounds.getMaxY()) {
                return Double.NaN;
            }

            tmin = tmax = t;

        } else {

            final double invDirX = dir.x == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.x);
            final double invDirY = dir.y == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.y);
            final double invDirZ = dir.z == 0.0 ? Double.POSITIVE_INFINITY : (1.0 / dir.z);
            final boolean signX = invDirX < 0.0;
            final boolean signY = invDirY < 0.0;
            final boolean signZ = invDirZ < 0.0;
            final double minX = tempBounds.getMinX();
            final double minY = tempBounds.getMinY();
            final double maxX = tempBounds.getMaxX();
            final double maxY = tempBounds.getMaxY();

            tmin = Double.NEGATIVE_INFINITY;
            tmax = Double.POSITIVE_INFINITY;
            if (Double.isInfinite(invDirX)) {
                if (minX <= originX && maxX >= originX) {
                    // move on, we are inside for the whole length
                } else {
                    return Double.NaN;
                }
            } else {
                tmin = ((signX ? maxX : minX) - originX) * invDirX;
                tmax = ((signX ? minX : maxX) - originX) * invDirX;
            }

            if (Double.isInfinite(invDirY)) {
                if (minY <= originY && maxY >= originY) {
                    // move on, we are inside for the whole length
                } else {
                    return Double.NaN;
                }
            } else {
                final double tymin = ((signY ? maxY : minY) - originY) * invDirY;
                final double tymax = ((signY ? minY : maxY) - originY) * invDirY;

                if ((tmin > tymax) || (tymin > tmax)) {
                    return Double.NaN;
                }
                if (tymin > tmin) {
                    tmin = tymin;
                }
                if (tymax < tmax) {
                    tmax = tymax;
                }
            }

            final double minZ = tempBounds.getMinZ();
            final double maxZ = tempBounds.getMaxZ();
            if (Double.isInfinite(invDirZ)) {
                if (minZ <= originZ && maxZ >= originZ) {
                    // move on, we are inside for the whole length
                } else {
                    return Double.NaN;
                }
            } else {
                final double tzmin = ((signZ ? maxZ : minZ) - originZ) * invDirZ;
                final double tzmax = ((signZ ? minZ : maxZ) - originZ) * invDirZ;

                if ((tmin > tzmax) || (tzmin > tmax)) {
                    return Double.NaN;
                }
                if (tzmin > tmin) {
                    tmin = tzmin;
                }
                if (tzmax < tmax) {
                    tmax = tzmax;
                }
            }
        }

        // For clip we use following semantics: pick the node normally
        // if there is an intersection with the clip node. We don't consider
        // clip node distance.
        MappedNode clip = getClip();
        if (clip != null
                // FIXME: All 3D picking is currently ignored by rendering.
                // Until this is fixed or defined differently (RT-28510),
                // we follow this behavior.
                && !(this instanceof Shape3D) && !(clip instanceof Shape3D)) {
            final double dirX = dir.x;
            final double dirY = dir.y;
            final double dirZ = dir.z;

            clip.updateLocalToParentTransform();

            boolean hitClip = true;
            try {
                clip.localToParentTx.inverseTransform(origin, origin);
                clip.localToParentTx.inverseDeltaTransform(dir, dir);
            } catch (NoninvertibleTransformException e) {
                hitClip = false;
            }
            hitClip = hitClip && clip.intersects(pickRay, null);
            pickRay.setOrigin(originX, originY, originZ);
            pickRay.setDirection(dirX, dirY, dirZ);

            if (!hitClip) {
                return Double.NaN;
            }
        }

        if (Double.isInfinite(tmin) || Double.isNaN(tmin)) {
            // We've got a nonsense pick ray or bounds.
            return Double.NaN;
        }

        final double minDistance = pickRay.getNearClip();
        final double maxDistance = pickRay.getFarClip();
        if (tmin < minDistance) {
            if (tmax >= minDistance) {
                // we are inside bounds
                return 0.0;
            } else {
                return Double.NaN;
            }
        } else if (tmin > maxDistance) {
            return Double.NaN;
        }

        return tmin;
    }


    // Good to find a home for commonly use util. code such as EPS.
    // and almostZero. This code currently defined in multiple places,
    // such as Affine3D and GeneralTransform3D.
    private static final double EPSILON_ABSOLUTE = 1.0e-5;

    static boolean almostZero(double a) {
        return ((a < EPSILON_ABSOLUTE) && (a > -EPSILON_ABSOLUTE));
    }

    /* *************************************************************************
     *                                                                         *
     *                      viewOrder property handling                        *
     *                                                                         *
     **************************************************************************/

    
    public final DoubleProperty viewOrderProperty() {
        return getMiscProperties().viewOrderProperty();
    }

    public final void setViewOrder(double value) {
        viewOrderProperty().set(value);
    }

    public final double getViewOrder() {
        return (miscProperties == null) ? DEFAULT_VIEW_ORDER
                : miscProperties.getViewOrder();
    }

    /* *************************************************************************
     *                                                                         *
     *                             Transformations                             *
     *                                                                         *
     **************************************************************************/
    
    public final ObservableList<Transform> getTransforms() {
        return transformsProperty();
    }

    private ObservableList<Transform> transformsProperty() {
        return getNodeTransformation().getTransforms();
    }

    public final void setTranslateX(double value) {
        translateXProperty().set(value);
    }

    public final double getTranslateX() {
        return (nodeTransformation == null)
                ? DEFAULT_TRANSLATE_X
                : nodeTransformation.getTranslateX();
    }

    
    public final DoubleProperty translateXProperty() {
        return getNodeTransformation().translateXProperty();
    }

    public final void setTranslateY(double value) {
        translateYProperty().set(value);
    }

    public final double getTranslateY() {
        return (nodeTransformation == null)
                ? DEFAULT_TRANSLATE_Y
                : nodeTransformation.getTranslateY();
    }

    
    public final DoubleProperty translateYProperty() {
        return getNodeTransformation().translateYProperty();
    }

    public final void setTranslateZ(double value) {
        translateZProperty().set(value);
    }

    public final double getTranslateZ() {
        return (nodeTransformation == null)
                ? DEFAULT_TRANSLATE_Z
                : nodeTransformation.getTranslateZ();
    }

    
    public final DoubleProperty translateZProperty() {
        return getNodeTransformation().translateZProperty();
    }

    public final void setScaleX(double value) {
        scaleXProperty().set(value);
    }

    public final double getScaleX() {
        return (nodeTransformation == null) ? DEFAULT_SCALE_X
                : nodeTransformation.getScaleX();
    }

    
    public final DoubleProperty scaleXProperty() {
        return getNodeTransformation().scaleXProperty();
    }

    public final void setScaleY(double value) {
        scaleYProperty().set(value);
    }

    public final double getScaleY() {
        return (nodeTransformation == null) ? DEFAULT_SCALE_Y
                : nodeTransformation.getScaleY();
    }

    
    public final DoubleProperty scaleYProperty() {
        return getNodeTransformation().scaleYProperty();
    }

    public final void setScaleZ(double value) {
        scaleZProperty().set(value);
    }

    public final double getScaleZ() {
        return (nodeTransformation == null) ? DEFAULT_SCALE_Z
                : nodeTransformation.getScaleZ();
    }

    
    public final DoubleProperty scaleZProperty() {
        return getNodeTransformation().scaleZProperty();
    }

    public final void setRotate(double value) {
        rotateProperty().set(value);
    }

    public final double getRotate() {
        return (nodeTransformation == null) ? DEFAULT_ROTATE
                : nodeTransformation.getRotate();
    }

    
    public final DoubleProperty rotateProperty() {
        return getNodeTransformation().rotateProperty();
    }

    public final void setRotationAxis(Point3D value) {
        rotationAxisProperty().set(value);
    }

    public final Point3D getRotationAxis() {
        return (nodeTransformation == null)
                ? DEFAULT_ROTATION_AXIS
                : nodeTransformation.getRotationAxis();
    }

    
    public final ObjectProperty<Point3D> rotationAxisProperty() {
        return getNodeTransformation().rotationAxisProperty();
    }

    
    public final ReadOnlyObjectProperty<Transform> localToParentTransformProperty() {
        return getNodeTransformation().localToParentTransformProperty();
    }

    private void invalidateLocalToParentTransform() {
        if (nodeTransformation != null) {
            nodeTransformation.invalidateLocalToParentTransform();
        }
    }

    public final Transform getLocalToParentTransform() {
        return localToParentTransformProperty().get();
    }

    
    public final ReadOnlyObjectProperty<Transform> localToSceneTransformProperty() {
        return getNodeTransformation().localToSceneTransformProperty();
    }

    private void invalidateLocalToSceneTransform() {
        if (nodeTransformation != null) {
            nodeTransformation.invalidateLocalToSceneTransform();
        }
    }

    public final Transform getLocalToSceneTransform() {
        return localToSceneTransformProperty().get();
    }

    private NodeTransformation nodeTransformation;

    private NodeTransformation getNodeTransformation() {
        if (nodeTransformation == null) {
            nodeTransformation = new NodeTransformation();
        }

        return nodeTransformation;
    }

    private boolean hasTransforms() {
        return (nodeTransformation != null)
                && nodeTransformation.hasTransforms();
    }

    // for tests only
    Transform getCurrentLocalToSceneTransformState() {
        if (nodeTransformation == null ||
                nodeTransformation.localToSceneTransform == null) {
            return null;
        }

        return nodeTransformation.localToSceneTransform.transform;
    }

    private static final double DEFAULT_TRANSLATE_X = 0;
    private static final double DEFAULT_TRANSLATE_Y = 0;
    private static final double DEFAULT_TRANSLATE_Z = 0;
    private static final double DEFAULT_SCALE_X = 1;
    private static final double DEFAULT_SCALE_Y = 1;
    private static final double DEFAULT_SCALE_Z = 1;
    private static final double DEFAULT_ROTATE = 0;
    private static final Point3D DEFAULT_ROTATION_AXIS = Rotate.Z_AXIS;

    private final class NodeTransformation {
        private DoubleProperty translateX;
        private DoubleProperty translateY;
        private DoubleProperty translateZ;
        private DoubleProperty scaleX;
        private DoubleProperty scaleY;
        private DoubleProperty scaleZ;
        private DoubleProperty rotate;
        private ObjectProperty<Point3D> rotationAxis;
        private ObservableList<Transform> transforms;
        private LazyTransformProperty localToParentTransform;
        private LazyTransformProperty localToSceneTransform;
        private int listenerReasons = 0;
        private InvalidationListener localToSceneInvLstnr;

        private InvalidationListener getLocalToSceneInvalidationListener() {
            if (localToSceneInvLstnr == null) {
                localToSceneInvLstnr = observable -> invalidateLocalToSceneTransform();
            }
            return localToSceneInvLstnr;
        }

        public void incListenerReasons() {
            if (listenerReasons == 0) {
                MappedNode n = MappedNode.this.getParent();
                if (n != null) {
                    n.localToSceneTransformProperty().addListener(
                            getLocalToSceneInvalidationListener());
                }
            }
            listenerReasons++;
        }

        public void decListenerReasons() {
            listenerReasons--;
            if (listenerReasons == 0) {
                MappedNode n = MappedNode.this.getParent();
                if (n != null) {
                    n.localToSceneTransformProperty().removeListener(
                            getLocalToSceneInvalidationListener());
                }
                if (localToSceneTransform != null) {
                    localToSceneTransform.validityUnknown();
                }
            }
        }

        public final Transform getLocalToParentTransform() {
            return localToParentTransformProperty().get();
        }

        public final ReadOnlyObjectProperty<Transform> localToParentTransformProperty() {
            if (localToParentTransform == null) {
                localToParentTransform = new LazyTransformProperty() {
                    @Override
                    protected Transform computeTransform(Transform reuse) {
                        updateLocalToParentTransform();
                        return TransformUtils.immutableTransform(reuse,
                                localToParentTx.getMxx(), localToParentTx.getMxy(), localToParentTx.getMxz(), localToParentTx.getMxt(),
                                localToParentTx.getMyx(), localToParentTx.getMyy(), localToParentTx.getMyz(), localToParentTx.getMyt(),
                                localToParentTx.getMzx(), localToParentTx.getMzy(), localToParentTx.getMzz(), localToParentTx.getMzt());
                    }

                    @Override
                    protected boolean validityKnown() {
                        return true;
                    }

                    @Override
                    protected int computeValidity() {
                        return valid;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "localToParentTransform";
                    }
                };
            }

            return localToParentTransform;
        }

        public void invalidateLocalToParentTransform() {
            if (localToParentTransform != null) {
                localToParentTransform.invalidate();
            }
        }

        public final Transform getLocalToSceneTransform() {
            return localToSceneTransformProperty().get();
        }

        class LocalToSceneTransformProperty extends LazyTransformProperty {
            // need this to track number of listeners
            private List localToSceneListeners;
            // stamps to watch for parent changes when the listeners
            // are not present
            private long stamp, parentStamp;

            @Override
            protected Transform computeTransform(Transform reuse) {
                stamp++;
                updateLocalToParentTransform();

                MappedNode parentNode = MappedNode.this.getParent();
                if (parentNode != null) {
                    final LocalToSceneTransformProperty parentProperty =
                            (LocalToSceneTransformProperty) parentNode.localToSceneTransformProperty();
                    final Transform parentTransform = parentProperty.getInternalValue();

                    parentStamp = parentProperty.stamp;

                    return TransformUtils.immutableTransform(reuse,
                            parentTransform,
                            ((LazyTransformProperty) localToParentTransformProperty()).getInternalValue());
                } else {
                    return TransformUtils.immutableTransform(reuse,
                            ((LazyTransformProperty) localToParentTransformProperty()).getInternalValue());
                }
            }

            @Override
            public Object getBean() {
                return MappedNode.this;
            }

            @Override
            public String getName() {
                return "localToSceneTransform";
            }

            @Override
            protected boolean validityKnown() {
                return listenerReasons > 0;
            }

            @Override
            protected int computeValidity() {
                if (valid != VALIDITY_UNKNOWN) {
                    return valid;
                }

                MappedNode n = (MappedNode) getBean();
                MappedNode parent = n.getParent();

                if (parent != null) {
                    final LocalToSceneTransformProperty parentProperty =
                            (LocalToSceneTransformProperty) parent.localToSceneTransformProperty();

                    if (parentStamp != parentProperty.stamp) {
                        valid = INVALID;
                        return INVALID;
                    }

                    int parentValid = parentProperty.computeValidity();
                    if (parentValid == INVALID) {
                        valid = INVALID;
                    }
                    return parentValid;
                }

                // Validity unknown for root means it is valid
                return VALID;
            }

            @Override
            public void addListener(InvalidationListener listener) {
                incListenerReasons();
                if (localToSceneListeners == null) {
                    localToSceneListeners = new LinkedList<Object>();
                }
                localToSceneListeners.add(listener);
                super.addListener(listener);
            }

            @Override
            public void addListener(ChangeListener<? super Transform> listener) {
                incListenerReasons();
                if (localToSceneListeners == null) {
                    localToSceneListeners = new LinkedList<Object>();
                }
                localToSceneListeners.add(listener);
                super.addListener(listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                if (localToSceneListeners != null &&
                        localToSceneListeners.remove(listener)) {
                    decListenerReasons();
                }
                super.removeListener(listener);
            }

            @Override
            public void removeListener(ChangeListener<? super Transform> listener) {
                if (localToSceneListeners != null &&
                        localToSceneListeners.remove(listener)) {
                    decListenerReasons();
                }
                super.removeListener(listener);
            }
        }

        public final ReadOnlyObjectProperty<Transform> localToSceneTransformProperty() {
            if (localToSceneTransform == null) {
                localToSceneTransform = new LocalToSceneTransformProperty();
            }

            return localToSceneTransform;
        }

        public void invalidateLocalToSceneTransform() {
            if (localToSceneTransform != null) {
                localToSceneTransform.invalidate();
            }
        }

        public double getTranslateX() {
            return (translateX == null) ? DEFAULT_TRANSLATE_X
                    : translateX.get();
        }

        public final DoubleProperty translateXProperty() {
            if (translateX == null) {
                translateX = new StyleableDoubleProperty(DEFAULT_TRANSLATE_X) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.TRANSLATE_X;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "translateX";
                    }
                };
            }
            return translateX;
        }

        public double getTranslateY() {
            return (translateY == null) ? DEFAULT_TRANSLATE_Y : translateY.get();
        }

        public final DoubleProperty translateYProperty() {
            if (translateY == null) {
                translateY = new StyleableDoubleProperty(DEFAULT_TRANSLATE_Y) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.TRANSLATE_Y;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "translateY";
                    }
                };
            }
            return translateY;
        }

        public double getTranslateZ() {
            return (translateZ == null) ? DEFAULT_TRANSLATE_Z : translateZ.get();
        }

        public final DoubleProperty translateZProperty() {
            if (translateZ == null) {
                translateZ = new StyleableDoubleProperty(DEFAULT_TRANSLATE_Z) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.TRANSLATE_Z;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "translateZ";
                    }
                };
            }
            return translateZ;
        }

        public double getScaleX() {
            return (scaleX == null) ? DEFAULT_SCALE_X : scaleX.get();
        }

        public final DoubleProperty scaleXProperty() {
            if (scaleX == null) {
                scaleX = new StyleableDoubleProperty(DEFAULT_SCALE_X) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.SCALE_X;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "scaleX";
                    }
                };
            }
            return scaleX;
        }

        public double getScaleY() {
            return (scaleY == null) ? DEFAULT_SCALE_Y : scaleY.get();
        }

        public final DoubleProperty scaleYProperty() {
            if (scaleY == null) {
                scaleY = new StyleableDoubleProperty(DEFAULT_SCALE_Y) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.SCALE_Y;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "scaleY";
                    }
                };
            }
            return scaleY;
        }

        public double getScaleZ() {
            return (scaleZ == null) ? DEFAULT_SCALE_Z : scaleZ.get();
        }

        public final DoubleProperty scaleZProperty() {
            if (scaleZ == null) {
                scaleZ = new StyleableDoubleProperty(DEFAULT_SCALE_Z) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.SCALE_Z;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "scaleZ";
                    }
                };
            }
            return scaleZ;
        }

        public double getRotate() {
            return (rotate == null) ? DEFAULT_ROTATE : rotate.get();
        }

        public final DoubleProperty rotateProperty() {
            if (rotate == null) {
                rotate = new StyleableDoubleProperty(DEFAULT_ROTATE) {
                    @Override
                    public void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.ROTATE;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "rotate";
                    }
                };
            }
            return rotate;
        }

        public Point3D getRotationAxis() {
            return (rotationAxis == null) ? DEFAULT_ROTATION_AXIS
                    : rotationAxis.get();
        }

        public final ObjectProperty<Point3D> rotationAxisProperty() {
            if (rotationAxis == null) {
                rotationAxis = new ObjectPropertyBase<Point3D>(
                        DEFAULT_ROTATION_AXIS) {
                    @Override
                    protected void invalidated() {
                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "rotationAxis";
                    }
                };
            }
            return rotationAxis;
        }

        public ObservableList<Transform> getTransforms() {
            if (transforms == null) {
                transforms = new TrackableObservableList<Transform>() {
                    @Override
                    protected void onChanged(Change<Transform> c) {
                        while (c.next()) {
                            for (Transform t : c.getRemoved()) {
                                TransformHelper.remove(t, MappedNode.this);
                            }
                            for (Transform t : c.getAddedSubList()) {
                                TransformHelper.add(t, MappedNode.this);
                            }
                        }

                        MappedNodeHelper.transformsChanged(MappedNode.this);
                    }
                };
            }

            return transforms;
        }

        public boolean canSetTranslateX() {
            return (translateX == null) || !translateX.isBound();
        }

        public boolean canSetTranslateY() {
            return (translateY == null) || !translateY.isBound();
        }

        public boolean canSetTranslateZ() {
            return (translateZ == null) || !translateZ.isBound();
        }

        public boolean canSetScaleX() {
            return (scaleX == null) || !scaleX.isBound();
        }

        public boolean canSetScaleY() {
            return (scaleY == null) || !scaleY.isBound();
        }

        public boolean canSetScaleZ() {
            return (scaleZ == null) || !scaleZ.isBound();
        }

        public boolean canSetRotate() {
            return (rotate == null) || !rotate.isBound();
        }

        public boolean hasTransforms() {
            return (transforms != null && !transforms.isEmpty());
        }

        public boolean hasScaleOrRotate() {
            if (scaleX != null && scaleX.get() != DEFAULT_SCALE_X) {
                return true;
            }
            if (scaleY != null && scaleY.get() != DEFAULT_SCALE_Y) {
                return true;
            }
            if (scaleZ != null && scaleZ.get() != DEFAULT_SCALE_Z) {
                return true;
            }
            if (rotate != null && rotate.get() != DEFAULT_ROTATE) {
                return true;
            }
            return false;
        }

    }

    ////////////////////////////
    //  Private Implementation
    ////////////////////////////

    /* *************************************************************************
     *                                                                         *
     *                        Event Handler Properties                         *
     *                                                                         *
     **************************************************************************/

    private EventHandlerProperties eventHandlerProperties;

    private EventHandlerProperties getEventHandlerProperties() {
        if (eventHandlerProperties == null) {
            eventHandlerProperties =
                    new EventHandlerProperties(
                            getInternalEventDispatcher().getEventHandlerManager(),
                            this);
        }

        return eventHandlerProperties;
    }

    /* *************************************************************************
     *                                                                         *
     *                       Component Orientation Properties                  *
     *                                                                         *
     **************************************************************************/

    private ObjectProperty<NodeOrientation> nodeOrientation;
    private EffectiveOrientationProperty effectiveNodeOrientationProperty;

    private static final byte EFFECTIVE_ORIENTATION_LTR = 0;
    private static final byte EFFECTIVE_ORIENTATION_RTL = 1;
    private static final byte EFFECTIVE_ORIENTATION_MASK = 1;
    private static final byte AUTOMATIC_ORIENTATION_LTR = 0;
    private static final byte AUTOMATIC_ORIENTATION_RTL = 2;
    private static final byte AUTOMATIC_ORIENTATION_MASK = 2;

    private byte resolvedNodeOrientation =
            EFFECTIVE_ORIENTATION_LTR | AUTOMATIC_ORIENTATION_LTR;

    public final void setNodeOrientation(NodeOrientation orientation) {
        nodeOrientationProperty().set(orientation);
    }

    public final NodeOrientation getNodeOrientation() {
        return nodeOrientation == null ? NodeOrientation.INHERIT : nodeOrientation.get();
    }
    
    public final ObjectProperty<NodeOrientation> nodeOrientationProperty() {
        if (nodeOrientation == null) {
            nodeOrientation = new StyleableObjectProperty<NodeOrientation>(NodeOrientation.INHERIT) {
                @Override
                protected void invalidated() {
                    nodeResolvedOrientationInvalidated();
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "nodeOrientation";
                }

                @Override
                public CssMetaData getCssMetaData() {
                    //TODO - not supported
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            };
        }
        return nodeOrientation;
    }

    public final NodeOrientation getEffectiveNodeOrientation() {
        return (getEffectiveOrientation(resolvedNodeOrientation)
                == EFFECTIVE_ORIENTATION_LTR)
                ? NodeOrientation.LEFT_TO_RIGHT
                : NodeOrientation.RIGHT_TO_LEFT;
    }

    
    public final ReadOnlyObjectProperty<NodeOrientation>
    effectiveNodeOrientationProperty() {
        if (effectiveNodeOrientationProperty == null) {
            effectiveNodeOrientationProperty =
                    new EffectiveOrientationProperty();
        }

        return effectiveNodeOrientationProperty;
    }

    
    public boolean usesMirroring() {
        return true;
    }

    final void parentResolvedOrientationInvalidated() {
        if (getNodeOrientation() == NodeOrientation.INHERIT) {
            nodeResolvedOrientationInvalidated();
        } else {
            // mirroring changed
            MappedNodeHelper.transformsChanged(this);
        }
    }

    final void nodeResolvedOrientationInvalidated() {
        final byte oldResolvedNodeOrientation =
                resolvedNodeOrientation;

        resolvedNodeOrientation =
                (byte) (calcEffectiveNodeOrientation()
                        | calcAutomaticNodeOrientation());

        if ((effectiveNodeOrientationProperty != null)
                && (getEffectiveOrientation(resolvedNodeOrientation)
                != getEffectiveOrientation(
                oldResolvedNodeOrientation))) {
            effectiveNodeOrientationProperty.invalidate();
        }

        // mirroring changed
        MappedNodeHelper.transformsChanged(this);

        if (resolvedNodeOrientation != oldResolvedNodeOrientation) {
            nodeResolvedOrientationChanged();
        }
    }

    void nodeResolvedOrientationChanged() {
        // overriden in MappedParent
    }

    private MappedNode getMirroringOrientationParent() {
        MappedNode parentValue = getParent();
        while (parentValue != null) {
            if (parentValue.usesMirroring()) {
                return parentValue;
            }
            parentValue = parentValue.getParent();
        }

        final MappedNode subSceneValue = getSubScene();
        if (subSceneValue != null) {
            return subSceneValue;
        }

        return null;
    }

    private MappedNode getOrientationParent() {
        final MappedNode parentValue = getParent();
        if (parentValue != null) {
            return parentValue;
        }

        final MappedNode subSceneValue = getSubScene();
        if (subSceneValue != null) {
            return subSceneValue;
        }

        return null;
    }

    private byte calcEffectiveNodeOrientation() {
        final NodeOrientation nodeOrientationValue = getNodeOrientation();
        if (nodeOrientationValue != NodeOrientation.INHERIT) {
            return (nodeOrientationValue == NodeOrientation.LEFT_TO_RIGHT)
                    ? EFFECTIVE_ORIENTATION_LTR
                    : EFFECTIVE_ORIENTATION_RTL;
        }

        final MappedNode parentValue = getOrientationParent();
        if (parentValue != null) {
            return getEffectiveOrientation(parentValue.resolvedNodeOrientation);
        }

        final MappedScene sceneValue = getScene();
        if (sceneValue != null) {
            return (sceneValue.getEffectiveNodeOrientation()
                    == NodeOrientation.LEFT_TO_RIGHT)
                    ? EFFECTIVE_ORIENTATION_LTR
                    : EFFECTIVE_ORIENTATION_RTL;
        }

        return EFFECTIVE_ORIENTATION_LTR;
    }

    private byte calcAutomaticNodeOrientation() {
        if (!usesMirroring()) {
            return AUTOMATIC_ORIENTATION_LTR;
        }

        final NodeOrientation nodeOrientationValue = getNodeOrientation();
        if (nodeOrientationValue != NodeOrientation.INHERIT) {
            return (nodeOrientationValue == NodeOrientation.LEFT_TO_RIGHT)
                    ? AUTOMATIC_ORIENTATION_LTR
                    : AUTOMATIC_ORIENTATION_RTL;
        }

        final MappedNode parentValue = getMirroringOrientationParent();
        if (parentValue != null) {
            // automatic node orientation is inherited
            return getAutomaticOrientation(parentValue.resolvedNodeOrientation);
        }

        final MappedScene sceneValue = getScene();
        if (sceneValue != null) {
            return (sceneValue.getEffectiveNodeOrientation()
                    == NodeOrientation.LEFT_TO_RIGHT)
                    ? AUTOMATIC_ORIENTATION_LTR
                    : AUTOMATIC_ORIENTATION_RTL;
        }

        return AUTOMATIC_ORIENTATION_LTR;
    }

    // Return true if the node needs to be mirrored.
    // A node has mirroring if the orientation differs from the parent
    // package private for testing
    final boolean hasMirroring() {
        final MappedNode parentValue = getOrientationParent();

        final byte thisOrientation =
                getAutomaticOrientation(resolvedNodeOrientation);
        final byte parentOrientation =
                (parentValue != null)
                        ? getAutomaticOrientation(
                        parentValue.resolvedNodeOrientation)
                        : AUTOMATIC_ORIENTATION_LTR;

        return thisOrientation != parentOrientation;
    }

    private static byte getEffectiveOrientation(
            final byte resolvedNodeOrientation) {
        return (byte) (resolvedNodeOrientation & EFFECTIVE_ORIENTATION_MASK);
    }

    private static byte getAutomaticOrientation(
            final byte resolvedNodeOrientation) {
        return (byte) (resolvedNodeOrientation & AUTOMATIC_ORIENTATION_MASK);
    }

    private final class EffectiveOrientationProperty
            extends ReadOnlyObjectPropertyBase<NodeOrientation> {
        @Override
        public NodeOrientation get() {
            return getEffectiveNodeOrientation();
        }

        @Override
        public Object getBean() {
            return MappedNode.this;
        }

        @Override
        public String getName() {
            return "effectiveNodeOrientation";
        }

        public void invalidate() {
            fireValueChangedEvent();
        }
    }

    /* *************************************************************************
     *                                                                         *
     *                       Misc Seldom Used Properties                       *
     *                                                                         *
     **************************************************************************/

    private MiscProperties miscProperties;

    private MiscProperties getMiscProperties() {
        if (miscProperties == null) {
            miscProperties = new MiscProperties();
        }

        return miscProperties;
    }

    private static final double DEFAULT_VIEW_ORDER = 0;
    private static final boolean DEFAULT_CACHE = false;
    private static final CacheHint DEFAULT_CACHE_HINT = CacheHint.DEFAULT;
    private static final MappedNode DEFAULT_CLIP = null;
    private static final Cursor DEFAULT_CURSOR = null;
    private static final DepthTest DEFAULT_DEPTH_TEST = DepthTest.INHERIT;
    private static final boolean DEFAULT_DISABLE = false;
    private static final Effect DEFAULT_EFFECT = null;
    private static final InputMethodRequests DEFAULT_INPUT_METHOD_REQUESTS =
            null;
    private static final boolean DEFAULT_MOUSE_TRANSPARENT = false;

    private final class MiscProperties {
        private LazyBoundsProperty boundsInParent;
        private LazyBoundsProperty boundsInLocal;
        private BooleanProperty cache;
        private ObjectProperty<CacheHint> cacheHint;
        private ObjectProperty<MappedNode> clip;
        private ObjectProperty<Cursor> cursor;
        private ObjectProperty<DepthTest> depthTest;
        private BooleanProperty disable;
        private ObjectProperty<Effect> effect;
        private ObjectProperty<InputMethodRequests> inputMethodRequests;
        private BooleanProperty mouseTransparent;
        private DoubleProperty viewOrder;

        public double getViewOrder() {
            return (viewOrder == null) ? DEFAULT_VIEW_ORDER : viewOrder.get();
        }

        public final DoubleProperty viewOrderProperty() {
            if (viewOrder == null) {
                viewOrder = new StyleableDoubleProperty(DEFAULT_VIEW_ORDER) {
                    @Override
                    public void invalidated() {
                        MappedParent p = getParent();
                        if (p != null) {
                            // MappedParent will be responsible to update sorted children list
                            p.markViewOrderChildrenDirty();
                        }
                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_VIEW_ORDER);
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.VIEW_ORDER;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "viewOrder";
                    }
                };
            }
            return viewOrder;
        }

        public final Bounds getBoundsInParent() {
            return boundsInParentProperty().get();
        }

        public final ReadOnlyObjectProperty<Bounds> boundsInParentProperty() {
            if (boundsInParent == null) {
                boundsInParent = new LazyBoundsProperty() {
                    
                    @Override
                    protected Bounds computeBounds() {
                        BaseBounds tempBounds = TempState.getInstance().bounds;
                        tempBounds = getTransformedBounds(
                                tempBounds,
                                BaseTransform.IDENTITY_TRANSFORM);
                        return new BoundingBox(tempBounds.getMinX(),
                                tempBounds.getMinY(),
                                tempBounds.getMinZ(),
                                tempBounds.getWidth(),
                                tempBounds.getHeight(),
                                tempBounds.getDepth());
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "boundsInParent";
                    }
                };
            }

            return boundsInParent;
        }

        public void invalidateBoundsInParent() {
            if (boundsInParent != null) {
                boundsInParent.invalidate();
            }
        }

        public final Bounds getBoundsInLocal() {
            return boundsInLocalProperty().get();
        }

        public final ReadOnlyObjectProperty<Bounds> boundsInLocalProperty() {
            if (boundsInLocal == null) {
                boundsInLocal = new LazyBoundsProperty() {
                    @Override
                    protected Bounds computeBounds() {
                        BaseBounds tempBounds = TempState.getInstance().bounds;
                        tempBounds = getLocalBounds(
                                tempBounds,
                                BaseTransform.IDENTITY_TRANSFORM);
                        return new BoundingBox(tempBounds.getMinX(),
                                tempBounds.getMinY(),
                                tempBounds.getMinZ(),
                                tempBounds.getWidth(),
                                tempBounds.getHeight(),
                                tempBounds.getDepth());
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "boundsInLocal";
                    }
                };
            }

            return boundsInLocal;
        }

        public void invalidateBoundsInLocal() {
            if (boundsInLocal != null) {
                boundsInLocal.invalidate();
            }
        }

        public final boolean isCache() {
            return (cache == null) ? DEFAULT_CACHE
                    : cache.get();
        }

        public final BooleanProperty cacheProperty() {
            if (cache == null) {
                cache = new BooleanPropertyBase(DEFAULT_CACHE) {
                    @Override
                    protected void invalidated() {
                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_CACHE);
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "cache";
                    }
                };
            }
            return cache;
        }

        public final CacheHint getCacheHint() {
            return (cacheHint == null) ? DEFAULT_CACHE_HINT
                    : cacheHint.get();
        }

        public final ObjectProperty<CacheHint> cacheHintProperty() {
            if (cacheHint == null) {
                cacheHint = new ObjectPropertyBase<CacheHint>(DEFAULT_CACHE_HINT) {
                    @Override
                    protected void invalidated() {
                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_CACHE);
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "cacheHint";
                    }
                };
            }
            return cacheHint;
        }

        public final MappedNode getClip() {
            return (clip == null) ? DEFAULT_CLIP : clip.get();
        }

        public final ObjectProperty<MappedNode> clipProperty() {
            if (clip == null) {
                clip = new ObjectPropertyBase<MappedNode>(DEFAULT_CLIP) {

                    //temp variables used when clip was invalid to rollback to
                    // last value
                    private MappedNode oldClip;

                    @Override
                    protected void invalidated() {
                        final MappedNode newClip = get();
                        if ((newClip != null)
                                && ((newClip.isConnected()
                                && newClip.clipParent != MappedNode.this)
                                || wouldCreateCycle(MappedNode.this,
                                newClip))) {
                            // Assigning this node to clip is illegal.
                            // Roll back to the previous state and throw an
                            // exception.
                            final String cause =
                                    newClip.isConnected()
                                            && (newClip.clipParent != MappedNode.this)
                                            ? "node already connected"
                                            : "cycle detected";

                            if (isBound()) {
                                unbind();
                                set(oldClip);
                                throw new IllegalArgumentException(
                                        "MappedNode's clip set to incorrect value "
                                                + " through binding"
                                                + " (" + cause + ", node  = "
                                                + MappedNode.this + ", clip = "
                                                + clip + ")."
                                                + " Binding has been removed.");
                            } else {
                                set(oldClip);
                                throw new IllegalArgumentException(
                                        "MappedNode's clip set to incorrect value"
                                                + " (" + cause + ", node  = "
                                                + MappedNode.this + ", clip = "
                                                + clip + ").");
                            }
                        } else {
                            if (oldClip != null) {
                                oldClip.clipParent = null;
                                oldClip.setScenes(null, null);
                                oldClip.updateTreeVisible(false);
                            }

                            if (newClip != null) {
                                newClip.clipParent = MappedNode.this;
                                newClip.setScenes(getScene(), getSubScene());
                                newClip.updateTreeVisible(true);
                            }

                            MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_CLIP);

                            // the local bounds have (probably) changed
                            localBoundsChanged();

                            oldClip = newClip;
                        }
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "clip";
                    }
                };
            }
            return clip;
        }

        public final Cursor getCursor() {
            return (cursor == null) ? DEFAULT_CURSOR : cursor.get();
        }

        public final ObjectProperty<Cursor> cursorProperty() {
            if (cursor == null) {
                cursor = new StyleableObjectProperty<Cursor>(DEFAULT_CURSOR) {

                    @Override
                    protected void invalidated() {
                        final MappedScene sceneValue = getScene();
                        if (sceneValue != null) {
                            sceneValue.markCursorDirty();
                        }
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.CURSOR;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "cursor";
                    }

                };
            }
            return cursor;
        }

        public final DepthTest getDepthTest() {
            return (depthTest == null) ? DEFAULT_DEPTH_TEST
                    : depthTest.get();
        }

        public final ObjectProperty<DepthTest> depthTestProperty() {
            if (depthTest == null) {
                depthTest = new ObjectPropertyBase<DepthTest>(DEFAULT_DEPTH_TEST) {
                    @Override protected void invalidated() {
                        computeDerivedDepthTest();
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "depthTest";
                    }
                };
            }
            return depthTest;
        }

        public final boolean isDisable() {
            return (disable == null) ? DEFAULT_DISABLE : disable.get();
        }

        public final BooleanProperty disableProperty() {
            if (disable == null) {
                disable = new BooleanPropertyBase(DEFAULT_DISABLE) {
                    @Override
                    protected void invalidated() {
                        updateDisabled();
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "disable";
                    }
                };
            }
            return disable;
        }

        public final Effect getEffect() {
            return (effect == null) ? DEFAULT_EFFECT : effect.get();
        }

        public final ObjectProperty<Effect> effectProperty() {
            if (effect == null) {
                effect = new StyleableObjectProperty<Effect>(DEFAULT_EFFECT) {
                    private Effect oldEffect = null;
                    private int oldBits;

                    private final AbstractNotifyListener effectChangeListener =
                            new AbstractNotifyListener() {

                                @Override
                                public void invalidated(Observable valueModel) {
                                    int newBits = ((IntegerProperty) valueModel).get();
                                    int changedBits = newBits ^ oldBits;
                                    oldBits = newBits;
                                    if (EffectDirtyBits.isSet(
                                            changedBits,
                                            EffectDirtyBits.EFFECT_DIRTY)
                                            && EffectDirtyBits.isSet(
                                            newBits,
                                            EffectDirtyBits.EFFECT_DIRTY)) {
                                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.EFFECT_EFFECT);
                                    }
                                    if (EffectDirtyBits.isSet(
                                            changedBits,
                                            EffectDirtyBits.BOUNDS_CHANGED)) {
                                        localBoundsChanged();
                                    }
                                }
                            };

                    @Override
                    protected void invalidated() {
                        Effect _effect = get();
                        if (oldEffect != null) {
                            EffectHelper.effectDirtyProperty(oldEffect).removeListener(
                                    effectChangeListener.getWeakListener());
                        }
                        oldEffect = _effect;
                        if (_effect != null) {
                            EffectHelper.effectDirtyProperty(_effect)
                                    .addListener(
                                            effectChangeListener.getWeakListener());
                            if (EffectHelper.isEffectDirty(_effect)) {
                                MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.EFFECT_EFFECT);
                            }
                            oldBits = EffectHelper.effectDirtyProperty(_effect).get();
                        }

                        MappedNodeHelper.markDirty(MappedNode.this, DirtyBits.NODE_EFFECT);
                        // bounds may have changed regardless whether
                        // the dirty flag on effect is set
                        localBoundsChanged();
                    }

                    @Override
                    public CssMetaData getCssMetaData() {
                        return StyleableProperties.EFFECT;
                    }

                    @Override
                    public Object getBean() {
                        return MappedNode.this;
                    }

                    @Override
                    public String getName() {
                        return "effect";
                    }
                };
            }
            return effect;
        }

        public final InputMethodRequests getInputMethodRequests() {
            return (inputMethodRequests == null) ? DEFAULT_INPUT_METHOD_REQUESTS
                    : inputMethodRequests.get();
        }

        public ObjectProperty<InputMethodRequests>
        inputMethodRequestsProperty() {
            if (inputMethodRequests == null) {
                inputMethodRequests =
                        new SimpleObjectProperty<InputMethodRequests>(
                                MappedNode.this,
                                "inputMethodRequests",
                                DEFAULT_INPUT_METHOD_REQUESTS);
            }
            return inputMethodRequests;
        }

        public final boolean isMouseTransparent() {
            return (mouseTransparent == null) ? DEFAULT_MOUSE_TRANSPARENT
                    : mouseTransparent.get();
        }

        public final BooleanProperty mouseTransparentProperty() {
            if (mouseTransparent == null) {
                mouseTransparent =
                        new SimpleBooleanProperty(
                                MappedNode.this,
                                "mouseTransparent",
                                DEFAULT_MOUSE_TRANSPARENT);
            }
            return mouseTransparent;
        }

        public boolean canSetCursor() {
            return (cursor == null) || !cursor.isBound();
        }

        public boolean canSetEffect() {
            return (effect == null) || !effect.isBound();
        }
    }

    /* *************************************************************************
     *                                                                         *
     *                             Mouse Handling                              *
     *                                                                         *
     **************************************************************************/

    public final void setMouseTransparent(boolean value) {
        mouseTransparentProperty().set(value);
    }

    public final boolean isMouseTransparent() {
        return (miscProperties == null) ? DEFAULT_MOUSE_TRANSPARENT
                : miscProperties.isMouseTransparent();
    }

    
    public final BooleanProperty mouseTransparentProperty() {
        return getMiscProperties().mouseTransparentProperty();
    }

    
    private ReadOnlyBooleanWrapper hover;

    protected final void setHover(boolean value) {
        hoverPropertyImpl().set(value);
    }

    public final boolean isHover() {
        return hover == null ? false : hover.get();
    }

    public final ReadOnlyBooleanProperty hoverProperty() {
        return hoverPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper hoverPropertyImpl() {
        if (hover == null) {
            hover = new ReadOnlyBooleanWrapper() {

                @Override
                protected void invalidated() {
                    PlatformLogger logger = Logging.getInputLogger();
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this + " hover=" + get());
                    }
                    pseudoClassStateChanged(HOVER_PSEUDOCLASS_STATE, get());
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "hover";
                }
            };
        }
        return hover;
    }

    
    private ReadOnlyBooleanWrapper pressed;

    protected final void setPressed(boolean value) {
        pressedPropertyImpl().set(value);
    }

    public final boolean isPressed() {
        return pressed == null ? false : pressed.get();
    }

    public final ReadOnlyBooleanProperty pressedProperty() {
        return pressedPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper pressedPropertyImpl() {
        if (pressed == null) {
            pressed = new ReadOnlyBooleanWrapper() {

                @Override
                protected void invalidated() {
                    PlatformLogger logger = Logging.getInputLogger();
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this + " pressed=" + get());
                    }
                    pseudoClassStateChanged(PRESSED_PSEUDOCLASS_STATE, get());
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "pressed";
                }
            };
        }
        return pressed;
    }

    public final void setOnContextMenuRequested(
            EventHandler<? super ContextMenuEvent> value) {
        onContextMenuRequestedProperty().set(value);
    }

    public final EventHandler<? super ContextMenuEvent> getOnContextMenuRequested() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.onContextMenuRequested();
    }

    
    public final ObjectProperty<EventHandler<? super ContextMenuEvent>>
    onContextMenuRequestedProperty() {
        return getEventHandlerProperties().onContextMenuRequestedProperty();
    }

    public final void setOnMouseClicked(
            EventHandler<? super MouseEvent> value) {
        onMouseClickedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseClicked() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseClicked();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseClickedProperty() {
        return getEventHandlerProperties().onMouseClickedProperty();
    }

    public final void setOnMouseDragged(
            EventHandler<? super MouseEvent> value) {
        onMouseDraggedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseDragged() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseDragged();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseDraggedProperty() {
        return getEventHandlerProperties().onMouseDraggedProperty();
    }

    public final void setOnMouseEntered(
            EventHandler<? super MouseEvent> value) {
        onMouseEnteredProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseEntered() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseEntered();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseEnteredProperty() {
        return getEventHandlerProperties().onMouseEnteredProperty();
    }

    public final void setOnMouseExited(
            EventHandler<? super MouseEvent> value) {
        onMouseExitedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseExited() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseExited();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseExitedProperty() {
        return getEventHandlerProperties().onMouseExitedProperty();
    }

    public final void setOnMouseMoved(
            EventHandler<? super MouseEvent> value) {
        onMouseMovedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseMoved() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseMoved();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseMovedProperty() {
        return getEventHandlerProperties().onMouseMovedProperty();
    }

    public final void setOnMousePressed(
            EventHandler<? super MouseEvent> value) {
        onMousePressedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMousePressed() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMousePressed();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMousePressedProperty() {
        return getEventHandlerProperties().onMousePressedProperty();
    }

    public final void setOnMouseReleased(
            EventHandler<? super MouseEvent> value) {
        onMouseReleasedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseReleased() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseReleased();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onMouseReleasedProperty() {
        return getEventHandlerProperties().onMouseReleasedProperty();
    }

    public final void setOnDragDetected(
            EventHandler<? super MouseEvent> value) {
        onDragDetectedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnDragDetected() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnDragDetected();
    }

    
    public final ObjectProperty<EventHandler<? super MouseEvent>>
    onDragDetectedProperty() {
        return getEventHandlerProperties().onDragDetectedProperty();
    }

    public final void setOnMouseDragOver(
            EventHandler<? super MouseDragEvent> value) {
        onMouseDragOverProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragOver() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseDragOver();
    }

    
    public final ObjectProperty<EventHandler<? super MouseDragEvent>>
    onMouseDragOverProperty() {
        return getEventHandlerProperties().onMouseDragOverProperty();
    }

    public final void setOnMouseDragReleased(
            EventHandler<? super MouseDragEvent> value) {
        onMouseDragReleasedProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragReleased() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseDragReleased();
    }

    
    public final ObjectProperty<EventHandler<? super MouseDragEvent>>
    onMouseDragReleasedProperty() {
        return getEventHandlerProperties().onMouseDragReleasedProperty();
    }

    public final void setOnMouseDragEntered(
            EventHandler<? super MouseDragEvent> value) {
        onMouseDragEnteredProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragEntered() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseDragEntered();
    }

    
    public final ObjectProperty<EventHandler<? super MouseDragEvent>>
    onMouseDragEnteredProperty() {
        return getEventHandlerProperties().onMouseDragEnteredProperty();
    }

    public final void setOnMouseDragExited(
            EventHandler<? super MouseDragEvent> value) {
        onMouseDragExitedProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragExited() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnMouseDragExited();
    }

    
    public final ObjectProperty<EventHandler<? super MouseDragEvent>>
    onMouseDragExitedProperty() {
        return getEventHandlerProperties().onMouseDragExitedProperty();
    }


    /* *************************************************************************
     *                                                                         *
     *                           Gestures Handling                             *
     *                                                                         *
     **************************************************************************/

    public final void setOnScrollStarted(
            EventHandler<? super ScrollEvent> value) {
        onScrollStartedProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScrollStarted() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnScrollStarted();
    }

    
    public final ObjectProperty<EventHandler<? super ScrollEvent>>
    onScrollStartedProperty() {
        return getEventHandlerProperties().onScrollStartedProperty();
    }

    public final void setOnScroll(
            EventHandler<? super ScrollEvent> value) {
        onScrollProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScroll() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnScroll();
    }

    
    public final ObjectProperty<EventHandler<? super ScrollEvent>>
    onScrollProperty() {
        return getEventHandlerProperties().onScrollProperty();
    }

    public final void setOnScrollFinished(
            EventHandler<? super ScrollEvent> value) {
        onScrollFinishedProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScrollFinished() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnScrollFinished();
    }

    
    public final ObjectProperty<EventHandler<? super ScrollEvent>>
    onScrollFinishedProperty() {
        return getEventHandlerProperties().onScrollFinishedProperty();
    }

    public final void setOnRotationStarted(
            EventHandler<? super RotateEvent> value) {
        onRotationStartedProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotationStarted() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnRotationStarted();
    }

    
    public final ObjectProperty<EventHandler<? super RotateEvent>>
    onRotationStartedProperty() {
        return getEventHandlerProperties().onRotationStartedProperty();
    }

    public final void setOnRotate(
            EventHandler<? super RotateEvent> value) {
        onRotateProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotate() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnRotate();
    }

    
    public final ObjectProperty<EventHandler<? super RotateEvent>>
    onRotateProperty() {
        return getEventHandlerProperties().onRotateProperty();
    }

    public final void setOnRotationFinished(
            EventHandler<? super RotateEvent> value) {
        onRotationFinishedProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotationFinished() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnRotationFinished();
    }

    
    public final ObjectProperty<EventHandler<? super RotateEvent>>
    onRotationFinishedProperty() {
        return getEventHandlerProperties().onRotationFinishedProperty();
    }

    public final void setOnZoomStarted(
            EventHandler<? super ZoomEvent> value) {
        onZoomStartedProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoomStarted() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnZoomStarted();
    }

    
    public final ObjectProperty<EventHandler<? super ZoomEvent>>
    onZoomStartedProperty() {
        return getEventHandlerProperties().onZoomStartedProperty();
    }

    public final void setOnZoom(
            EventHandler<? super ZoomEvent> value) {
        onZoomProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoom() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnZoom();
    }

    
    public final ObjectProperty<EventHandler<? super ZoomEvent>>
    onZoomProperty() {
        return getEventHandlerProperties().onZoomProperty();
    }

    public final void setOnZoomFinished(
            EventHandler<? super ZoomEvent> value) {
        onZoomFinishedProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoomFinished() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnZoomFinished();
    }

    
    public final ObjectProperty<EventHandler<? super ZoomEvent>>
    onZoomFinishedProperty() {
        return getEventHandlerProperties().onZoomFinishedProperty();
    }

    public final void setOnSwipeUp(
            EventHandler<? super SwipeEvent> value) {
        onSwipeUpProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeUp() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnSwipeUp();
    }

    
    public final ObjectProperty<EventHandler<? super SwipeEvent>>
    onSwipeUpProperty() {
        return getEventHandlerProperties().onSwipeUpProperty();
    }

    public final void setOnSwipeDown(
            EventHandler<? super SwipeEvent> value) {
        onSwipeDownProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeDown() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnSwipeDown();
    }

    
    public final ObjectProperty<EventHandler<? super SwipeEvent>>
    onSwipeDownProperty() {
        return getEventHandlerProperties().onSwipeDownProperty();
    }

    public final void setOnSwipeLeft(
            EventHandler<? super SwipeEvent> value) {
        onSwipeLeftProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeLeft() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnSwipeLeft();
    }

    
    public final ObjectProperty<EventHandler<? super SwipeEvent>>
    onSwipeLeftProperty() {
        return getEventHandlerProperties().onSwipeLeftProperty();
    }

    public final void setOnSwipeRight(
            EventHandler<? super SwipeEvent> value) {
        onSwipeRightProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeRight() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnSwipeRight();
    }

    
    public final ObjectProperty<EventHandler<? super SwipeEvent>>
    onSwipeRightProperty() {
        return getEventHandlerProperties().onSwipeRightProperty();
    }


    /* *************************************************************************
     *                                                                         *
     *                             Touch Handling                              *
     *                                                                         *
     **************************************************************************/

    public final void setOnTouchPressed(
            EventHandler<? super TouchEvent> value) {
        onTouchPressedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchPressed() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnTouchPressed();
    }

    
    public final ObjectProperty<EventHandler<? super TouchEvent>>
    onTouchPressedProperty() {
        return getEventHandlerProperties().onTouchPressedProperty();
    }

    public final void setOnTouchMoved(
            EventHandler<? super TouchEvent> value) {
        onTouchMovedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchMoved() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnTouchMoved();
    }

    
    public final ObjectProperty<EventHandler<? super TouchEvent>>
    onTouchMovedProperty() {
        return getEventHandlerProperties().onTouchMovedProperty();
    }

    public final void setOnTouchReleased(
            EventHandler<? super TouchEvent> value) {
        onTouchReleasedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchReleased() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnTouchReleased();
    }

    
    public final ObjectProperty<EventHandler<? super TouchEvent>>
    onTouchReleasedProperty() {
        return getEventHandlerProperties().onTouchReleasedProperty();
    }

    public final void setOnTouchStationary(
            EventHandler<? super TouchEvent> value) {
        onTouchStationaryProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchStationary() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnTouchStationary();
    }

    
    public final ObjectProperty<EventHandler<? super TouchEvent>>
    onTouchStationaryProperty() {
        return getEventHandlerProperties().onTouchStationaryProperty();
    }

    /* *************************************************************************
     *                                                                         *
     *                           Keyboard Handling                             *
     *                                                                         *
     **************************************************************************/

    public final void setOnKeyPressed(
            EventHandler<? super KeyEvent> value) {
        onKeyPressedProperty().set(value);
    }

    public final EventHandler<? super KeyEvent> getOnKeyPressed() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnKeyPressed();
    }

    
    public final ObjectProperty<EventHandler<? super KeyEvent>>
    onKeyPressedProperty() {
        return getEventHandlerProperties().onKeyPressedProperty();
    }

    public final void setOnKeyReleased(
            EventHandler<? super KeyEvent> value) {
        onKeyReleasedProperty().set(value);
    }

    public final EventHandler<? super KeyEvent> getOnKeyReleased() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnKeyReleased();
    }

    
    public final ObjectProperty<EventHandler<? super KeyEvent>>
    onKeyReleasedProperty() {
        return getEventHandlerProperties().onKeyReleasedProperty();
    }

    public final void setOnKeyTyped(
            EventHandler<? super KeyEvent> value) {
        onKeyTypedProperty().set(value);
    }

    public final EventHandler<? super KeyEvent> getOnKeyTyped() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnKeyTyped();
    }

    
    public final ObjectProperty<EventHandler<? super KeyEvent>>
    onKeyTypedProperty() {
        return getEventHandlerProperties().onKeyTypedProperty();
    }

    /* *************************************************************************
     *                                                                         *
     *                           Input Method Handling                         *
     *                                                                         *
     **************************************************************************/

    public final void setOnInputMethodTextChanged(
            EventHandler<? super InputMethodEvent> value) {
        onInputMethodTextChangedProperty().set(value);
    }

    public final EventHandler<? super InputMethodEvent>
    getOnInputMethodTextChanged() {
        return (eventHandlerProperties == null)
                ? null : eventHandlerProperties.getOnInputMethodTextChanged();
    }

    
    public final ObjectProperty<EventHandler<? super InputMethodEvent>>
    onInputMethodTextChangedProperty() {
        return getEventHandlerProperties().onInputMethodTextChangedProperty();
    }

    public final void setInputMethodRequests(InputMethodRequests value) {
        inputMethodRequestsProperty().set(value);
    }

    public final InputMethodRequests getInputMethodRequests() {
        return (miscProperties == null)
                ? DEFAULT_INPUT_METHOD_REQUESTS
                : miscProperties.getInputMethodRequests();
    }

    
    public final ObjectProperty<InputMethodRequests> inputMethodRequestsProperty() {
        return getMiscProperties().inputMethodRequestsProperty();
    }

    /* *************************************************************************
     *                                                                         *
     *                             Focus Traversal                             *
     *                                                                         *
     **************************************************************************/

    
    final class FocusedProperty extends ReadOnlyBooleanPropertyBase {
        private boolean value;
        private boolean valid = true;
        private boolean needsChangeEvent = false;

        public void store(final boolean value) {
            if (value != this.value) {
                this.value = value;
                markInvalid();
            }
        }

        public void notifyListeners() {
            if (needsChangeEvent) {
                fireValueChangedEvent();
                needsChangeEvent = false;
            }
        }

        private void markInvalid() {
            if (valid) {
                valid = false;

                pseudoClassStateChanged(FOCUSED_PSEUDOCLASS_STATE, get());
                PlatformLogger logger = Logging.getFocusLogger();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(this + " focused=" + get());
                }

                needsChangeEvent = true;

                notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUSED);
            }
        }

        @Override
        public boolean get() {
            valid = true;
            return value;
        }

        @Override
        public Object getBean() {
            return MappedNode.this;
        }

        @Override
        public String getName() {
            return "focused";
        }
    }

    
    private FocusedProperty focused;

    protected final void setFocused(boolean value) {
        FocusedProperty fp = focusedPropertyImpl();
        if (fp.value != value) {
            fp.store(value);
            fp.notifyListeners();
        }
    }

    public final boolean isFocused() {
        return focused == null ? false : focused.get();
    }

    public final ReadOnlyBooleanProperty focusedProperty() {
        return focusedPropertyImpl();
    }

    private FocusedProperty focusedPropertyImpl() {
        if (focused == null) {
            focused = new FocusedProperty();
        }
        return focused;
    }

    
    private BooleanProperty focusTraversable;

    public final void setFocusTraversable(boolean value) {
        focusTraversableProperty().set(value);
    }
    public final boolean isFocusTraversable() {
        return focusTraversable == null ? false : focusTraversable.get();
    }

    public final BooleanProperty focusTraversableProperty() {
        if (focusTraversable == null) {
            focusTraversable = new StyleableBooleanProperty(false) {

                @Override
                public void invalidated() {
                    MappedScene _scene = getScene();
                    if (_scene != null) {
                        if (get()) {
                            _scene.initializeInternalEventDispatcher();
                        }
                        focusSetDirty(_scene);
                    }
                }

                @Override
                public CssMetaData getCssMetaData() {
                    return StyleableProperties.FOCUS_TRAVERSABLE;
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "focusTraversable";
                }
            };
        }
        return focusTraversable;
    }

    
    private void focusSetDirty(MappedScene s) {
        if (s != null &&
                (this == s.getFocusOwner() || isFocusTraversable())) {
            s.setFocusDirty(true);
        }
    }

    
    public void requestFocus() {
        if (getScene() != null) {
            getScene().requestFocus(this);
        }
    }

    
    final boolean traverse(Direction dir) {
        if (getScene() == null) {
            return false;
        }
        return getScene().traverse(this, dir);
    }

    ////////////////////////////
    //  Private Implementation
    ////////////////////////////

    
    @Override
    public String toString() {
        String klassName = getClass().getName();
        String simpleName = klassName.substring(klassName.lastIndexOf('.')+1);
        StringBuilder sbuf = new StringBuilder(simpleName);
        boolean hasId = id != null && !"".equals(getId());
        boolean hasStyleClass = !getStyleClass().isEmpty();

        if (!hasId) {
            sbuf.append('@');
            sbuf.append(Integer.toHexString(hashCode()));
        } else {
            sbuf.append("[id=");
            sbuf.append(getId());
            if (!hasStyleClass) sbuf.append("]");
        }
        if (hasStyleClass) {
            if (!hasId) sbuf.append('[');
            else sbuf.append(", ");
            sbuf.append("styleClass=");
            sbuf.append(getStyleClass());
            sbuf.append("]");
        }
        return sbuf.toString();
    }

    private void preprocessMouseEvent(MouseEvent e) {
        final EventType<?> eventType = e.getEventType();
        if (eventType == MouseEvent.MOUSE_PRESSED) {
            for (MappedNode n = this; n != null; n = n.getParent()) {
                n.setPressed(e.isPrimaryButtonDown());
            }
            return;
        }
        if (eventType == MouseEvent.MOUSE_RELEASED) {
            for (MappedNode n = this; n != null; n = n.getParent()) {
                n.setPressed(e.isPrimaryButtonDown());
            }
            return;
        }

        if (e.getTarget() == this) {
            // the mouse event types are translated only when the node uses
            // its internal event dispatcher, so both entered / exited variants
            // are possible here

            if ((eventType == MouseEvent.MOUSE_ENTERED)
                    || (eventType == MouseEvent.MOUSE_ENTERED_TARGET)) {
                setHover(true);
                return;
            }

            if ((eventType == MouseEvent.MOUSE_EXITED)
                    || (eventType == MouseEvent.MOUSE_EXITED_TARGET)) {
                setHover(false);
                return;
            }
        }
    }

    void markDirtyLayoutBranch() {
        MappedParent p = getParent();
        while (p != null && p.layoutFlag == LayoutFlags.CLEAN) {
            p.setLayoutFlag(LayoutFlags.DIRTY_BRANCH);
            if (p.isSceneRoot()) {
                Toolkit.getToolkit().requestNextPulse();
                if (getSubScene() != null) {
                    getSubScene().setDirtyLayout(p);
                }
            }
            p = p.getParent();
        }

    }

    private boolean isWindowShowing() {
        MappedScene s = getScene();
        if (s == null) return false;
        MappedWindow w = s.getWindow();
        return w != null && w.isShowing();
    }

    final boolean isTreeShowing() {
        return isTreeVisible() && isWindowShowing();
    }

    private void updateTreeVisible(boolean parentChanged) {
        boolean isTreeVisible = isVisible();
        final MappedNode parentNode = getParent() != null ? getParent() :
                clipParent != null ? clipParent :
                        getSubScene() != null ? getSubScene() : null;
        if (isTreeVisible) {
            isTreeVisible = parentNode == null || parentNode.isTreeVisible();
        }
        // When the parent has changed to visible and we have unsynchronized visibility,
        // we have to synchronize, because the rendering will now pass through the newly-visible parent
        // Otherwise an invisible MappedNode might get rendered
        if (parentChanged && parentNode != null && parentNode.isTreeVisible()
                && isDirty(DirtyBits.NODE_VISIBLE)) {
            addToSceneDirtyList();
        }
        setTreeVisible(isTreeVisible);
    }

    private boolean treeVisible;
    private TreeVisiblePropertyReadOnly treeVisibleRO;

    final void setTreeVisible(boolean value) {
        if (treeVisible != value) {
            treeVisible = value;
            updateCanReceiveFocus();
            focusSetDirty(getScene());
            if (getClip() != null) {
                getClip().updateTreeVisible(true);
            }
            if (treeVisible && !isDirtyEmpty()) {
                addToSceneDirtyList();
            }
            ((TreeVisiblePropertyReadOnly) treeVisibleProperty()).invalidate();
            if (MappedNode.this instanceof MappedSubScene) {
                MappedNode subSceneRoot = ((MappedSubScene)MappedNode.this).getRoot();
                if (subSceneRoot != null) {
                    // MappedSubScene.getRoot() is only null if it's constructor
                    // has not finished.
                    subSceneRoot.setTreeVisible(value && subSceneRoot.isVisible());
                }
            }
        }
    }

    final boolean isTreeVisible() {
        return treeVisibleProperty().get();
    }

    final BooleanExpression treeVisibleProperty() {
        if (treeVisibleRO == null) {
            treeVisibleRO = new TreeVisiblePropertyReadOnly();
        }
        return treeVisibleRO;
    }

    class TreeVisiblePropertyReadOnly extends BooleanExpression {

        private ExpressionHelper<Boolean> helper;
        private boolean valid;

        @Override
        public void addListener(InvalidationListener listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        @Override
        public void addListener(ChangeListener<? super Boolean> listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(ChangeListener<? super Boolean> listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        protected void invalidate() {
            if (valid) {
                valid = false;
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        }

        @Override
        public boolean get() {
            valid = true;
            return MappedNode.this.treeVisible;
        }

    }

    private boolean canReceiveFocus = false;

    private void setCanReceiveFocus(boolean value) {
        canReceiveFocus = value;
    }

    final boolean isCanReceiveFocus() {
        return canReceiveFocus;
    }

    private void updateCanReceiveFocus() {
        setCanReceiveFocus(getScene() != null
                && !isDisabled()
                && isTreeVisible());
    }

    // for indenting messages based on scene-graph depth
    String indent() {
        String indent = "";
        MappedParent p = this.getParent();
        while (p != null) {
            indent += "  ";
            p = p.getParent();
        }
        return indent;
    }

    /*
     * Should we underline the mnemonic character?
     */
    private BooleanProperty showMnemonics;

    final void setShowMnemonics(boolean value) {
        showMnemonicsProperty().set(value);
    }

    final boolean isShowMnemonics() {
        return showMnemonics == null ? false : showMnemonics.get();
    }

    final BooleanProperty showMnemonicsProperty() {
        if (showMnemonics == null) {
            showMnemonics = new BooleanPropertyBase(false) {

                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(SHOW_MNEMONICS_PSEUDOCLASS_STATE, get());
                }

                @Override
                public Object getBean() {
                    return MappedNode.this;
                }

                @Override
                public String getName() {
                    return "showMnemonics";
                }
            };
        }
        return showMnemonics;
    }


    
    private MappedNode labeledBy = null;


    /* *************************************************************************
     *                                                                         *
     *                         Event Dispatch                                  *
     *                                                                         *
     **************************************************************************/

    // PENDING_DOC_REVIEW
    
    private ObjectProperty<EventDispatcher> eventDispatcher;

    public final void setEventDispatcher(EventDispatcher value) {
        eventDispatcherProperty().set(value);
    }

    public final EventDispatcher getEventDispatcher() {
        return eventDispatcherProperty().get();
    }

    public final ObjectProperty<EventDispatcher> eventDispatcherProperty() {
        initializeInternalEventDispatcher();
        return eventDispatcher;
    }

    private NodeEventDispatcher internalEventDispatcher;

    // PENDING_DOC_REVIEW
    
    public final <T extends Event> void addEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher().getEventHandlerManager()
                .addEventHandler(eventType, eventHandler);
    }

    // PENDING_DOC_REVIEW
    
    public final <T extends Event> void removeEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher()
                .getEventHandlerManager()
                .removeEventHandler(eventType, eventHandler);
    }

    // PENDING_DOC_REVIEW
    
    public final <T extends Event> void addEventFilter(
            final EventType<T> eventType,
            final EventHandler<? super T> eventFilter) {
        getInternalEventDispatcher().getEventHandlerManager()
                .addEventFilter(eventType, eventFilter);
    }

    // PENDING_DOC_REVIEW
    
    public final <T extends Event> void removeEventFilter(
            final EventType<T> eventType,
            final EventHandler<? super T> eventFilter) {
        getInternalEventDispatcher().getEventHandlerManager()
                .removeEventFilter(eventType, eventFilter);
    }

    
    protected final <T extends Event> void setEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        getInternalEventDispatcher().getEventHandlerManager()
                .setEventHandler(eventType, eventHandler);
    }

    private NodeEventDispatcher getInternalEventDispatcher() {
        initializeInternalEventDispatcher();
        return internalEventDispatcher;
    }

    private void initializeInternalEventDispatcher() {
        if (internalEventDispatcher == null) {
            internalEventDispatcher = createInternalEventDispatcher();
            eventDispatcher = new SimpleObjectProperty<EventDispatcher>(
                    MappedNode.this,
                    "eventDispatcher",
                    internalEventDispatcher);
        }
    }

    private NodeEventDispatcher createInternalEventDispatcher() {
        return new NodeEventDispatcher(this);
    }

    
    private EventDispatcher preprocessMouseEventDispatcher;

    // PENDING_DOC_REVIEW
    
    @Override
    public EventDispatchChain buildEventDispatchChain(
            EventDispatchChain tail) {

        if (preprocessMouseEventDispatcher == null) {
            preprocessMouseEventDispatcher = (event, tail1) -> {
                event = tail1.dispatchEvent(event);
                if (event instanceof MouseEvent) {
                    preprocessMouseEvent((MouseEvent) event);
                }

                return event;
            };
        }

        tail = tail.prepend(preprocessMouseEventDispatcher);

        // prepend all event dispatchers from this node to the root
        MappedNode curNode = this;
        do {
            if (curNode.eventDispatcher != null) {
                final EventDispatcher eventDispatcherValue =
                        curNode.eventDispatcher.get();
                if (eventDispatcherValue != null) {
                    tail = tail.prepend(eventDispatcherValue);
                }
            }
            final MappedNode curParent = curNode.getParent();
            curNode = curParent != null ? curParent : curNode.getSubScene();
        } while (curNode != null);

        if (getScene() != null) {
            // prepend scene's dispatch chain
            tail = getScene().buildEventDispatchChain(tail);
        }

        return tail;
    }

    // PENDING_DOC_REVIEW
    
    public final void fireEvent(Event event) {

        /* Log input events.  We do a coarse filter for at least the FINE
         * level and then granularize from there.
         */
        if (event instanceof InputEvent) {
            PlatformLogger logger = Logging.getInputLogger();
            if (logger.isLoggable(Level.FINE)) {
                EventType eventType = event.getEventType();
                if (eventType == MouseEvent.MOUSE_ENTERED ||
                        eventType == MouseEvent.MOUSE_EXITED) {
                    logger.finer(event.toString());
                } else if (eventType == MouseEvent.MOUSE_MOVED ||
                        eventType == MouseEvent.MOUSE_DRAGGED) {
                    logger.finest(event.toString());
                } else {
                    logger.fine(event.toString());
                }
            }
        }

        Event.fireEvent(this, event);
    }

    /* *************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/


    
    @Override
    public String getTypeSelector() {

        final Class<?> clazz = getClass();
        final Package pkg = clazz.getPackage();

        // package could be null. not likely, but could be.
        int plen = 0;
        if (pkg != null) {
            plen = pkg.getName().length();
        }

        final int clen = clazz.getName().length();
        final int pos = (0 < plen && plen < clen) ? plen + 1 : 0;

        return clazz.getName().substring(pos);
    }

    
    @Override
    public MappedStyleable getStyleableParent() {
        return getParent();
    }


    
    protected Boolean getInitialFocusTraversable() {
        return Boolean.FALSE;
    }

    
    protected Cursor getInitialCursor() {
        return null;
    }

    
    private static class StyleableProperties {

        private static final CssMetaData<MappedNode,Cursor> CURSOR =
                new CssMetaData<MappedNode,Cursor>("-fx-cursor", CursorConverter.getInstance()) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.miscProperties == null || node.miscProperties.canSetCursor();
                    }

                    @Override
                    public StyleableProperty<Cursor> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Cursor>)node.cursorProperty();
                    }

                    @Override
                    public Cursor getInitialValue(MappedNode node) {
                        // Most controls default focusTraversable to true.
                        // Give a way to have them return the correct default value.
                        return node.getInitialCursor();
                    }

                };
        private static final CssMetaData<MappedNode,Effect> EFFECT =
                new CssMetaData<MappedNode,Effect>("-fx-effect", EffectConverter.getInstance()) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.miscProperties == null || node.miscProperties.canSetEffect();
                    }

                    @Override
                    public StyleableProperty<Effect> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Effect>)node.effectProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Boolean> FOCUS_TRAVERSABLE =
                new CssMetaData<MappedNode,Boolean>("-fx-focus-traversable",
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.focusTraversable == null || !node.focusTraversable.isBound();
                    }

                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Boolean>)node.focusTraversableProperty();
                    }

                    @Override
                    public Boolean getInitialValue(MappedNode node) {
                        // Most controls default focusTraversable to true.
                        // Give a way to have them return the correct default value.
                        return node.getInitialFocusTraversable();
                    }

                };
        private static final CssMetaData<MappedNode,Number> OPACITY =
                new CssMetaData<MappedNode,Number>("-fx-opacity",
                        SizeConverter.getInstance(), 1.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.opacity == null || !node.opacity.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.opacityProperty();
                    }
                };
        private static final CssMetaData<MappedNode,BlendMode> BLEND_MODE =
                new CssMetaData<MappedNode,BlendMode>("-fx-blend-mode", new EnumConverter<BlendMode>(BlendMode.class)) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.blendMode == null || !node.blendMode.isBound();
                    }

                    @Override
                    public StyleableProperty<BlendMode> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<BlendMode>)node.blendModeProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> ROTATE =
                new CssMetaData<MappedNode,Number>("-fx-rotate",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.rotate == null
                                || node.nodeTransformation.canSetRotate();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.rotateProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> SCALE_X =
                new CssMetaData<MappedNode,Number>("-fx-scale-x",
                        SizeConverter.getInstance(), 1.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.scaleX == null
                                || node.nodeTransformation.canSetScaleX();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.scaleXProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> SCALE_Y =
                new CssMetaData<MappedNode,Number>("-fx-scale-y",
                        SizeConverter.getInstance(), 1.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.scaleY == null
                                || node.nodeTransformation.canSetScaleY();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.scaleYProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> SCALE_Z =
                new CssMetaData<MappedNode,Number>("-fx-scale-z",
                        SizeConverter.getInstance(), 1.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.scaleZ == null
                                || node.nodeTransformation.canSetScaleZ();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.scaleZProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> TRANSLATE_X =
                new CssMetaData<MappedNode,Number>("-fx-translate-x",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.translateX == null
                                || node.nodeTransformation.canSetTranslateX();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.translateXProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> TRANSLATE_Y =
                new CssMetaData<MappedNode,Number>("-fx-translate-y",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.translateY == null
                                || node.nodeTransformation.canSetTranslateY();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.translateYProperty();
                    }
                };
        private static final CssMetaData<MappedNode,Number> TRANSLATE_Z =
                new CssMetaData<MappedNode,Number>("-fx-translate-z",
                        SizeConverter.getInstance(), 0.0) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.nodeTransformation == null
                                || node.nodeTransformation.translateZ == null
                                || node.nodeTransformation.canSetTranslateZ();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Number>)node.translateZProperty();
                    }
                };
        private static final CssMetaData<MappedNode, Number> VIEW_ORDER
                = new CssMetaData<MappedNode, Number>("-fx-view-order",
                SizeConverter.getInstance(), 0.0) {

            @Override
            public boolean isSettable(MappedNode node) {
                return node.miscProperties == null
                        || node.miscProperties.viewOrder == null
                        || !node.miscProperties.viewOrder.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(MappedNode node) {
                return (StyleableProperty<Number>) node.viewOrderProperty();
            }
        };
        private static final CssMetaData<MappedNode,Boolean> VISIBILITY =
                new CssMetaData<MappedNode,Boolean>("visibility",
                        new StyleConverter<String,Boolean>() {

                            @Override
                            // [ visible | hidden | collapse | inherit ]
                            public Boolean convert(ParsedValue<String, Boolean> value, Font font) {
                                final String sval = value != null ? value.getValue() : null;
                                return "visible".equalsIgnoreCase(sval);
                            }

                        },
                        Boolean.TRUE) {

                    @Override
                    public boolean isSettable(MappedNode node) {
                        return node.visible == null || !node.visible.isBound();
                    }

                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(MappedNode node) {
                        return (StyleableProperty<Boolean>)node.visibleProperty();
                    }
                };

        private static final List<CssMetaData<? extends MappedStyleable, ?>> STYLEABLES;

        static {

            final List<CssMetaData<? extends MappedStyleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends MappedStyleable, ?>>();
            styleables.add(CURSOR);
            styleables.add(EFFECT);
            styleables.add(FOCUS_TRAVERSABLE);
            styleables.add(OPACITY);
            styleables.add(BLEND_MODE);
            styleables.add(ROTATE);
            styleables.add(SCALE_X);
            styleables.add(SCALE_Y);
            styleables.add(SCALE_Z);
            styleables.add(VIEW_ORDER);
            styleables.add(TRANSLATE_X);
            styleables.add(TRANSLATE_Y);
            styleables.add(TRANSLATE_Z);
            styleables.add(VISIBILITY);
            STYLEABLES = Collections.unmodifiableList(styleables);

        }
    }

    
    public static List<CssMetaData<? extends MappedStyleable, ?>> getClassCssMetaData() {
        //
        // Super-lazy instantiation pattern from Bill Pugh. StyleableProperties
        // is referenced no earlier (and therefore loaded no earlier by the
        // class loader) than the moment that  getClassCssMetaData() is called.
        // This avoids loading the CssMetaData instances until the point at
        // which CSS needs the data.
        //
        return StyleableProperties.STYLEABLES;
    }

    

    @Override
    public List<CssMetaData<? extends MappedStyleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    /*
     * @return  The Styles that match this CSS property for the given MappedNode. The
     * list is sorted by descending specificity.
     */
    // SB-dependency: RT-21096 has been filed to track this
    static List<Style> getMatchingStyles(CssMetaData cssMetaData, MappedStyleable styleable) {
        return CssStyleHelper.getMatchingStyles(styleable, cssMetaData);
    }

    final ObservableMap<StyleableProperty<?>, List<Style>> getStyleMap() {
        ObservableMap<StyleableProperty<?>, List<Style>> map =
                (ObservableMap<StyleableProperty<?>, List<Style>>)getProperties().get("STYLEMAP");
        Map<StyleableProperty<?>, List<Style>> ret = CssStyleHelper.getMatchingStyles(map, this);
        if (ret != null) {
            if (ret instanceof ObservableMap) return (ObservableMap)ret;
            return FXCollections.observableMap(ret);
        }
        return FXCollections.<StyleableProperty<?>, List<Style>>emptyObservableMap();
    }

    /*
     * RT-17293
     */
    // SB-dependency: RT-21096 has been filed to track this
    final void setStyleMap(ObservableMap<StyleableProperty<?>, List<Style>> styleMap) {
        if (styleMap != null) getProperties().put("STYLEMAP", styleMap);
        else getProperties().remove("STYLEMAP");
    }

    /*
     * Find CSS styles that were used to style this MappedNode in its current pseudo-class state. The map will contain the styles from this node and,
     * if the node is a MappedParent, its children. The node corresponding to an entry in the Map can be obtained by casting a StyleableProperty key to a
     * javafx.beans.property.Property and calling getBean(). The List contains only those styles used to style the property and will contain
     * styles used to resolve lookup values.
     *
     * @param styleMap A Map to be populated with the styles. If null, a new Map will be allocated.
     * @return The Map populated with matching styles.
     */
    // SB-dependency: RT-21096 has been filed to track this
    Map<StyleableProperty<?>,List<Style>> findStyles(Map<StyleableProperty<?>,List<Style>> styleMap) {

        Map<StyleableProperty<?>, List<Style>> ret = CssStyleHelper.getMatchingStyles(styleMap, this);
        return (ret != null) ? ret : Collections.<StyleableProperty<?>, List<Style>>emptyMap();
    }

    
    CssFlags cssFlag = CssFlags.CLEAN;

    
    final CssFlags getCSSFlags() { return cssFlag; }

    
    private void requestCssStateTransition() {
        // If there is no scene, then we cannot make it dirty, so we'll leave
        // the flag alone
        if (getScene() == null) return;
        // Don't bother doing anything if the cssFlag is not CLEAN.
        // If the flag indicates a DIRTY_BRANCH, the flag needs to be changed
        // to UPDATE to ensure that MappedNodeHelper.processCSS is called on the node.
        if (cssFlag == CssFlags.CLEAN || cssFlag == CssFlags.DIRTY_BRANCH) {
            cssFlag = CssFlags.UPDATE;
            notifyParentsOfInvalidatedCSS();
        }
    }

    
    public final void pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {

        final boolean modified = active
                ? pseudoClassStates.add(pseudoClass)
                : pseudoClassStates.remove(pseudoClass);

        if (modified && styleHelper != null) {
            final boolean isTransition = styleHelper.pseudoClassStateChanged(pseudoClass);
            if (isTransition) {
                requestCssStateTransition();
            }
        }
    }

    // package so that StyleHelper can get at it
    final ObservableSet<PseudoClass> pseudoClassStates = new PseudoClassState();
    private final ObservableSet<PseudoClass> unmodifiablePseudoClassStates =
            FXCollections.unmodifiableObservableSet(pseudoClassStates);
    
    @Override
    public final ObservableSet<PseudoClass> getPseudoClassStates() {
        return unmodifiablePseudoClassStates;
    }

    // Walks up the tree telling each parent that the pseudo class state of
    // this node has changed.
    final void notifyParentsOfInvalidatedCSS() {
        MappedSubScene subScene = getSubScene();
        MappedParent root = (subScene != null) ?
                subScene.getRoot() : getScene().getRoot();

        if (!root.isDirty(DirtyBits.NODE_CSS)) {
            // Ensure that MappedScene.root is marked as dirty. If the scene isn't
            // dirty, nothing will get repainted. This bit is cleared from
            // MappedScene in doCSSPass().
            MappedNodeHelper.markDirty(root, DirtyBits.NODE_CSS);
            if (subScene != null) {
                // If the node is part of a subscene, then we must ensure that
                // the we not only mark subScene.root dirty, but continue and
                // call subScene.notifyParentsOfInvalidatedCSS() until
                // MappedScene.root gets marked dirty, via the recursive call:
                subScene.cssFlag = CssFlags.UPDATE;
                subScene.notifyParentsOfInvalidatedCSS();
            }
        }
        MappedParent _parent = getParent();
        while (_parent != null) {
            if (_parent.cssFlag == CssFlags.CLEAN) {
                _parent.cssFlag = CssFlags.DIRTY_BRANCH;
                _parent = _parent.getParent();
            } else {
                _parent = null;
            }
        }
    }

    final void recalculateRelativeSizeProperties(Font fontForRelativeSizes) {
        if (styleHelper != null) {
            styleHelper.recalculateRelativeSizeProperties(this, fontForRelativeSizes);
        }
    }

    final void reapplyCSS() {

        if (getScene() == null) return;

        if (cssFlag == CssFlags.REAPPLY) return;

        if (cssFlag == CssFlags.DIRTY_BRANCH) {
            // JDK-8193445 - don't reapply CSS from here
            // Defer CSS application to this MappedNode by marking cssFlag as REAPPLY
            cssFlag = CssFlags.REAPPLY;
            return;
        }

        // RT-36838 - don't reapply CSS in the middle of an update
        if (cssFlag == CssFlags.UPDATE) {
            cssFlag = CssFlags.REAPPLY;
            notifyParentsOfInvalidatedCSS();
            return;
        }

        reapplyCss();

        //
        // One idiom employed by developers is to, during the layout pass,
        // add or remove nodes from the scene. For example, a ScrollPane
        // might add scroll bars to itself if it determines during layout
        // that it needs them, or a ListView might add cells to itself if
        // it determines that it needs to. In such situations we must
        // apply the CSS immediately and not add it to the scene's queue
        // for deferred action.
        //
        if (getParent() != null && getParent().isPerformingLayout()) {
            MappedNodeHelper.processCSS(this);
        } else {
            notifyParentsOfInvalidatedCSS();
        }

    }

    //
    // This method "reapplies" CSS to this node and all of its children. Reapplying CSS
    // means that new style maps are calculated for the node. The process of reapplying
    // CSS may reset the CSS properties of a node to their initial state, but the _new_
    // styles are not applied as part of this process.
    //
    // There is no check of the CSS state of a child since reapply takes precedence
    // over other CSS states.
    //
    private void reapplyCss() {

        // Hang on to current styleHelper so we can know whether
        // createStyleHelper returned the same styleHelper
        final CssStyleHelper oldStyleHelper = styleHelper;

        // CSS state is "REAPPLY"
        cssFlag = CssFlags.REAPPLY;

        styleHelper = CssStyleHelper.createStyleHelper(this);

        // REAPPLY to my children, too.
        if (this instanceof MappedParent) {

            // minor optimization to avoid calling createStyleHelper on children
            // when we know there will not be any change in the style maps.
            final boolean visitChildren =
                    // If we don't have a styleHelper, then we should visit the children of this parent
                    // since there might be styles that depend on being a child of this parent.
                    // In other words, we have .a > .b { blah: blort; }, but no styles for ".a" itself.
                    styleHelper == null ||
                            // if the styleHelper changed, then we definitely need to visit the children
                            // since the new styles may have an effect on the children's styles calculated values.
                            (oldStyleHelper != styleHelper) ||
                            // If our parent is null, then we're the root of a scene or sub-scene, most likely,
                            // and we'll visit children because elsewhere the code depends on root.reapplyCSS()
                            // to force css to be reapplied (whether it needs to be or not).
                            (getParent() == null) ||
                            // If our parent's cssFlag is other than clean, then the parent may have just had
                            // CSS reapplied. If the parent just had CSS reapplied, then some of its styles
                            // may affect my children's styles.
                            (getParent().cssFlag != CssFlags.CLEAN);

            if (visitChildren) {

                List<MappedNode> children = ((MappedParent) this).getChildren();
                for (int n = 0, nMax = children.size(); n < nMax; n++) {
                    MappedNode child = children.get(n);
                    child.reapplyCss();
                }
            }

        } else if (this instanceof MappedSubScene) {

            // MappedSubScene root is a MappedParent, but reapplyCss is a private method in MappedNode
            final MappedNode subSceneRoot = ((MappedSubScene)this).getRoot();
            if (subSceneRoot != null) {
                subSceneRoot.reapplyCss();
            }

        } else if (styleHelper == null) {
            //
            // If this is not a MappedParent and there is no styleHelper, then the CSS state is "CLEAN"
            // since there are no styles to apply or children to update.
            //
            cssFlag = CssFlags.CLEAN;
            return;
        }

        cssFlag = CssFlags.UPDATE;

    }

    void processCSS() {
        switch (cssFlag) {
            case CLEAN:
                break;
            case DIRTY_BRANCH:
            {
                MappedParent me = (MappedParent)this;
                // clear the flag first in case the flag is set to something
                // other than clean by downstream processing.
                me.cssFlag = CssFlags.CLEAN;
                List<MappedNode> children = me.getChildren();
                for (int i=0, max=children.size(); i<max; i++) {
                    children.get(i).processCSS();
                }
                break;
            }
            case REAPPLY:
            case UPDATE:
            default:
                MappedNodeHelper.processCSS(this);
        }
    }

    
    public final void applyCss() {

        if (getScene() == null) {
            return;
        }

        // update, unless reapply
        if (cssFlag != CssFlags.REAPPLY) cssFlag = CssFlags.UPDATE;

        //
        // RT-28394 - need to see if any ancestor has a flag UPDATE
        // If so, process css from the top-most CssFlags.UPDATE node
        // since my ancestor's styles may affect mine.
        //
        // If the scene-graph root isn't NODE_CSS dirty, then all my
        // ancestor flags should be CLEAN and I can skip this lookup.
        //
        MappedNode topMost = this;

        final boolean dirtyRoot = getScene().getRoot().isDirty(com.sun.javafx.scene.DirtyBits.NODE_CSS);
        if (dirtyRoot) {

            MappedNode _parent = getParent();
            while (_parent != null) {
                if (_parent.cssFlag == CssFlags.UPDATE || _parent.cssFlag == CssFlags.REAPPLY) {
                    topMost = _parent;
                }
                _parent = _parent.getParent();
            }

            // Note: this code used to mark the parent nodes with DIRTY_BRANCH,
            // but that isn't necessary since UPDATE will apply css to all of
            // a MappedParent's children.

            // If we're at the root of the scene-graph, make sure the NODE_CSS
            // dirty bit is cleared (see MappedScene#doCSSPass())
            if (topMost == getScene().getRoot()) {
                getScene().getRoot().clearDirty(DirtyBits.NODE_CSS);
            }
        }

        topMost.processCSS();

    }

    /*
     * If invoked, will update styles from here on down. This method should not be called directly. If
     * overridden, the overriding method must at some point call {@code super.processCSSImpl} to ensure that
     * this MappedNode's CSS state is properly updated.
     *
     * Note that the difference between this method and {@link #applyCss()} is that this method
     * updates styles for this node on down; whereas, {@code applyCss()} looks for the top-most ancestor that needs
     * CSS update and apply styles from that node on down.
     *
     * Note: This method MUST only be called via its accessor method.
     */
    private void doProcessCSS() {

        // Nothing to do...
        if (cssFlag == CssFlags.CLEAN) return;

        // if REAPPLY was deferred, process it now...
        if (cssFlag == CssFlags.REAPPLY) {
            reapplyCss();
        }

        // Clear the flag first in case the flag is set to something
        // other than clean by downstream processing.
        cssFlag = CssFlags.CLEAN;

        // Transition to the new state and apply styles
        if (styleHelper != null && getScene() != null) {
            styleHelper.transitionToState(this);
        }
    }


    
    CssStyleHelper styleHelper;

    private static final PseudoClass HOVER_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("hover");
    private static final PseudoClass PRESSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("pressed");
    private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");
    private static final PseudoClass FOCUSED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass SHOW_MNEMONICS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("show-mnemonics");

    private static abstract class LazyTransformProperty
            extends ReadOnlyObjectProperty<Transform> {

        protected static final int VALID = 0;
        protected static final int INVALID = 1;
        protected static final int VALIDITY_UNKNOWN = 2;
        protected int valid = INVALID;

        private ExpressionHelper<Transform> helper;

        private Transform transform;
        private boolean canReuse = false;

        @Override
        public void addListener(InvalidationListener listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        @Override
        public void addListener(ChangeListener<? super Transform> listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(ChangeListener<? super Transform> listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        protected Transform getInternalValue() {
            if (valid == INVALID ||
                    (valid == VALIDITY_UNKNOWN && computeValidity() == INVALID)) {
                transform = computeTransform(canReuse ? transform : null);
                canReuse = true;
                valid = validityKnown() ? VALID : VALIDITY_UNKNOWN;
            }

            return transform;
        }

        @Override
        public Transform get() {
            transform = getInternalValue();
            canReuse = false;
            return transform;
        }

        public void validityUnknown() {
            if (valid == VALID) {
                valid = VALIDITY_UNKNOWN;
            }
        }

        public void invalidate() {
            if (valid != INVALID) {
                valid = INVALID;
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        }

        protected abstract boolean validityKnown();
        protected abstract int computeValidity();
        protected abstract Transform computeTransform(Transform reuse);
    }

    private static abstract class LazyBoundsProperty
            extends ReadOnlyObjectProperty<Bounds> {
        private ExpressionHelper<Bounds> helper;
        private boolean valid;

        private Bounds bounds;

        @Override
        public void addListener(InvalidationListener listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        @Override
        public void addListener(ChangeListener<? super Bounds> listener) {
            helper = ExpressionHelper.addListener(helper, this, listener);
        }

        @Override
        public void removeListener(ChangeListener<? super Bounds> listener) {
            helper = ExpressionHelper.removeListener(helper, listener);
        }

        @Override
        public Bounds get() {
            if (!valid) {
                bounds = computeBounds();
                valid = true;
            }

            return bounds;
        }

        public void invalidate() {
            if (valid) {
                valid = false;
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        }

        protected abstract Bounds computeBounds();
    }

    private static final BoundsAccessor boundsAccessor = (bounds, tx, node) -> node.getGeomBounds(bounds, tx);

    
    private ObjectProperty<AccessibleRole> accessibleRole;

    public final void setAccessibleRole(AccessibleRole value) {
        if (value == null) value = AccessibleRole.NODE;
        accessibleRoleProperty().set(value);
    }

    public final AccessibleRole getAccessibleRole() {
        if (accessibleRole == null) return AccessibleRole.NODE;
        return accessibleRoleProperty().get();
    }

    public final ObjectProperty<AccessibleRole> accessibleRoleProperty() {
        if (accessibleRole == null) {
            accessibleRole = new SimpleObjectProperty<AccessibleRole>(this, "accessibleRole", AccessibleRole.NODE);
        }
        return accessibleRole;
    }

    public final void setAccessibleRoleDescription(String value) {
        accessibleRoleDescriptionProperty().set(value);
    }

    public final String getAccessibleRoleDescription() {
        if (accessibilityProperties == null) return null;
        if (accessibilityProperties.accessibleRoleDescription == null) return null;
        return accessibleRoleDescriptionProperty().get();
    }

    
    public final ObjectProperty<String> accessibleRoleDescriptionProperty() {
        return getAccessibilityProperties().getAccessibleRoleDescription();
    }

    public final void setAccessibleText(String value) {
        accessibleTextProperty().set(value);
    }

    public final String getAccessibleText() {
        if (accessibilityProperties == null) return null;
        if (accessibilityProperties.accessibleText == null) return null;
        return accessibleTextProperty().get();
    }

    
    public final ObjectProperty<String> accessibleTextProperty() {
        return getAccessibilityProperties().getAccessibleText();
    }

    public final void setAccessibleHelp(String value) {
        accessibleHelpProperty().set(value);
    }

    public final String getAccessibleHelp() {
        if (accessibilityProperties == null) return null;
        if (accessibilityProperties.accessibleHelp == null) return null;
        return accessibleHelpProperty().get();
    }

    
    public final ObjectProperty<String> accessibleHelpProperty() {
        return getAccessibilityProperties().getAccessibleHelp();
    }

    AccessibilityProperties accessibilityProperties;
    private AccessibilityProperties getAccessibilityProperties() {
        if (accessibilityProperties == null) {
            accessibilityProperties = new AccessibilityProperties();
        }
        return accessibilityProperties;
    }

    private class AccessibilityProperties {
        ObjectProperty<String> accessibleRoleDescription;
        ObjectProperty<String> getAccessibleRoleDescription() {
            if (accessibleRoleDescription == null) {
                accessibleRoleDescription = new SimpleObjectProperty<String>(MappedNode.this, "accessibleRoleDescription", null);
            }
            return accessibleRoleDescription;
        }
        ObjectProperty<String> accessibleText;
        ObjectProperty<String> getAccessibleText() {
            if (accessibleText == null) {
                accessibleText = new SimpleObjectProperty<String>(MappedNode.this, "accessibleText", null);
            }
            return accessibleText;
        }
        ObjectProperty<String> accessibleHelp;
        ObjectProperty<String> getAccessibleHelp() {
            if (accessibleHelp == null) {
                accessibleHelp = new SimpleObjectProperty<String>(MappedNode.this, "accessibleHelp", null);
            }
            return accessibleHelp;
        }
    }

    
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        switch (attribute) {
            case ROLE: return getAccessibleRole();
            case ROLE_DESCRIPTION: return getAccessibleRoleDescription();
            case TEXT: return getAccessibleText();
            case HELP: return getAccessibleHelp();
            case PARENT: return getParent();
            case SCENE: return getScene();
            case BOUNDS: return localToScreen(getBoundsInLocal());
            case DISABLED: return isDisabled();
            case FOCUSED: return isFocused();
            case VISIBLE: return isVisible();
            case LABELED_BY: return labeledBy;
            default: return null;
        }
    }

    
    public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
        switch (action) {
            case REQUEST_FOCUS:
                if (isFocusTraversable()) {
                    requestFocus();
                }
                break;
            case SHOW_MENU: {
                Bounds b = getBoundsInLocal();
                Point2D pt = localToScreen(b.getMaxX(), b.getMaxY());
                ContextMenuEvent event =
                        new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                                b.getMaxX(), b.getMaxY(), pt.getX(), pt.getY(),
                                false, new PickResult(this, b.getMaxX(), b.getMaxY()));
                Event.fireEvent(this, event);
                break;
            }
            default:
        }
    }

    
    public final void notifyAccessibleAttributeChanged(AccessibleAttribute attributes) {
        if (accessible == null) {
            MappedScene scene = getScene();
            if (scene != null) {
                accessible = scene.removeAccessible(this);
            }
        }
        if (accessible != null) {
            accessible.sendNotification(attributes);
        }
    }

    Accessible accessible;
    Accessible getAccessible() {
        if (accessible == null) {
            MappedScene scene = getScene();
            /* It is possible the node was reparented and getAccessible()
             * is called before the pulse. Try to recycle the accessible
             * before creating a new one.
             * Note: this code relies that an accessible can never be on
             * more than one MappedScene#accMap. Thus, the only way
             * scene#removeAccessible() returns non-null is if the node
             * old scene and new scene are the same object.
             */
            if (scene != null) {
                accessible = scene.removeAccessible(this);
            }
        }
        if (accessible == null) {
            accessible = Application.GetApplication().createAccessible();
            accessible.setEventHandler(new Accessible.EventHandler() {
                @SuppressWarnings("removal")
                @Override public AccessControlContext getAccessControlContext() {
                    MappedScene scene = getScene();
                    if (scene == null) {
                        /* This can happen during the release process of an accessible object. */
                        throw new RuntimeException("Accessbility requested for node not on a scene");
                    }
                    if (scene.getPeer() != null) {
                        return scene.getPeer().getAccessControlContext();
                    } else {
                        /* In some rare cases the accessible for a MappedNode is needed
                         * before its scene is made visible. For example, the screen reader
                         * might ask a Menu for its ContextMenu before the ContextMenu
                         * is made visible. That is a problem because the MappedWindow for the
                         * ContextMenu is only created immediately before the first time
                         * it is shown.
                         */
                        return scene.acc;
                    }
                }
                @Override public Object getAttribute(AccessibleAttribute attribute, Object... parameters) {
                    return queryAccessibleAttribute(attribute, parameters);
                }
                @Override public void executeAction(AccessibleAction action, Object... parameters) {
                    executeAccessibleAction(action, parameters);
                }
                @Override public String toString() {
                    String klassName = MappedNode.this.getClass().getName();
                    return klassName.substring(klassName.lastIndexOf('.')+1);
                }
            });
        }
        return accessible;
    }

    void releaseAccessible() {
        Accessible acc = this.accessible;
        if (acc != null) {
            accessible = null;
            acc.dispose();
        }
    }

}
