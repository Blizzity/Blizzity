package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.util.Utils;

public class MappedPerspectiveCameraHelper extends MappedCameraHelper {

    private static final MappedPerspectiveCameraHelper theInstance;
    private static PerspectiveCameraAccessor perspectiveCameraAccessor;

    static {
        theInstance = new MappedPerspectiveCameraHelper();
        Utils.forceInit(MappedPerspectiveCamera.class);
    }

    private static MappedPerspectiveCameraHelper getInstance() {
        return theInstance;
    }

    public static void initHelper(MappedPerspectiveCamera perspectiveCamera) {
        setHelper(perspectiveCamera, getInstance());
    }

    @Override
    protected MappedNGNode createPeerImpl(MappedNode node) {
        return perspectiveCameraAccessor.doCreatePeer(node);
    }

    @Override
    protected void updatePeerImpl(MappedNode node) {
        super.updatePeerImpl(node);
        perspectiveCameraAccessor.doUpdatePeer(node);
    }

    public static void setPerspectiveCameraAccessor(final PerspectiveCameraAccessor newAccessor) {
        if (perspectiveCameraAccessor != null) {
            throw new IllegalStateException();
        }

        perspectiveCameraAccessor = newAccessor;
    }

    public interface PerspectiveCameraAccessor {
        MappedNGNode doCreatePeer(MappedNode node);
        void doUpdatePeer(MappedNode node);
    }

}
