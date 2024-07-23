package com.github.WildePizza.gui.javafx.mapped;

import java.security.AccessController;

class PropertyHelper {

    // Function to return whether a system property is set to true. Note that
    // this runs within a doPrivilege block so this function must be package-private.
    static boolean getBooleanProperty(final String propName) {
        try {
            @SuppressWarnings("removal")
            boolean answer =
                    AccessController.doPrivileged((java.security.PrivilegedAction<Boolean>) () -> {
                        String propVal = System.getProperty(propName);
                        return "true".equals(propVal.toLowerCase());
                    });
            return answer;
        } catch (Exception any) {
        }
        return false;
    }

}
