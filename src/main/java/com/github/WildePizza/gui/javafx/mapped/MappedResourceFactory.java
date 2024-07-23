package com.github.WildePizza.gui.javafx.mapped;

import com.sun.prism.*;
import com.sun.prism.impl.TextureResourcePool;
import com.sun.prism.shape.ShapeRep;

public interface MappedResourceFactory extends GraphicsResource {

    /**
     * Returns whether this resource factory has been disposed.
     * If this resource factory has been disposed, it is no longer valid and
     * will need to be recreated before any new resources can be created.
     * Any attempt to create a resource will be ignored and will return null.
     *
     * @return true if this resource factory has been disposed.
     */
    public boolean isDisposed();

    /**
     * Returns status of this graphics device, possibly reinitializing it.
     * If the device is not ready the createRTTexture and
     * present operations will fail.
     * As long as the device has not been disposed, creation of shaders and
     * regular textures will succeed and return valid resources.
     * All hardware resources (RenderTargets and SwapChains) have to be recreated
     * after a device-lost event notification.
     *
     * NOTE: since this method can reinitialize the graphics device if it has
     * been released, it should only be called at the start of a rendering pass.
     *
     * @return true if this graphics device is ready for use.
     */
    public boolean isDeviceReady();

    public TextureResourcePool getTextureResourcePool();

    /**
     * Returns a new {@code MappedTexture} containing the pixels from the given
     * image with the indicated texture edge wrap mode.
     * Note that the dimensions of the returned texture may be larger
     * than those of the given image.
     * <p>
     * Equivalent to (but perhaps more efficient than):
     * <pre><code>
     *     PixelFormat format = image.getPixelFormat();
     *     int w = image.getWidth();
     *     int h = image.getHeight();
     *     MappedTexture tex = createTexture(format, usageHint, wrapMode, w, h);
     *     tex.update(image, 0, 0, w, h);
     * </code></pre>
     *
     * @param image the pixel data to be uploaded to the new texture
     * @param usageHint the Dynamic vs. Static nature of the texture data
     * @param wrapMode the desired edge behavior (clamping vs. wrapping)
     * @return a new texture
     */
    public MappedTexture createTexture(Image image,
                                       MappedTexture.Usage usageHint,
                                       MappedTexture.WrapMode wrapMode);

    /**
     * Returns a new {@code MappedTexture} containing the pixels from the given
     * image with the indicated texture edge wrap mode.
     * Note that the dimensions of the returned texture may be larger
     * than those of the given image.
     * <p>
     * Equivalent to (but perhaps more efficient than):
     * <pre><code>
     *     PixelFormat format = image.getPixelFormat();
     *     int w = image.getWidth();
     *     int h = image.getHeight();
     *     MappedTexture tex = createTexture(format, usageHint, wrapMode, w, h, useMipmap);
     *     tex.update(image, 0, 0, w, h);
     * </code></pre>
     *
     * @param image the pixel data to be uploaded to the new texture
     * @param usageHint the Dynamic vs. Static nature of the texture data
     * @param wrapMode the desired edge behavior (clamping vs. wrapping)
     * @param useMipmap the flag indicates should texture be created with mipmap
     * @return a new texture
     */
    public MappedTexture createTexture(Image image, MappedTexture.Usage usageHint,
                                 MappedTexture.WrapMode wrapMode, boolean useMipmap);

    /**
     * Returns a new {@code MappedTexture} with the given format and edge wrapping
     * support.  Note that the dimensions of the returned texture may be larger
     * than those requested and the wrap mode may be a simulated version of
     * the type requested.
     *
     * @param formatHint intended pixel format of the data to be stored
     *     in this texture
     * @param usageHint the Dynamic vs. Static nature of the texture data
     * @param wrapMode intended wrap mode to be used for the texture
     * @param w width of the content in the texture
     * @param h height of the content in the texture
     * @return texture most appropriate for the given intended format, wrap
     * mode and dimensions
     */
    public MappedTexture createTexture(PixelFormat formatHint,
                                       MappedTexture.Usage usageHint,
                                       MappedTexture.WrapMode wrapMode,
                                       int w, int h);

    /**
     * Returns a new {@code MappedTexture} with the given format and edge wrapping
     * support.  Note that the dimensions of the returned texture may be larger
     * than those requested and the wrap mode may be a simulated version of
     * the type requested.
     *
     * @param formatHint intended pixel format of the data to be stored
     *     in this texture
     * @param usageHint the Dynamic vs. Static nature of the texture data
     * @param wrapMode intended wrap mode to be used for the texture
     * @param w width of the content in the texture
     * @param h height of the content in the texture
     * @param useMipmap the flag indicates should texture be created with mipmap
     * @return texture most appropriate for the given intended format, wrap
     * mode and dimensions
     */
    public MappedTexture createTexture(PixelFormat formatHint, MappedTexture.Usage usageHint,
                                 MappedTexture.WrapMode wrapMode, int w, int h, boolean useMipmap);

    /**
     * Returns a new {@code MappedTexture} that can contain the video image as specified
     * in the provided {@code MediaFrame}. Note that padding is almost implicit
     * since this method has to accommodate the line strides of each plane. Also
     * due to renderer limitations, some format conversion may be necessary so
     * the texture format may end up being different from the video image format.
     *
     * @param frame the video image that we need to create a new texture for
     * @return texture most appropriate for the given video image.
     */
    public MappedTexture createTexture(MediaFrame frame);

    /**
     * Returns a {@code MappedTexture} for the given image set up to use or
     * simulate the indicated wrap mode.
     * If no texture could be found in the cache, this method will create a
     * new texture and put it in the cache before returning it.
     * NOTE: the caller of this method should not hold a reference to the
     * cached texture beyond its immediate needs since the cache may be
     * cleared at any time.
     *
     * @param image the pixel data to be uploaded if the texture is new or
     *     needs new fringe pixels to simulate a new wrap mode
     * @param wrapMode the mode that describes the behavior for samples
     *     outside the content area
     * @return a cached texture
     */
    public MappedTexture getCachedTexture(Image image, MappedTexture.WrapMode wrapMode);

    /**
     * Returns a {@code MappedTexture} for the given image set up to use or
     * simulate the indicated wrap mode.
     * If no texture could be found in the cache, this method will create a
     * new texture and put it in the cache before returning it.
     * NOTE: the caller of this method should not hold a reference to the
     * cached texture beyond its immediate needs since the cache may be
     * cleared at any time.
     *
     * @param image the pixel data to be uploaded if the texture is new or
     *     needs new fringe pixels to simulate a new wrap mode
     * @param wrapMode the mode that describes the behavior for samples
     *     outside the content
     * @param useMipmap the flag indicates should mipmapping be used for this
     *     texture
     * @return a cached texture
     */
    public MappedTexture getCachedTexture(Image image, MappedTexture.WrapMode wrapMode, boolean useMipmap);

    /**
     * Returns true if the given {@code PixelFormat} is supported; otherwise
     * returns false.
     * <p>
     * Note that the following formats are guaranteed to be supported
     * across all devices:
     * <pre><code>
     *     BYTE_RGB
     *     BYTE_RGBA_PRE
     *     BYTE_GRAY
     *     BYTE_ALPHA
     * </code></pre>
     * <p>
     * Support for the other formats depends on the capabilities of the
     * device.  Be sure to call this method before attempting to create
     * a {@code MappedTexture} with a non-standard format and plan to have an
     * alternate codepath if the given format is not supported.
     *
     * @param format the {@code PixelFormat} to test
     * @return true if the given format is supported; false otherwise
     */
    public boolean isFormatSupported(PixelFormat format);

    public boolean isWrapModeSupported(MappedTexture.WrapMode mode);

    /**
     * Returns the maximum supported texture dimension for this device.
     * For example, if this method returns 2048, it means that textures
     * larger than 2048x2048 cannot be created.
     *
     * @return the maximum supported texture dimension
     */
    public int getMaximumTextureSize();

    public int getRTTWidth(int w, MappedTexture.WrapMode wrapMode);
    public int getRTTHeight(int h, MappedTexture.WrapMode wrapMode);

    public MappedTexture createMaskTexture(int width, int height, MappedTexture.WrapMode wrapMode);
    public MappedTexture createFloatTexture(int width, int height);
    public MappedRTTexture createRTTexture(int width, int height, MappedTexture.WrapMode wrapMode);
    public MappedRTTexture createRTTexture(int width, int height, MappedTexture.WrapMode wrapMode, boolean msaa);

    /**
     * A MappedTexture may have been obtained from a different resource factory.
     * @param tex the texture to check.
     * @return whether this texture is compatible.
     */
    public boolean isCompatibleTexture(MappedTexture tex);

    public Presentable createPresentable(PresentableState pState);

    public ShapeRep createPathRep();
    public ShapeRep createRoundRectRep();
    public ShapeRep createEllipseRep();
    public ShapeRep createArcRep();

    public void addFactoryListener(ResourceFactoryListener l);
    public void removeFactoryListener(ResourceFactoryListener l);

    public void setRegionTexture(MappedTexture texture);
    public MappedTexture getRegionTexture();
    public void setGlyphTexture(MappedTexture texture);
    public MappedTexture getGlyphTexture();
    public boolean isSuperShaderAllowed();

    /*
     * 3D stuff
     */
    public PhongMaterial createPhongMaterial();
    public MeshView createMeshView(Mesh mesh);
    public Mesh createMesh();
}
