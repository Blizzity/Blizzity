package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.sg.prism.NGShape3D;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import com.sun.javafx.logging.PlatformLogger;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;



public abstract class MappedShape3D extends MappedNode {
    static {
        // This is used by classes in different packages to get access to
        // private and package private methods.
        MappedShape3DHelper.setShape3DAccessor(new MappedShape3DHelper.Shape3DAccessor() {
            @Override
            public void doUpdatePeer(MappedNode node) {
                ((MappedShape3D) node).doUpdatePeer();
            }

            @Override
            public BaseBounds doComputeGeomBounds(MappedNode node,
                                                  BaseBounds bounds, BaseTransform tx) {
                return ((MappedShape3D) node).doComputeGeomBounds(bounds, tx);
            }

            @Override
            public boolean doComputeContains(MappedNode node, double localX, double localY) {
                return ((MappedShape3D) node).doComputeContains(localX, localY);
            }
        });
    }

    // NOTE: Need a way to specify shape tessellation resolution, may use metric relate to window resolution
    // Will not support dynamic refinement in FX8

    // TODO: 3D - May provide user convenient utility to compose images in a single image for shapes such as Box or Cylinder

    private static final PhongMaterial DEFAULT_MATERIAL = new PhongMaterial();

    protected MappedShape3D() {
        if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
            String logname = MappedShape3D.class.getName();
            PlatformLogger.getLogger(logname).warning("System can't support "
                    + "ConditionalFeature.SCENE3D");
        }
    }

//    PredefinedMeshManager manager = PredefinedMeshManager.getInstance();
    Key key;

    
    abstract static class Key {

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();
    }

    
    private ObjectProperty<Material> material;

    public final void setMaterial(Material value) {
        materialProperty().set(value);
    }

    public final Material getMaterial() {
        return material == null ? null : material.get();
    }

    public final ObjectProperty<Material> materialProperty() {
        if (material == null) {
            material = new SimpleObjectProperty<Material>(MappedShape3D.this,
                    "material") {

                private Material old = null;
                private final ChangeListener<Boolean> materialChangeListener =
                        (observable, oldValue, newValue) -> {
                            if (newValue) {
                                MappedNodeHelper.markDirty(MappedShape3D.this, DirtyBits.MATERIAL);
                            }
                        };
                private final WeakChangeListener<Boolean> weakMaterialChangeListener =
                        new WeakChangeListener(materialChangeListener);

                @Override protected void invalidated() {
                    if (old != null) {
                        MaterialHelper.dirtyProperty(old).removeListener(weakMaterialChangeListener);
                    }
                    Material newMaterial = get();
                    if (newMaterial != null) {
                        MaterialHelper.dirtyProperty(newMaterial).addListener(weakMaterialChangeListener);
                    }
                    MappedNodeHelper.markDirty(MappedShape3D.this, DirtyBits.MATERIAL);
                    MappedNodeHelper.geomChanged(MappedShape3D.this);
                    old = newMaterial;
                }
            };
        }
        return material;
    }

    
    private ObjectProperty<DrawMode> drawMode;

    public final void setDrawMode(DrawMode value) {
        drawModeProperty().set(value);
    }

    public final DrawMode getDrawMode() {
        return drawMode == null ? DrawMode.FILL : drawMode.get();
    }

    public final ObjectProperty<DrawMode> drawModeProperty() {
        if (drawMode == null) {
            drawMode = new SimpleObjectProperty<DrawMode>(MappedShape3D.this,
                    "drawMode", DrawMode.FILL) {

                @Override
                protected void invalidated() {
                    MappedNodeHelper.markDirty(MappedShape3D.this, DirtyBits.NODE_DRAWMODE);
                }
            };
        }
        return drawMode;
    }

    
    private ObjectProperty<CullFace> cullFace;

    public final void setCullFace(CullFace value) {
        cullFaceProperty().set(value);
    }

    public final CullFace getCullFace() {
        return cullFace == null ? CullFace.BACK : cullFace.get();
    }

    public final ObjectProperty<CullFace> cullFaceProperty() {
        if (cullFace == null) {
            cullFace = new SimpleObjectProperty<CullFace>(MappedShape3D.this,
                    "cullFace", CullFace.BACK) {

                @Override
                protected void invalidated() {
                    MappedNodeHelper.markDirty(MappedShape3D.this, DirtyBits.NODE_CULLFACE);
                }
            };
        }
        return cullFace;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        // TODO: 3D - Evaluate this logic
        return new BoxBounds(0, 0, 0, 0, 0, 0);
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private boolean doComputeContains(double localX, double localY) {
        return false;
    }

    /*
     * Note: This method MUST only be called via its accessor method.
     */
    private void doUpdatePeer() {
        final NGShape3D peer = MappedNodeHelper.getPeer(this);
        if (MappedNodeHelper.isDirty(this, DirtyBits.MATERIAL)) {
            Material mat = getMaterial() == null ? DEFAULT_MATERIAL : getMaterial();
            MaterialHelper.updatePG(mat); // new material should be updated
            peer.setMaterial(MaterialHelper.getNGMaterial(mat));
        }
        if (MappedNodeHelper.isDirty(this, DirtyBits.NODE_DRAWMODE)) {
            peer.setDrawMode(getDrawMode() == null ? DrawMode.FILL : getDrawMode());
        }
        if (MappedNodeHelper.isDirty(this, DirtyBits.NODE_CULLFACE)) {
            peer.setCullFace(getCullFace() == null ? CullFace.BACK : getCullFace());
        }
    }

}
