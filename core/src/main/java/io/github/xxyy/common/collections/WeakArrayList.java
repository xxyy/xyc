/*
 * Copyright (c) 2013 - 2014 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without permission from the
 *  original author and may result in legal steps being taken.
 */

package io.github.xxyy.common.collections;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An alternative {@link java.util.ArrayList} implementation that only keeps weak references to its contents.
 * <b>Works exactly the same as a normal ArrayList!</b>
 * Note that this implementation does <b>NOT</b> support {@code null} elements!
 * Also note that the indexes of elements are subject to change, since garbage-collected elements are removed from the list, shifting the indexes.
 * Further note that listIterator() and subList() methods are <b>not</b> supported by this implementation.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 31/01/14
 */
public final class WeakArrayList<E> implements List<E> {

    private final ArrayList<WeakReference<E>> dataList;

    public WeakArrayList() {
        dataList = new ArrayList<>();
    }

    public WeakArrayList(final int initialCapacity) {
        dataList = new ArrayList<>(initialCapacity);
    }

    public WeakArrayList(final Collection<E> collectionToCopy) {
        dataList = new ArrayList<>();

        addAll(collectionToCopy);
    }

    @Override
    public int size() {
        return dataList.size();
    }

    @Override
    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    @Override
    public boolean contains(@NotNull final Object toCheck) {
        for (final WeakReference<E> aDataList : dataList) {
            if (toCheck.equals(aDataList.get())) {
                return true;
            }
        }

        return false;
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return new WeakListIterator<>(dataList.iterator());
    }

    @Override
    @NotNull
    public Object[] toArray() {
        return dataList.toArray();
    }

    @Override
    @NotNull
    public <T1> T1[] toArray(@NotNull final T1[] a) {
        //noinspection SuspiciousToArrayCall
        return dataList.toArray(a);
    }

    @Override
    public boolean add(@NotNull final E t) {
        return dataList.add(new WeakReference<>(t));
    }

    @Override
    public boolean remove(@NotNull final Object toDelete) {
        final Iterator<WeakReference<E>> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            if (toDelete.equals(iterator.next().get())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> toCheck) {
        for (final Object obj : toCheck) {
            if (!contains(obj)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends E> toAdd) {
        //Two iterations, but it's more important not to get into an illegal state
        Validate.noNullElements(toAdd, "This implementation does not support null elements!");

        //noinspection Convert2streamapi
        for (final E newElement : toAdd) {
            add(newElement);
        }

        return true;
    }

    @Override
    public boolean addAll(final int index, @NotNull final Collection<? extends E> c) {
        throw new UnsupportedOperationException(); //I don't see an need for this. Anyone have some spare time?
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> toRemove) {
        boolean changed = false;
        for (final Object obj : toRemove) {
            if (changed) {
                remove(obj); //If it's already true, ignore subsequent calls
            } else {
                changed = remove(obj); //It is already false, nothing can break
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> toRetain) {
        final Iterator<WeakReference<E>> iterator = dataList.iterator();
        boolean changed = false;

        while (iterator.hasNext()) {
            if (!toRetain.contains(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        dataList.clear();
    }

    @Override
    public E get(final int index) {
        return dataList.get(index).get();
    }

    @Override
    public E set(final int index, final E element) {
        final E prevOccupier = dataList.get(index).get();
        dataList.set(index, new WeakReference<>(element));
        return prevOccupier;
    }

    @Override
    public void add(final int index, final E element) {
        dataList.add(index, new WeakReference<>(element));
    }

    @Override
    public E remove(final int index) {
        return dataList.remove(index).get();
    }

    @Override
    public int indexOf(@NotNull final Object toCheck) {
        for (int i = 0; i < size(); i++) {
            if (toCheck.equals(get(i))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(final Object toCheck) {
        for (int i = size(); i >= 0; i--) { //Iterate backwards for #efficiency
            if (toCheck.equals(get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @NotNull
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Override
    @NotNull
    public ListIterator<E> listIterator(final int index) {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Override
    @NotNull
    public List<E> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException(); //TODO implement
    }

}