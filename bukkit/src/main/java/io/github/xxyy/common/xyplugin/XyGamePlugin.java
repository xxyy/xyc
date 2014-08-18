/*
 * Copyright (c) 2013 - 2014 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without permission from the
 *  original author and may result in legal steps being taken.
 */

package io.github.xxyy.common.xyplugin;

/**
 * A plugin using XyGameLib.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 */
public interface XyGamePlugin {
    /**
     * Called when a fatal error occurred. If possible, this should shutdown or restart the server running this plugin.
     *
     * @param desc   Short description of the error.
     * @param caller Which method reported this error?
     */
    public void setError(String desc, String caller);
}