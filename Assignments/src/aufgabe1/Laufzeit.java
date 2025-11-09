package aufgabe1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Laufzeit {

    // File object representing the dictionary file
    static File file = null;

    public static void main(String[] args) throws FileNotFoundException {

        // Specify the file path containing dictionary words
        file = new File("src/aufgabe1/dtengl.txt");

        // Test the SortedArrayDictionary implementation
        System.out.println("Test SortedArrayDictionary:");
        Dictionary<String, String> sortedArrayDictionary = new SortedArrayDictionary<>();
        testDict(sortedArrayDictionary);

        // Uncomment the following sections to test different dictionary implementations

        // Test the HashDictionary implementation
         System.out.println("Test LinkedHashDictionary:");
         Dictionary<String, String> linkedhashDictionary = new LinkedHashDictionary<>(11);
         testDict(linkedhashDictionary);

        // Test the OpenDictionary implementation
        System.out.println("Test OpenHashDictionary:");
        Dictionary<String, String> openDictionary = new OpenHashDictionary<>();
        testDict(openDictionary);


        // Test the BinaryTreeDictionary implementation
        System.out.println("Test BinaryTreeDictionary:");
        Dictionary<String, String> binaryTreeDictionary = new BinaryTreeDictionary<>();
        testDict(binaryTreeDictionary);
    }

    /**
     * Tests dictionary performance by:
     * 1. Inserting words from the file.
     * 2. Measuring time taken for insertions.
     * 3. Searching for German words.
     * 4. Searching for English words.
     */
    private static void testDict(Dictionary<String, String> dict) throws FileNotFoundException {
        Scanner scanner = new Scanner(file); // Read file data

        // Insertion Performance Test
        System.out.println("Insert:");
        long startTime = System.currentTimeMillis();
        int i = 0;

        // Read each line, split into German and English words, then insert into the dictionary
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            dict.insert(parts[0], parts[1]); // Insert word pair (German -> English)
            i++;

            // Print time taken after inserting 8000 entries
            if (i == 8000) {
                long time8000 = System.currentTimeMillis() - startTime;
                System.out.println("  8000: " + time8000 + " ms");
            }
        }
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("~16000: " + endTime + " ms"); // Time for inserting all words

        // Search Performance Test (German Words)
        List<String> germanWords = new ArrayList<>();
        scanner = new Scanner(file);

        // Collect all German words from the file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            germanWords.add(parts[0]);
        }

        // Measure search time for German words
        System.out.println("Search german:");
        startTime = System.currentTimeMillis();
        i = 0;
        for (String word : germanWords) {
            dict.search(word); // Search for each German word
            i++;

            // Print time taken after searching 8000 words
            if (i == 8000) {
                long time8000 = System.currentTimeMillis() - startTime;
                System.out.println("  8000: " + time8000 + " ms");
            }
        }
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("~16000: " + endTime + " ms"); // Time for searching all words

        // Search Performance Test (English Words)
        List<String> englishWords = new ArrayList<>();
        scanner = new Scanner(file);

        // Collect all English words from the file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            englishWords.add(parts[1]);
        }

        // Measure search time for English words
        System.out.println("Search english:");
        startTime = System.currentTimeMillis();
        i = 0;
        for (String word : englishWords) {
            dict.search(word); // Search for each English word
            i++;

            // Print time taken after searching 8000 words
            if (i == 8000) {
                long time8000 = System.currentTimeMillis() - startTime;
                System.out.println("  8000: " + time8000 + " ms");
            }
        }
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("~16000: " + endTime + " ms"); // Time for searching all words
        }
}
