package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import logger.Logger;

public class Parser {
    private static final Set<String> operators = Set.of(
            "print",
            "writeFile",
            "assign",
            "printFromTo",
            "semWait",
            "semSignal");

    private Parser() {
        throw new IllegalStateException("Utility class");
    }

    public static ArrayList<String[]> parse(String path) {
        ArrayList<String[]> args = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] words = line.split(" ");
                // check if the first word is an operator
                if (!operators.contains(words[0])) {
                    throw new IllegalArgumentException("Invalid operator: " + words[0]);
                }

                // check if the instruction is nested
                if (words.length == 4) {
                    assignRead(words, args);
                } else if (words.length > 2 && words[2].equals("input")) {
                    assignInput(words, args);
                } else {
                    args.add(words);
                }
            }
        } catch (IOException e) {
            Logger.logln(logger.Logger.ANSI_RED + "[ERROR] IOException: " + e.getMessage() + logger.Logger.ANSI_RESET);
        }
        return args;
    }

    private static void assignInput(String[] words, ArrayList<String[]> args) {
        /*
         * changes the instruction from:
         * >>> assign a input
         * to:
         * >>> input
         * >>> assign a I
         */
        if (!words[2].equals("readFile") && !words[0].equals("assign")) {
            throw new IllegalArgumentException("Invalid instruction: " + words[2]);
        }
        String[] first = new String[1];
        String[] second = new String[3];

        second[0] = words[0];
        second[1] = words[1];
        second[2] = "I";

        first[0] = words[2];
        args.add(first);
        args.add(second);
    }

    private static void assignRead(String[] words, ArrayList<String[]> args) {
        /*
         * changes the instruction from:
         * >>> assign a readFile b
         * to:
         * >>> readFile b
         * >>> assign a R
         */
        String[] first = new String[2];
        String[] second = new String[3];

        second[0] = words[0];
        second[1] = words[1];
        second[2] = "R";

        first[0] = words[2];
        first[1] = words[3];

        args.add(first);
        args.add(second);
    }

}
