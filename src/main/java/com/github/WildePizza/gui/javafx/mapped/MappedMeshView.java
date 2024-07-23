package com.github.WildePizza.gui.javafx.mapped;

import com.sun.prism.GraphicsResource;
import com.sun.prism.Material;
import javafx.scene.shape.CullFace;

import java.awt.*;

public interface MappedMeshView extends GraphicsResource {

    public final static int CULL_NONE = CullFace.NONE.ordinal();
    public final static int CULL_BACK = CullFace.BACK.ordinal();
    public final static int CULL_FRONT = CullFace.FRONT.ordinal();

    public void setCullingMode(int mode);

    public void setMaterial(Material material);

    public void setWireframe(boolean wireframe);

    public void setAmbientLight(float r, float g, float b);

    public void setLight(int index, float x, float y, float z, float r, float g, float b, float w,
                         float ca, float la, float qa, float maxRange, float dirX, float dirY, float dirZ,
                         float innerAngle, float outerAngle, float falloff);

    public void render(MappedGraphics g);

    public boolean isValid();
}
