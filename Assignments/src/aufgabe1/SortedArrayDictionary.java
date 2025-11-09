package aufgabe1;

import java.util.Iterator;

// SortedArrayDictionary: Implements a dictionary using a sorted array
// K must be comparable so that elements remain sorted
public class SortedArrayDictionary<K extends Comparable<? super K>, V> implements Dictionary<K, V> {

    private Entry<K, V>[] dic;  // Array to store key-value pairs
    private int size;  // Current number of elements
    private static final int DEF_CAPACITY = 32;  // Default array size

    // Constructor: Initializes an empty dictionary with default capacity
    public SortedArrayDictionary() {
        this.dic = new Entry[DEF_CAPACITY];
        this.size = 0;
    }

    // Searches for the index of a given key in the dictionary
    // Returns the index if found, otherwise returns -1
    private int searchKey(K key) { // improved key search, bevore linear, now binary search
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = key.compareTo(dic[mid].getKey());

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid;  // Key found, return its index
            }
        }
        return -1;  // Key not found
    }

    // Expands the array size if needed to accommodate more elements
    private void ensureCapacity(int newCapacity) {
        if (newCapacity < size)
            return;  // Ensure new capacity is greater than current size
        Entry[] old = dic;
        dic = new Entry[newCapacity];  // Create a new larger array
        System.arraycopy(old, 0, dic, 0, size);  // Copy existing elements
    }

    // Inserts a key-value pair into the dictionary while maintaining sorted order
    @Override
    public V insert(K key, V value) {
        int i = searchKey(key);  // Check if the key already exists

        if (i >= 0) {  // If key exists, update the value
            V old = dic[i].getValue();
            dic[i].setValue(value);
            return old;
        }

        // If array is full, increase its size
        if (dic.length == size) {
            ensureCapacity(2 * size);  // Double the array size
        }

        // Find correct position to insert the new key while maintaining sorted order
        int j = size - 1;
        while (j >= 0 && key.compareTo(dic[j].getKey()) < 0) {
            dic[j + 1] = dic[j];  // Shift elements to the right
            j--;
        }

        dic[j + 1] = new Entry<K, V>(key, value);  // Insert the new entry
        size++;
        return null;  // New entry added, return null
    }

    // Searches for a key and returns the associated value
    @Override
    public V search(K key) {
        int i = searchKey(key);  // Find the key index
        if (i >= 0) //
            return dic[i].getValue();  // Return value if key is found
        else
            return null;  // Return null if key does not exist
    }

    // Removes a key from the dictionary
    @Override
    public V remove(K key) {
        int i = searchKey(key);  // Find key index
        if (i == -1)
            return null;  // Key not found, return null

        V r = dic[i].getValue();  // Store value to return
        for (int j = i; j < size - 1; j++)
            dic[j] = dic[j + 1];  // Shift elements left to remove the key
        dic[--size] = null;  // Clear the last entry to prevent memory leaks
        return r;  // Return the removed value
    }

    // Returns the number of elements in the dictionary
    @Override
    public int size() {
        return this.size;
    }

    // Iterator implementation for traversing the dictionary
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<Entry<K, V>>() {

            int index = 0;  // Keeps track of the current position

            @Override
            public boolean hasNext() {
                return index < size;  // True if there are more elements
            }

            @Override
            public Entry next() {
                Entry<K, V> returnEntry = dic[index++];
                return returnEntry;  // Return current element and move to next
            }
        };
    }
}