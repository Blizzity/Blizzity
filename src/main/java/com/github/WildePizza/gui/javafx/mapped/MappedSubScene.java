package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.css.MappedStyleManager;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;
import com.sun.javafx.scene.traversal.TopMostTraversalEngine;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.css.Stylesheet;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point3D;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Paint;

import java.io.File;
import java.util.ArrayList;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGSubScene;
import com.sun.javafx.tk.Toolkit;

import com.sun.javafx.logging.PlatformLogger;


public class MappedSubScene extends MappedNode {
    static {
        // This is used by classes in different packages to get access to
        // private and package private methods.
        MappedSubSceneHelper.setSubSceneAccessor(new MappedSubSceneHelper.SubSceneAccessor() {
            @Override
            public MappedNGNode doCreatePeer(MappedNode node) {
                return ((MappedSubScene) node).doCreatePeer();
            }

            @Override
            public void doUpdatePeer(MappedNode node) {
                ((MappedSubScene) node).doUpdatePeer();
            }

            @Override
            public BaseBounds doComputeGeomBounds(MappedNode node,
                                                  BaseBounds bounds, BaseTransform tx) {
                return ((MappedSubScene) node).doComputeGeomBounds(bounds, tx);
            }

            @Override
            public boolean doComputeContains(MappedNode node, double localX, double localY) {
                return ((MappedSubScene) node).doComputeContains(localX, localY);
            }

            @Override
            public void doProcessCSS(MappedNode node) {
                ((MappedSubScene) node).doProcessCSS();
            }

            @Override
            public void doPickNodeLocal(MappedNode node, PickRay localPickRay,
                                        PickResultChooser result) {
                ((MappedSubScene) node).doPickNodeLocal(localPickRay, result);
            }

            @Override
            public boolean isDepthBuffer(MappedSubScene subScene) {
                return subScene.isDepthBufferInternal();
            };

            @Override
            public MappedCamera getEffectiveCamera(MappedSubScene subScene) {
                return subScene.getEffectiveCamera();
            }

        });
    }

    {
        // To initialize the class helper at the begining each constructor of this class
        MappedSubSceneHelper.initHelper(this);
    }
    
    public MappedSubScene(@NamedArg("root") MappedParent root, @NamedArg("width") double width, @NamedArg("height") double height) {
        this(root, width, height, false, SceneAntialiasing.DISABLED);
    }
    public MappedSubScene(@NamedArg("root") MappedParent root, @NamedArg("width") double width, @NamedArg("height") double height,
                    @NamedArg("depthBuffer") boolean depthBuffer, @NamedArg("antiAliasing") SceneAntialiasing antiAliasing)
    {
        this.depthBuffer = depthBuffer;
        this.antiAliasing = antiAliasing;
        boolean isAntiAliasing = !(antiAliasing == null || antiAliasing == SceneAntialiasing.DISABLED);
        setRoot(root);
        setWidth(width);
        setHeight(height);

        if ((depthBuffer || isAntiAliasing) && !is3DSupported) {
            String logname = MappedSubScene.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "ConditionalFeature.SCENE3D");
        }
        if (isAntiAliasing && !Toolkit.getToolkit().isMSAASupported()) {
            String logname = MappedSubScene.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "antiAliasing");
        }
    }

    private static boolean is3DSupported =
            Platform.isSupported(ConditionalFeature.SCENE3D);

    private final SceneAntialiasing antiAliasing;

    
    public final SceneAntialiasing getAntiAliasing() {
        return antiAliasing;
    }

    private final boolean depthBuffer;

    
    public final boolean isDepthBuffer() {
        return depthBuffer;
    }

    private boolean isDepthBufferInternal() {
        return is3DSupported ? depthBuffer : false;
    }

    
    private ObjectProperty<MappedParent> root;

    public final void setRoot(MappedParent value) {
        rootProperty().set(value);
    }

    public final MappedParent getRoot() {
        return root == null ? null : root.get();
    }

    public final ObjectProperty<MappedParent> rootProperty() {
        if (root == null) {
            root = new ObjectPropertyBase<MappedParent>() {
                private MappedParent oldRoot;

                private void forceUnbind() {
                    System.err.println("Unbinding illegal root.");
                    unbind();
                }

                @Override
                protected void invalidated() {
                    MappedParent _value = get();

                    if (_value == null) {
                        if (isBound()) { forceUnbind(); }
                        throw new NullPointerException("MappedScene's root cannot be null");
                    }
                    if (_value.getParent() != null) {
                        if (isBound()) { forceUnbind(); }
                        throw new IllegalArgumentException(_value +
                                "is already inside a scene-graph and cannot be set as root");
                    }
                    if (_value.getClipParent() != null) {
                        if (isBound()) forceUnbind();
                        throw new IllegalArgumentException(_value +
                                "is set as a clip on another node, so cannot be set as root");
                    }
                    if ((_value.getScene() != null &&
                            _value.getScene().getRoot() == _value) ||
                            (_value.getSubScene() != null &&
                                    _value.getSubScene().getRoot() == _value &&
                                    _value.getSubScene() != MappedSubScene.this))
                    {
                        if (isBound()) { forceUnbind(); }
                        throw new IllegalArgumentException(_value +
                                "is already set as root of another scene or subScene");
                    }

                    // disabled and isTreeVisible properties are inherited
                    _value.setTreeVisible(isTreeVisible());
                    _value.setDisabled(isDisabled());

                    if (oldRoot != null) {
                        MappedStyleManager.getInstance().forget(MappedSubScene.this);
                        oldRoot.setScenes(null, null);
                    }
                    oldRoot = _value;
                    _value.getStyleClass().add(0, "root");
                    _value.setScenes(getScene(), MappedSubScene.this);
                    markDirty(SubSceneDirtyBits.ROOT_SG_DIRTY);
                    _value.resize(getWidth(), getHeight()); // maybe no-op if root is not resizable
                    _value.requestLayout();
                }

                @Override
                public Object getBean() {
                    return MappedSubScene.this;
                }

                @Override
                public String getName() {
                    return "root";
                }
            };
        }
        return root;
    }

    
    private ObjectProperty<MappedCamera> camera;

    public final void setCamera(MappedCamera value) {
        cameraProperty().set(value);
    }

    public final MappedCamera getCamera() {
        return camera == null ? null : camera.get();
    }

    public final ObjectProperty<MappedCamera> cameraProperty() {
        if (camera == null) {
            camera = new ObjectPropertyBase<MappedCamera>() {
                MappedCamera oldCamera = null;

                @Override
                protected void invalidated() {
                    MappedCamera _value = get();
                    if (_value != null) {
                        if (_value instanceof MappedPerspectiveCamera
                                && !MappedSubScene.is3DSupported) {
                            String logname = MappedSubScene.class.getName();
                            PlatformLogger.getLogger(logname).warning("System can't support "
                                    + "ConditionalFeature.SCENE3D");
                        }
                        // Illegal value if it belongs to any scene or other subscene
                        if ((_value.getScene() != null || _value.getSubScene() != null)
                                && (_value.getScene() != getScene() || _value.getSubScene() != MappedSubScene.this)) {
                            throw new IllegalArgumentException(_value
                                    + "is already part of other scene or subscene");
                        }
                        // throws exception if the camera already has a different owner
                        _value.setOwnerSubScene(MappedSubScene.this);
                        _value.setViewWidth(getWidth());
                        _value.setViewHeight(getHeight());
                    }
                    markDirty(SubSceneDirtyBits.CAMERA_DIRTY);
                    if (oldCamera != null && oldCamera != _value) {
                        oldCamera.setOwnerSubScene(null);
                    }
                    oldCamera = _value;
                }

                @Override
                public Object getBean() {
                    return MappedSubScene.this;
                }

                @Override
                public String getName() {
                    return "camera";
                }
            };
        }
        return camera;
    }

    private MappedCamera defaultCamera;

    MappedCamera getEffectiveCamera() {
        final MappedCamera cam = getCamera();
        if (cam == null
                || (cam instanceof MappedPerspectiveCamera && !is3DSupported)) {
            if (defaultCamera == null) {
                defaultCamera = new ParallelCamera();
                defaultCamera.setOwnerSubScene(this);
                defaultCamera.setViewWidth(getWidth());
                defaultCamera.setViewHeight(getHeight());
            }
            return defaultCamera;
        }

        return cam;
    }

    // Used by the camera
    final void markContentDirty() {
        markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
    }

    
    private DoubleProperty width;

    public final void setWidth(double value) {
        widthProperty().set(value);
    }

    public final double getWidth() {
        return width == null ? 0.0 : width.get();
    }

    public final DoubleProperty widthProperty() {
        if (width == null) {
            width = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    final MappedParent _root = getRoot();
                    //TODO - use a better method to update mirroring
                    if (_root.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
                        MappedNodeHelper.transformsChanged(_root);
                    }
                    if (_root.isResizable()) {
                        _root.resize(get() - _root.getLayoutX() - _root.getTranslateX(), _root.getLayoutBounds().getHeight());
                    }
                    markDirty(SubSceneDirtyBits.SIZE_DIRTY);
                    MappedNodeHelper.geomChanged(MappedSubScene.this);

                    getEffectiveCamera().setViewWidth(get());
                }

                @Override
                public Object getBean() {
                    return MappedSubScene.this;
                }

                @Override
                public String getName() {
                    return "width";
                }
            };
        }
        return width;
    }

    
    private DoubleProperty height;

    public final void setHeight(double value) {
        heightProperty().set(value);
    }

    public final double getHeight() {
        return height == null ? 0.0 : height.get();
    }

    public final DoubleProperty heightProperty() {
        if (height == null) {
            height = new DoublePropertyBase() {

                @Override
                public void invalidated() {
                    final MappedParent _root = getRoot();
                    if (_root.isResizable()) {
                        _root.resize(_root.getLayoutBounds().getWidth(), get() - _root.getLayoutY() - _root.getTranslateY());
                    }
                    markDirty(SubSceneDirtyBits.SIZE_DIRTY);
                    MappedNodeHelper.geomChanged(MappedSubScene.this);

                    getEffectiveCamera().setViewHeight(get());
                }

                @Override
                public Object getBean() {
                    return MappedSubScene.this;
                }

                @Override
                public String getName() {
                    return "height";
                }
            };
        }
        return height;
    }

    
    private ObjectProperty<Paint> fill;

    public final void setFill(Paint value) {
        fillProperty().set(value);
    }

    public final Paint getFill() {
        return fill == null ? null : fill.get();
    }

    public final ObjectProperty<Paint> fillProperty() {
        if (fill == null) {
            fill = new ObjectPropertyBase<Paint>(null) {

                @Override
                protected void invalidated() {
                    markDirty(SubSceneDirtyBits.FILL_DIRTY);
                }

                @Override
                public Object getBean() {
                    return MappedSubScene.this;
                }

                @Override
                public String getName() {
                    return "fill";
                }
            };
        }
        return fill;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        // TODO deal with clip node

        dirtyNodes = false;
        if (isDirty()) {
            NGSubScene peer = getPeer();
            final MappedCamera cam = getEffectiveCamera();
            boolean contentChanged = false;
            if (cam.getSubScene() == null &&
                    isDirty(SubSceneDirtyBits.CONTENT_DIRTY)) {
                // When camera is not a part of the graph, then its
                // owner(subscene) must take care of syncing it. And when a
                // property on the camera changes it will mark subscenes
                // CONTENT_DIRTY.
                cam.syncPeer();
            }
            if (isDirty(SubSceneDirtyBits.FILL_DIRTY)) {
                Object platformPaint = getFill() == null ? null :
                        Toolkit.getPaintAccessor().getPlatformPaint(getFill());
                peer.setFillPaint(platformPaint);
                contentChanged = true;
            }
            if (isDirty(SubSceneDirtyBits.SIZE_DIRTY)) {
                // Note change in size is a geom change and is handled by peer
                peer.setWidth((float)getWidth());
                peer.setHeight((float)getHeight());
            }
            if (isDirty(SubSceneDirtyBits.CAMERA_DIRTY)) {
                peer.setCamera((MappedNGCamera) cam.getPeer());
                contentChanged = true;
            }
            if (isDirty(SubSceneDirtyBits.ROOT_SG_DIRTY)) {
                peer.setRoot(getRoot().getPeer());
                contentChanged = true;
            }
            contentChanged |= syncLights();
            if (contentChanged || isDirty(SubSceneDirtyBits.CONTENT_DIRTY)) {
                peer.markContentDirty();
            }

            clearDirtyBits();
        }

    }

    @Override
    void nodeResolvedOrientationChanged() {
        getRoot().parentResolvedOrientationInvalidated();
    }

    /* *********************************************************************
     *                         CSS                                         *
     **********************************************************************/
    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doProcessCSS() {
        // Nothing to do...
        if (cssFlag == CssFlags.CLEAN) { return; }

        if (getRoot().cssFlag == CssFlags.CLEAN) {
            getRoot().cssFlag = cssFlag;
        }
        MappedSubSceneHelper.superProcessCSS(this);
        getRoot().processCSS();
    }

    @Override
    void processCSS() {
        MappedParent root = getRoot();
        if (root.isDirty(DirtyBits.NODE_CSS)) {
            root.clearDirty(DirtyBits.NODE_CSS);
            if (cssFlag == CssFlags.CLEAN) { cssFlag = CssFlags.UPDATE; }
        }
        super.processCSS();
    }

    private ObjectProperty<String> userAgentStylesheet = null;
    
    public final ObjectProperty<String> userAgentStylesheetProperty() {
        if (userAgentStylesheet == null) {
            userAgentStylesheet = new SimpleObjectProperty<String>(MappedSubScene.this, "userAgentStylesheet", null) {
                @Override protected void invalidated() {
                    MappedStyleManager.getInstance().forget(MappedSubScene.this);
                    reapplyCSS();
                }
            };
        }
        return userAgentStylesheet;
    }

    
    public final String getUserAgentStylesheet() {
        return userAgentStylesheet == null ? null : userAgentStylesheet.get();
    }

    
    public final void setUserAgentStylesheet(String url) {
        userAgentStylesheetProperty().set(url);
    }

    @Override void updateBounds() {
        super.updateBounds();
        getRoot().updateBounds();
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private MappedNGNode doCreatePeer() {
        if (!is3DSupported) {
            return new NGSubScene(false, false);
        }
        boolean aa = !(antiAliasing == null || antiAliasing == SceneAntialiasing.DISABLED);
        return new NGSubScene(depthBuffer, aa && Toolkit.getToolkit().isMSAASupported());
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        int w = (int)Math.ceil(width.get());
        int h = (int)Math.ceil(height.get());
        bounds = bounds.deriveWithNewBounds(0.0f, 0.0f, 0.0f,
                w, h, 0.0f);
        bounds = tx.transform(bounds, bounds);
        return bounds;
    }

    /* *********************************************************************
     *                         Dirty Bits                                  *
     **********************************************************************/
    boolean dirtyLayout = false;
    void setDirtyLayout(MappedParent p) {
        if (!dirtyLayout && p != null && p.getSubScene() == this &&
                this.getScene() != null) {
            dirtyLayout = true;
            markDirtyLayoutBranch();
            markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
        }
    }

    private boolean dirtyNodes = false;
    void setDirty(MappedNode n) {
        if (!dirtyNodes && n != null && n.getSubScene() == this &&
                this.getScene() != null) {
            dirtyNodes = true;
            markDirty(SubSceneDirtyBits.CONTENT_DIRTY);
        }
    }

    void layoutPass() {
        if (dirtyLayout) {
            MappedParent r = getRoot();
            if (r != null) {
                r.layout();
            }
            dirtyLayout = false;
        }
    }

    private TopMostTraversalEngine traversalEngine = new SubSceneTraversalEngine(this);

    boolean traverse(MappedNode node, Direction dir) {
        return traversalEngine.trav(node, dir) != null;
    }

    private enum SubSceneDirtyBits {
        SIZE_DIRTY,
        FILL_DIRTY,
        ROOT_SG_DIRTY,
        CAMERA_DIRTY,
        LIGHTS_DIRTY,
        CONTENT_DIRTY;

        private int mask;

        private SubSceneDirtyBits() { mask = 1 << ordinal(); }

        public final int getMask() { return mask; }
    }

    private int dirtyBits = ~0;

    private void clearDirtyBits() { dirtyBits = 0; }

    private boolean isDirty() { return dirtyBits != 0; }

    // Should not be called directly, instead use markDirty
    private void setDirty(SubSceneDirtyBits dirtyBit) {
        this.dirtyBits |= dirtyBit.getMask();
    }

    private boolean isDirty(SubSceneDirtyBits dirtyBit) {
        return ((this.dirtyBits & dirtyBit.getMask()) != 0);
    }

    private void markDirty(SubSceneDirtyBits dirtyBit) {
        if (!isDirty()) {
            // Force MappedSubScene to redraw
            MappedNodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
        }
        setDirty(dirtyBit);
    }

    /* *********************************************************************
     *                           Picking                                   *
     **********************************************************************/

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private boolean doComputeContains(double localX, double localY) {
        if (subSceneComputeContains(localX, localY)) {
            return true;
        } else {
            return MappedNodeHelper.computeContains(getRoot(), localX, localY);
        }
    }

    
    private boolean subSceneComputeContains(double localX, double localY) {
        if (localX < 0 || localY < 0 || localX > getWidth() || localY > getHeight()) {
            return false;
        }
        return getFill() != null;
    }

    /*
     * Generates a pick ray based on local coordinates and camera. Then finds a
     * top-most child node that intersects the pick ray.
     */
    private PickResult pickRootSG(double localX, double localY) {
        final double viewWidth = getWidth();
        final double viewHeight = getHeight();
        if (localX < 0 || localY < 0 || localX > viewWidth || localY > viewHeight) {
            return null;
        }
        final PickResultChooser result = new PickResultChooser();
        final PickRay pickRay = getEffectiveCamera().computePickRay(localX, localY, new PickRay());
        pickRay.getDirectionNoClone().normalize();
        getRoot().pickNode(pickRay, result);
        return result.toPickResult();
    }

    
    private void doPickNodeLocal(PickRay localPickRay, PickResultChooser result) {
        final double boundsDistance = intersectsBounds(localPickRay);
        if (!Double.isNaN(boundsDistance) && result.isCloser(boundsDistance)) {
            final Point3D intersectPt = PickResultChooser.computePoint(
                    localPickRay, boundsDistance);
            final PickResult subSceneResult =
                    pickRootSG(intersectPt.getX(), intersectPt.getY());
            if (subSceneResult != null) {
                result.offerSubScenePickResult(this, subSceneResult, boundsDistance);
            } else if (isPickOnBounds() ||
                    subSceneComputeContains(intersectPt.getX(), intersectPt.getY())) {
                result.offer(this, boundsDistance, intersectPt);
            }
        }
    }

    private List<LightBase> lights = new ArrayList<>();

    // @param light must not be null
    final void addLight(LightBase light) {
        if (!lights.contains(light)) {
            markDirty(SubSceneDirtyBits.LIGHTS_DIRTY);
            lights.add(light);
        }
    }

    final void removeLight(LightBase light) {
        if (lights.remove(light)) {
            markDirty(SubSceneDirtyBits.LIGHTS_DIRTY);
        }
    }

    
    private boolean syncLights() {
        boolean lightOwnerChanged = false;
        if (!isDirty(SubSceneDirtyBits.LIGHTS_DIRTY)) {
            return lightOwnerChanged;
        }
        NGSubScene pgSubScene = getPeer();
        NGLightBase peerLights[] = pgSubScene.getLights();
        if (!lights.isEmpty() || (peerLights != null)) {
            if (lights.isEmpty()) {
                pgSubScene.setLights(null);
            } else {
                if (peerLights == null || peerLights.length < lights.size()) {
                    peerLights = new NGLightBase[lights.size()];
                }
                int i = 0;
                for (; i < lights.size(); i++) {
                    peerLights[i] = lights.get(i).getPeer();
                }
                // Clear the rest of the list
                while (i < peerLights.length && peerLights[i] != null) {
                    peerLights[i++] = null;
                }
                pgSubScene.setLights(peerLights);
            }
            lightOwnerChanged = true;
        }
        return lightOwnerChanged;
    }

}
