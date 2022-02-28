package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 10;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 3;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;
    int numElement;
    double resizingLoadFactorThreshold;
    int initialChainCount;
    int chainInitialCapacity;

    // You're encouraged to add extra fields (and helper methods) though!

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.resizingLoadFactorThreshold = resizingLoadFactorThreshold;
        this.initialChainCount = initialChainCount;
        this.chains = createArrayOfChains(initialChainCount);
        this.chainInitialCapacity = chainInitialCapacity;
        numElement = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            if (chains[0] != null) {
                return chains[0].get(key);
            }
        } else {
            if (chains[Math.abs(key.hashCode() % chains.length)] != null) {
                return chains[Math.abs(key.hashCode() % chains.length)].get(key);
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (!containsKey(key)) {
            if (numElement * 1.0 / chains.length >= resizingLoadFactorThreshold) {
                AbstractIterableMap<K, V>[] newChain = createArrayOfChains(chains.length * 2);
                for (int i = 0; i < chains.length; i += 1) {
                    if (chains[i] != null) {
                        Iterator<Map.Entry<K, V>> iter = chains[i].iterator();
                        while (iter.hasNext()) {
                            Map.Entry<K, V> element = iter.next();
                            K eleKey = element.getKey();
                            V eleValue = element.getValue();
                            int index = Math.abs(eleKey.hashCode() % newChain.length);
                            if (newChain[index] == null) {
                                newChain[index] = createChain(chainInitialCapacity);
                            }
                            newChain[index].put(eleKey, eleValue);
                        }
                    }
                }
                chains = newChain;
            }
            if (key == null) {
                if (chains[0] == null) {
                    chains[0] = createChain(chainInitialCapacity);
                }
                chains[0].put(key, value);
            } else {
                if (chains[Math.abs(key.hashCode() % chains.length)] == null) {
                    chains[Math.abs(key.hashCode() % chains.length)] = createChain(chainInitialCapacity);
                }
                chains[Math.abs(key.hashCode() % chains.length)].put(key, value);
            }
            numElement += 1;
            return null;
        } else {
            V oldValue = chains[Math.abs(key.hashCode() % chains.length)].get(key);
            chains[Math.abs(key.hashCode() % chains.length)].put(key, value);
            return oldValue;
        }
    }

    @Override
    public V remove(Object key) {
        if (key == null && chains[0] != null) {
            return chains[0].remove(key);
        }
        if (chains[Math.abs(key.hashCode() % chains.length)] != null) {
            numElement -= 1;
            return chains[Math.abs(key.hashCode() % chains.length)].remove(key);
        }
        return null;
    }

    @Override
    public void clear() {
        numElement = 0;
        chains = createArrayOfChains(chains.length);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            if (chains[0] != null) {
                return chains[0].containsKey(key);
            }
        } else {
            if (chains[Math.abs(key.hashCode() % chains.length)] != null) {
                return chains[Math.abs(key.hashCode() % chains.length)].containsKey(key);
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
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private Iterator<Map.Entry<K, V>> arrayIter;
        private int oldChainIndex;
        private int newChainIndex;
        private int arrayIndex;
        // You may add more fields and constructor parameters

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            oldChainIndex = -1;
            newChainIndex = 0;
            arrayIndex = 0;
        }

        @Override
        public boolean hasNext() {
            if (newChainIndex >= chains.length) {
                return false;
            }
            while (chains[newChainIndex] == null || !chains[newChainIndex].iterator().hasNext()) {
                if (newChainIndex + 1 >= chains.length) {
                    return false;
                }
                newChainIndex += 1;
            }
            if (arrayIndex < chains[newChainIndex].size()) {
                return true;
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            while (chains[newChainIndex] == null || !chains[newChainIndex].iterator().hasNext()) {
                if (newChainIndex + 1 >= chains.length) {
                    throw new NoSuchElementException();
                }
                newChainIndex += 1;
            }
            if (newChainIndex != oldChainIndex) {
                arrayIter = chains[newChainIndex].iterator();
                oldChainIndex = newChainIndex;
            }
            if (arrayIndex < chains[newChainIndex].size()) {
                arrayIndex += 1;
                if (arrayIndex >= chains[newChainIndex].size()) {
                    newChainIndex += 1;
                    arrayIndex = 0;
                }
                return arrayIter.next();
            }
            throw new NoSuchElementException();
        }
    }
}
