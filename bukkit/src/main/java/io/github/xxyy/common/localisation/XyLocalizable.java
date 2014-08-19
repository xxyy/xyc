/*
 * Copyright (c) 2013 - 2014 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without explicit written permission
 *  from the original author and may result in legal steps being taken.
 */

package io.github.xxyy.common.localisation;

/**
 * Objects that have their own locale files!
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 */
public interface XyLocalizable {
    /**
     * @return an array of the names of languages included in this object's JAR.
     */
    public String[] getShippedLocales();

    /**
     * @return the name of the folder that {@link XyLocalizable} should be saved in.
     */
    public String getName();
}
