package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    int numElement;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        numElement = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < entries.length; i += 1) {
            if (key == null) {
                if (entries[i] != null && entries[i].getKey() == null) {
                    return entries[i].getValue();
                }
            } else {
                if (entries[i] != null && entries[i].getKey() != null) {
                    if (entries[i].getKey().equals(key)) {
                        return entries[i].getValue();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (!containsKey(key)) {
            if (entries.length == numElement) {
                SimpleEntry<K, V>[] newEntry = createArrayOfEntries(entries.length * 2);
                for (int i = 0; i < entries.length; i += 1) {
                    newEntry[i] = entries[i];
                }
                entries = newEntry;
            }
            entries[numElement] = new SimpleEntry<K, V>(key, value);
            numElement += 1;
            return null;
        } else {
            V oldValue = null;
            for (int i = 0; i < entries.length; i += 1) {
                if (entries[i] != null && entries[i].getKey().equals(key)) {
                    oldValue = entries[i].getValue();
                    entries[i].setValue(value);
                }
            }
            return oldValue;
        }
    }

    @Override
    public V remove(Object key) {
        for (int i = 0; i < entries.length; i += 1) {
            if (key == null) {
                if (entries[i] != null && entries[i].getKey() == null) {
                    V oldValue = entries[i].getValue();
                    entries[i] = entries[numElement - 1];
                    entries[numElement - 1] = null;
                    numElement -= 1;
                    return oldValue;
                }
            } else {
                if (entries[i] != null && entries[i].getKey() != null) {
                    if (entries[i].getKey().equals(key)) {
                        V oldValue = entries[i].getValue();
                        entries[i] = entries[numElement - 1];
                        entries[numElement - 1] = null;
                        numElement -= 1;
                        return oldValue;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void clear() {
        numElement = 0;
        for (int i = 0; i < entries.length; i += 1) {
            entries[i] = null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < entries.length; i += 1) {
            if (key == null) {
                if (entries[i] != null && entries[i].getKey() == null) {
                    return true;
                }
            } else {
                if (entries[i] != null && entries[i].getKey() != null) {
                    if (entries[i].getKey().equals(key)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return numElement;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int index;
        // You may add more fields and constructor parameters

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            index = -1;
            this.entries = entries;
        }

        @Override
        public boolean hasNext() {
            if (index + 1 >= entries.length) {
                return false;
            }
            return (entries[index + 1] != null);
        }

        @Override
        public Map.Entry<K, V> next() {
            index += 1;
            if (index >= entries.length || entries[index] == null) {
                throw new NoSuchElementException();
            }
            return entries[index];
        }
    }
}
