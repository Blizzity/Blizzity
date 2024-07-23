package com.github.WildePizza.gui.javafx.mapped;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;

public final class MappedNodeEffectInput extends Effect {
    public static enum RenderType {
        EFFECT_CONTENT,
        CLIPPED_CONTENT,
        FULL_CONTENT,
    };

    private MappedNGNode node;
    private RenderType renderType;
    private BaseBounds tempBounds = new RectBounds();

    private ImageData cachedIdentityImageData;
    private ImageData cachedTransformedImageData;
    private BaseTransform cachedTransform;

    public MappedNodeEffectInput(MappedNGNode node) {
        this(node, RenderType.EFFECT_CONTENT);
    }

    public MappedNodeEffectInput(MappedNGNode node, RenderType renderType) {
        this.node = node;
        this.renderType = renderType;
    }

    public MappedNGNode getNode() {
        return node;
    }

    public void setNode(MappedNGNode node) {
        if (this.node != node) {
            this.node = node;
            flush();
        }
    }

    static boolean contains(ImageData cachedImage, Rectangle imgbounds) {
        // We only cache ImageData objects with Identity transforms installed...
        Rectangle cachedBounds = cachedImage.getUntransformedBounds();
        return cachedBounds.contains(imgbounds);
    }

    @Override
    public BaseBounds getBounds(BaseTransform transform,
                                Effect defaultInput)
    {
        // TODO: update Effect.getBounds() to take Rectangle2D param so
        // that we can avoid creating garbage here? (RT-23958)
        BaseTransform t = transform == null ?
                BaseTransform.IDENTITY_TRANSFORM : transform;
        tempBounds = node.getContentBounds(tempBounds, t);
        return tempBounds.copy();
    }

    @Override
    public ImageData filter(FilterContext fctx,
                            BaseTransform transform,
                            Rectangle outputClip,
                            Object renderHelper,
                            Effect defaultInput)
    {
        if (renderHelper instanceof MappedPrRenderInfo) {
            MappedGraphics g = ((MappedPrRenderInfo) renderHelper).getGraphics();
            if (g != null) {
                render(g, transform);
                return null;
            }
        }
        Rectangle bounds =
                getImageBoundsForNode(node, renderType, transform, outputClip);
        if (transform.isIdentity()) {
            if (cachedIdentityImageData != null &&
                    contains(cachedIdentityImageData, bounds) &&
                    cachedIdentityImageData.validate(fctx))
            {
                cachedIdentityImageData.addref();
                return cachedIdentityImageData;
            }
        } else if (cachedTransformedImageData != null &&
                contains(cachedTransformedImageData, bounds) &&
                cachedTransformedImageData.validate(fctx) &&
                cachedTransform.equals(transform))
        {
            cachedTransformedImageData.addref();
            return cachedTransformedImageData;
        }
        // this ImageData will be validated by whoever uses the result of this
        // filter operation
        ImageData retData =
                getImageDataForBoundedNode(fctx, node, renderType, transform, bounds);
        if (transform.isIdentity()) {
            flushIdentityImage();
            cachedIdentityImageData = retData;
            cachedIdentityImageData.addref();
        } else {
            flushTransformedImage();
            cachedTransform = transform.copy();
            cachedTransformedImageData = retData;
            cachedTransformedImageData.addref();
        }
        return retData;
    }

    @Override
    public AccelType getAccelType(FilterContext fctx) {
        return AccelType.INTRINSIC;
    }

    public void flushIdentityImage() {
        if (cachedIdentityImageData != null) {
            cachedIdentityImageData.unref();
            cachedIdentityImageData = null;
        }
    }

    public void flushTransformedImage() {
        if (cachedTransformedImageData != null) {
            cachedTransformedImageData.unref();
            cachedTransformedImageData = null;
        }
        cachedTransform = null;
    }

    public void flush() {
        flushIdentityImage();
        flushTransformedImage();
    }

    public void render(MappedGraphics g, BaseTransform transform) {
        BaseTransform savetx = null;
        if (!transform.isIdentity()) {
            savetx = g.getTransformNoClone().copy();
            g.transform(transform);
        }
        node.renderContent(g);
        if (savetx != null) {
            g.setTransform(savetx);
        }
    }

    static ImageData getImageDataForNode(FilterContext fctx,
                                         MappedNGNode node, boolean contentOnly,
                                         BaseTransform transform,
                                         Rectangle clip)
    {
        RenderType rendertype = contentOnly
                ? RenderType.EFFECT_CONTENT
                : RenderType.FULL_CONTENT;
        Rectangle r = getImageBoundsForNode(node, rendertype, transform, clip);
        return getImageDataForBoundedNode(fctx, node, rendertype, transform, r);
    }

    static Rectangle getImageBoundsForNode(MappedNGNode node, RenderType type,
                                           BaseTransform transform,
                                           Rectangle clip)
    {
        BaseBounds bounds = new RectBounds();
        switch (type) {
            case EFFECT_CONTENT:
                bounds = node.getContentBounds(bounds, transform);
                break;
            case FULL_CONTENT:
                bounds = node.getCompleteBounds(bounds, transform);
                break;
            case CLIPPED_CONTENT:
                bounds = node.getClippedBounds(bounds, transform);
                break;
        }
        Rectangle r = new Rectangle(bounds);
        if (clip != null) {
            r.intersectWith(clip);
        }
        return r;
    }

    
    private static ImageData
    getImageDataForBoundedNode(FilterContext fctx,
                               MappedNGNode node, RenderType renderType,
                               BaseTransform transform,
                               Rectangle bounds)
    {
        MappedPrDrawable ret = (MappedPrDrawable)
                Effect.getCompatibleImage(fctx, bounds.width, bounds.height);
        if (ret != null) {
            MappedGraphics g = ret.createGraphics();
            g.translate(-bounds.x, -bounds.y);
            if (transform != null) {
                g.transform(transform);
            }
            switch (renderType) {
                case EFFECT_CONTENT:
                    node.renderContent(g);
                    break;
                case FULL_CONTENT:
                    node.render(g);
                    break;
                case CLIPPED_CONTENT:
                    node.renderForClip(g);
                    break;
            }
        }
        return new ImageData(fctx, ret, bounds);
    }

    @Override
    public boolean reducesOpaquePixels() {
        return false;
    }

    @Override
    public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
        return null; // Never called
    }
}
