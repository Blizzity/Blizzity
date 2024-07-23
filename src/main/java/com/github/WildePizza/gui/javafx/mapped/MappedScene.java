package com.github.WildePizza.gui.javafx.mapped;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Accessible;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.Utils;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.css.MappedStyleManager;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.event.EventQueue;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.LayoutFlags;
import com.sun.javafx.scene.SceneEventDispatcher;
import com.sun.javafx.scene.input.DragboardHelper;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.javafx.scene.input.InputEventUtils;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.SceneTraversalEngine;
import com.sun.javafx.scene.traversal.TopMostTraversalEngine;
import com.sun.javafx.sg.prism.MappedNGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.tk.*;
import com.sun.prism.impl.PrismSettings;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;
import javafx.css.Stylesheet;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.MappedCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.MappedWindow;
import javafx.util.Callback;
import javafx.util.Duration;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;

import java.io.File;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.javafx.logging.PulseLogger;

import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.scene.input.ClipboardHelper;
import com.sun.javafx.scene.input.TouchPointHelper;
import java.lang.ref.WeakReference;


@DefaultProperty("root")
public class MappedScene implements EventTarget {

    private double widthSetByUser = -1.0;
    private double heightSetByUser = -1.0;
    private boolean sizeInitialized = false;
    private final boolean depthBuffer;
    private final SceneAntialiasing antiAliasing;

    private EnumSet<DirtyBits> dirtyBits = EnumSet.noneOf(DirtyBits.class);

    @SuppressWarnings("removal")
    final AccessControlContext acc = AccessController.getContext();

    private MappedCamera defaultCamera;

    
    private MappedNode transientFocusContainer;

    //Neither width nor height are initialized and will be calculated according to content when this MappedScene
    //is shown for the first time.
//    public MappedScene() {
//        //this(-1, -1, (MappedParent) new Group());
//        this(-1, -1, (MappedParent)null);
//    }

    
    public MappedScene(@NamedArg("root") MappedParent root) {
        this(root, -1, -1, Color.WHITE, false, SceneAntialiasing.DISABLED);
    }

//Public constructor initializing public-init properties
//When width < 0, and or height < 0 is passed, then width and/or height are understood as unitialized
//Unitialized dimension is calculated when MappedScene is shown for the first time.
//    public MappedScene(
//            @Default("-1") double width,
//            @Default("-1") double height) {
//        //this(width, height, (MappedParent)new Group());
//        this(width, height, (MappedParent)null);
//    }
//
//    public MappedScene(double width, double height, Paint fill) {
//        //this(width, height, (MappedParent) new Group());
//        this(width, height, (MappedParent)null);
//        setFill(fill);
//    }

    
    public MappedScene(@NamedArg("root") MappedParent root, @NamedArg("width") double width, @NamedArg("height") double height) {
        this(root, width, height, Color.WHITE, false, SceneAntialiasing.DISABLED);
    }

    
    public MappedScene(@NamedArg("root") MappedParent root, @NamedArg(value="fill", defaultValue="WHITE") Paint fill) {
        this(root, -1, -1, fill, false, SceneAntialiasing.DISABLED);
    }

    
    public MappedScene(@NamedArg("root") MappedParent root, @NamedArg("width") double width, @NamedArg("height") double height,
                 @NamedArg(value="fill", defaultValue="WHITE") Paint fill) {
        this(root, width, height, fill, false, SceneAntialiasing.DISABLED);
    }
    public MappedScene(@NamedArg("root") MappedParent root, @NamedArg(value="width", defaultValue="-1") double width, @NamedArg(value="height", defaultValue="-1") double height, @NamedArg("depthBuffer") boolean depthBuffer) {
        this(root, width, height, Color.WHITE, depthBuffer, SceneAntialiasing.DISABLED);
    }
    public MappedScene(@NamedArg("root") MappedParent root, @NamedArg(value="width", defaultValue="-1") double width, @NamedArg(value="height", defaultValue="-1") double height,
                 @NamedArg("depthBuffer") boolean depthBuffer,
                 @NamedArg(value="antiAliasing", defaultValue="DISABLED") SceneAntialiasing antiAliasing) {
        this(root, width, height, Color.WHITE, depthBuffer, antiAliasing);

        if (antiAliasing != null && antiAliasing != SceneAntialiasing.DISABLED &&
                !Toolkit.getToolkit().isMSAASupported())
        {
            String logname = MappedScene.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "antiAliasing");
        }
    }

    private MappedScene(MappedParent root, double width, double height, Paint fill,
                  boolean depthBuffer, SceneAntialiasing antiAliasing) {
        this.depthBuffer = depthBuffer;
        this.antiAliasing = antiAliasing;
        if (root == null) {
            throw new NullPointerException("Root cannot be null");
        }

        if ((depthBuffer || (antiAliasing != null && antiAliasing != SceneAntialiasing.DISABLED))
                && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
            String logname = MappedScene.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "ConditionalFeature.SCENE3D");
        }

        init();
        setRoot(root);
        init(width, height);
        setFill(fill);
    }

    static {
        MappedPerformanceTracker.setSceneAccessor(new MappedPerformanceTracker.SceneAccessor() {
            public void setPerfTracker(MappedScene scene, MappedPerformanceTracker tracker) {
                synchronized (trackerMonitor) {
                    scene.tracker = tracker;
                }
            }
            public MappedPerformanceTracker getPerfTracker(MappedScene scene) {
                synchronized (trackerMonitor) {
                    return scene.tracker;
                }
            }
        });
        MappedSceneHelper.setSceneAccessor(
                new MappedSceneHelper.SceneAccessor() {
                    @Override
                    public void enableInputMethodEvents(MappedScene scene, boolean enable) {
                        scene.enableInputMethodEvents(enable);
                    }

                    @Override
                    public void processKeyEvent(MappedScene scene, KeyEvent e) {
                        scene.processKeyEvent(e);
                    }

                    @Override
                    public void processMouseEvent(MappedScene scene, MouseEvent e) {
                        scene.processMouseEvent(e);
                    }

                    @Override
                    public void preferredSize(MappedScene scene) {
                        scene.preferredSize();
                    }

                    @Override
                    public void disposePeer(MappedScene scene) {
                        scene.disposePeer();
                    }

                    @Override
                    public void initPeer(MappedScene scene) {
                        scene.initPeer();
                    }

                    @Override
                    public void setWindow(MappedScene scene, MappedWindow window) {
                        scene.setWindow(window);
                    }

                    @Override
                    public TKScene getPeer(MappedScene scene) {
                        return scene.getPeer();
                    }

                    @Override
                    public void setAllowPGAccess(boolean flag) {
                        MappedScene.setAllowPGAccess(flag);
                    }

                    @Override
                    public void parentEffectiveOrientationInvalidated(
                            final MappedScene scene) {
                        scene.parentEffectiveOrientationInvalidated();
                    }

                    @Override
                    public MappedCamera getEffectiveCamera(MappedScene scene) {
                        return scene.getEffectiveCamera();
                    }

                    @Override
                    public MappedScene createPopupScene(MappedParent root) {
                        return new MappedScene(root) {
                            @Override
                            void doLayoutPass() {
                                resizeRootToPreferredSize(getRoot());
                                super.doLayoutPass();
                            }

                            @Override
                            void resizeRootOnSceneSizeChange(
                                    double newWidth,
                                    double newHeight) {
                                // don't resize
                            }
                        };
                    }

                    @Override
                    public void setTransientFocusContainer(MappedScene scene, MappedNode node) {
                        if (scene != null) {
                            scene.transientFocusContainer = node;
                        }
                    }

                    @Override
                    public Accessible getAccessible(MappedScene scene) {
                        return scene.getAccessible();
                    }
                });
    }

    // Reserve space for 30 nodes in the dirtyNodes set.
    private static final int MIN_DIRTY_CAPACITY = 30;

    // For debugging
    private static boolean inSynchronizer = false;
    private static boolean inMousePick = false;
    private static boolean allowPGAccess = false;
    private static int pgAccessCount = 0;

    
    static boolean isPGAccessAllowed() {
        return inSynchronizer || inMousePick || allowPGAccess;
    }

    static void setAllowPGAccess(boolean flag) {
        if (Utils.assertionEnabled()) {
            if (flag) {
                pgAccessCount++;
                allowPGAccess = true;
            }
            else {
                if (pgAccessCount <= 0) {
                    throw new java.lang.AssertionError("*** pgAccessCount underflow");
                }
                if (--pgAccessCount == 0) {
                    allowPGAccess = false;
                }
            }
        }
    }

    
    private static final boolean PLATFORM_DRAG_GESTURE_INITIATION = false;

    
    private MappedNode[] dirtyNodes;
    private int dirtyNodesSize;

    
    void addToDirtyList(MappedNode n) {
        if (dirtyNodes == null || dirtyNodesSize == 0) {
            if (peer != null) {
                Toolkit.getToolkit().requestNextPulse();
            }
        }

        if (dirtyNodes != null) {
            if (dirtyNodesSize == dirtyNodes.length) {
                MappedNode[] tmp = new MappedNode[dirtyNodesSize + (dirtyNodesSize >> 1)];
                System.arraycopy(dirtyNodes, 0, tmp, 0, dirtyNodesSize);
                dirtyNodes = tmp;
            }
            dirtyNodes[dirtyNodesSize++] = n;
        }
    }

    private void doCSSPass() {
        final MappedParent sceneRoot = getRoot();
        //
        // RT-17547: when the tree is synchronized, the dirty bits are
        // are cleared but the cssFlag might still be something other than
        // clean.
        //
        // Before RT-17547, the code checked the dirty bit. But this is
        // superfluous since the dirty bit will be set if the flag is not clean,
        // but the flag will never be anything other than clean if the dirty
        // bit is not set. The dirty bit is still needed, however, since setting
        // it ensures a pulse if no other dirty bits have been set.
        //
        // For the purpose of showing the change, the dirty bit
        // check code was commented out and not removed.
        //
//        if (sceneRoot.isDirty(com.sun.javafx.scene.DirtyBits.NODE_CSS)) {
        if (sceneRoot.cssFlag != CssFlags.CLEAN) {
            // The dirty bit isn't checked but we must ensure it is cleared.
            // The cssFlag is set to clean in either MappedNode.processCSS or
            // MappedNodeHelper.processCSS
            sceneRoot.clearDirty(com.sun.javafx.scene.DirtyBits.NODE_CSS);
            sceneRoot.processCSS();
        }
    }

    void doLayoutPass() {
        final MappedParent r = getRoot();
        if (r != null) {
            r.layout();
        }
    }

    
    private TKScene peer;

    /*
     * Get MappedScene's peer
     */
    TKScene getPeer() {
        return peer;
    }

    
    ScenePulseListener scenePulseListener = new ScenePulseListener();

    private List<Runnable> preLayoutPulseListeners;
    private List<Runnable> postLayoutPulseListeners;

    
    public final void addPreLayoutPulseListener(Runnable r) {
        Toolkit.getToolkit().checkFxUserThread();

        if (r == null) {
            throw new NullPointerException("MappedScene pulse listener should not be null");
        }
        if (preLayoutPulseListeners == null) {
            preLayoutPulseListeners = new CopyOnWriteArrayList<>();
        }
        preLayoutPulseListeners.add(r);
    }

    
    public final void removePreLayoutPulseListener(Runnable r) {
        Toolkit.getToolkit().checkFxUserThread();

        if (preLayoutPulseListeners == null) {
            return;
        }
        preLayoutPulseListeners.remove(r);
    }

    
    public final void addPostLayoutPulseListener(Runnable r) {
        Toolkit.getToolkit().checkFxUserThread();

        if (r == null) {
            throw new NullPointerException("MappedScene pulse listener should not be null");
        }
        if (postLayoutPulseListeners == null) {
            postLayoutPulseListeners = new CopyOnWriteArrayList<>();
        }
        postLayoutPulseListeners.add(r);
    }

    
    public final void removePostLayoutPulseListener(Runnable r) {
        Toolkit.getToolkit().checkFxUserThread();

        if (postLayoutPulseListeners == null) {
            return;
        }
        postLayoutPulseListeners.remove(r);
    }

    
    public final SceneAntialiasing getAntiAliasing() {
        return antiAliasing;
    }

    private boolean getAntiAliasingInternal() {
        return (antiAliasing != null &&
                Toolkit.getToolkit().isMSAASupported() &&
                Platform.isSupported(ConditionalFeature.SCENE3D)) ?
                antiAliasing != SceneAntialiasing.DISABLED : false;
    }

    
    private ReadOnlyObjectWrapper<MappedWindow> window;

    void setWindow(MappedWindow value) {
        windowPropertyImpl().set(value);
    }

    public final MappedWindow getWindow() {
        return window == null ? null : window.get();
    }

    public final ReadOnlyObjectProperty<MappedWindow> windowProperty() {
        return windowPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<MappedWindow> windowPropertyImpl() {
        if (window == null) {
            window = new ReadOnlyObjectWrapper<MappedWindow>() {
                private MappedWindow oldWindow;

                @Override protected void invalidated() {
                    final MappedWindow newWindow = get();
                    getKeyHandler().windowForSceneChanged(oldWindow, newWindow);
                    if (oldWindow != null) {
                        disposePeer();
                    }
                    if (newWindow != null) {
                        initPeer();
                    }
                    parentEffectiveOrientationInvalidated();

                    oldWindow = newWindow;
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "window";
                }
            };
        }
        return window;
    }

    void initPeer() {
        assert peer == null;

        MappedWindow window = getWindow();
        // initPeer() is only called from MappedWindow, either when the window
        // is being shown, or the window scene is being changed. In any case
        // this scene's window cannot be null.
        assert window != null;

        TKStage windowPeer = WindowHelper.getPeer(window);
        if (windowPeer == null) {
            // This is fine, the window is not visible. initPeer() will
            // be called again later, when the window is being shown.
            return;
        }

        final boolean isTransparentWindowsSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        if (!isTransparentWindowsSupported) {
            PlatformImpl.addNoTransparencyStylesheetToScene(this);
        }

        MappedPerformanceTracker.logEvent("MappedScene.initPeer started");

        setAllowPGAccess(true);

        Toolkit tk = Toolkit.getToolkit();
        peer = windowPeer.createTKScene(isDepthBufferInternal(), getAntiAliasingInternal(), acc);
        MappedPerformanceTracker.logEvent("MappedScene.initPeer TKScene created");
        peer.setTKSceneListener(new ScenePeerListener());
        peer.setTKScenePaintListener(new ScenePeerPaintListener());
        MappedPerformanceTracker.logEvent("MappedScene.initPeer TKScene set");
        peer.setRoot(getRoot().getPeer());
        peer.setFillPaint(getFill() == null ? null : tk.getPaint(getFill()));
        MappedNodeHelper.updatePeer(getEffectiveCamera());
        peer.setCamera((MappedNGCamera) getEffectiveCamera().getPeer());
        peer.markDirty();
        MappedPerformanceTracker.logEvent("MappedScene.initPeer TKScene initialized");

        setAllowPGAccess(false);

        tk.addSceneTkPulseListener(scenePulseListener);
        // listen to dnd gestures coming from the platform
        if (PLATFORM_DRAG_GESTURE_INITIATION) {
            if (dragGestureListener == null) {
                dragGestureListener = new DragGestureListener();
            }
            tk.registerDragGestureListener(peer, EnumSet.allOf(TransferMode.class), dragGestureListener);
        }
        tk.enableDrop(peer, new DropTargetListener());
        tk.installInputMethodRequests(peer, new InputMethodRequestsDelegate());

        MappedPerformanceTracker.logEvent("MappedScene.initPeer finished");
    }

    // FIXME: make this method package-scope in the next release
    
    @Deprecated(forRemoval = true, since = "17")
    public void disposePeer() {
        if (peer == null) {
            // This is fine, the window is either not shown yet and there is no
            // need in disposing scene peer, or is hidden and disposePeer()
            // has already been called.
            return;
        }

        MappedPerformanceTracker.logEvent("MappedScene.disposePeer started");

        Toolkit tk = Toolkit.getToolkit();
        tk.removeSceneTkPulseListener(scenePulseListener);
        if (accessible != null) {
            disposeAccessibles();
            MappedNode root = getRoot();
            if (root != null) root.releaseAccessible();
            accessible.dispose();
            accessible = null;
        }
        peer.dispose();
        peer = null;

        MappedPerformanceTracker.logEvent("MappedScene.disposePeer finished");
    }

    DnDGesture dndGesture = null;
    DragGestureListener dragGestureListener;
    
    private ReadOnlyDoubleWrapper x;

    private final void setX(double value) {
        xPropertyImpl().set(value);
    }

    public final double getX() {
        return x == null ? 0.0 : x.get();
    }

    public final ReadOnlyDoubleProperty xProperty() {
        return xPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper xPropertyImpl() {
        if (x == null) {
            x = new ReadOnlyDoubleWrapper(this, "x");
        }
        return x;
    }

    
    private ReadOnlyDoubleWrapper y;

    private final void setY(double value) {
        yPropertyImpl().set(value);
    }

    public final double getY() {
        return y == null ? 0.0 : y.get();
    }

    public final ReadOnlyDoubleProperty yProperty() {
        return yPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper yPropertyImpl() {
        if (y == null) {
            y = new ReadOnlyDoubleWrapper(this, "y");
        }
        return y;
    }

    
    private ReadOnlyDoubleWrapper width;

    private final void setWidth(double value) {
        widthPropertyImpl().set(value);
    }

    public final double getWidth() {
        return width == null ? 0.0 : width.get();
    }

    public final ReadOnlyDoubleProperty widthProperty() {
        return widthPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper widthPropertyImpl() {
        if (width == null) {
            width = new ReadOnlyDoubleWrapper() {

                @Override
                protected void invalidated() {
                    final MappedParent _root = getRoot();
                    //TODO - use a better method to update mirroring
                    if (_root.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
                        MappedNodeHelper.transformsChanged(_root);
                    }
                    if (_root.isResizable()) {
                        resizeRootOnSceneSizeChange(get() - _root.getLayoutX() - _root.getTranslateX(), _root.getLayoutBounds().getHeight());
                    }

                    getEffectiveCamera().setViewWidth(get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "width";
                }
            };
        }
        return width;
    }

    
    private ReadOnlyDoubleWrapper height;

    private final void setHeight(double value) {
        heightPropertyImpl().set(value);
    }

    public final double getHeight() {
        return height == null ? 0.0 : height.get();
    }

    public final ReadOnlyDoubleProperty heightProperty() {
        return heightPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper heightPropertyImpl() {
        if (height == null) {
            height = new ReadOnlyDoubleWrapper() {

                @Override
                protected void invalidated() {
                    final MappedParent _root = getRoot();
                    if (_root.isResizable()) {
                        resizeRootOnSceneSizeChange(_root.getLayoutBounds().getWidth(), get() - _root.getLayoutY() - _root.getTranslateY());
                    }

                    getEffectiveCamera().setViewHeight(get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "height";
                }
            };
        }
        return height;
    }

    void resizeRootOnSceneSizeChange(double newWidth, double newHeight) {
        getRoot().resize(newWidth, newHeight);
    }

    // Reusable target wrapper (to avoid creating new one for each picking)
    private TargetWrapper tmpTargetWrapper = new TargetWrapper();

    
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
                                && !Platform.isSupported(ConditionalFeature.SCENE3D)) {
                            String logname = MappedScene.class.getName();
                            PlatformLogger.getLogger(logname).warning("System can't support "
                                    + "ConditionalFeature.SCENE3D");
                        }
                        // Illegal value if it belongs to other scene or any subscene
                        if ((_value.getScene() != null && _value.getScene() != MappedScene.this)
                                || _value.getSubScene() != null) {
                            throw new IllegalArgumentException(_value
                                    + "is already part of other scene or subscene");
                        }
                        // throws exception if the camera already has a different owner
                        _value.setOwnerScene(MappedScene.this);
                        _value.setViewWidth(getWidth());
                        _value.setViewHeight(getHeight());
                    }
                    if (oldCamera != null && oldCamera != _value) {
                        oldCamera.setOwnerScene(null);
                    }
                    oldCamera = _value;
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "camera";
                }
            };
        }
        return camera;
    }

    MappedCamera getEffectiveCamera() {
        final MappedCamera cam = getCamera();
        if (cam == null
                || (cam instanceof MappedPerspectiveCamera
                && !Platform.isSupported(ConditionalFeature.SCENE3D))) {
            if (defaultCamera == null) {
                defaultCamera = new ParallelCamera();
                defaultCamera.setOwnerScene(this);
                defaultCamera.setViewWidth(getWidth());
                defaultCamera.setViewHeight(getHeight());
            }
            return defaultCamera;
        }

        return cam;
    }

    // Used by the camera
    void markCameraDirty() {
        markDirty(DirtyBits.CAMERA_DIRTY);
        setNeedsRepaint();
    }

    void markCursorDirty() {
        markDirty(DirtyBits.CURSOR_DIRTY);
    }

    
    private ObjectProperty<Paint> fill;

    public final void setFill(Paint value) {
        fillProperty().set(value);
    }

    public final Paint getFill() {
        return fill == null ? Color.WHITE : fill.get();
    }

    public final ObjectProperty<Paint> fillProperty() {
        if (fill == null) {
            fill = new ObjectPropertyBase<Paint>(Color.WHITE) {

                @Override
                protected void invalidated() {
                    markDirty(DirtyBits.FILL_DIRTY);
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "fill";
                }
            };
        }
        return fill;
    }

    
    private ObjectProperty<MappedParent> root;

    public final void setRoot(MappedParent value) {
        rootProperty().set(value);
    }

    public final MappedParent getRoot() {
        return root == null ? null : root.get();
    }

    MappedParent oldRoot;
    public final ObjectProperty<MappedParent> rootProperty() {
        if (root == null) {
            root = new ObjectPropertyBase<MappedParent>() {

                private void forceUnbind() {
                    System.err.println("Unbinding illegal root.");
                    unbind();
                }

                @Override
                protected void invalidated() {
                    MappedParent _value = get();

                    if (_value == null) {
                        if (isBound()) forceUnbind();
                        throw new NullPointerException("MappedScene's root cannot be null");
                    }

                    if (_value.getParent() != null) {
                        if (isBound()) forceUnbind();
                        throw new IllegalArgumentException(_value +
                                "is already inside a scene-graph and cannot be set as root");
                    }
                    if (_value.getClipParent() != null) {
                        if (isBound()) forceUnbind();
                        throw new IllegalArgumentException(_value +
                                "is set as a clip on another node, so cannot be set as root");
                    }
                    if (_value.getScene() != null && _value.getScene().getRoot() == _value && _value.getScene() != MappedScene.this) {
                        if (isBound()) forceUnbind();
                        throw new IllegalArgumentException(_value +
                                "is already set as root of another scene");
                    }

                    if (oldRoot != null) {
                        oldRoot.setScenes(null, null);
                    }
                    oldRoot = _value;
                    _value.getStyleClass().add(0, "root");
                    _value.setScenes(MappedScene.this, null);
                    markDirty(DirtyBits.ROOT_DIRTY);
                    _value.resize(getWidth(), getHeight()); // maybe no-op if root is not resizable
                    _value.requestLayout();
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "root";
                }
            };
        }
        return root;
    }

    void setNeedsRepaint() {
        if (this.peer != null) {
            peer.entireSceneNeedsRepaint();
        }
    }

    // Process CSS and layout and sync the scene prior to the snapshot
    // operation of the given node for this scene (currently the node
    // is unused but could possibly be used in the future to optimize this)
    void doCSSLayoutSyncForSnapshot(MappedNode node) {
        if (!sizeInitialized) {
            preferredSize();
        } else {
            doCSSPass();
        }

        // we do not need pulse in the snapshot code
        // because this scene can be stage-less
        doLayoutPass();

        getRoot().updateBounds();
        if (peer != null) {
            peer.waitForRenderingToComplete();
            peer.waitForSynchronization();
            try {
                // Run the synchronizer while holding the render lock
                scenePulseListener.synchronizeSceneNodes();
            } finally {
                peer.releaseSynchronization(false);
            }
        } else {
            scenePulseListener.synchronizeSceneNodes();
        }

    }

    // Shared method for MappedScene.snapshot and MappedNode.snapshot. It is static because
    // we might be doing a MappedNode snapshot with a null scene
    static WritableImage doSnapshot(MappedScene scene,
                                    double x, double y, double w, double h,
                                    MappedNode root, BaseTransform transform, boolean depthBuffer,
                                    Paint fill, MappedCamera camera, WritableImage wimg) {

        Toolkit tk = Toolkit.getToolkit();
        Toolkit.ImageRenderingContext context = new Toolkit.ImageRenderingContext();

        int xMin = (int)Math.floor(x);
        int yMin = (int)Math.floor(y);
        int width;
        int height;
        if (wimg == null) {
            int xMax = (int)Math.ceil(x + w);
            int yMax = (int)Math.ceil(y + h);
            width = Math.max(xMax - xMin, 1);
            height = Math.max(yMax - yMin, 1);
            wimg = new WritableImage(width, height);
        } else {
            width = (int)wimg.getWidth();
            height = (int)wimg.getHeight();
        }

        setAllowPGAccess(true);
        context.x = xMin;
        context.y = yMin;
        context.width = width;
        context.height = height;
        context.transform = transform;
        context.depthBuffer = depthBuffer;
        context.root = root.getPeer();
        context.platformPaint = fill == null ? null : tk.getPaint(fill);
        double cameraViewWidth = 1.0;
        double cameraViewHeight = 1.0;
        if (camera != null) {
            // temporarily adjust camera viewport to the snapshot size
            cameraViewWidth = camera.getViewWidth();
            cameraViewHeight = camera.getViewHeight();
            camera.setViewWidth(width);
            camera.setViewHeight(height);
            MappedNodeHelper.updatePeer(camera);
            context.camera = camera.getPeer();
        } else {
            context.camera = null;
        }

        // Grab the lights from the scene
        context.lights = null;
        if (scene != null && !scene.lights.isEmpty()) {
            context.lights = new NGLightBase[scene.lights.size()];
            for (int i = 0; i < scene.lights.size(); i++) {
                context.lights[i] = scene.lights.get(i).getPeer();
            }
        }

        Toolkit.WritableImageAccessor accessor = Toolkit.getWritableImageAccessor();
        context.platformImage = accessor.getTkImageLoader(wimg);
        setAllowPGAccess(false);
        Object tkImage = tk.renderToImage(context);

        if (tkImage != null) {
            accessor.loadTkImage(wimg, tkImage);
        }

        if (camera != null) {
            setAllowPGAccess(true);
            camera.setViewWidth(cameraViewWidth);
            camera.setViewHeight(cameraViewHeight);
            MappedNodeHelper.updatePeer(camera);
            setAllowPGAccess(false);
        }

        // if this scene belongs to some stage
        // we need to mark the entire scene as dirty
        // because dirty logic is buggy
        if (scene != null && scene.peer != null) {
            scene.setNeedsRepaint();
        }

        return wimg;
    }

    
    private WritableImage doSnapshot(WritableImage img) {
        // TODO: no need to do CSS, layout or sync in the deferred case,
        // if this scene is attached to a visible stage
        doCSSLayoutSyncForSnapshot(getRoot());

        double w = getWidth();
        double h = getHeight();
        BaseTransform transform = BaseTransform.IDENTITY_TRANSFORM;

        return doSnapshot(this, 0, 0, w, h,
                getRoot(), transform, isDepthBufferInternal(),
                getFill(), getEffectiveCamera(), img);
    }

    // Pulse listener used to run all deferred (async) snapshot requests
    private static TKPulseListener snapshotPulseListener = null;

    private static List<Runnable> snapshotRunnableListA;
    private static List<Runnable> snapshotRunnableListB;
    private static List<Runnable> snapshotRunnableList;

    @SuppressWarnings("removal")
    static void addSnapshotRunnable(final Runnable runnable) {
        Toolkit.getToolkit().checkFxUserThread();

        if (snapshotPulseListener == null) {
            snapshotRunnableListA = new ArrayList<Runnable>();
            snapshotRunnableListB = new ArrayList<Runnable>();
            snapshotRunnableList = snapshotRunnableListA;

            snapshotPulseListener = () -> {
                if (snapshotRunnableList.size() > 0) {
                    List<Runnable> runnables = snapshotRunnableList;
                    if (snapshotRunnableList == snapshotRunnableListA) {
                        snapshotRunnableList = snapshotRunnableListB;
                    } else {
                        snapshotRunnableList = snapshotRunnableListA;
                    }
                    for (Runnable r : runnables) {
                        try {
                            r.run();
                        } catch (Throwable th) {
                            System.err.println("Exception in snapshot runnable");
                            th.printStackTrace(System.err);
                        }
                    }
                    runnables.clear();
                }
            };

            // Add listener that will be called after all of the scenes have
            // had layout and CSS processing, and have been synced
            Toolkit.getToolkit().addPostSceneTkPulseListener(snapshotPulseListener);
        }

        final AccessControlContext acc = AccessController.getContext();
        snapshotRunnableList.add(() -> {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                runnable.run();
                return null;
            }, acc);
        });
        Toolkit.getToolkit().requestNextPulse();
    }

    
    public WritableImage snapshot(WritableImage image) {
        Toolkit.getToolkit().checkFxUserThread();

        return doSnapshot(image);
    }

    
    public void snapshot(Callback<SnapshotResult, Void> callback, WritableImage image) {
        Toolkit.getToolkit().checkFxUserThread();
        if (callback == null) {
            throw new NullPointerException("The callback must not be null");
        }

        final Callback<SnapshotResult, Void> theCallback = callback;
        final WritableImage theImage = image;

        // Create a deferred runnable that will be run from a pulse listener
        // that is called after all of the scenes have been synced but before
        // any of them have been rendered.
        final Runnable snapshotRunnable = () -> {
            WritableImage img = doSnapshot(theImage);
//                System.err.println("Calling snapshot callback");
            SnapshotResult result = new SnapshotResult(img, MappedScene.this, null);
            try {
                Void v = theCallback.call(result);
            } catch (Throwable th) {
                System.err.println("Exception in snapshot callback");
                th.printStackTrace(System.err);
            }
        };
//        System.err.println("Schedule a snapshot in the future");
        addSnapshotRunnable(snapshotRunnable);
    }

    
    private ObjectProperty<Cursor> cursor;

    public final void setCursor(Cursor value) {
        cursorProperty().set(value);
    }

    public final Cursor getCursor() {
        return cursor == null ? null : cursor.get();
    }

    public final ObjectProperty<Cursor> cursorProperty() {
        if (cursor == null) {
            cursor = new ObjectPropertyBase<Cursor>() {
                @Override
                protected void invalidated() {
                    markCursorDirty();
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "cursor";
                }
            };
        }
        return cursor;
    }

    
    public MappedNode lookup(String selector) {
        return getRoot().lookup(selector);
    }
    
    private final ObservableList<String> stylesheets  = new TrackableObservableList<String>() {
        @Override
        protected void onChanged(Change<String> c) {
            MappedStyleManager.getInstance().stylesheetsChanged(MappedScene.this, c);
            // RT-9784 - if stylesheet is removed, reset styled properties to
            // their initial value.
            c.reset();
            while(c.next()) {
                if (c.wasRemoved() == false) {
                    continue;
                }
                break; // no point in resetting more than once...
            }
            getRoot().reapplyCSS();
        }
    };

    
    public final ObservableList<String> getStylesheets() { return stylesheets; }

    private ObjectProperty<String> userAgentStylesheet = null;

    
    public final ObjectProperty<String> userAgentStylesheetProperty() {
        if (userAgentStylesheet == null) {
            userAgentStylesheet = new SimpleObjectProperty<String>(MappedScene.this, "userAgentStylesheet", null) {
                @Override protected void invalidated() {
                    MappedStyleManager.getInstance().forget(MappedScene.this);
                    getRoot().reapplyCSS();
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

    
    public final boolean isDepthBuffer() {
        return depthBuffer;
    }

    boolean isDepthBufferInternal() {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            return false;
        }
        return depthBuffer;
    }

    private void init(double width, double height) {
        if (width >= 0) {
            widthSetByUser = width;
            setWidth((float)width);
        }
        if (height >= 0) {
            heightSetByUser = height;
            setHeight((float)height);
        }
        sizeInitialized = (widthSetByUser >= 0 && heightSetByUser >= 0);
    }

    private void init() {
        if (MappedPerformanceTracker.isLoggingEnabled()) {
            MappedPerformanceTracker.logEvent("MappedScene.init for [" + this + "]");
        }
        mouseHandler = new MouseHandler();
        clickGenerator = new ClickGenerator();

        if (MappedPerformanceTracker.isLoggingEnabled()) {
            MappedPerformanceTracker.logEvent("MappedScene.init for [" + this + "] - finished");
        }
    }

    void preferredSize() {
        final MappedParent root = getRoot();

        // one or the other isn't initialized, need to perform layout in
        // order to ensure we can properly measure the preferred size of the
        // scene
        doCSSPass();

        resizeRootToPreferredSize(root);
        doLayoutPass();

        if (widthSetByUser < 0) {
            setWidth(root.isResizable()? root.getLayoutX() + root.getTranslateX() + root.getLayoutBounds().getWidth() :
                    root.getBoundsInParent().getMaxX());
        } else {
            setWidth(widthSetByUser);
        }

        if (heightSetByUser < 0) {
            setHeight(root.isResizable()? root.getLayoutY() + root.getTranslateY() + root.getLayoutBounds().getHeight() :
                    root.getBoundsInParent().getMaxY());
        } else {
            setHeight(heightSetByUser);
        }

        sizeInitialized = (getWidth() > 0) && (getHeight() > 0);

        MappedPerformanceTracker.logEvent("MappedScene preferred bounds computation complete");
    }

    final void resizeRootToPreferredSize(MappedParent root) {
        final double preferredWidth;
        final double preferredHeight;

        final Orientation contentBias = root.getContentBias();
        if (contentBias == null) {
            preferredWidth = getPreferredWidth(root, widthSetByUser, -1);
            preferredHeight = getPreferredHeight(root, heightSetByUser, -1);
        } else if (contentBias == Orientation.HORIZONTAL) {
            // height depends on width
            preferredWidth = getPreferredWidth(root, widthSetByUser, -1);
            preferredHeight = getPreferredHeight(root, heightSetByUser,
                    preferredWidth);
        } else /* if (contentBias == Orientation.VERTICAL) */ {
            // width depends on height
            preferredHeight = getPreferredHeight(root, heightSetByUser, -1);
            preferredWidth = getPreferredWidth(root, widthSetByUser,
                    preferredHeight);
        }

        root.resize(preferredWidth, preferredHeight);
    }

    private static double getPreferredWidth(MappedParent root,
                                            double forcedWidth,
                                            double height) {
        if (forcedWidth >= 0) {
            return forcedWidth;
        }
        final double normalizedHeight = (height >= 0) ? height : -1;
        return root.boundedSize(root.prefWidth(normalizedHeight),
                root.minWidth(normalizedHeight),
                root.maxWidth(normalizedHeight));
    }

    private static double getPreferredHeight(MappedParent root,
                                             double forcedHeight,
                                             double width) {
        if (forcedHeight >= 0) {
            return forcedHeight;
        }
        final double normalizedWidth = (width >= 0) ? width : -1;
        return root.boundedSize(root.prefHeight(normalizedWidth),
                root.minHeight(normalizedWidth),
                root.maxHeight(normalizedWidth));
    }

    private MappedPerformanceTracker tracker;
    private static final Object trackerMonitor = new Object();

    // mouse events handling
    private MouseHandler mouseHandler;
    private ClickGenerator clickGenerator;

    // gesture events handling
    private Point2D cursorScreenPos;
    private Point2D cursorScenePos;

    private static class TouchGesture {
        WeakReference<EventTarget> target;
        Point2D sceneCoords;
        Point2D screenCoords;
        boolean finished;
    }

    private final TouchGesture scrollGesture = new TouchGesture();
    private final TouchGesture zoomGesture = new TouchGesture();
    private final TouchGesture rotateGesture = new TouchGesture();
    private final TouchGesture swipeGesture = new TouchGesture();

    // touch events handling
    private TouchMap touchMap = new TouchMap();
    private TouchEvent nextTouchEvent = null;
    private TouchPoint[] touchPoints = null;
    private int touchEventSetId = 0;
    private int touchPointIndex = 0;
    private Map<Integer, EventTarget> touchTargets =
            new HashMap<Integer, EventTarget>();

    void processMouseEvent(MouseEvent e) {
        mouseHandler.process(e, false);
    }

    private void processMenuEvent(double x2, double y2, double xAbs, double yAbs, boolean isKeyboardTrigger) {
        EventTarget eventTarget = null;
        MappedScene.inMousePick = true;
        if (isKeyboardTrigger) {
            MappedNode sceneFocusOwner = getFocusOwner();

            // for keyboard triggers set coordinates inside focus owner
            final double xOffset = xAbs - x2;
            final double yOffset = yAbs - y2;
            if (sceneFocusOwner != null) {
                final Bounds bounds = sceneFocusOwner.localToScene(
                        sceneFocusOwner.getBoundsInLocal());
                x2 = bounds.getMinX() + bounds.getWidth() / 4;
                y2 = bounds.getMinY() + bounds.getHeight() / 2;
                eventTarget = sceneFocusOwner;
            } else {
                x2 = MappedScene.this.getWidth() / 4;
                y2 = MappedScene.this.getWidth() / 2;
                eventTarget = MappedScene.this;
            }

            xAbs = x2 + xOffset;
            yAbs = y2 + yOffset;
        }

        final PickResult res = pick(x2, y2);

        if (!isKeyboardTrigger) {
            eventTarget = res.getIntersectedNode();
            if (eventTarget == null) {
                eventTarget = this;
            }
        }

        if (eventTarget != null) {
            ContextMenuEvent context = new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                    x2, y2, xAbs, yAbs, isKeyboardTrigger, res);
            Event.fireEvent(eventTarget, context);
        }
        MappedScene.inMousePick = false;
    }

    private void processGestureEvent(GestureEvent e, TouchGesture gesture) {
        EventTarget pickedTarget = null;

        if (e.getEventType() == ZoomEvent.ZOOM_STARTED ||
                e.getEventType() == RotateEvent.ROTATION_STARTED ||
                e.getEventType() == ScrollEvent.SCROLL_STARTED) {
            gesture.target = null;
            gesture.finished = false;
        }

        if (gesture.target != null && (!gesture.finished || e.isInertia())) {
            pickedTarget = gesture.target.get();
        } else {
            pickedTarget = e.getPickResult().getIntersectedNode();
            if (pickedTarget == null) {
                pickedTarget = this;
            }
        }

        if (e.getEventType() == ZoomEvent.ZOOM_STARTED ||
                e.getEventType() == RotateEvent.ROTATION_STARTED ||
                e.getEventType() == ScrollEvent.SCROLL_STARTED) {
            gesture.target = new WeakReference<>(pickedTarget);
        }
        if (e.getEventType() != ZoomEvent.ZOOM_FINISHED &&
                e.getEventType() != RotateEvent.ROTATION_FINISHED &&
                e.getEventType() != ScrollEvent.SCROLL_FINISHED &&
                !e.isInertia()) {
            gesture.sceneCoords = new Point2D(e.getSceneX(), e.getSceneY());
            gesture.screenCoords = new Point2D(e.getScreenX(), e.getScreenY());
        }

        if (pickedTarget != null) {
            Event.fireEvent(pickedTarget, e);
        }

        if (e.getEventType() == ZoomEvent.ZOOM_FINISHED ||
                e.getEventType() == RotateEvent.ROTATION_FINISHED ||
                e.getEventType() == ScrollEvent.SCROLL_FINISHED) {
            gesture.finished = true;
        }
    }

    private void processTouchEvent(TouchEvent e, TouchPoint[] touchPoints) {
        inMousePick = true;
        touchEventSetId++;

        List<TouchPoint> touchList = Arrays.asList(touchPoints);

        // fire all the events
        for (TouchPoint tp : touchPoints) {
            if (tp.getTarget() != null) {
                EventType<TouchEvent> type = null;
                switch (tp.getState()) {
                    case MOVED:
                        type = TouchEvent.TOUCH_MOVED;
                        break;
                    case PRESSED:
                        type = TouchEvent.TOUCH_PRESSED;
                        break;
                    case RELEASED:
                        type = TouchEvent.TOUCH_RELEASED;
                        break;
                    case STATIONARY:
                        type = TouchEvent.TOUCH_STATIONARY;
                        break;
                }

                for (TouchPoint t : touchPoints) {
                    TouchPointHelper.reset(t);
                }

                TouchEvent te = new TouchEvent(type, tp, touchList,
                        touchEventSetId, e.isShiftDown(), e.isControlDown(),
                        e.isAltDown(), e.isMetaDown());

                Event.fireEvent(tp.getTarget(), te);
            }
        }

        // process grabbing
        for (TouchPoint tp : touchPoints) {
            EventTarget grabbed = tp.getGrabbed();
            if (grabbed != null) {
                touchTargets.put(tp.getId(), grabbed);
            };

            if (grabbed == null || tp.getState() == TouchPoint.State.RELEASED) {
                touchTargets.remove(tp.getId());
            }
        }

        inMousePick = false;
    }

    
    MappedNode test_pick(double x, double y) {
        inMousePick = true;
        PickResult result = mouseHandler.pickNode(new PickRay(x, y, 1,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        inMousePick = false;
        if (result != null) {
            return result.getIntersectedNode();
        }
        return null;
    }

    private PickResult pick(final double x, final double y) {
        pick(tmpTargetWrapper, x, y);
        return tmpTargetWrapper.getResult();
    }

    private boolean isInScene(double x, double y) {
        if (x < 0 || y < 0 || x > getWidth() || y > getHeight())  {
            return false;
        }

        MappedWindow w = getWindow();
        if (w instanceof Stage
                && ((Stage) w).getStyle() == StageStyle.TRANSPARENT
                && getFill() == null) {
            return false;
        }

        return true;
    }

    private void pick(TargetWrapper target, final double x, final double y) {
        final PickRay pickRay = getEffectiveCamera().computePickRay(
                x, y, null);

        final double mag = pickRay.getDirectionNoClone().length();
        pickRay.getDirectionNoClone().normalize();
        final PickResult res = mouseHandler.pickNode(pickRay);
        if (res != null) {
            target.setNodeResult(res);
        } else {
            //TODO: is this the intersection with projection plane?
            Vec3d o = pickRay.getOriginNoClone();
            Vec3d d = pickRay.getDirectionNoClone();
            target.setSceneResult(new PickResult(
                            null, new Point3D(
                            o.x + mag * d.x,
                            o.y + mag * d.y,
                            o.z + mag * d.z),
                            mag),
                    isInScene(x, y) ? this : null);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * Key Events and Focus Traversal                                          *
     *                                                                         *
     **************************************************************************/

    /*
     * We cannot initialize keyHandler in init because some of the triggers
     * access it before the init block.
     * No clue why def keyHandler = bind lazy {KeyHandler{scene:this};}
     * does not compile.
     */
    private KeyHandler keyHandler = null;
    private KeyHandler getKeyHandler() {
        if (keyHandler == null) {
            keyHandler = new KeyHandler();
        }
        return keyHandler;
    }
    
    private boolean focusDirty = true;

    final void setFocusDirty(boolean value) {
        if (!focusDirty) {
            Toolkit.getToolkit().requestNextPulse();
        }
        focusDirty = value;
    }

    final boolean isFocusDirty() {
        return focusDirty;
    }

    private TopMostTraversalEngine traversalEngine = new SceneTraversalEngine(this);

    
    boolean traverse(MappedNode node, Direction dir) {
        if (node.getSubScene() != null) {
            return node.getSubScene().traverse(node, dir);
        }
        return traversalEngine.trav(node, dir) != null;
    }

    
    private void focusInitial() {
        traversalEngine.traverseToFirst();
    }

    
    private void focusIneligible(MappedNode node) {
        traverse(node, Direction.NEXT);
    }

    // FIXME: make this method package-scope in the next release
    
    @Deprecated(forRemoval = true, since = "17")
    public void processKeyEvent(KeyEvent e) {
        if (dndGesture != null) {
            if (!dndGesture.processKey(e)) {
                dndGesture = null;
            }
        }

        getKeyHandler().process(e);
    }

    void requestFocus(MappedNode node) {
        getKeyHandler().requestFocus(node);
    }

    private MappedNode oldFocusOwner;

    
    private ReadOnlyObjectWrapper<MappedNode> focusOwner = new ReadOnlyObjectWrapper<MappedNode>(this, "focusOwner") {

        @Override
        protected void invalidated() {
            if (oldFocusOwner != null) {
                ((MappedNode.FocusedProperty) oldFocusOwner.focusedProperty()).store(false);
            }
            MappedNode value = get();
            if (value != null) {
                ((MappedNode.FocusedProperty) value.focusedProperty()).store(keyHandler.windowFocused);
                if (value != oldFocusOwner) {
                    value.getScene().enableInputMethodEvents(
                            value.getInputMethodRequests() != null
                                    && value.getOnInputMethodTextChanged() != null);
                }
            }
            // for the rest of the method we need to update the oldFocusOwner
            // and use a local copy of it because the user handlers can cause
            // recurrent calls of requestFocus
            MappedNode localOldOwner = oldFocusOwner;
            oldFocusOwner = value;
            if (localOldOwner != null) {
                ((MappedNode.FocusedProperty) localOldOwner.focusedProperty()).notifyListeners();
            }
            if (value != null) {
                ((MappedNode.FocusedProperty) value.focusedProperty()).notifyListeners();
            }
            PlatformLogger logger = Logging.getFocusLogger();
            if (logger.isLoggable(Level.FINE)) {
                if (value == get()) {
                    logger.fine("Changed focus from "
                            + localOldOwner + " to " + value);
                } else {
                    logger.fine("Changing focus from "
                            + localOldOwner + " to " + value
                            + " canceled by nested requestFocus");
                }
            }
            if (accessible != null) {
                accessible.sendNotification(AccessibleAttribute.FOCUS_NODE);
            }
        }
    };

    public final MappedNode getFocusOwner() {
        return focusOwner.get();
    }

    public final ReadOnlyObjectProperty<MappedNode> focusOwnerProperty() {
        return focusOwner.getReadOnlyProperty();
    }

    // For testing.
    void focusCleanup() {
        scenePulseListener.focusCleanup();
    }

    private void processInputMethodEvent(InputMethodEvent e) {
        MappedNode node = getFocusOwner();
        if (node != null) {
            node.fireEvent(e);
        }
    }

    // FIXME: make this method package-scope in the next release
    
    @Deprecated(forRemoval = true, since = "17")
    public void enableInputMethodEvents(boolean enable) {
        if (peer != null) {
            peer.enableInputMethodEvents(enable);
        }
    }

    
    boolean isQuiescent() {
        final MappedParent r = getRoot();
        return !isFocusDirty()
                && (r == null || (r.cssFlag == CssFlags.CLEAN &&
                r.layoutFlag == LayoutFlags.CLEAN));
    }

    
    Runnable testPulseListener = null;

    
    private void markDirty(DirtyBits dirtyBit) {
        setDirty(dirtyBit);
        if (peer != null) {
            Toolkit.getToolkit().requestNextPulse();
        }
    }

    
    private void setDirty(DirtyBits dirtyBit) {
        dirtyBits.add(dirtyBit);
    }

    
    private boolean isDirty(DirtyBits dirtyBit) {
        return dirtyBits.contains(dirtyBit);
    }

    
    private boolean isDirtyEmpty() {
        return dirtyBits.isEmpty();
    }

    
    private void clearDirty() {
        dirtyBits.clear();
    }

    private enum DirtyBits {
        FILL_DIRTY,
        ROOT_DIRTY,
        CAMERA_DIRTY,
        LIGHTS_DIRTY,
        CURSOR_DIRTY;
    }

    private List<LightBase> lights = new ArrayList<>();

    // @param light must not be null
    final void addLight(LightBase light) {
        if (!lights.contains(light)) {
            lights.add(light);
            markDirty(DirtyBits.LIGHTS_DIRTY);
        }
    }

    final void removeLight(LightBase light) {
        if (lights.remove(light)) {
            markDirty(DirtyBits.LIGHTS_DIRTY);
        }
    }

    
    private void syncLights() {
        if (!isDirty(DirtyBits.LIGHTS_DIRTY)) {
            return;
        }
        inSynchronizer = true;
        NGLightBase peerLights[] = peer.getLights();
        if (!lights.isEmpty() || (peerLights != null)) {
            if (lights.isEmpty()) {
                peer.setLights(null);
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
                peer.setLights(peerLights);
            }
        }
        inSynchronizer = false;
    }

    //INNER CLASSES

    /* *****************************************************************************
     *                                                                             *
     * MappedScene Pulse Listener                                                        *
     *                                                                             *
     ******************************************************************************/

    class ScenePulseListener implements TKPulseListener {

        private boolean firstPulse = true;

        
        private void synchronizeSceneNodes() {
            Toolkit.getToolkit().checkFxUserThread();

            MappedScene.inSynchronizer = true;

            // if dirtyNodes is null then that means this MappedScene has not yet been
            // synchronized, and so we will simply synchronize every node in the
            // scene and then create the dirty nodes array list
            if (MappedScene.this.dirtyNodes == null) {
                // must do this recursively
                syncAll(getRoot());
                dirtyNodes = new MappedNode[MIN_DIRTY_CAPACITY];

            } else {
                // This is not the first time this scene has been synchronized,
                // so we will only synchronize those nodes that need it
                for (int i = 0 ; i < dirtyNodesSize; ++i) {
                    MappedNode node = dirtyNodes[i];
                    dirtyNodes[i] = null;
                    if (node.getScene() == MappedScene.this) {
                        node.syncPeer();
                    }
                }
                dirtyNodesSize = 0;
            }

            MappedScene.inSynchronizer = false;
        }

        
        private int syncAll(MappedNode node) {
            node.syncPeer();
            int size = 1;
            if (node instanceof MappedParent) {
                MappedParent p = (MappedParent) node;
                final int childrenCount = p.getChildren().size();

                for (int i = 0; i < childrenCount; i++) {
                    MappedNode n = p.getChildren().get(i);
                    if (n != null) {
                        size += syncAll(n);
                    }
                }
            } else if (node instanceof MappedSubScene) {
                MappedSubScene subScene = (MappedSubScene)node;
                size += syncAll(subScene.getRoot());
            }
            if (node.getClip() != null) {
                size += syncAll(node.getClip());
            }

            return size;
        }

        private void synchronizeSceneProperties() {
            inSynchronizer = true;
            if (isDirty(DirtyBits.ROOT_DIRTY)) {
                peer.setRoot(getRoot().getPeer());
            }

            if (isDirty(DirtyBits.FILL_DIRTY)) {
                Toolkit tk = Toolkit.getToolkit();
                peer.setFillPaint(getFill() == null ? null : tk.getPaint(getFill()));
            }

            // new camera was set on the scene or old camera changed
            final MappedCamera cam = getEffectiveCamera();
            if (isDirty(DirtyBits.CAMERA_DIRTY)) {
                MappedNodeHelper.updatePeer(cam);
                peer.setCamera((MappedNGCamera) cam.getPeer());
            }

            if (isDirty(DirtyBits.CURSOR_DIRTY)) {
                mouseHandler.updateCursor(getCursor());
                mouseHandler.updateCursorFrame();
            }

            clearDirty();
            inSynchronizer = false;
        }

        
        private void focusCleanup() {
            if (MappedScene.this.isFocusDirty()) {
                final MappedNode oldOwner = MappedScene.this.getFocusOwner();
                if (oldOwner == null) {
                    MappedScene.this.focusInitial();
                } else if (oldOwner.getScene() != MappedScene.this) {
                    MappedScene.this.requestFocus(null);
                    MappedScene.this.focusInitial();
                } else if (!oldOwner.isCanReceiveFocus()) {
                    MappedScene.this.requestFocus(null);
                    MappedScene.this.focusIneligible(oldOwner);
                }
                MappedScene.this.setFocusDirty(false);
            }
        }

        @Override
        public void pulse() {
            if (MappedScene.this.tracker != null) {
                MappedScene.this.tracker.pulse();
            }
            if (firstPulse) {
                MappedPerformanceTracker.logEvent("MappedScene - first repaint");
            }

            focusCleanup();

            disposeAccessibles();

            // run any scene pre pulse listeners immediately _before_ css / layout,
            // and before scene synchronization
            if (preLayoutPulseListeners != null) {
                for (Runnable r : preLayoutPulseListeners) {
                    r.run();
                }
            }

            if (PULSE_LOGGING_ENABLED) {
                PulseLogger.newPhase("CSS Pass");
            }
            MappedScene.this.doCSSPass();

            if (PULSE_LOGGING_ENABLED) {
                PulseLogger.newPhase("Layout Pass");
            }
            MappedScene.this.doLayoutPass();

            // run any scene post pulse listeners immediately _after_ css / layout,
            // and before scene synchronization
            if (postLayoutPulseListeners != null) {
                for (Runnable r : postLayoutPulseListeners) {
                    r.run();
                }
            }

            boolean dirty = dirtyNodes == null || dirtyNodesSize != 0 || !isDirtyEmpty();
            if (dirty) {
                if (PULSE_LOGGING_ENABLED) {
                    PulseLogger.newPhase("Update bounds");
                }
                getRoot().updateBounds();
                if (peer != null) {
                    try {
                        if (PULSE_LOGGING_ENABLED) {
                            PulseLogger.newPhase("Waiting for previous rendering");
                        }
                        peer.waitForRenderingToComplete();
                        peer.waitForSynchronization();
                        // synchronize scene properties
                        if (PULSE_LOGGING_ENABLED) {
                            PulseLogger.newPhase("Copy state to render graph");
                        }
                        syncLights();
                        synchronizeSceneProperties();
                        // Run the synchronizer
                        synchronizeSceneNodes();
                        MappedScene.this.mouseHandler.pulse();
                        // Tell the scene peer that it needs to repaint
                        peer.markDirty();
                    } finally {
                        peer.releaseSynchronization(true);
                    }
                } else {
                    if (PULSE_LOGGING_ENABLED) {
                        PulseLogger.newPhase("Synchronize with null peer");
                    }
                    synchronizeSceneNodes();
                    MappedScene.this.mouseHandler.pulse();
                }

                if (MappedScene.this.getRoot().cssFlag != CssFlags.CLEAN) {
                    MappedNodeHelper.markDirty(MappedScene.this.getRoot(),
                            com.sun.javafx.scene.DirtyBits.NODE_CSS);
                }
            }

            // required for image cursor created from animated image
            MappedScene.this.mouseHandler.updateCursorFrame();

            if (firstPulse) {
                if (MappedPerformanceTracker.isLoggingEnabled()) {
                    MappedPerformanceTracker.logEvent("MappedScene - first repaint - layout complete");
                    if (PrismSettings.perfLogFirstPaintFlush) {
                        MappedPerformanceTracker.outputLog();
                    }
                    if (PrismSettings.perfLogFirstPaintExit) {
                        System.exit(0);
                    }
                }
                firstPulse = false;
            }

            if (testPulseListener != null) {
                testPulseListener.run();
            }
        }
    }

    /* *****************************************************************************
     *                                                                             *
     * MappedScene Peer Listener                                                         *
     *                                                                             *
     ******************************************************************************/

    class ScenePeerListener implements TKSceneListener {
        @Override
        public void changedLocation(float x, float y) {
            if (x != MappedScene.this.getX()) {
                MappedScene.this.setX(x);
            }
            if (y != MappedScene.this.getY()) {
                MappedScene.this.setY(y);
            }
        }

        @Override
        public void changedSize(float w, float h) {
            if (w != MappedScene.this.getWidth()) MappedScene.this.setWidth(w);
            if (h != MappedScene.this.getHeight()) MappedScene.this.setHeight(h);
        }

        @Override
        public void mouseEvent(EventType<MouseEvent> type, double x, double y, double screenX, double screenY,
                               MouseButton button, boolean popupTrigger, boolean synthesized,
                               boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown,
                               boolean primaryDown, boolean middleDown, boolean secondaryDown,
                               boolean backDown, boolean forwardDown)
        {
            MouseEvent mouseEvent = new MouseEvent(type, x, y, screenX, screenY, button,
                    0, // click count will be adjusted by clickGenerator later anyway
                    shiftDown, controlDown, altDown, metaDown,
                    primaryDown, middleDown, secondaryDown, backDown, forwardDown,
                    synthesized, popupTrigger, false, null);
            processMouseEvent(mouseEvent);
        }


        @Override
        public void keyEvent(KeyEvent keyEvent)
        {
            processKeyEvent(keyEvent);
        }

        @Override
        public void inputMethodEvent(EventType<InputMethodEvent> type,
                                     ObservableList<InputMethodTextRun> composed, String committed,
                                     int caretPosition)
        {
            InputMethodEvent inputMethodEvent = new InputMethodEvent(
                    type, composed, committed, caretPosition);
            processInputMethodEvent(inputMethodEvent);
        }

        public void menuEvent(double x, double y, double xAbs, double yAbs,
                              boolean isKeyboardTrigger) {
            MappedScene.this.processMenuEvent(x, y, xAbs,yAbs, isKeyboardTrigger);
        }

        @Override
        public void scrollEvent(
                EventType<ScrollEvent> eventType,
                double scrollX, double scrollY,
                double totalScrollX, double totalScrollY,
                double xMultiplier, double yMultiplier,
                int touchCount,
                int scrollTextX, int scrollTextY,
                int defaultTextX, int defaultTextY,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            ScrollEvent.HorizontalTextScrollUnits xUnits = scrollTextX > 0 ?
                    ScrollEvent.HorizontalTextScrollUnits.CHARACTERS :
                    ScrollEvent.HorizontalTextScrollUnits.NONE;

            double xText = scrollTextX < 0 ? 0 : scrollTextX * scrollX;

            ScrollEvent.VerticalTextScrollUnits yUnits = scrollTextY > 0 ?
                    ScrollEvent.VerticalTextScrollUnits.LINES :
                    (scrollTextY < 0 ?
                            ScrollEvent.VerticalTextScrollUnits.PAGES :
                            ScrollEvent.VerticalTextScrollUnits.NONE);

            double yText = scrollTextY < 0 ? scrollY : scrollTextY * scrollY;

            xMultiplier = defaultTextX > 0 && scrollTextX >= 0
                    ? Math.round(xMultiplier * scrollTextX / defaultTextX)
                    : xMultiplier;

            yMultiplier = defaultTextY > 0 && scrollTextY >= 0
                    ? Math.round(yMultiplier * scrollTextY / defaultTextY)
                    : yMultiplier;

            if (eventType == ScrollEvent.SCROLL_FINISHED) {
                x = scrollGesture.sceneCoords.getX();
                y = scrollGesture.sceneCoords.getY();
                screenX = scrollGesture.screenCoords.getX();
                screenY = scrollGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            MappedScene.this.processGestureEvent(new ScrollEvent(
                            eventType,
                            x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia,
                            scrollX * xMultiplier, scrollY * yMultiplier,
                            totalScrollX * xMultiplier, totalScrollY * yMultiplier,
                            xMultiplier, yMultiplier,
                            xUnits, xText, yUnits, yText, touchCount, pick(x, y)),
                    scrollGesture);
            inMousePick = false;
        }

        @Override
        public void zoomEvent(
                EventType<ZoomEvent> eventType,
                double zoomFactor, double totalZoomFactor,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            if (eventType == ZoomEvent.ZOOM_FINISHED) {
                x = zoomGesture.sceneCoords.getX();
                y = zoomGesture.sceneCoords.getY();
                screenX = zoomGesture.screenCoords.getX();
                screenY = zoomGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            MappedScene.this.processGestureEvent(new ZoomEvent(eventType,
                            x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia,
                            zoomFactor, totalZoomFactor, pick(x, y)),
                    zoomGesture);
            inMousePick = false;
        }

        @Override
        public void rotateEvent(
                EventType<RotateEvent> eventType, double angle, double totalAngle,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            if (eventType == RotateEvent.ROTATION_FINISHED) {
                x = rotateGesture.sceneCoords.getX();
                y = rotateGesture.sceneCoords.getY();
                screenX = rotateGesture.screenCoords.getX();
                screenY = rotateGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            MappedScene.this.processGestureEvent(new RotateEvent(
                            eventType, x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia, angle, totalAngle, pick(x, y)),
                    rotateGesture);
            inMousePick = false;

        }

        @Override
        public void swipeEvent(
                EventType<SwipeEvent> eventType, int touchCount,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown, boolean _direct) {

            if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            MappedScene.this.processGestureEvent(new SwipeEvent(
                            eventType, x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown, _direct,
                            touchCount, pick(x, y)),
                    swipeGesture);
            inMousePick = false;
        }

        @Override
        public void touchEventBegin(
                long time, int touchCount, boolean isDirect,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown) {

            if (!isDirect) {
                nextTouchEvent = null;
                return;
            }
            nextTouchEvent = new TouchEvent(
                    TouchEvent.ANY, null, null, 0,
                    _shiftDown, _controlDown, _altDown, _metaDown);
            if (touchPoints == null || touchPoints.length != touchCount) {
                touchPoints = new TouchPoint[touchCount];
            }
            touchPointIndex = 0;
        }

        @Override
        public void touchEventNext(
                TouchPoint.State state, long touchId,
                double x, double y, double screenX, double screenY) {

            inMousePick = true;
            if (nextTouchEvent == null) {
                // ignore indirect touch events
                return;
            }
            touchPointIndex++;
            int id = (state == TouchPoint.State.PRESSED
                    ? touchMap.add(touchId) :  touchMap.get(touchId));
            if (state == TouchPoint.State.RELEASED) {
                touchMap.remove(touchId);
            }
            int order = touchMap.getOrder(id);

            if (order >= touchPoints.length) {
                throw new RuntimeException("Too many touch points reported");
            }

            // pick target
            boolean isGrabbed = false;
            PickResult pickRes = pick(x, y);
            EventTarget pickedTarget = touchTargets.get(id);
            if (pickedTarget == null) {
                pickedTarget = pickRes.getIntersectedNode();
                if (pickedTarget == null) {
                    pickedTarget = MappedScene.this;
                }
            } else {
                isGrabbed = true;
            }

            TouchPoint tp = new TouchPoint(id, state,
                    x, y, screenX, screenY, pickedTarget, pickRes);

            touchPoints[order] = tp;

            if (isGrabbed) {
                tp.grab(pickedTarget);
            }
            if (tp.getState() == TouchPoint.State.PRESSED) {
                tp.grab(pickedTarget);
                touchTargets.put(tp.getId(), pickedTarget);
            } else if (tp.getState() == TouchPoint.State.RELEASED) {
                touchTargets.remove(tp.getId());
            }
            inMousePick = false;
        }

        @Override
        public void touchEventEnd() {
            if (nextTouchEvent == null) {
                // ignore indirect touch events
                return;
            }

            if (touchPointIndex != touchPoints.length) {
                throw new RuntimeException("Wrong number of touch points reported");
            }

            MappedScene.this.processTouchEvent(nextTouchEvent, touchPoints);

            if (touchMap.cleanup()) {
                // gesture finished
                touchEventSetId = 0;
            }
        }

        @Override
        public Accessible getSceneAccessible() {
            return getAccessible();
        }
    }

    private class ScenePeerPaintListener implements TKScenePaintListener {
        @Override
        public void frameRendered() {
            // must use tracker with synchronization since this method is called on render thread
            synchronized (trackerMonitor) {
                if (MappedScene.this.tracker != null) {
                    MappedScene.this.tracker.frameRendered();
                }
            }
        }
    }

    /* *****************************************************************************
     *                                                                             *
     * Drag and Drop                                                               *
     *                                                                             *
     ******************************************************************************/

    class DropTargetListener implements TKDropTargetListener {

        /*
         * This function is called when an drag operation enters a valid drop target.
         * This may be from either an internal or external dnd operation.
         */
        @Override
        public TransferMode dragEnter(double x, double y, double screenX, double screenY,
                                      TransferMode transferMode, TKClipboard dragboard)
        {
            if (dndGesture == null) {
                dndGesture = new DnDGesture();
            }
            Dragboard db = DragboardHelper.createDragboard(dragboard);
            dndGesture.dragboard = db;
            DragEvent dragEvent =
                    new DragEvent(DragEvent.ANY, dndGesture.dragboard, x, y, screenX, screenY,
                            transferMode, null, null, pick(x, y));
            return dndGesture.processTargetEnterOver(dragEvent);
        }

        @Override
        public TransferMode dragOver(double x, double y, double screenX, double screenY,
                                     TransferMode transferMode)
        {
            if (MappedScene.this.dndGesture == null) {
                System.err.println("GOT A dragOver when dndGesture is null!");
                return null;
            } else {
                if (dndGesture.dragboard == null) {
                    throw new RuntimeException("dndGesture.dragboard is null in dragOver");
                }
                DragEvent dragEvent =
                        new DragEvent(DragEvent.ANY, dndGesture.dragboard, x, y, screenX, screenY,
                                transferMode, null, null, pick(x, y));
                return dndGesture.processTargetEnterOver(dragEvent);
            }
        }

        @Override
        public void dragExit(double x, double y, double screenX, double screenY) {
            if (dndGesture == null) {
                System.err.println("GOT A dragExit when dndGesture is null!");
            } else {
                if (dndGesture.dragboard == null) {
                    throw new RuntimeException("dndGesture.dragboard is null in dragExit");
                }
                DragEvent dragEvent =
                        new DragEvent(DragEvent.ANY, dndGesture.dragboard, x, y, screenX, screenY,
                                null, null, null, pick(x, y));
                dndGesture.processTargetExit(dragEvent);
                if (dndGesture.source == null) {
                    dndGesture.dragboard = null;
                    dndGesture = null;
                }
            }
        }


        @Override
        public TransferMode drop(double x, double y, double screenX, double screenY,
                                 TransferMode transferMode)
        {
            if (dndGesture == null) {
                System.err.println("GOT A drop when dndGesture is null!");
                return null;
            } else {
                if (dndGesture.dragboard == null) {
                    throw new RuntimeException("dndGesture.dragboard is null in dragDrop");
                }
                DragEvent dragEvent =
                        new DragEvent(DragEvent.ANY, dndGesture.dragboard, x, y, screenX, screenY,
                                transferMode, null, null, pick(x, y));
                // Data dropped to the app can be accessed without restriction
                DragboardHelper.setDataAccessRestriction(dndGesture.dragboard, false);

                TransferMode tm;
                try {
                    tm = dndGesture.processTargetDrop(dragEvent);
                } finally {
                    DragboardHelper.setDataAccessRestriction(
                            dndGesture.dragboard, true);
                }

                if (dndGesture.source == null) {
                    dndGesture.dragboard = null;
                    dndGesture = null;
                }
                return tm;
            }
        }
    }

    class DragGestureListener implements TKDragGestureListener {

        @Override
        public void dragGestureRecognized(double x, double y, double screenX, double screenY,
                                          int button, TKClipboard dragboard)
        {
            Dragboard db = DragboardHelper.createDragboard(dragboard);
            dndGesture = new DnDGesture();
            dndGesture.dragboard = db;
            // TODO: support mouse buttons in DragEvent
            DragEvent dragEvent = new DragEvent(DragEvent.ANY, db, x, y, screenX, screenY,
                    null, null, null, pick(x, y));
            dndGesture.processRecognized(dragEvent);
            dndGesture = null;
        }
    }

    
    class DnDGesture {
        private final double hysteresisSizeX =
                Toolkit.getToolkit().getMultiClickMaxX();
        private final double hysteresisSizeY =
                Toolkit.getToolkit().getMultiClickMaxY();

        private EventTarget source = null;
        private Set<TransferMode> sourceTransferModes = null;
        private TransferMode acceptedTransferMode = null;
        private Dragboard dragboard = null;
        private EventTarget potentialTarget = null;
        private EventTarget target = null;
        private DragDetectedState dragDetected = DragDetectedState.NOT_YET;
        private double pressedX;
        private double pressedY;
        private List<EventTarget> currentTargets = new ArrayList<EventTarget>();
        private List<EventTarget> newTargets = new ArrayList<EventTarget>();
        private EventTarget fullPDRSource = null;

        
        private void fireEvent(EventTarget target, Event e) {
            if (target != null) {
                Event.fireEvent(target, e);
            }
        }

        
        private void processingDragDetected() {
            dragDetected = DragDetectedState.PROCESSING;
        }

        
        private void dragDetectedProcessed() {
            dragDetected = DragDetectedState.DONE;
            final boolean hasContent = (dragboard != null) && (ClipboardHelper.contentPut(dragboard));
            if (hasContent) {
                /* start DnD */
                Toolkit.getToolkit().startDrag(MappedScene.this.peer,
                        sourceTransferModes,
                        new DragSourceListener(),
                        dragboard);
            } else if (fullPDRSource != null) {
                /* start PDR */
                MappedScene.this.mouseHandler.enterFullPDR(fullPDRSource);
            }

            fullPDRSource = null;
        }

        
        private void processDragDetection(MouseEvent mouseEvent) {

            if (dragDetected != DragDetectedState.NOT_YET) {
                mouseEvent.setDragDetect(false);
                return;
            }

            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                pressedX = mouseEvent.getSceneX();
                pressedY = mouseEvent.getSceneY();

                mouseEvent.setDragDetect(false);

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {

                double deltaX = Math.abs(mouseEvent.getSceneX() - pressedX);
                double deltaY = Math.abs(mouseEvent.getSceneY() - pressedY);
                mouseEvent.setDragDetect(deltaX > hysteresisSizeX ||
                        deltaY > hysteresisSizeY);

            }
        }

        
        private boolean process(MouseEvent mouseEvent, EventTarget target) {
            boolean continueProcessing = true;
            if (!PLATFORM_DRAG_GESTURE_INITIATION) {

                if (dragDetected != DragDetectedState.DONE &&
                        (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED ||
                                mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) &&
                        mouseEvent.isDragDetect()) {

                    processingDragDetected();

                    if (target != null) {
                        final MouseEvent detectedEvent = mouseEvent.copyFor(
                                mouseEvent.getSource(), target,
                                MouseEvent.DRAG_DETECTED);

                        try {
                            fireEvent(target, detectedEvent);
                        } finally {
                            // Putting data to dragboard finished, restrict access to them
                            if (dragboard != null) {
                                DragboardHelper.setDataAccessRestriction(
                                        dragboard, true);
                            }
                        }
                    }

                    dragDetectedProcessed();
                }

                if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    continueProcessing = false;
                }
            }
            return continueProcessing;
        }

        /*
         * Called when a drag source is recognized. This occurs at the very start of
         * the publicly visible drag and drop API, as it is responsible for calling
         * the MappedNode.onDragSourceRecognized function.
         */
        private boolean processRecognized(DragEvent de) {
            MouseEvent me = new MouseEvent(
                    MouseEvent.DRAG_DETECTED, de.getX(), de.getY(),
                    de.getSceneX(), de.getScreenY(), MouseButton.PRIMARY, 1,
                    false, false, false, false, false, true, false, false, false,
                    false, de.getPickResult());

            processingDragDetected();

            final EventTarget target = de.getPickResult().getIntersectedNode();
            try {
                fireEvent(target != null ? target : MappedScene.this, me);
            } finally {
                // Putting data to dragboard finished, restrict access to them
                if (dragboard != null) {
                    DragboardHelper.setDataAccessRestriction(
                            dragboard, true);
                }
            }

            dragDetectedProcessed();

            final boolean hasContent = dragboard != null
                    && !dragboard.getContentTypes().isEmpty();
            return hasContent;
        }

        private void processDropEnd(DragEvent de) {
            if (source == null) {
                System.out.println("MappedScene.DnDGesture.processDropEnd() - UNEXPECTD - source is NULL");
                return;
            }

            de = new DragEvent(de.getSource(), source, DragEvent.DRAG_DONE,
                    de.getDragboard(), de.getSceneX(), de.getSceneY(),
                    de.getScreenX(), de.getScreenY(),
                    de.getTransferMode(), source, target, de.getPickResult());

            Event.fireEvent(source, de);

            tmpTargetWrapper.clear();
            handleExitEnter(de, tmpTargetWrapper);

            // at this point the drag and drop operation is completely over, so we
            // can tell the toolkit that it can clean up if needs be.
            Toolkit.getToolkit().stopDrag(dragboard);
        }

        private TransferMode processTargetEnterOver(DragEvent de) {
            pick(tmpTargetWrapper, de.getSceneX(), de.getSceneY());
            final EventTarget pickedTarget = tmpTargetWrapper.getEventTarget();

            if (dragboard == null) {
                dragboard = createDragboard(de, false);
            }

            de = new DragEvent(de.getSource(), pickedTarget, de.getEventType(),
                    dragboard, de.getSceneX(), de.getSceneY(),
                    de.getScreenX(), de.getScreenY(),
                    de.getTransferMode(), source, potentialTarget, de.getPickResult());

            handleExitEnter(de, tmpTargetWrapper);

            de = new DragEvent(de.getSource(), pickedTarget, DragEvent.DRAG_OVER,
                    de.getDragboard(), de.getSceneX(), de.getSceneY(),
                    de.getScreenX(), de.getScreenY(),
                    de.getTransferMode(), source, potentialTarget, de.getPickResult());

            fireEvent(pickedTarget, de);

            Object acceptingObject = de.getAcceptingObject();
            potentialTarget = acceptingObject instanceof EventTarget
                    ? (EventTarget) acceptingObject : null;
            acceptedTransferMode = de.getAcceptedTransferMode();
            return acceptedTransferMode;
        }

        private void processTargetActionChanged(DragEvent de) {
            // Do we want DRAG_TRANSFER_MODE_CHANGED event?
//            final MappedNode pickedNode = MappedScene.this.mouseHandler.pickNode(de.getX(), de.getY());
//            if (pickedNode != null && pickedNode.isTreeVisible()) {
//                de = DragEvent.copy(de.getSource(), pickedNode, source,
//                        pickedNode, de, DragEvent.DRAG_TRANSFER_MODE_CHANGED);
//
//                if (dragboard == null) {
//                    dragboard = createDragboard(de);
//                }
//                dragboard = de.getPlatformDragboard();
//
//                fireEvent(pickedNode, de);
//            }
        }

        private void processTargetExit(DragEvent de) {
            if (dragboard == null) {
                // dragboard should have been created in processTargetEnterOver()
                throw new NullPointerException("dragboard is null in processTargetExit()");
            }

            if (currentTargets.size() > 0) {
                potentialTarget = null;
                tmpTargetWrapper.clear();
                handleExitEnter(de, tmpTargetWrapper);
            }
        }

        private TransferMode processTargetDrop(DragEvent de) {
            pick(tmpTargetWrapper, de.getSceneX(), de.getSceneY());
            final EventTarget pickedTarget = tmpTargetWrapper.getEventTarget();

            de = new DragEvent(de.getSource(), pickedTarget, DragEvent.DRAG_DROPPED,
                    de.getDragboard(), de.getSceneX(), de.getSceneY(),
                    de.getScreenX(), de.getScreenY(),
                    acceptedTransferMode, source, potentialTarget, de.getPickResult());

            if (dragboard == null) {
                // dragboard should have been created in processTargetEnterOver()
                throw new NullPointerException("dragboard is null in processTargetDrop()");
            }

            handleExitEnter(de, tmpTargetWrapper);

            fireEvent(pickedTarget, de);

            Object acceptingObject = de.getAcceptingObject();
            potentialTarget = acceptingObject instanceof EventTarget
                    ? (EventTarget) acceptingObject : null;
            target = potentialTarget;

            TransferMode result = de.isDropCompleted() ?
                    de.getAcceptedTransferMode() : null;

            tmpTargetWrapper.clear();
            handleExitEnter(de, tmpTargetWrapper);

            return result;
        }

        private void handleExitEnter(DragEvent e, TargetWrapper target) {
            EventTarget currentTarget =
                    currentTargets.size() > 0 ? currentTargets.get(0) : null;

            if (target.getEventTarget() != currentTarget) {

                target.fillHierarchy(newTargets);

                int i = currentTargets.size() - 1;
                int j = newTargets.size() - 1;

                while (i >= 0 && j >= 0 && currentTargets.get(i) == newTargets.get(j)) {
                    i--;
                    j--;
                }

                for (; i >= 0; i--) {
                    EventTarget t = currentTargets.get(i);
                    if (potentialTarget == t) {
                        potentialTarget = null;
                    }
                    e = e.copyFor(e.getSource(), t, source,
                            potentialTarget, DragEvent.DRAG_EXITED_TARGET);
                    Event.fireEvent(t, e);
                }

                potentialTarget = null;
                for (; j >= 0; j--) {
                    EventTarget t = newTargets.get(j);
                    e = e.copyFor(e.getSource(), t, source,
                            potentialTarget, DragEvent.DRAG_ENTERED_TARGET);
                    Object acceptingObject = e.getAcceptingObject();
                    if (acceptingObject instanceof EventTarget) {
                        potentialTarget = (EventTarget) acceptingObject;
                    }
                    Event.fireEvent(t, e);
                }

                currentTargets.clear();
                currentTargets.addAll(newTargets);
                newTargets.clear();
            }
        }

//        function getIntendedTransferMode(e:MouseEvent):TransferMode {
//            return if (e.altDown) TransferMode.COPY else TransferMode.MOVE;
//        }

        /*
         * Function that hooks into the key processing code in MappedScene to handle the
         * situation where a drag and drop event is taking place and the user presses
         * the escape key to cancel the drag and drop operation.
         */
        private boolean processKey(KeyEvent e) {
            //note: this seems not to be called, the DnD cancelation is provided by platform
            if ((e.getEventType() == KeyEvent.KEY_PRESSED) && (e.getCode() == KeyCode.ESCAPE)) {

                // cancel drag and drop
                DragEvent de = new DragEvent(
                        source, source, DragEvent.DRAG_DONE, dragboard, 0, 0, 0, 0,
                        null, source, null, null);
                if (source != null) {
                    Event.fireEvent(source, de);
                }

                tmpTargetWrapper.clear();
                handleExitEnter(de, tmpTargetWrapper);

                return false;
            }
            return true;
        }

        /*
         * This starts the drag gesture running, creating the dragboard used for
         * the remainder of this drag and drop operation.
         */
        private Dragboard startDrag(EventTarget source, Set<TransferMode> t) {
            if (dragDetected != DragDetectedState.PROCESSING) {
                throw new IllegalStateException("Cannot start drag and drop "
                        + "outside of DRAG_DETECTED event handler");
            }

            if (t.isEmpty()) {
                dragboard = null;
            } else if (dragboard == null) {
                dragboard = createDragboard(null, true);
            }

            // The app can see what it puts to dragboard without restriction
            DragboardHelper.setDataAccessRestriction(dragboard, false);

            this.source = source;
            potentialTarget = source;
            sourceTransferModes = t;
            return dragboard;
        }

        /*
         * This starts the full PDR gesture.
         */
        private void startFullPDR(EventTarget source) {
            fullPDRSource = source;
        }

        private Dragboard createDragboard(final DragEvent de, boolean isDragSource) {
            Dragboard dragboard = null;
            if (de != null) {
                dragboard = de.getDragboard();
                if (dragboard != null) {
                    return dragboard;
                }
            }
            TKClipboard dragboardPeer = peer.createDragboard(isDragSource);
            return DragboardHelper.createDragboard(dragboardPeer);
        }
    }

    
    private enum DragDetectedState {
        NOT_YET,
        PROCESSING,
        DONE
    }

    class DragSourceListener implements TKDragSourceListener {

        @Override
        public void dragDropEnd(double x, double y, double screenX, double screenY,
                                TransferMode transferMode)
        {
            if (dndGesture != null) {
                if (dndGesture.dragboard == null) {
                    throw new RuntimeException("dndGesture.dragboard is null in dragDropEnd");
                }
                DragEvent dragEvent =
                        new DragEvent(DragEvent.ANY, dndGesture.dragboard, x, y, screenX, screenY,
                                transferMode, null, null, null);

                // DRAG_DONE event is delivered to gesture source, it can access
                // its own data without restriction
                DragboardHelper.setDataAccessRestriction(dndGesture.dragboard, false);
                try {
                    dndGesture.processDropEnd(dragEvent);
                } finally {
                    DragboardHelper.setDataAccessRestriction(dndGesture.dragboard, true);
                }
                dndGesture = null;
            }
        }
    }

    /* *****************************************************************************
     *                                                                             *
     * Mouse Event Handling                                                        *
     *                                                                             *
     ******************************************************************************/

    static class ClickCounter {
        Toolkit toolkit = Toolkit.getToolkit();
        private int count;
        private boolean out;
        private boolean still;
        private Timeline timeout;
        private double pressedX, pressedY;

        private void inc() { count++; }
        private int get() { return count; }
        private boolean isStill() { return still; }

        private void clear() {
            count = 0;
            stopTimeout();
        }

        private void out() {
            out = true;
            stopTimeout();
        }

        private void applyOut() {
            if (out) clear();
            out = false;
        }

        private void moved(double x, double y) {
            if (Math.abs(x - pressedX) > toolkit.getMultiClickMaxX() ||
                    Math.abs(y - pressedY) > toolkit.getMultiClickMaxY()) {
                out();
                still = false;
            }
        }

        private void start(double x, double y) {
            pressedX = x;
            pressedY = y;
            out = false;

            if (timeout != null) {
                timeout.stop();
            }
            timeout = new Timeline();
            timeout.getKeyFrames().add(
                    new KeyFrame(new Duration(toolkit.getMultiClickTime()),
                            event -> {
                                out = true;
                                timeout = null;
                            }
                    ));
            timeout.play();
            still = true;
        }

        private void stopTimeout() {
            if (timeout != null) {
                timeout.stop();
                timeout = null;
            }
        }
    }

    static class ClickGenerator {
        private ClickCounter lastPress = null;

        private Map<MouseButton, ClickCounter> counters =
                new EnumMap<MouseButton, ClickCounter>(MouseButton.class);
        private List<EventTarget> pressedTargets = new ArrayList<EventTarget>();
        private List<EventTarget> releasedTargets = new ArrayList<EventTarget>();

        public ClickGenerator() {
            for (MouseButton mb : MouseButton.values()) {
                if (mb != MouseButton.NONE) {
                    counters.put(mb, new ClickCounter());
                }
            }
        }

        private MouseEvent preProcess(MouseEvent e) {
            for (ClickCounter cc : counters.values()) {
                cc.moved(e.getSceneX(), e.getSceneY());
            }

            ClickCounter cc = counters.get(e.getButton());
            boolean still = lastPress != null ? lastPress.isStill() : false;

            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {

                if (! e.isPrimaryButtonDown()) { counters.get(MouseButton.PRIMARY).clear(); }
                if (! e.isSecondaryButtonDown()) { counters.get(MouseButton.SECONDARY).clear(); }
                if (! e.isMiddleButtonDown()) { counters.get(MouseButton.MIDDLE).clear(); }
                if (! e.isBackButtonDown()) { counters.get(MouseButton.BACK).clear(); }
                if (! e.isForwardButtonDown()) { counters.get(MouseButton.FORWARD).clear(); }
                cc.applyOut();
                cc.inc();
                cc.start(e.getSceneX(), e.getSceneY());
                lastPress = cc;
            }

            return new MouseEvent(e.getEventType(), e.getSceneX(), e.getSceneY(),
                    e.getScreenX(), e.getScreenY(), e.getButton(),
                    cc != null && e.getEventType() != MouseEvent.MOUSE_MOVED ? cc.get() : 0,
                    e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown(),
                    e.isPrimaryButtonDown(), e.isMiddleButtonDown(), e.isSecondaryButtonDown(),
                    e.isBackButtonDown(), e.isForwardButtonDown(),
                    e.isSynthesized(), e.isPopupTrigger(), still, e.getPickResult());
        }

        private void postProcess(MouseEvent e, TargetWrapper target, TargetWrapper pickedTarget) {

            if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                ClickCounter cc = counters.get(e.getButton());

                target.fillHierarchy(pressedTargets);
                pickedTarget.fillHierarchy(releasedTargets);
                int i = pressedTargets.size() - 1;
                int j = releasedTargets.size() - 1;

                EventTarget clickedTarget = null;
                while (i >= 0 && j >= 0 && pressedTargets.get(i) == releasedTargets.get(j)) {
                    clickedTarget = pressedTargets.get(i);
                    i--;
                    j--;
                }

                pressedTargets.clear();
                releasedTargets.clear();

                if (clickedTarget != null && lastPress != null) {
                    MouseEvent click = new MouseEvent(null, clickedTarget,
                            MouseEvent.MOUSE_CLICKED, e.getSceneX(), e.getSceneY(),
                            e.getScreenX(), e.getScreenY(), e.getButton(),
                            cc.get(),
                            e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown(),
                            e.isPrimaryButtonDown(), e.isMiddleButtonDown(), e.isSecondaryButtonDown(),
                            e.isBackButtonDown(), e.isForwardButtonDown(),
                            e.isSynthesized(), e.isPopupTrigger(), lastPress.isStill(), e.getPickResult());
                    Event.fireEvent(clickedTarget, click);
                }
            }
        }
    }

    
    void generateMouseExited(MappedNode removing) {
        mouseHandler.handleNodeRemoval(removing);
    }

    class MouseHandler {
        private TargetWrapper pdrEventTarget = new TargetWrapper(); // pdr - press-drag-release
        private boolean pdrInProgress = false;
        private boolean fullPDREntered = false;

        private EventTarget currentEventTarget = null;
        private MouseEvent lastEvent;
        private boolean hover = false;

        private boolean primaryButtonDown = false;
        private boolean secondaryButtonDown = false;
        private boolean middleButtonDown = false;
        private boolean backButtonDown = false;
        private boolean forwardButtonDown = false;

        private EventTarget fullPDRSource = null;
        private TargetWrapper fullPDRTmpTargetWrapper = new TargetWrapper();

        /* lists needed for enter/exit events generation */
        private final List<EventTarget> pdrEventTargets = new ArrayList<EventTarget>();
        private final List<EventTarget> currentEventTargets = new ArrayList<EventTarget>();
        private final List<EventTarget> newEventTargets = new ArrayList<EventTarget>();

        private final List<EventTarget> fullPDRCurrentEventTargets = new ArrayList<EventTarget>();
        private final List<EventTarget> fullPDRNewEventTargets = new ArrayList<EventTarget>();
        private EventTarget fullPDRCurrentTarget = null;

        private Cursor currCursor;
        private CursorFrame currCursorFrame;
        private EventQueue queue = new EventQueue();

        private Runnable pickProcess = new Runnable() {

            @Override
            public void run() {
                // Make sure this is run only if the peer is still alive
                // and there is an event to deliver
                if (MappedScene.this.peer != null && lastEvent != null) {
                    process(lastEvent, true);
                }
            }
        };

        private void pulse() {
            if (hover && lastEvent != null) {
                //Shouldn't run user code directly. User can call stage.showAndWait() and block the pulse.
                Platform.runLater(pickProcess);
            }
        }

        private void clearPDREventTargets() {
            pdrInProgress = false;
            currentEventTarget = currentEventTargets.size() > 0
                    ? currentEventTargets.get(0) : null;
            pdrEventTarget.clear();
            pdrEventTargets.clear();
        }

        public void enterFullPDR(EventTarget gestureSource) {
            fullPDREntered = true;
            fullPDRSource = gestureSource;
            fullPDRCurrentTarget = null;
            fullPDRCurrentEventTargets.clear();
        }

        public void exitFullPDR(MouseEvent e) {
            if (!fullPDREntered) {
                return;
            }
            fullPDREntered = false;
            for (int i = fullPDRCurrentEventTargets.size() - 1; i >= 0; i--) {
                EventTarget entered = fullPDRCurrentEventTargets.get(i);
                Event.fireEvent(entered, MouseEvent.copyForMouseDragEvent(e,
                        entered, entered,
                        MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
                        fullPDRSource, e.getPickResult()));
            }
            fullPDRSource = null;
            fullPDRCurrentEventTargets.clear();
            fullPDRCurrentTarget = null;
        }

        private void handleNodeRemoval(MappedNode removing) {
            if (lastEvent == null) {
                // this can happen only if everything has been exited anyway
                return;
            }


            if (currentEventTargets.contains(removing)) {
                int i = 0;
                EventTarget trg = null;
                while(trg != removing) {
                    trg = currentEventTargets.get(i++);

                    queue.postEvent(lastEvent.copyFor(trg, trg,
                            MouseEvent.MOUSE_EXITED_TARGET));
                }
                currentEventTargets.subList(0, i).clear();
            }

            if (fullPDREntered && fullPDRCurrentEventTargets.contains(removing)) {
                int i = 0;
                EventTarget trg = null;
                while (trg != removing) {
                    trg = fullPDRCurrentEventTargets.get(i++);

                    queue.postEvent(
                            MouseEvent.copyForMouseDragEvent(lastEvent, trg, trg,
                                    MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
                                    fullPDRSource, lastEvent.getPickResult()));
                }

                fullPDRCurrentEventTargets.subList(0, i).clear();
            }

            queue.fire();

            if (pdrInProgress && pdrEventTargets.contains(removing)) {
                int i = 0;
                EventTarget trg = null;
                while (trg != removing) {
                    trg = pdrEventTargets.get(i++);

                    // trg.setHover(false) - already taken care of
                    // by the code above which sent a mouse exited event
                    ((MappedNode) trg).setPressed(false);
                }
                pdrEventTargets.subList(0, i).clear();

                trg = pdrEventTargets.get(0);
                final PickResult res = pdrEventTarget.getResult();
                if (trg instanceof MappedNode) {
                    pdrEventTarget.setNodeResult(new PickResult((MappedNode) trg,
                            res.getIntersectedPoint(), res.getIntersectedDistance()));
                } else {
                    pdrEventTarget.setSceneResult(new PickResult(null,
                                    res.getIntersectedPoint(), res.getIntersectedDistance()),
                            (MappedScene) trg);
                }
            }
        }

        private void handleEnterExit(MouseEvent e, TargetWrapper pickedTarget) {
            if (pickedTarget.getEventTarget() != currentEventTarget ||
                    e.getEventType() == MouseEvent.MOUSE_EXITED) {

                if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
                    newEventTargets.clear();
                } else {
                    pickedTarget.fillHierarchy(newEventTargets);
                }

                int newTargetsSize = newEventTargets.size();
                int i = currentEventTargets.size() - 1;
                int j = newTargetsSize - 1;
                int k = pdrEventTargets.size() - 1;

                while (i >= 0 && j >= 0 && currentEventTargets.get(i) == newEventTargets.get(j)) {
                    i--;
                    j--;
                    k--;
                }

                final int memk = k;
                for (; i >= 0; i--, k--) {
                    final EventTarget exitedEventTarget = currentEventTargets.get(i);
                    if (pdrInProgress &&
                            (k < 0 || exitedEventTarget != pdrEventTargets.get(k))) {
                        break;
                    }
                    queue.postEvent(e.copyFor(
                            exitedEventTarget, exitedEventTarget,
                            MouseEvent.MOUSE_EXITED_TARGET));
                }

                k = memk;
                for (; j >= 0; j--, k--) {
                    final EventTarget enteredEventTarget = newEventTargets.get(j);
                    if (pdrInProgress &&
                            (k < 0 || enteredEventTarget != pdrEventTargets.get(k))) {
                        break;
                    }
                    queue.postEvent(e.copyFor(
                            enteredEventTarget, enteredEventTarget,
                            MouseEvent.MOUSE_ENTERED_TARGET));
                }

                currentEventTarget = pickedTarget.getEventTarget();
                currentEventTargets.clear();
                for (j++; j < newTargetsSize; j++) {
                    currentEventTargets.add(newEventTargets.get(j));
                }
            }
            queue.fire();
        }

        private void process(MouseEvent e, boolean onPulse) {
            Toolkit.getToolkit().checkFxUserThread();
            MappedScene.inMousePick = true;

            cursorScreenPos = new Point2D(e.getScreenX(), e.getScreenY());
            cursorScenePos = new Point2D(e.getSceneX(), e.getSceneY());

            boolean gestureStarted = false;
            if (!onPulse) {
                if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    if (!(primaryButtonDown || secondaryButtonDown || middleButtonDown ||
                            backButtonDown || forwardButtonDown) &&
                            MappedScene.this.dndGesture == null) {
                        //old gesture ended and new one started
                        gestureStarted = true;
                        if (!PLATFORM_DRAG_GESTURE_INITIATION) {
                            MappedScene.this.dndGesture = new DnDGesture();
                        }
                        clearPDREventTargets();
                    }
                } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                    // gesture ended
                    clearPDREventTargets();
                } else if (e.getEventType() == MouseEvent.MOUSE_ENTERED) {
                    hover = true;
                } else if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
                    hover = false;
                }

                primaryButtonDown = e.isPrimaryButtonDown();
                secondaryButtonDown = e.isSecondaryButtonDown();
                middleButtonDown = e.isMiddleButtonDown();
                backButtonDown = e.isBackButtonDown();
                forwardButtonDown = e.isForwardButtonDown();
            }

            pick(tmpTargetWrapper, e.getSceneX(), e.getSceneY());
            PickResult res = tmpTargetWrapper.getResult();
            if (res != null) {
                e = new MouseEvent(e.getEventType(), e.getSceneX(), e.getSceneY(),
                        e.getScreenX(), e.getScreenY(), e.getButton(), e.getClickCount(),
                        e.isShiftDown(), e.isControlDown(), e.isAltDown(), e.isMetaDown(),
                        e.isPrimaryButtonDown(), e.isMiddleButtonDown(), e.isSecondaryButtonDown(),
                        e.isBackButtonDown(), e.isForwardButtonDown(),
                        e.isSynthesized(), e.isPopupTrigger(), e.isStillSincePress(), res);
            }

            if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
                tmpTargetWrapper.clear();
            }

            TargetWrapper target;
            if (pdrInProgress) {
                target = pdrEventTarget;
            } else {
                target = tmpTargetWrapper;
            }

            if (gestureStarted) {
                pdrEventTarget.copy(target);
                pdrEventTarget.fillHierarchy(pdrEventTargets);
            }

            if (!onPulse) {
                e = clickGenerator.preProcess(e);
            }

            // enter/exit handling
            handleEnterExit(e, tmpTargetWrapper);

            //deliver event to the target node
            if (MappedScene.this.dndGesture != null) {
                MappedScene.this.dndGesture.processDragDetection(e);
            }

            if (fullPDREntered && e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                processFullPDR(e, onPulse);
            }

            if (target.getEventTarget() != null) {
                if (e.getEventType() != MouseEvent.MOUSE_ENTERED
                        && e.getEventType() != MouseEvent.MOUSE_EXITED
                        && !onPulse) {
                    Event.fireEvent(target.getEventTarget(), e);
                }
            }

            if (fullPDREntered && e.getEventType() != MouseEvent.MOUSE_RELEASED) {
                processFullPDR(e, onPulse);
            }

            if (!onPulse) {
                clickGenerator.postProcess(e, target, tmpTargetWrapper);
            }

            // handle drag and drop

            if (!PLATFORM_DRAG_GESTURE_INITIATION && !onPulse) {
                if (MappedScene.this.dndGesture != null) {
                    if (!MappedScene.this.dndGesture.process(e, target.getEventTarget())) {
                        dndGesture = null;
                    }
                }
            }

            Cursor cursor = target.getCursor();
            if (e.getEventType() != MouseEvent.MOUSE_EXITED) {
                if (cursor == null && hover) {
                    cursor = MappedScene.this.getCursor();
                }

                updateCursor(cursor);
                updateCursorFrame();
            }

            if (gestureStarted) {
                pdrInProgress = true;
            }

            if (pdrInProgress &&
                    !(primaryButtonDown || secondaryButtonDown || middleButtonDown ||
                            backButtonDown || forwardButtonDown)) {
                clearPDREventTargets();
                exitFullPDR(e);
                // we need to do new picking in case the originally picked node
                // was moved or removed by the event handlers
                pick(tmpTargetWrapper, e.getSceneX(), e.getSceneY());
                handleEnterExit(e, tmpTargetWrapper);
            }

            lastEvent = e.getEventType() == MouseEvent.MOUSE_EXITED ? null : e;
            MappedScene.inMousePick = false;
        }

        private void processFullPDR(MouseEvent e, boolean onPulse) {

            pick(fullPDRTmpTargetWrapper, e.getSceneX(), e.getSceneY());
            final PickResult result = fullPDRTmpTargetWrapper.getResult();

            final EventTarget eventTarget = fullPDRTmpTargetWrapper.getEventTarget();

            // enter/exit handling
            if (eventTarget != fullPDRCurrentTarget) {

                fullPDRTmpTargetWrapper.fillHierarchy(fullPDRNewEventTargets);

                int newTargetsSize = fullPDRNewEventTargets.size();
                int i = fullPDRCurrentEventTargets.size() - 1;
                int j = newTargetsSize - 1;

                while (i >= 0 && j >= 0 &&
                        fullPDRCurrentEventTargets.get(i) == fullPDRNewEventTargets.get(j)) {
                    i--;
                    j--;
                }

                for (; i >= 0; i--) {
                    final EventTarget exitedEventTarget = fullPDRCurrentEventTargets.get(i);
                    Event.fireEvent(exitedEventTarget, MouseEvent.copyForMouseDragEvent(e,
                            exitedEventTarget, exitedEventTarget,
                            MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
                            fullPDRSource, result));
                }

                for (; j >= 0; j--) {
                    final EventTarget enteredEventTarget = fullPDRNewEventTargets.get(j);
                    Event.fireEvent(enteredEventTarget, MouseEvent.copyForMouseDragEvent(e,
                            enteredEventTarget, enteredEventTarget,
                            MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
                            fullPDRSource, result));
                }

                fullPDRCurrentTarget = eventTarget;
                fullPDRCurrentEventTargets.clear();
                fullPDRCurrentEventTargets.addAll(fullPDRNewEventTargets);
                fullPDRNewEventTargets.clear();
            }
            // done enter/exit handling

            // event delivery
            if (eventTarget != null && !onPulse) {
                if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    Event.fireEvent(eventTarget, MouseEvent.copyForMouseDragEvent(e,
                            eventTarget, eventTarget,
                            MouseDragEvent.MOUSE_DRAG_OVER,
                            fullPDRSource, result));
                }
                if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    Event.fireEvent(eventTarget, MouseEvent.copyForMouseDragEvent(e,
                            eventTarget, eventTarget,
                            MouseDragEvent.MOUSE_DRAG_RELEASED,
                            fullPDRSource, result));
                }
            }
        }

        private void updateCursor(Cursor newCursor) {
            if (currCursor != newCursor) {
                if (currCursor != null) {
                    currCursor.deactivate();
                }

                if (newCursor != null) {
                    newCursor.activate();
                }

                currCursor = newCursor;
            }
        }

        public void updateCursorFrame() {
            final CursorFrame newCursorFrame =
                    (currCursor != null)
                            ? currCursor.getCurrentFrame()
                            : Cursor.DEFAULT.getCurrentFrame();
            if (currCursorFrame != newCursorFrame) {
                if (MappedScene.this.peer != null) {
                    MappedScene.this.peer.setCursor(newCursorFrame);
                }

                currCursorFrame = newCursorFrame;
            }
        }

        private PickResult pickNode(PickRay pickRay) {
            PickResultChooser r = new PickResultChooser();
            MappedScene.this.getRoot().pickNode(pickRay, r);
            return r.toPickResult();
        }
    }

    /* *****************************************************************************
     *                                                                             *
     * Key Event Handling                                                          *
     *                                                                             *
     ******************************************************************************/

    class KeyHandler {
        private void setFocusOwner(final MappedNode value) {
            // Cancel IM composition if there is one in progress.
            // This needs to be done before the focus owner is switched as it
            // generates event that needs to be delivered to the old focus owner.
            if (oldFocusOwner != null) {
                final MappedScene s = oldFocusOwner.getScene();
                if (s != null) {
                    final TKScene peer = s.getPeer();
                    if (peer != null) {
                        peer.finishInputMethodComposition();
                    }
                }
            }
            focusOwner.set(value);
        }

        private boolean windowFocused;
        protected boolean isWindowFocused() { return windowFocused; }
        protected void setWindowFocused(boolean value) {
            windowFocused = value;
            if (getFocusOwner() != null) {
                getFocusOwner().setFocused(windowFocused);
            }
            if (windowFocused) {
                if (accessible != null) {
                    accessible.sendNotification(AccessibleAttribute.FOCUS_NODE);
                }
            }
        }

        private void windowForSceneChanged(MappedWindow oldWindow, MappedWindow window) {
            if (oldWindow != null) {
                oldWindow.focusedProperty().removeListener(sceneWindowFocusedListener);
            }

            if (window != null) {
                window.focusedProperty().addListener(sceneWindowFocusedListener);
                setWindowFocused(window.isFocused());
            } else {
                setWindowFocused(false);
            }
        }

        private final InvalidationListener sceneWindowFocusedListener = valueModel -> setWindowFocused(((ReadOnlyBooleanProperty)valueModel).get());

        private void process(KeyEvent e) {
            final MappedNode sceneFocusOwner = getFocusOwner();
            final EventTarget eventTarget =
                    (sceneFocusOwner != null && sceneFocusOwner.getScene() == MappedScene.this) ? sceneFocusOwner
                            : MappedScene.this;

            // send the key event to the current focus owner or to scene if
            // the focus owner is not set
            Event.fireEvent(eventTarget, e);
        }

        private void requestFocus(MappedNode node) {
            if (getFocusOwner() == node || (node != null && !node.isCanReceiveFocus())) {
                return;
            }
            setFocusOwner(node);
        }
    }
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

    public final ObjectProperty<EventDispatcher>
    eventDispatcherProperty() {
        initializeInternalEventDispatcher();
        return eventDispatcher;
    }

    private SceneEventDispatcher internalEventDispatcher;

    // Delegates requests from platform input method to the focused
    // node's one, if any.
    class InputMethodRequestsDelegate implements ExtendedInputMethodRequests {
        @Override
        public Point2D getTextLocation(int offset) {
            InputMethodRequests requests = getClientRequests();
            if (requests != null) {
                return requests.getTextLocation(offset);
            } else {
                return new Point2D(0, 0);
            }
        }

        @Override
        public int getLocationOffset(int x, int y) {
            InputMethodRequests requests = getClientRequests();
            if (requests != null) {
                return requests.getLocationOffset(x, y);
            } else {
                return 0;
            }
        }

        @Override
        public void cancelLatestCommittedText() {
            InputMethodRequests requests = getClientRequests();
            if (requests != null) {
                requests.cancelLatestCommittedText();
            }
        }

        @Override
        public String getSelectedText() {
            InputMethodRequests requests = getClientRequests();
            if (requests != null) {
                return requests.getSelectedText();
            }
            return null;
        }

        @Override
        public int getInsertPositionOffset() {
            InputMethodRequests requests = getClientRequests();
            if (requests != null && requests instanceof ExtendedInputMethodRequests) {
                return ((ExtendedInputMethodRequests)requests).getInsertPositionOffset();
            }
            return 0;
        }

        @Override
        public String getCommittedText(int begin, int end) {
            InputMethodRequests requests = getClientRequests();
            if (requests != null && requests instanceof ExtendedInputMethodRequests) {
                return ((ExtendedInputMethodRequests)requests).getCommittedText(begin, end);
            }
            return null;
        }

        @Override
        public int getCommittedTextLength() {
            InputMethodRequests requests = getClientRequests();
            if (requests != null && requests instanceof ExtendedInputMethodRequests) {
                return ((ExtendedInputMethodRequests)requests).getCommittedTextLength();
            }
            return 0;
        }

        private InputMethodRequests getClientRequests() {
            MappedNode focusOwner = getFocusOwner();
            if (focusOwner != null) {
                return focusOwner.getInputMethodRequests();
            }
            return null;
        }
    }

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
        getInternalEventDispatcher().getEventHandlerManager()
                .removeEventHandler(eventType,
                        eventHandler);
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

    private SceneEventDispatcher getInternalEventDispatcher() {
        initializeInternalEventDispatcher();
        return internalEventDispatcher;
    }

    final void initializeInternalEventDispatcher() {
        if (internalEventDispatcher == null) {
            internalEventDispatcher = createInternalEventDispatcher();
            eventDispatcher = new SimpleObjectProperty<EventDispatcher>(
                    this,
                    "eventDispatcher",
                    internalEventDispatcher);
        }
    }

    private SceneEventDispatcher createInternalEventDispatcher() {
        return new SceneEventDispatcher(this);
    }

    
    public void addMnemonic(Mnemonic m) {
        getInternalEventDispatcher().getKeyboardShortcutsHandler()
                .addMnemonic(m);
    }


    
    public void removeMnemonic(Mnemonic m) {
        getInternalEventDispatcher().getKeyboardShortcutsHandler()
                .removeMnemonic(m);
    }

    final void clearNodeMnemonics(MappedNode node) {
        getInternalEventDispatcher().getKeyboardShortcutsHandler()
                .clearNodeMnemonics(node);
    }


    
    public ObservableMap<KeyCombination, ObservableList<Mnemonic>> getMnemonics() {
        return getInternalEventDispatcher().getKeyboardShortcutsHandler()
                .getMnemonics();
    }

    
    public ObservableMap<KeyCombination, Runnable> getAccelerators() {
        return getInternalEventDispatcher().getKeyboardShortcutsHandler()
                .getAccelerators();
    }

    // PENDING_DOC_REVIEW
    
    @Override
    public EventDispatchChain buildEventDispatchChain(
            EventDispatchChain tail) {
        if (eventDispatcher != null) {
            final EventDispatcher eventDispatcherValue = eventDispatcher.get();
            if (eventDispatcherValue != null) {
                tail = tail.prepend(eventDispatcherValue);
            }
        }

        if (getWindow() != null) {
            tail = getWindow().buildEventDispatchChain(tail);
        }

        return tail;
    }

    /* *************************************************************************
     *                                                                         *
     *                             Context Menus                               *
     *                                                                         *
     **************************************************************************/

    

    private ObjectProperty<EventHandler<? super ContextMenuEvent>> onContextMenuRequested;

    public final void setOnContextMenuRequested(EventHandler<? super ContextMenuEvent> value) {
        onContextMenuRequestedProperty().set(value);
    }

    public final EventHandler<? super ContextMenuEvent> getOnContextMenuRequested() {
        return onContextMenuRequested == null ? null : onContextMenuRequested.get();
    }

    public final ObjectProperty<EventHandler<? super ContextMenuEvent>> onContextMenuRequestedProperty() {
        if (onContextMenuRequested == null) {
            onContextMenuRequested = new ObjectPropertyBase<EventHandler<? super ContextMenuEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onContextMenuRequested";
                }
            };
        }
        return onContextMenuRequested;
    }

    /* *************************************************************************
     *                                                                         *
     *                             Mouse Handling                              *
     *                                                                         *
     **************************************************************************/

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseClicked;

    public final void setOnMouseClicked(EventHandler<? super MouseEvent> value) {
        onMouseClickedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseClicked() {
        return onMouseClicked == null ? null : onMouseClicked.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseClickedProperty() {
        if (onMouseClicked == null) {
            onMouseClicked = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_CLICKED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseClicked";
                }
            };
        }
        return onMouseClicked;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseDragged;

    public final void setOnMouseDragged(EventHandler<? super MouseEvent> value) {
        onMouseDraggedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseDragged() {
        return onMouseDragged == null ? null : onMouseDragged.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseDraggedProperty() {
        if (onMouseDragged == null) {
            onMouseDragged = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_DRAGGED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseDragged";
                }
            };
        }
        return onMouseDragged;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseEntered;

    public final void setOnMouseEntered(EventHandler<? super MouseEvent> value) {
        onMouseEnteredProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseEntered() {
        return onMouseEntered == null ? null : onMouseEntered.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseEnteredProperty() {
        if (onMouseEntered == null) {
            onMouseEntered = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_ENTERED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseEntered";
                }
            };
        }
        return onMouseEntered;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseExited;

    public final void setOnMouseExited(EventHandler<? super MouseEvent> value) {
        onMouseExitedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseExited() {
        return onMouseExited == null ? null : onMouseExited.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseExitedProperty() {
        if (onMouseExited == null) {
            onMouseExited = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_EXITED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseExited";
                }
            };
        }
        return onMouseExited;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseMoved;

    public final void setOnMouseMoved(EventHandler<? super MouseEvent> value) {
        onMouseMovedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseMoved() {
        return onMouseMoved == null ? null : onMouseMoved.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseMovedProperty() {
        if (onMouseMoved == null) {
            onMouseMoved = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_MOVED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseMoved";
                }
            };
        }
        return onMouseMoved;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMousePressed;

    public final void setOnMousePressed(EventHandler<? super MouseEvent> value) {
        onMousePressedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMousePressed() {
        return onMousePressed == null ? null : onMousePressed.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMousePressedProperty() {
        if (onMousePressed == null) {
            onMousePressed = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_PRESSED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMousePressed";
                }
            };
        }
        return onMousePressed;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onMouseReleased;

    public final void setOnMouseReleased(EventHandler<? super MouseEvent> value) {
        onMouseReleasedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnMouseReleased() {
        return onMouseReleased == null ? null : onMouseReleased.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onMouseReleasedProperty() {
        if (onMouseReleased == null) {
            onMouseReleased = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.MOUSE_RELEASED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseReleased";
                }
            };
        }
        return onMouseReleased;
    }

    
    private ObjectProperty<EventHandler<? super MouseEvent>> onDragDetected;

    public final void setOnDragDetected(EventHandler<? super MouseEvent> value) {
        onDragDetectedProperty().set(value);
    }

    public final EventHandler<? super MouseEvent> getOnDragDetected() {
        return onDragDetected == null ? null : onDragDetected.get();
    }

    public final ObjectProperty<EventHandler<? super MouseEvent>> onDragDetectedProperty() {
        if (onDragDetected == null) {
            onDragDetected = new ObjectPropertyBase<EventHandler<? super MouseEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseEvent.DRAG_DETECTED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragDetected";
                }
            };
        }
        return onDragDetected;
    }

    
    private ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragOver;

    public final void setOnMouseDragOver(EventHandler<? super MouseDragEvent> value) {
        onMouseDragOverProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragOver() {
        return onMouseDragOver == null ? null : onMouseDragOver.get();
    }

    public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragOverProperty() {
        if (onMouseDragOver == null) {
            onMouseDragOver = new ObjectPropertyBase<EventHandler<? super MouseDragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseDragOver";
                }
            };
        }
        return onMouseDragOver;
    }

    
    private ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragReleased;

    public final void setOnMouseDragReleased(EventHandler<? super MouseDragEvent> value) {
        onMouseDragReleasedProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragReleased() {
        return onMouseDragReleased == null ? null : onMouseDragReleased.get();
    }

    public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragReleasedProperty() {
        if (onMouseDragReleased == null) {
            onMouseDragReleased = new ObjectPropertyBase<EventHandler<? super MouseDragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseDragReleased";
                }
            };
        }
        return onMouseDragReleased;
    }

    
    private ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragEntered;

    public final void setOnMouseDragEntered(EventHandler<? super MouseDragEvent> value) {
        onMouseDragEnteredProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragEntered() {
        return onMouseDragEntered == null ? null : onMouseDragEntered.get();
    }

    public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragEnteredProperty() {
        if (onMouseDragEntered == null) {
            onMouseDragEntered = new ObjectPropertyBase<EventHandler<? super MouseDragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseDragEntered";
                }
            };
        }
        return onMouseDragEntered;
    }

    
    private ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragExited;

    public final void setOnMouseDragExited(EventHandler<? super MouseDragEvent> value) {
        onMouseDragExitedProperty().set(value);
    }

    public final EventHandler<? super MouseDragEvent> getOnMouseDragExited() {
        return onMouseDragExited == null ? null : onMouseDragExited.get();
    }

    public final ObjectProperty<EventHandler<? super MouseDragEvent>> onMouseDragExitedProperty() {
        if (onMouseDragExited == null) {
            onMouseDragExited = new ObjectPropertyBase<EventHandler<? super MouseDragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onMouseDragExited";
                }
            };
        }
        return onMouseDragExited;
    }


    /* *************************************************************************
     *                                                                         *
     *                           Gestures Handling                             *
     *                                                                         *
     **************************************************************************/

    
    private ObjectProperty<EventHandler<? super ScrollEvent>> onScrollStarted;

    public final void setOnScrollStarted(EventHandler<? super ScrollEvent> value) {
        onScrollStartedProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScrollStarted() {
        return onScrollStarted == null ? null : onScrollStarted.get();
    }

    public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollStartedProperty() {
        if (onScrollStarted == null) {
            onScrollStarted = new ObjectPropertyBase<EventHandler<? super ScrollEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ScrollEvent.SCROLL_STARTED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onScrollStarted";
                }
            };
        }
        return onScrollStarted;
    }

    
    private ObjectProperty<EventHandler<? super ScrollEvent>> onScroll;

    public final void setOnScroll(EventHandler<? super ScrollEvent> value) {
        onScrollProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScroll() {
        return onScroll == null ? null : onScroll.get();
    }

    public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollProperty() {
        if (onScroll == null) {
            onScroll = new ObjectPropertyBase<EventHandler<? super ScrollEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ScrollEvent.SCROLL, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onScroll";
                }
            };
        }
        return onScroll;
    }

    
    private ObjectProperty<EventHandler<? super ScrollEvent>> onScrollFinished;

    public final void setOnScrollFinished(EventHandler<? super ScrollEvent> value) {
        onScrollFinishedProperty().set(value);
    }

    public final EventHandler<? super ScrollEvent> getOnScrollFinished() {
        return onScrollFinished == null ? null : onScrollFinished.get();
    }

    public final ObjectProperty<EventHandler<? super ScrollEvent>> onScrollFinishedProperty() {
        if (onScrollFinished == null) {
            onScrollFinished = new ObjectPropertyBase<EventHandler<? super ScrollEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ScrollEvent.SCROLL_FINISHED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onScrollFinished";
                }
            };
        }
        return onScrollFinished;
    }

    
    private ObjectProperty<EventHandler<? super RotateEvent>> onRotationStarted;

    public final void setOnRotationStarted(EventHandler<? super RotateEvent> value) {
        onRotationStartedProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotationStarted() {
        return onRotationStarted == null ? null : onRotationStarted.get();
    }

    public final ObjectProperty<EventHandler<? super RotateEvent>> onRotationStartedProperty() {
        if (onRotationStarted == null) {
            onRotationStarted = new ObjectPropertyBase<EventHandler<? super RotateEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(RotateEvent.ROTATION_STARTED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onRotationStarted";
                }
            };
        }
        return onRotationStarted;
    }

    
    private ObjectProperty<EventHandler<? super RotateEvent>> onRotate;

    public final void setOnRotate(EventHandler<? super RotateEvent> value) {
        onRotateProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotate() {
        return onRotate == null ? null : onRotate.get();
    }

    public final ObjectProperty<EventHandler<? super RotateEvent>> onRotateProperty() {
        if (onRotate == null) {
            onRotate = new ObjectPropertyBase<EventHandler<? super RotateEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(RotateEvent.ROTATE, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onRotate";
                }
            };
        }
        return onRotate;
    }

    
    private ObjectProperty<EventHandler<? super RotateEvent>> onRotationFinished;

    public final void setOnRotationFinished(EventHandler<? super RotateEvent> value) {
        onRotationFinishedProperty().set(value);
    }

    public final EventHandler<? super RotateEvent> getOnRotationFinished() {
        return onRotationFinished == null ? null : onRotationFinished.get();
    }

    public final ObjectProperty<EventHandler<? super RotateEvent>> onRotationFinishedProperty() {
        if (onRotationFinished == null) {
            onRotationFinished = new ObjectPropertyBase<EventHandler<? super RotateEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(RotateEvent.ROTATION_FINISHED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onRotationFinished";
                }
            };
        }
        return onRotationFinished;
    }

    
    private ObjectProperty<EventHandler<? super ZoomEvent>> onZoomStarted;

    public final void setOnZoomStarted(EventHandler<? super ZoomEvent> value) {
        onZoomStartedProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoomStarted() {
        return onZoomStarted == null ? null : onZoomStarted.get();
    }

    public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomStartedProperty() {
        if (onZoomStarted == null) {
            onZoomStarted = new ObjectPropertyBase<EventHandler<? super ZoomEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ZoomEvent.ZOOM_STARTED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onZoomStarted";
                }
            };
        }
        return onZoomStarted;
    }

    
    private ObjectProperty<EventHandler<? super ZoomEvent>> onZoom;

    public final void setOnZoom(EventHandler<? super ZoomEvent> value) {
        onZoomProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoom() {
        return onZoom == null ? null : onZoom.get();
    }

    public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomProperty() {
        if (onZoom == null) {
            onZoom = new ObjectPropertyBase<EventHandler<? super ZoomEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ZoomEvent.ZOOM, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onZoom";
                }
            };
        }
        return onZoom;
    }

    
    private ObjectProperty<EventHandler<? super ZoomEvent>> onZoomFinished;

    public final void setOnZoomFinished(EventHandler<? super ZoomEvent> value) {
        onZoomFinishedProperty().set(value);
    }

    public final EventHandler<? super ZoomEvent> getOnZoomFinished() {
        return onZoomFinished == null ? null : onZoomFinished.get();
    }

    public final ObjectProperty<EventHandler<? super ZoomEvent>> onZoomFinishedProperty() {
        if (onZoomFinished == null) {
            onZoomFinished = new ObjectPropertyBase<EventHandler<? super ZoomEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(ZoomEvent.ZOOM_FINISHED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onZoomFinished";
                }
            };
        }
        return onZoomFinished;
    }

    
    private ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeUp;

    public final void setOnSwipeUp(EventHandler<? super SwipeEvent> value) {
        onSwipeUpProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeUp() {
        return onSwipeUp == null ? null : onSwipeUp.get();
    }

    public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeUpProperty() {
        if (onSwipeUp == null) {
            onSwipeUp = new ObjectPropertyBase<EventHandler<? super SwipeEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(SwipeEvent.SWIPE_UP, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onSwipeUp";
                }
            };
        }
        return onSwipeUp;
    }

    
    private ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeDown;

    public final void setOnSwipeDown(EventHandler<? super SwipeEvent> value) {
        onSwipeDownProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeDown() {
        return onSwipeDown == null ? null : onSwipeDown.get();
    }

    public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeDownProperty() {
        if (onSwipeDown == null) {
            onSwipeDown = new ObjectPropertyBase<EventHandler<? super SwipeEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(SwipeEvent.SWIPE_DOWN, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onSwipeDown";
                }
            };
        }
        return onSwipeDown;
    }

    
    private ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeLeft;

    public final void setOnSwipeLeft(EventHandler<? super SwipeEvent> value) {
        onSwipeLeftProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeLeft() {
        return onSwipeLeft == null ? null : onSwipeLeft.get();
    }

    public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeLeftProperty() {
        if (onSwipeLeft == null) {
            onSwipeLeft = new ObjectPropertyBase<EventHandler<? super SwipeEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(SwipeEvent.SWIPE_LEFT, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onSwipeLeft";
                }
            };
        }
        return onSwipeLeft;
    }

    
    private ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeRight;

    public final void setOnSwipeRight(EventHandler<? super SwipeEvent> value) {
        onSwipeRightProperty().set(value);
    }

    public final EventHandler<? super SwipeEvent> getOnSwipeRight() {
        return onSwipeRight == null ? null : onSwipeRight.get();
    }

    public final ObjectProperty<EventHandler<? super SwipeEvent>> onSwipeRightProperty() {
        if (onSwipeRight == null) {
            onSwipeRight = new ObjectPropertyBase<EventHandler<? super SwipeEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(SwipeEvent.SWIPE_RIGHT, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onSwipeRight";
                }
            };
        }
        return onSwipeRight;
    }

    /* *************************************************************************
     *                                                                         *
     *                            Touch Handling                               *
     *                                                                         *
     **************************************************************************/

    
    private ObjectProperty<EventHandler<? super TouchEvent>> onTouchPressed;

    public final void setOnTouchPressed(EventHandler<? super TouchEvent> value) {
        onTouchPressedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchPressed() {
        return onTouchPressed == null ? null : onTouchPressed.get();
    }

    public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchPressedProperty() {
        if (onTouchPressed == null) {
            onTouchPressed = new ObjectPropertyBase<EventHandler<? super TouchEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(TouchEvent.TOUCH_PRESSED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onTouchPressed";
                }
            };
        }
        return onTouchPressed;
    }

    
    private ObjectProperty<EventHandler<? super TouchEvent>> onTouchMoved;

    public final void setOnTouchMoved(EventHandler<? super TouchEvent> value) {
        onTouchMovedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchMoved() {
        return onTouchMoved == null ? null : onTouchMoved.get();
    }

    public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchMovedProperty() {
        if (onTouchMoved == null) {
            onTouchMoved = new ObjectPropertyBase<EventHandler<? super TouchEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(TouchEvent.TOUCH_MOVED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onTouchMoved";
                }
            };
        }
        return onTouchMoved;
    }

    
    private ObjectProperty<EventHandler<? super TouchEvent>> onTouchReleased;

    public final void setOnTouchReleased(EventHandler<? super TouchEvent> value) {
        onTouchReleasedProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchReleased() {
        return onTouchReleased == null ? null : onTouchReleased.get();
    }

    public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchReleasedProperty() {
        if (onTouchReleased == null) {
            onTouchReleased = new ObjectPropertyBase<EventHandler<? super TouchEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(TouchEvent.TOUCH_RELEASED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onTouchReleased";
                }
            };
        }
        return onTouchReleased;
    }

    
    private ObjectProperty<EventHandler<? super TouchEvent>> onTouchStationary;

    public final void setOnTouchStationary(EventHandler<? super TouchEvent> value) {
        onTouchStationaryProperty().set(value);
    }

    public final EventHandler<? super TouchEvent> getOnTouchStationary() {
        return onTouchStationary == null ? null : onTouchStationary.get();
    }

    public final ObjectProperty<EventHandler<? super TouchEvent>> onTouchStationaryProperty() {
        if (onTouchStationary == null) {
            onTouchStationary = new ObjectPropertyBase<EventHandler<? super TouchEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(TouchEvent.TOUCH_STATIONARY, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onTouchStationary";
                }
            };
        }
        return onTouchStationary;
    }

    /*
     * This class provides reordering and ID mapping of particular touch points.
     * Platform may report arbitrary touch point IDs and they may be reused
     * during one gesture. This class keeps track of it and provides
     * sequentially sorted IDs, unique in scope of a gesture.
     *
     * Some platforms report always small numbers, these take fast paths through
     * the algorithm, directly indexing an array. Bigger numbers take a slow
     * path using a hash map.
     *
     * The algorithm performance was measured and it doesn't impose
     * any significant slowdown on the event delivery.
     */
    private static class TouchMap {
        private static final int FAST_THRESHOLD = 10;
        int[] fastMap = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Map<Long, Integer> slowMap = new HashMap<Long, Integer>();
        List<Integer> order = new LinkedList<Integer>();
        List<Long> removed = new ArrayList<Long>(10);
        int counter = 0;
        int active = 0;

        public int add(long id) {
            counter++;
            active++;
            if (id < FAST_THRESHOLD) {
                fastMap[(int) id] = counter;
            } else {
                slowMap.put(id, counter);
            }
            order.add(counter);
            return counter;
        }

        public void remove(long id) {
            // book the removal - it needs to be done after all touch points
            // of an event are processed - see cleanup()
            removed.add(id);
        }

        public int get(long id) {
            if (id < FAST_THRESHOLD) {
                int result = fastMap[(int) id];
                if (result == 0) {
                    throw new RuntimeException("Platform reported wrong "
                            + "touch point ID");
                }
                return result;
            } else {
                try {
                    return slowMap.get(id);
                } catch (NullPointerException e) {
                    throw new RuntimeException("Platform reported wrong "
                            + "touch point ID");
                }
            }
        }

        public int getOrder(int id) {
            return order.indexOf(id);
        }

        // returns true if gesture finished (no finger is touched)
        public boolean cleanup() {
            for (long id : removed) {
                active--;
                order.remove(Integer.valueOf(get(id)));
                if (id < FAST_THRESHOLD) {
                    fastMap[(int) id] = 0;
                } else {
                    slowMap.remove(id);
                }
                if (active == 0) {
                    // gesture finished
                    counter = 0;
                }
            }
            removed.clear();
            return active == 0;
        }
    }


    /* *************************************************************************
     *                                                                         *
     *                         Drag and Drop Handling                          *
     *                                                                         *
     **************************************************************************/

    private ObjectProperty<EventHandler<? super DragEvent>> onDragEntered;

    public final void setOnDragEntered(EventHandler<? super DragEvent> value) {
        onDragEnteredProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragEntered() {
        return onDragEntered == null ? null : onDragEntered.get();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>> onDragEnteredProperty() {
        if (onDragEntered == null) {
            onDragEntered = new ObjectPropertyBase<EventHandler<? super DragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(DragEvent.DRAG_ENTERED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragEntered";
                }
            };
        }
        return onDragEntered;
    }

    private ObjectProperty<EventHandler<? super DragEvent>> onDragExited;

    public final void setOnDragExited(EventHandler<? super DragEvent> value) {
        onDragExitedProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragExited() {
        return onDragExited == null ? null : onDragExited.get();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>> onDragExitedProperty() {
        if (onDragExited == null) {
            onDragExited = new ObjectPropertyBase<EventHandler<? super DragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(DragEvent.DRAG_EXITED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragExited";
                }
            };
        }
        return onDragExited;
    }

    private ObjectProperty<EventHandler<? super DragEvent>> onDragOver;

    public final void setOnDragOver(EventHandler<? super DragEvent> value) {
        onDragOverProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragOver() {
        return onDragOver == null ? null : onDragOver.get();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>> onDragOverProperty() {
        if (onDragOver == null) {
            onDragOver = new ObjectPropertyBase<EventHandler<? super DragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(DragEvent.DRAG_OVER, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragOver";
                }
            };
        }
        return onDragOver;
    }

    // Do we want DRAG_TRANSFER_MODE_CHANGED event?
//    private ObjectProperty<EventHandler<? super DragEvent>> onDragTransferModeChanged;
//
//    public final void setOnDragTransferModeChanged(EventHandler<? super DragEvent> value) {
//        onDragTransferModeChangedProperty().set(value);
//    }
//
//    public final EventHandler<? super DragEvent> getOnDragTransferModeChanged() {
//        return onDragTransferModeChanged == null ? null : onDragTransferModeChanged.get();
//    }
//
//    
//    public ObjectProperty<EventHandler<? super DragEvent>> onDragTransferModeChangedProperty() {
//        if (onDragTransferModeChanged == null) {
//            onDragTransferModeChanged = new SimpleObjectProperty<EventHandler<? super DragEvent>>() {
//
//                @Override
//                protected void invalidated() {
//                    setEventHandler(DragEvent.DRAG_TRANSFER_MODE_CHANGED, get());
//                }
//            };
//        }
//        return onDragTransferModeChanged;
//    }

    private ObjectProperty<EventHandler<? super DragEvent>> onDragDropped;

    public final void setOnDragDropped(EventHandler<? super DragEvent> value) {
        onDragDroppedProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragDropped() {
        return onDragDropped == null ? null : onDragDropped.get();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>> onDragDroppedProperty() {
        if (onDragDropped == null) {
            onDragDropped = new ObjectPropertyBase<EventHandler<? super DragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(DragEvent.DRAG_DROPPED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragDropped";
                }
            };
        }
        return onDragDropped;
    }

    private ObjectProperty<EventHandler<? super DragEvent>> onDragDone;

    public final void setOnDragDone(EventHandler<? super DragEvent> value) {
        onDragDoneProperty().set(value);
    }

    public final EventHandler<? super DragEvent> getOnDragDone() {
        return onDragDone == null ? null : onDragDone.get();
    }

    
    public final ObjectProperty<EventHandler<? super DragEvent>> onDragDoneProperty() {
        if (onDragDone == null) {
            onDragDone = new ObjectPropertyBase<EventHandler<? super DragEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(DragEvent.DRAG_DONE, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onDragDone";
                }
            };
        }
        return onDragDone;
    }

    
    public Dragboard startDragAndDrop(TransferMode... transferModes) {
        return startDragAndDrop(this, transferModes);
    }

    
    public void startFullDrag() {
        startFullDrag(this);
    }


    Dragboard startDragAndDrop(EventTarget source, TransferMode... transferModes) {
        Toolkit.getToolkit().checkFxUserThread();
        if (dndGesture == null ||
                (dndGesture.dragDetected != DragDetectedState.PROCESSING))
        {
            throw new IllegalStateException("Cannot start drag and drop " +
                    "outside of DRAG_DETECTED event handler");
        }

        Set<TransferMode> set = EnumSet.noneOf(TransferMode.class);
        for (TransferMode tm : InputEventUtils.safeTransferModes(transferModes)) {
            set.add(tm);
        }
        return dndGesture.startDrag(source, set);
    }

    void startFullDrag(EventTarget source) {
        Toolkit.getToolkit().checkFxUserThread();
        if (dndGesture.dragDetected != DragDetectedState.PROCESSING) {
            throw new IllegalStateException("Cannot start full drag " +
                    "outside of DRAG_DETECTED event handler");
        }

        if (dndGesture != null) {
            dndGesture.startFullPDR(source);
            return;
        }

        throw new IllegalStateException("Cannot start full drag when "
                + "mouse button is not pressed");
    }

    /* *************************************************************************
     *                                                                         *
     *                           Keyboard Handling                             *
     *                                                                         *
     **************************************************************************/

    
    private ObjectProperty<EventHandler<? super KeyEvent>> onKeyPressed;

    public final void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
        onKeyPressedProperty().set(value);
    }

    public final EventHandler<? super KeyEvent> getOnKeyPressed() {
        return onKeyPressed == null ? null : onKeyPressed.get();
    }

    public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyPressedProperty() {
        if (onKeyPressed == null) {
            onKeyPressed = new ObjectPropertyBase<EventHandler<? super KeyEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(KeyEvent.KEY_PRESSED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onKeyPressed";
                }
            };
        }
        return onKeyPressed;
    }

    
    private ObjectProperty<EventHandler<? super KeyEvent>> onKeyReleased;

    public final void setOnKeyReleased(EventHandler<? super KeyEvent> value) {
        onKeyReleasedProperty().set(value);
    }

    public final EventHandler<? super KeyEvent> getOnKeyReleased() {
        return onKeyReleased == null ? null : onKeyReleased.get();
    }

    public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyReleasedProperty() {
        if (onKeyReleased == null) {
            onKeyReleased = new ObjectPropertyBase<EventHandler<? super KeyEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(KeyEvent.KEY_RELEASED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onKeyReleased";
                }
            };
        }
        return onKeyReleased;
    }

    
    private ObjectProperty<EventHandler<? super KeyEvent>> onKeyTyped;

    public final void setOnKeyTyped(
            EventHandler<? super KeyEvent> value) {
        onKeyTypedProperty().set( value);

    }

    public final EventHandler<? super KeyEvent> getOnKeyTyped(
    ) {
        return onKeyTyped == null ? null : onKeyTyped.get();
    }

    public final ObjectProperty<EventHandler<? super KeyEvent>> onKeyTypedProperty(
    ) {
        if (onKeyTyped == null) {
            onKeyTyped = new ObjectPropertyBase<EventHandler<? super KeyEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(KeyEvent.KEY_TYPED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onKeyTyped";
                }
            };
        }
        return onKeyTyped;
    }

    /* *************************************************************************
     *                                                                         *
     *                           Input Method Handling                         *
     *                                                                         *
     **************************************************************************/

    
    private ObjectProperty<EventHandler<? super InputMethodEvent>> onInputMethodTextChanged;

    public final void setOnInputMethodTextChanged(
            EventHandler<? super InputMethodEvent> value) {
        onInputMethodTextChangedProperty().set( value);
    }

    public final EventHandler<? super InputMethodEvent> getOnInputMethodTextChanged() {
        return onInputMethodTextChanged == null ? null : onInputMethodTextChanged.get();
    }

    public final ObjectProperty<EventHandler<? super InputMethodEvent>> onInputMethodTextChangedProperty() {
        if (onInputMethodTextChanged == null) {
            onInputMethodTextChanged = new ObjectPropertyBase<EventHandler<? super InputMethodEvent>>() {

                @Override
                protected void invalidated() {
                    setEventHandler(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, get());
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "onInputMethodTextChanged";
                }
            };
        }
        return onInputMethodTextChanged;
    }

    /*
     * This class represents a picked target - either node, or scne, or null.
     * It provides functionality needed for the targets and covers the fact
     * that they are different kinds of animals.
     */
    private static class TargetWrapper {
        private MappedScene scene;
        private MappedNode node;
        private PickResult result;

        
        public void fillHierarchy(final List<EventTarget> list) {
            list.clear();
            MappedNode n = node;
            while(n != null) {
                list.add(n);
                final MappedParent p = n.getParent();
                n = p != null ? p : n.getSubScene();
            }

            if (scene != null) {
                list.add(scene);
            }
        }

        public EventTarget getEventTarget() {
            return node != null ? node : scene;
        }

        public Cursor getCursor() {
            Cursor cursor = null;
            if (node != null) {
                cursor = node.getCursor();
                MappedNode n = node.getParent();
                while (cursor == null && n != null) {
                    cursor = n.getCursor();

                    final MappedParent p = n.getParent();
                    n = p != null ? p : n.getSubScene();
                }
            }
            return cursor;
        }

        public void clear() {
            set(null, null);
            result = null;
        }

        public void setNodeResult(PickResult result) {
            if (result != null) {
                this.result = result;
                final MappedNode n = result.getIntersectedNode();
                set(n, n.getScene());
            }
        }

        // Pass null scene if the mouse is outside of the window content
        public void setSceneResult(PickResult result, MappedScene scene) {
            if (result != null) {
                this.result = result;
                set(null, scene);
            }
        }

        public PickResult getResult() {
            return result;
        }

        public void copy(TargetWrapper tw) {
            node = tw.node;
            scene = tw.scene;
            result = tw.result;
        }

        private void set(MappedNode n, MappedScene s) {
            node = n;
            scene = s;
        }
    }

    /* ***********************************************************************
     *                                                                        *
     *                                                                        *
     *                                                                        *
     *************************************************************************/

    private static final Object USER_DATA_KEY = new Object();
    // A map containing a set of properties for this scene
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

    /* *************************************************************************
     *                                                                         *
     *                       Component Orientation Properties                  *
     *                                                                         *
     **************************************************************************/

    @SuppressWarnings("removal")
    private static final NodeOrientation defaultNodeOrientation =
            AccessController.doPrivileged(
                    (PrivilegedAction<Boolean>) () -> Boolean.getBoolean("javafx.scene.nodeOrientation.RTL")) ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.INHERIT;



    private ObjectProperty<NodeOrientation> nodeOrientation;
    private EffectiveOrientationProperty effectiveNodeOrientationProperty;

    private NodeOrientation effectiveNodeOrientation;

    public final void setNodeOrientation(NodeOrientation orientation) {
        nodeOrientationProperty().set(orientation);
    }

    public final NodeOrientation getNodeOrientation() {
        return nodeOrientation == null ? defaultNodeOrientation : nodeOrientation.get();
    }

    
    public final ObjectProperty<NodeOrientation> nodeOrientationProperty() {
        if (nodeOrientation == null) {
            nodeOrientation = new StyleableObjectProperty<NodeOrientation>(defaultNodeOrientation) {
                @Override
                protected void invalidated() {
                    sceneEffectiveOrientationInvalidated();
                    getRoot().applyCss();
                }

                @Override
                public Object getBean() {
                    return MappedScene.this;
                }

                @Override
                public String getName() {
                    return "nodeOrientation";
                }

                @Override
                public CssMetaData getCssMetaData() {
                    //TODO - not yet supported
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }
        return nodeOrientation;
    }

    public final NodeOrientation getEffectiveNodeOrientation() {
        if (effectiveNodeOrientation == null) {
            effectiveNodeOrientation = calcEffectiveNodeOrientation();
        }

        return effectiveNodeOrientation;
    }

    
    public final ReadOnlyObjectProperty<NodeOrientation>
    effectiveNodeOrientationProperty() {
        if (effectiveNodeOrientationProperty == null) {
            effectiveNodeOrientationProperty =
                    new EffectiveOrientationProperty();
        }

        return effectiveNodeOrientationProperty;
    }

    private void parentEffectiveOrientationInvalidated() {
        if (getNodeOrientation() == NodeOrientation.INHERIT) {
            sceneEffectiveOrientationInvalidated();
        }
    }

    private void sceneEffectiveOrientationInvalidated() {
        effectiveNodeOrientation = null;

        if (effectiveNodeOrientationProperty != null) {
            effectiveNodeOrientationProperty.invalidate();
        }

        getRoot().parentResolvedOrientationInvalidated();
    }

    private NodeOrientation calcEffectiveNodeOrientation() {
        NodeOrientation orientation = getNodeOrientation();
        if (orientation == NodeOrientation.INHERIT) {
            MappedWindow window = getWindow();
            if (window != null) {
                MappedWindow parent = null;
                if (window instanceof Stage) {
                    parent = ((Stage)window).getOwner();
                } else {
                    if (window instanceof PopupWindow) {
                        parent = ((PopupWindow)window).getOwnerWindow();
                    }
                }
                if (parent != null) {
                    MappedScene scene = parent.getScene();
                    if (scene != null) return scene.getEffectiveNodeOrientation();
                }
            }
            return NodeOrientation.LEFT_TO_RIGHT;
        }
        return orientation;
    }

    private final class EffectiveOrientationProperty
            extends ReadOnlyObjectPropertyBase<NodeOrientation> {
        @Override
        public NodeOrientation get() {
            return getEffectiveNodeOrientation();
        }

        @Override
        public Object getBean() {
            return MappedScene.this;
        }

        @Override
        public String getName() {
            return "effectiveNodeOrientation";
        }

        public void invalidate() {
            fireValueChangedEvent();
        }
    }

    private Map<MappedNode, Accessible> accMap;
    Accessible removeAccessible(MappedNode node) {
        if (accMap == null) return null;
        return accMap.remove(node);
    }

    void addAccessible(MappedNode node, Accessible acc) {
        if (accMap == null) {
            accMap = new HashMap<MappedNode, Accessible>();
        }
        accMap.put(node, acc);
    }

    private void disposeAccessibles() {
        if (accMap != null) {
            for (Map.Entry<MappedNode, Accessible> entry : accMap.entrySet()) {
                MappedNode node = entry.getKey();
                Accessible acc = entry.getValue();
                if (node.accessible != null) {
                    /* This node has already been initialized to another scene.
                     * Note an accessible can be returned to the node before the
                     * pulse if getAccessible() is called. In which case it must
                     * already being removed from accMap.
                     */
                    if (node.accessible == acc) {
                        System.err.println("[A11y] 'node.accessible == acc' should never happen.");
                    }
                    if (node.getScene() == this) {
                        System.err.println("[A11y] 'node.getScene() == this' should never happen.");
                    }
                    acc.dispose();
                } else {
                    if (node.getScene() == this) {
                        node.accessible = acc;
                    } else {
                        acc.dispose();
                    }
                }
            }
            accMap.clear();
        }
    }

    private Accessible accessible;
    Accessible getAccessible() {
        /*
         * The accessible for the MappedScene should never be
         * requested when the peer is not set.
         * This can only happen in a error case where a
         * descender of this MappedScene was not disposed and
         * it still being used by the AT client and trying
         * to reach to the top level window.
         */
        if (peer == null) return null;
        if (accessible == null) {
            accessible = Application.GetApplication().createAccessible();
            accessible.setEventHandler(new Accessible.EventHandler() {
                @SuppressWarnings("removal")
                @Override public AccessControlContext getAccessControlContext() {
                    return getPeer().getAccessControlContext();
                }

                @Override public Object getAttribute(AccessibleAttribute attribute,
                                                     Object... parameters) {
                    switch (attribute) {
                        case CHILDREN: {
                            MappedParent root = getRoot();
                            if (root != null) {
                                return FXCollections.observableArrayList(root);
                            }
                            break;
                        }
                        case TEXT: {
                            MappedWindow w = getWindow();
                            if (w instanceof Stage) {
                                return ((Stage)w).getTitle();
                            }
                            break;
                        }
                        case NODE_AT_POINT: {
                            MappedWindow window = getWindow();
                            /* is this screen to scene translation correct ? not considering camera ? */
                            Point2D pt = (Point2D)parameters[0];
                            PickResult res = pick(pt.getX() - getX() - window.getX(), pt.getY() - getY() - window.getY());
                            if (res != null) {
                                MappedNode node = res.getIntersectedNode();
                                if (node != null) return node;
                            }
                            return getRoot();//not sure
                        }
                        case ROLE: {
                            if (getRoot() != null && getRoot().getAccessibleRole() == AccessibleRole.DIALOG) {
                                return AccessibleRole.DIALOG;
                            } else {
                                return AccessibleRole.PARENT;
                            }
                        }
                        case SCENE: return MappedScene.this;
                        case FOCUS_NODE: {
                            if (transientFocusContainer != null) {
                                return transientFocusContainer.queryAccessibleAttribute(AccessibleAttribute.FOCUS_NODE);
                            }
                            return getFocusOwner();
                        }
                        default:
                    }
                    return super.getAttribute(attribute, parameters);
                }
            });
            PlatformImpl.accessibilityActiveProperty().set(true);
        }
        return accessible;
    }
}
