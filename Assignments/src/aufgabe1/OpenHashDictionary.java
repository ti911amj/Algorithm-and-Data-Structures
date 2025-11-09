package aufgabe1;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class OpenHashDictionary<K, V> implements Dictionary<K, V> {

    // The state of a table entry: FREE = empty, FULL = contains a value, DELETED = was used but now removed
    private enum State { FREE, FULL, DELETED }

    // Internal class representing a single entry in the hash table
    private static class Entry<K, V> {
        K key;
        V value;
        State state;

        // Constructor for an empty slot
        Entry() {
            this.state = State.FREE;
        }

        // Constructor for a full slot
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.state = State.FULL;
        }
    }

    // Constants for load factor and default table size
    private static final double LOAD_FACTOR = 0.66;
    private static final int DEFAULT_CAPACITY = 7; // 7 = 4*1 + 3 → required prime form

    private Entry<K, V>[] data; // Hash table
    private int size; // Number of FULL entries in the table

    // Constructor: initializes the table with default capacity
    public OpenHashDictionary() {
        data = createTable(DEFAULT_CAPACITY);
        size = 0;
    }

    // Creates a table with the given capacity, initializing each slot as FREE
    @SuppressWarnings("unchecked")
    private Entry<K, V>[] createTable(int capacity) {
        Entry<K, V>[] table = new Entry[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new Entry<>();
        }
        return table;
    }

    // Hash function using positive modulo
    private int hash(Object key) {
        return Math.floorMod(key.hashCode(), data.length);
    }

    // Resize the table when load factor is exceeded
    private void resize() {
        int newCapacity = data.length * 2;
        // Find next valid prime of the form 4i + 3
        while (!isValidPrime(newCapacity)) {
            newCapacity++;
        }

        Entry<K, V>[] oldData = data;
        data = createTable(newCapacity);
        size = 0;

        // Re-insert all active entries into the new table
        for (Entry<K, V> entry : oldData) {
            if (entry.state == State.FULL) {
                insert(entry.key, entry.value);
            }
        }
    }

    // Checks if a number is a prime of the form 4i + 3
    private boolean isValidPrime(int n) {
        if (n % 4 != 3) return false;
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    // Inserts a new key-value pair or updates existing key
    @Override
    public V insert(K key, V value) {
        // Check if we need to resize based on current load
        if ((double) size / data.length > LOAD_FACTOR) {
            resize();
        }

        int hash = hash(key);
        int i = 0;
        int index;
        int firstDeleted = -1;

        // Quadratic probing with alternating signs: +1, -1, +4, -4, +9, -9, ...
        while (true) {
            index = Math.floorMod(hash + (int) Math.pow(-1, i) * i * i, data.length);
            Entry<K, V> entry = data[index];

            // Found a free spot
            if (entry.state == State.FREE) {
                if (firstDeleted != -1) index = firstDeleted; // reuse first deleted if found
                data[index] = new Entry<>(key, value);
                size++;
                return null;
            }

            // Remember the first deleted entry (prefer reusing over inserting new)
            if (entry.state == State.DELETED && firstDeleted == -1) {
                firstDeleted = index;
            }

            // Key already exists → update value
            if (entry.state == State.FULL && entry.key.equals(key)) {
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }

            i++;
        }
    }

    // Searches for a key and returns its associated value
    @Override
    public V search(K key) {
        int hash = hash(key);
        int i = 0;

        while (true) {
            int index = Math.floorMod(hash + (int) Math.pow(-1, i) * i * i, data.length);
            Entry<K, V> entry = data[index];

            if (entry.state == State.FREE) return null; // Stop if we hit an empty slot
            if (entry.state == State.FULL && entry.key.equals(key)) return entry.value;

            i++;
        }
    }

    // Removes an entry by marking it as DELETED
    @Override
    public V remove(K key) {
        int hash = hash(key);
        int i = 0;

        while (true) {
            int index = Math.floorMod(hash + (int) Math.pow(-1, i) * i * i, data.length);
            Entry<K, V> entry = data[index];

            if (entry.state == State.FREE) return null; // Key not found
            if (entry.state == State.FULL && entry.key.equals(key)) {
                entry.state = State.DELETED; // Mark as deleted
                size--;
                return entry.value;
            }

            i++;
        }
    }

    // Returns the number of stored elements
    @Override
    public int size() {
        return size;
    }

    // Returns an iterator over all FULL entries
    @Override
    public Iterator<Dictionary.Entry<K, V>> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                while (index < data.length && data[index].state != State.FULL) {
                    index++;
                }
                return index < data.length;
            }

            @Override
            public Dictionary.Entry<K, V> next() {
                if (!hasNext()) throw new NoSuchElementException();
                Entry<K, V> entry = data[index++];
                return new Dictionary.Entry<>(entry.key, entry.value);
            }
        };
    }
}
