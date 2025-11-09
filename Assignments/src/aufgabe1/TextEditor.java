package aufgabe1;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class TextEditor {

    private static Dictionary<String, String> dic;

    public static void main (String[] args) throws Exception {
        System.out.println("Welcome to Dictionary TUI");

        Scanner scanner = new Scanner(System.in);
        do {
            String input = scanner.nextLine();
            commands(input);
        } while(true);
    }

    private static void commands(String command) throws Exception {
        String args[] = command.split(" ");

        switch (args[0]) {
            case "create":
                create(args);
                break;
            case "r":
                if (dic == null)
                    System.out.println("Use 'create' to create your first Dictionary!");
                else
                    read(args.length > 1 ? Integer.parseInt(args[1]) : Integer.MAX_VALUE);
                break;
            case "p":
                if (dic == null)
                    System.out.println("Use 'create' to create your first Dictionary!");
                else
                    print();
                break;
            case "s":
                if (dic == null)
                    System.out.println("Use 'create' to create your first Dictionary!");
                else
                    search(args);
                break;
            case "i":
                if (dic == null)
                    System.out.println("Use 'create' to create your first Dictionary!");
                else
                    insert(args);
                break;
            case "d":
                if (dic == null)
                    System.out.println("Use 'create' to create your first Dictionary!");
                else
                    remove(args);
                break;
            case "exit":
                System.exit(0);
                break;
        }
    }

    private static void create(String[] args) {
        System.out.println("Creating new Dictionary");
        if (args.length < 2 || args[1].equals("SortedArrayDictionary"))
            dic = new SortedArrayDictionary<>();
       // else if (args[1].equals("HashDictionary"))
           // dic = new HashDictionary<>(3);
        //else if (args[1].equals("Binary"))
        //    dic = new BinaryTreeDictionary<>();
    }

    private static void print() {
        for (Dictionary.Entry<String, String> v : dic)
            System.out.println(v.getKey() + ": " + v.getValue());
    }

    private static void read(int n) throws IOException {
        int counter = 0;
        File selectedFile = null;
        String line;

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\HTWG_Module\\AlgoDat\\Assignments\\src\\aufgabe1\\dtengl.txt"));
        int rv = chooser.showOpenDialog(null);
        if (rv == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        } else {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            long start = System.nanoTime();
            while ((line = br.readLine()) != null && counter < n) {
                String[] words = line.split(" ");
                dic.insert(words[0], words[1]);
                counter++;
            }
            long stop = System.nanoTime();
            System.out.println("Read took " + ((stop - start) / 1000000) + "ms");
        }
    }

    private static void search(String[] args) {
        try {
            System.out.printf(dic.search(args[1]));
        } catch (NullPointerException e) {
            System.err.println("Wort wurde nicht gefunden!");
        }
    }

    private static void insert(String[] args) {
        System.out.printf("Adding %s: %s to the Dictionary\n", args[1], args[2]);
        dic.insert(args[1], args[2]);
    }

    private static void remove(String[] args) {
        System.out.printf("Removing %s from Dictionary\n", args[1]);
        dic.remove(args[1]);
    }
}