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

package li.l1t.common.util.config;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * Provides type-safe accessor methods for configurations stored as maps from string to object. This is especially
 * useful with Bukkit's configuration serialisation.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-11
 */
public interface MapConfig {
    /**
     * Adds a type error handler to this configuration. The error handler gets notified of all type errors that lead to
     * methods of this configuration not returning a value. Normally, an error handler would log a message when it is
     * notified of an error, so that users can find out why the configuration is not parsed the way they expected. The
     * first argument to the error handler is a short message describing the error, and the second argument is an array
     * of extra data that may be helpful for fixing the issue.
     *
     * @param typeErrorHandler the new error handler
     */
    void addTypeErrorHandler(BiConsumer<String, Object[]> typeErrorHandler);

    /**
     * @return the mutable map containing the raw data contained in this configuration
     */
    Map<String, Object> raw();

    /**
     * @param key the key to operate on
     * @return whether this configuration contains a mapping for given key
     */
    boolean containsKey(String key);

    /**
     * @param key the key to find the object value for
     * @return the value for that key, or null if none
     */
    Object get(String key);

    /**
     * @param key the key to find the object value for
     * @return an optional containing the value for that key, or an empty optional
     */
    Optional<Object> find(String key);

    /**
     * Finds the value for a key if it is an instance of given type. Note that an empty optional returned by this method
     * may either mean that this configuration does not contain a mapping for given key or that the value for that
     * mapping is not an instance of type. To find out which one it is, use {@link #containsKey(String)}.
     *
     * @param key  the key to operate on
     * @param type the expected type of the value
     * @param <T>  the type of the value
     * @return an optional containing the value associated with given key of given type, or an empty optional
     */
    <T> Optional<T> findTyped(String key, Class<T> type);

    /**
     * @param key the key to find the string value for
     * @return an optional containing te value for given key if it is a string, or an empty optional
     */
    Optional<String> findString(String key);

    /**
     * @param key the key to find the string value of
     * @return an optional containing the result of applying {@link String#valueOf(Object)} to the value of given key,
     * or an empty optional if there is no mapping for given key
     */
    Optional<String> stringify(String key);

    /**
     * Gets the collection value associated with given key and filters it to only contain given value type, then
     * collects it to given collection type using given collector. Calls the type error handler for every value of the
     * collection that is not of given type. If there is no value associated with given key, or that value is not a
     * collection, this returns an empty collection.
     *
     * @param key       the key to operate on
     * @param valueType the value type to be contained by the collection
     * @param collector the collector that is used to create a collection of the target type
     * @param <R>       the type of collection to be returned
     * @param <V>       the type of value to be contained by the collection
     * @return a collection of requested type with requested values created from the collection present at given key, or
     * an empty collection
     */
    <V, R extends Collection<V>> R getCollection(
            String key, Class<V> valueType, Collector<V, ?, R> collector
    );

    /**
     * Gets the map at given key, or an empty map if there is no value associated with given key. Any
     * values that are not instances of the value type and any keys that are not instances of the key type are ignored.
     *
     * @param key       the key to operate on
     * @param valueType the expected value type
     * @param keyType   the expected key type
     * @param <V>       the value type
     * @param <K>       the key type
     * @return the K-V map at given key, or an empty map
     */
    <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType);
}
