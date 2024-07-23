package com.github.WildePizza.gui.javafx.mapped;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.Effect.AccelType;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.FloatMap;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.LockableResource;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.MappedRenderer;

public abstract class MappedRenderer {

    
    public static enum RendererState {
        
        NOTREADY,
        
        OK,
        
        LOST,
        
        DISPOSED
    }

    public static final String rootPkg = "com.sun.scenario.effect";
    private static final Map<FilterContext, MappedRenderer> rendererMap =
            new HashMap<FilterContext, MappedRenderer>(1);
    private Map<String, EffectPeer> peerCache =
            Collections.synchronizedMap(new HashMap<String, EffectPeer>(5));
    private final MappedImagePool imagePool;

    @SuppressWarnings("removal")
    protected static final boolean verbose = AccessController.doPrivileged(
            (PrivilegedAction<Boolean>) () -> Boolean.getBoolean("decora.verbose"));

    protected MappedRenderer() {
        this.imagePool = new MappedImagePool();
    }

    
    public abstract AccelType getAccelType();

    public abstract int getCompatibleWidth(int w);
    public abstract int getCompatibleHeight(int h);
    public abstract MappedPoolFilterable createCompatibleImage(int w, int h);

    public MappedPoolFilterable getCompatibleImage(int w, int h) {
        return imagePool.checkOut(this, w, h);
    }

    public void releaseCompatibleImage(Filterable image) {
        if (image instanceof MappedPoolFilterable) {
            MappedImagePool pool = ((MappedPoolFilterable) image).getImagePool();
            if (pool != null) {
                pool.checkIn((MappedPoolFilterable) image);
                return;
            }
//        } else {
            // Error?
        }
        image.unlock();
    }

    
    public void releasePurgatory() {
        imagePool.releasePurgatory();
    }

    
    public abstract void clearImage(Filterable image);
    public abstract ImageData createImageData(FilterContext fctx,
                                              Filterable src);

    public ImageData transform(FilterContext fctx, ImageData img,
                               int xpow2scales, int ypow2scales)
    {
        if (!img.getTransform().isIdentity()) {
            throw new InternalError("transform by powers of 2 requires untransformed source");
        }
        if ((xpow2scales | ypow2scales) == 0) {
            return img;
        }
        Affine2D at = new Affine2D();
        // Any amount of upscaling and up to 1 level of downscaling
        // can be handled by the filters themselves...
        while (xpow2scales < -1 || ypow2scales < -1) {
            Rectangle origbounds = img.getUntransformedBounds();
            Rectangle newbounds = new Rectangle(origbounds);
            double xscale = 1.0;
            double yscale = 1.0;
            if (xpow2scales < 0) {
                // To avoid loss, only scale down one step at a time
                xscale = 0.5;
                newbounds.width = (origbounds.width + 1) / 2;
                newbounds.x /= 2;
                xpow2scales++;
            }
            if (ypow2scales < 0) {
                // To avoid loss, only scale down one step at a time
                yscale = 0.5;
                newbounds.height = (origbounds.height + 1) / 2;
                newbounds.y /= 2;
                ypow2scales++;
            }
            at.setToScale(xscale, yscale);
            img = transform(fctx, img, at, origbounds, newbounds);
        }
        if ((xpow2scales | ypow2scales) != 0) {
            // assert xscale >= -1 and yscale >= -1
            double xscale = (xpow2scales < 0) ? 0.5 : 1 << xpow2scales;
            double yscale = (ypow2scales < 0) ? 0.5 : 1 << ypow2scales;
            at.setToScale(xscale, yscale);
            img = img.transform(at);
        }
        return img;
    }

    public abstract Filterable transform(FilterContext fctx,
                                         Filterable original,
                                         BaseTransform transform,
                                         Rectangle origBounds,
                                         Rectangle xformBounds);
    public abstract ImageData transform(FilterContext fctx, ImageData original,
                                        BaseTransform transform,
                                        Rectangle origBounds,
                                        Rectangle xformBounds);

    // NOTE: these two methods are only relevant to HW codepaths; should
    // find a way to push them down a level...
    public LockableResource createFloatTexture(int w, int h) {
        throw new InternalError();
    }
    public void updateFloatTexture(LockableResource texture, FloatMap map) {
        throw new InternalError();
    }

    
    public final synchronized EffectPeer
    getPeerInstance(FilterContext fctx, String name, int unrollCount)
    {
        // first look for a previously cached peer using only the base name
        // (e.g. GaussianBlur); software peers do not (currently) have
        // unrolled loops, so this step should locate those...
        EffectPeer peer = peerCache.get(name);
        if (peer != null) {
            return peer;
        }
        // failing that, if there is a positive unrollCount, we attempt
        // to find a previously cached hardware peer for that unrollCount
        if (unrollCount > 0) {
            peer = peerCache.get(name + "_" + unrollCount);
            if (peer != null) {
                return peer;
            }
        }

        peer = createPeer(fctx, name, unrollCount);
        if (peer == null) {
            throw new RuntimeException("Could not create peer  " + name +
                    " for renderer " + this);
        }
        // use the peer's unique name as the hashmap key
        peerCache.put(peer.getUniqueName(), peer);

        return peer;
    }


    
    public abstract RendererState getRendererState();

    
    protected abstract EffectPeer createPeer(FilterContext fctx,
                                             String name, int unrollCount);

    
    protected Collection<EffectPeer> getPeers() {
        return peerCache.values();
    }

    
    protected static MappedRenderer getSoftwareRenderer() {
        return MappedRendererFactory.getSoftwareRenderer();
    }

    
    protected abstract MappedRenderer getBackupRenderer();

    
    protected MappedRenderer getRendererForSize(Effect effect, int approxW, int approxH) {
        return this;
    }

    
    public static synchronized MappedRenderer getRenderer(FilterContext fctx) {
        if (fctx == null) {
            throw new IllegalArgumentException("FilterContext must be non-null");
        }

        MappedRenderer r = rendererMap.get(fctx);
        if (r != null) {
            if (r.getRendererState() == RendererState.NOTREADY) {
                return r;
            }
            if (r.getRendererState() == RendererState.OK) {
                return r;
            }
            if (r.getRendererState() == RendererState.LOST) {
                // use the backup while the renderer is in lost state, until
                // it is disposed (or forever if it can't be disposed/reset)
                // Note: we don't add it to the cache to prevent permanent
                // association of the backup renderer and this filter context.
                return r.getBackupRenderer();
            }
            if (r.getRendererState() == RendererState.DISPOSED) {
                r = null;
                // we remove disposed renderers below instead of here to cover
                // cases where we never use a context which the disposed
                // renderer is associated with
            }
        }

        if (r == null) {
            // clean up all disposed renderers first
            Collection<MappedRenderer> renderers = rendererMap.values();
            for (Iterator<MappedRenderer> iter = renderers.iterator(); iter.hasNext();)
            {
                MappedRenderer ren = iter.next();
                if (ren.getRendererState() == RendererState.DISPOSED) {
                    ren.imagePool.dispose();
                    iter.remove();
                }
            }

            r = MappedRendererFactory.createRenderer(fctx);
            if (r == null) {
                throw new RuntimeException("Error creating a MappedRenderer");
            } else {
                if (verbose) {
                    String klassName = r.getClass().getName();
                    String rname = klassName.substring(klassName.lastIndexOf(".")+1);
                    Object screen = fctx.getReferent();
                    System.out.println("Created " + rname +
                            " (AccelType=" + r.getAccelType() +
                            ") for " + screen);
                }
            }
            rendererMap.put(fctx, r);
        }
        return r;
    }

    
    public static MappedRenderer getRenderer(FilterContext fctx, Effect effect,
                                       int approxW, int approxH) {
        return getRenderer(fctx).getRendererForSize(effect, approxW, approxH);
    }

    
    public abstract boolean isImageDataCompatible(ImageData id);
}
