package com.github.WildePizza.gui.javafx.mapped;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.javafx.PlatformUtil;
import com.sun.scenario.effect.FilterContext;


class MappedRendererFactory {

    private static String rootPkg = MappedRenderer.rootPkg;
    private static boolean tryRSL = true;
    private static boolean trySIMD = false;
    // by default we only enable jogl hw acceleration on MacOS
    private static boolean tryJOGL = PlatformUtil.isMac();
    private static boolean tryPrism = true;

    static {
        try {
            if ("false".equals(System.getProperty("decora.rsl"))) {
                tryRSL = false;
            }
            if ("false".equals(System.getProperty("decora.simd"))) {
                trySIMD = false;
            }
            String tryJOGLProp = System.getProperty("decora.jogl");
            if (tryJOGLProp != null) {
                tryJOGL = Boolean.parseBoolean(tryJOGLProp);
            }
            if ("false".equals(System.getProperty("decora.prism"))) {
                tryPrism = false;
            }
        } catch (SecurityException ignore) {
        }
    }

    private static boolean isRSLFriendly(Class klass) {
        // can't use reflection here to check for sun.* class when running
        // in sandbox; however, we are allowed to walk up the tree and
        // check names of interfaces loaded by the system
        if (klass.getName().equals("sun.java2d.pipe.hw.AccelGraphicsConfig")) {
            return true;
        }
        boolean rsl = false;
        for (Class iface : klass.getInterfaces()) {
            if (isRSLFriendly(iface)) {
                rsl = true;
                break;
            }
        }
        return rsl;
    }

    private static boolean isRSLAvailable(FilterContext fctx) {
        return isRSLFriendly(fctx.getReferent().getClass());
    }

    private static MappedRenderer createRSLRenderer(FilterContext fctx) {
        try {
            Class klass = Class.forName(rootPkg + ".impl.j2d.rsl.RSLRenderer");
            Method m = klass.getMethod("createRenderer",
                    new Class[] { FilterContext.class });
            return (MappedRenderer)m.invoke(null, new Object[] { fctx });
        } catch (Throwable e) {}

        return null;
    }

    private static MappedRenderer createJOGLRenderer(FilterContext fctx) {
        if (tryJOGL) {
            try {
                Class klass = Class.forName(rootPkg + ".impl.j2d.jogl.JOGLRenderer");
                Method m = klass.getMethod("createRenderer",
                        new Class[] { FilterContext.class });
                return (MappedRenderer)m.invoke(null, new Object[] { fctx });
            } catch (Throwable e) {}
            // don't disable jogl if failed, it may be available for other config
        }
        return null;
    }

    private static MappedRenderer createPrismRenderer(FilterContext fctx) {
        if (tryPrism) {
            try {
                Class klass = Class.forName(rootPkg + ".impl.prism.MappedPrRenderer");
                Method m = klass.getMethod("createRenderer",
                        new Class[] { FilterContext.class });
                return (MappedRenderer)m.invoke(null, new Object[] { fctx });
            } catch (Throwable e) {
                e.printStackTrace();
            }
            // don't disable prism if failed, it may be available for other config
        }
        return null;
    }

    private static MappedRenderer getSSERenderer() {
        if (trySIMD) {
            try {
                Class klass = Class.forName(rootPkg + ".impl.j2d.J2DSWRenderer");
                Method m = klass.getMethod("getSSEInstance", (Class[])null);
                MappedRenderer sseRenderer = (MappedRenderer)m.invoke(null, (Object[])null);
                if (sseRenderer != null) {
                    return sseRenderer;
                }
            } catch (Throwable e) {e.printStackTrace();}
            // don't bother trying to find SSE renderer again
            trySIMD = false;
        }
        return null;
    }

    private static MappedRenderer getJavaRenderer() {
        try {
            Class klass = Class.forName(rootPkg + ".impl.prism.sw.PSWRenderer");
            Class screenClass = Class.forName("com.sun.glass.ui.Screen");
            Method m = klass.getMethod("createJSWInstance",
                    new Class[] { screenClass });
            MappedRenderer jswRenderer =
                    (MappedRenderer)m.invoke(null, new Object[] { null } );
            if (jswRenderer != null) {
                return jswRenderer;
            }
        } catch (Throwable e) {e.printStackTrace();}
        return null;
    }

    private static MappedRenderer getJavaRenderer(FilterContext fctx) {
        try {
            Class klass = Class.forName(rootPkg + ".impl.prism.sw.PSWRenderer");
            Method m = klass.getMethod("createJSWInstance",
                    new Class[] { FilterContext.class });
            MappedRenderer jswRenderer =
                    (MappedRenderer)m.invoke(null, new Object[] { fctx } );
            if (jswRenderer != null) {
                return jswRenderer;
            }
        } catch (Throwable e) {}
        return null;
    }

    static MappedRenderer getSoftwareRenderer() {
        MappedRenderer r = getSSERenderer();
        if (r == null) {
            r = getJavaRenderer();
        }
        return r;
    }

    @SuppressWarnings("removal")
    static MappedRenderer createRenderer(final FilterContext fctx) {
        return AccessController.doPrivileged((PrivilegedAction<MappedRenderer>) () -> {
            MappedRenderer r = null;
            // Class.getSimpleName is not available on CDC
            String klassName = fctx.getClass().getName();
            String simpleName = klassName.substring(klassName.lastIndexOf(".") + 1);

            if (simpleName.equals("PrFilterContext") && tryPrism) {
                r = createPrismRenderer(fctx);
            }
            // check to see whether one of the hardware accelerated
            // Java 2D pipelines is in use and exposes the necessary
            // "resource sharing layer" APIs (only in Sun's JDK 6u10 and above)
            if (r == null && tryRSL && isRSLAvailable(fctx)) {
                // try locating an RSLRenderer (need to use reflection in case
                // certain RSL backend classes are not available;
                // this step will trigger lazy downloading of impl jars
                // via JNLP, if not already available)
                r = createRSLRenderer(fctx);
            }
            if (r == null && tryJOGL) {
                // next try the JOGL renderer
                r = createJOGLRenderer(fctx);
            }
            if (r == null && trySIMD) {
                // next try the SSE renderer
                r = getSSERenderer();
            }
            if (r == null) {
                // otherwise, fall back on the Java/CPU renderer
                r = getJavaRenderer(fctx);
            }
            return r;
        });
    }
}
