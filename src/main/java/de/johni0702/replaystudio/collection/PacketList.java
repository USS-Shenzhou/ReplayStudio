package de.johni0702.replaystudio.collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.johni0702.replaystudio.PacketData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A list for PacketData allowing efficient modification.
 * The difference to LinkedList is that it always ensures correct packet ordering unless specified otherwise.
 * This list is not thread-safe.
 */
public class PacketList implements List<PacketData> {

    /**
     * Number of elements in this list.
     */
    protected int size;

    /**
     * The first element in this list.
     */
    protected ListEntry first;

    /**
     * The last element in this list.
     */
    protected ListEntry last;

    /**
     * Creates a new empty list.
     */
    public PacketList() {

    }

    /**
     * Creates a new list containing the specified packet data.
     * @param from Iterable of packet data
     * @throws java.lang.IllegalArgumentException If the iterable contains packets in an invalid order.
     */
    public PacketList(Iterable<PacketData> from) {
        this(from, Predicates.alwaysTrue());
    }

    /**
     * Creates a new list containing the specified packet data.
     * @param from Iterable of packet data
     * @param filter Filter for whether to include a packet
     * @throws java.lang.IllegalArgumentException If the iterable contains packets in an invalid order.
     */
    public PacketList(@NonNull Iterable<PacketData> from, @NonNull Predicate<PacketData> filter) {
        long lastTime = 0;
        ListEntry last = null;
        for (PacketData data : from) {
            if (filter.apply(data)) {
                ListEntry entry = new ListEntry(data);
                if (last == null) {
                    first = entry;
                } else {
                    if (lastTime > data.getTime()) {
                        throw new IllegalArgumentException("The specified iterable contains elements in an invalid order.");
                    }
                    last.next = entry;
                }
                entry.previous = last;
                last = entry;
                size++;
            }
        }
        if (last != null) {
            this.last = last;
            last.next = null;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        ListEntry e = first;
        while (e != null) {
            if (e.data.equals(o)) {
                return true;
            }
            e = e.next;
        }
        return false;
    }

    @Override
    public PacketListIterator iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        ListEntry e = first;
        while (e != null) {
            array[i++] = e.data;
            e = e.next;
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }
        ListEntry e = first;
        for (int i = 0; i < a.length; i++) {
            if (e != null) {
                a[i] = (T) e.data;
                e = e.next;
            }
        }
        return a;
    }

    @Override
    public boolean add(PacketData packetData) {
        // Go beyond the last packet with the specified timestamp and insert the new packet there
        iterator().skipTo(packetData.getTime() + 1).add(packetData);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        PacketListIterator iter = iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends PacketData> c) {
        PacketListIterator iter = iterator();
        for (PacketData data : c) {
            iter.skipTo(data.getTime()).add(data);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends PacketData> c) {
        return addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        PacketListIterator iter = iterator();
        while (iter.hasNext()) {
            if (c.contains(iter.next())) {
                iter.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        PacketListIterator iter = iterator();
        while (iter.hasNext()) {
            if (!c.contains(iter.next())) {
                iter.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void sort(Comparator<? super PacketData> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        size = 0;
        first = null;
        last = null;
    }

    @Override
    public PacketData get(int index) {
        try {
            return listIterator(index).next();
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public PacketData set(int index, PacketData element) {
        PacketListIterator iter = listIterator(index);
        PacketData previous = iter.next();
        iter.set(element);
        return previous;
    }

    @Override
    public void add(int index, PacketData element) {
        listIterator(index).add(element);
    }

    @Override
    public PacketData remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        PacketListIterator iter = listIterator(index);
        PacketData previous = iter.next();
        iter.remove();
        return previous;
    }

    @Override
    public int indexOf(Object o) {
        PacketListIterator iter = iterator();
        int i = 0;
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        PacketListIterator iter = listIterator(size);
        int i = size;
        while (iter.hasPrevious()) {
            i--;
            if (iter.previous().equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public PacketListIterator listIterator() {
        return new PacketListIterator(this);
    }

    @Override
    public PacketListIterator listIterator(int index) {
        return new PacketListIterator(this, index);
    }

    @Override
    public List<PacketData> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "[" + StringUtils.join(this, ", ") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof List) {
            PacketListIterator iter = this.listIterator();
            ListIterator<?> other = ((List<?>) obj).listIterator();
            while (other.hasNext() && iter.hasNext()) {
                if (!Objects.equals(other.next(), iter.next())) {
                    return false;
                }
            }
            return !(other.hasNext() || iter.hasNext());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (PacketData data : this) {
            result = 31 * result + data.hashCode();
        }
        return result;
    }

    @RequiredArgsConstructor
    static final class ListEntry {
        final PacketData data;

        ListEntry next;
        ListEntry previous;
    }
}