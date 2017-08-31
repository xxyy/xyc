/*
 * MIT License
 *
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package li.l1t.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides static utility methods to help with outputting text
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 18.8.14
 */
public final class TextOutputHelper {
    private TextOutputHelper() {

    }

    /**
     * Prints a message to {@link System#out} and the provided logger {@code lgr} if it is not {@code null}.
     *
     * @param message Message to print
     * @param lgr     The logger to print it to, can be {@code null}.
     * @param lvl     {@link java.util.logging.Level} to use
     */
    public static void printAndOrLog(String message, Logger lgr, Level lvl) {
        System.out.println(message);
        if (lgr != null) {
            lgr.log(lvl, message);
        }
    }
}
