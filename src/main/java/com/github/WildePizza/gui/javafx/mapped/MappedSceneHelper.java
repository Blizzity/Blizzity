package com.github.WildePizza.gui.javafx.mapped;

import com.sun.glass.ui.Accessible;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.util.Utils;
import javafx.scene.MappedCamera;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.MappedWindow;


public final class MappedSceneHelper {
    private static SceneAccessor sceneAccessor;

    static {
        Utils.forceInit(MappedScene.class);
    }

    private MappedSceneHelper() {
    }

    public static void enableInputMethodEvents(MappedScene scene, boolean enable) {
        sceneAccessor.enableInputMethodEvents(scene, enable);
    }

    public static void processKeyEvent(MappedScene scene, KeyEvent e) {
        sceneAccessor.processKeyEvent(scene, e);
    }

    public static void processMouseEvent(MappedScene scene, MouseEvent e) {
        sceneAccessor.processMouseEvent(scene, e);
    }

    public static void preferredSize(MappedScene scene) {
        sceneAccessor.preferredSize(scene);
    }

    public static void disposePeer(MappedScene scene) {
        sceneAccessor.disposePeer(scene);
    }

    public static void initPeer(MappedScene scene) {
        sceneAccessor.initPeer(scene);
    }

    public static void setWindow(MappedScene scene, MappedWindow window) {
        sceneAccessor.setWindow(scene, window);
    }

    public static TKScene getPeer(MappedScene scene) {
        return sceneAccessor.getPeer(scene);
    }

    public static void setAllowPGAccess(boolean flag) {
        sceneAccessor.setAllowPGAccess(flag);
    }

    public static void parentEffectiveOrientationInvalidated(
            final MappedScene scene) {
        sceneAccessor.parentEffectiveOrientationInvalidated(scene);
    }

    public static MappedCamera getEffectiveCamera(final MappedScene scene) {
        return sceneAccessor.getEffectiveCamera(scene);
    }

    public static MappedScene createPopupScene(final MappedParent root) {
        return sceneAccessor.createPopupScene(root);
    }

    public static Accessible getAccessible(MappedScene scene) {
        return sceneAccessor.getAccessible(scene);
    }

    public static void setSceneAccessor(final SceneAccessor newAccessor) {
        if (sceneAccessor != null) {
            throw new IllegalStateException();
        }

        sceneAccessor = newAccessor;
    }

    public static SceneAccessor getSceneAccessor() {
        if (sceneAccessor == null) throw new IllegalStateException();
        return sceneAccessor;
    }

    public interface SceneAccessor {
        void enableInputMethodEvents(MappedScene scene, boolean enable);

        void processKeyEvent(MappedScene scene, KeyEvent e);

        void processMouseEvent(MappedScene scene, MouseEvent e);

        void preferredSize(MappedScene scene);

        void disposePeer(MappedScene scene);

        void initPeer(MappedScene scene);

        void setWindow(MappedScene scene, MappedWindow window);

        TKScene getPeer(MappedScene scene);

        void setAllowPGAccess(boolean flag);

        void parentEffectiveOrientationInvalidated(MappedScene scene);

        MappedCamera getEffectiveCamera(MappedScene scene);

        MappedScene createPopupScene(MappedParent root);

        void setTransientFocusContainer(MappedScene scene, MappedNode node);

        Accessible getAccessible(MappedScene scene);
    }

}
