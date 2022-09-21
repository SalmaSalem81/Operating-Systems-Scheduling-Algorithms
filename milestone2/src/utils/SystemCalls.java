package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import logger.Logger;
import mutex.Mutex;
import os.Process;

public class SystemCalls {
    private SystemCalls() {
        throw new IllegalStateException("Utility class");
    }

    public static void printFromTo(Process process, String arg1, String arg2) {
        int loopStart;
        int loopEnd;
        try {
            loopStart = Integer.parseInt((String) process.getMemoryMap().get(arg1));
        } catch (NumberFormatException e) {
            Logger.logln(Logger.ANSI_RED + "[Error] Invalid loop start value" + Logger.ANSI_RESET);
            System.exit(1);
            return;
        }
        try {
            loopEnd = Integer.parseInt((String) process.getMemoryMap().get(arg2));
        } catch (NumberFormatException e) {
            Logger.logln(Logger.ANSI_RED + "[Error] Invalid loop end value" + Logger.ANSI_RESET);
            System.exit(1);
            return;
        }
        for (int i = loopStart; i <= loopEnd; i++) {
            Logger.log(i + " ");
        }
        Logger.logln("");
    }

    public static void print(Process process, String arg1) {
        String printMessage = (String) process.getMemoryMap().get(arg1);
        if (printMessage == null) {
            Logger.log(Logger.ANSI_RED + "[Error] Invalid variable name" + arg1 + Logger.ANSI_RESET);
            System.exit(1);
            return;
        }
        Logger.logln(printMessage);
    }

    public static void assign(Process process, String arg1, Object arg2) {
        /**
         * Assigns the value of arg2 to the variable arg1 in the process's memory map.
         *
         * >>> assign a b
         *
         * if b = 3:
         * a = 3
         * else:
         * a = 'b'
         */

        process.getMemoryMap().put(arg1, process.getMemoryMap().getOrDefault(arg2, arg2));
    }

    public static void input(Process process, String arg1) {
        /**
         * Reads input from the user and assigns it to the variable I in the process's
         * memory map
         */
        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
        String input;
        try {
            Logger.log("\nEnter value " + arg1 + ": ");
            input = reader.readLine();
            Logger.logln("input : " + input);
            process.setVariable("I", input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(Process process, String fileName, String data) {
        String value = (String) process.getMemoryMap().get(data);
        // create a file with the name $(fileName).
        // write the $(value) to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        /*
         * Reads the contents of the file with the name $(fileName) and returns it as a
         * string.
         */
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            Logger.logln(Logger.ANSI_RED + "[ERROR] reading file " + fileName + Logger.ANSI_RESET);
        }
        return sb.toString();
    }

    public static void semWait(Process process, Mutex mutex) {
        mutex.wait(process);
    }

    public static Process semPost(Process process, Mutex mutex) {
        return mutex.post(process);
    }

}
