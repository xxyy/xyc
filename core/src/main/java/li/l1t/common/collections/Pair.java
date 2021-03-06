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

package li.l1t.common.collections;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Represents an immutable pair of two objects related to each other.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2014-07-22
 */
public class Pair<L, R> {
    @Nullable
    protected final L left;
    @Nullable
    protected final R right;

    /**
     * Creates a new pair.
     *
     * @param left  the left element
     * @param right the right element
     * @deprecated was not intended to be exposed - use {@link #pairOf(Object, Object)}
     */
    @Deprecated
    public Pair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a new pair with given elements.
     *
     * @param left  the left element
     * @param right the right element
     * @param <L>   the left type
     * @param <R>   the right type
     * @return a new pair
     */
    @SuppressWarnings("deprecation")
    public static <L, R> Pair<L, R> pairOf(L left, R right) {
        return new Pair<>(left, right);
    }

    /**
     * @return the left element of this pair
     */
    @Nullable
    public L getLeft() {
        return left;
    }

    /**
     * @return the right element of this pair
     */
    @Nullable
    public R getRight() {
        return right;
    }

    /**
     * Creates a Stream out of this Couple's elements. Left will always be the first element.
     *
     * @return A Stream of this Couple's elements.
     */
    public Stream<?> stream() {
        return Stream.of(left, right);
    }

    /**
     * Checks whether this pair contains the parameter.
     *
     * @param obj object to check for
     * @return whether any of this pair's contents is the parameter
     */
    public boolean contains(@Nonnull Object obj) {
        return obj.equals(getLeft()) && obj.equals(getRight());
    }

    /**
     * Returns a new couple with this couple's right element and a new left element.
     *
     * @param newLeft the new left element
     * @return a new couple
     */
    public Pair<L, R> withLeft(@Nullable L newLeft) {
        return pairOf(newLeft, this.right);
    }

    /**
     * Returns a new couple with this couple's left element and a new right element.
     *
     * @param newRight the new right element
     * @return a new couple
     */
    public Pair<L, R> withRight(@Nullable R newRight) {
        return pairOf(this.left, newRight);
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pair pair = (Pair) o;

        if (left != null ? !left.equals(pair.left) : pair.left != null) return false;
        if (right != null ? !right.equals(pair.right) : pair.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + " L='" + left + "', R='" + right + "'}";
    }
}
