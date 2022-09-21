package utils;

import java.util.HashMap;
import java.util.Queue;

import logger.Logger;
import mutex.Mutex;
import os.Process;

public class Interpreter {
    private Interpreter() {
        throw new IllegalStateException("Utility class");
    }

    public static void interrupt(Process currentProcess, HashMap<String, Mutex> mutexMap, Queue<Process> readyQueue) {
        /*
         * execute instruction
         * format: operator [operand1] [operand2]
         */

        String[] instruction = currentProcess.pollInstruction();
        String operator = instruction[0];
        switch (operator) {
            case "input":
                input(currentProcess);
                break;

            case "semWait":
                semWait(currentProcess, mutexMap, instruction);
                break;

            case "semSignal":
                semSignal(currentProcess, mutexMap, readyQueue, instruction);
                break;

            case "print":
                print(currentProcess, instruction);
                break;

            case "printFromTo":
                printFromTo(currentProcess, instruction);
                break;

            case "assign":
                assign(currentProcess, instruction);
                break;

            case "writeFile":
                writeFile(currentProcess, instruction);
                break;

            case "readFile":
                // readFile a
                readFile(currentProcess, instruction);
                break;

            default:
                throw new IllegalArgumentException("Invalid operator"); // change to custom exception
        }
    }

    private static void readFile(Process currentProcess, String[] instruction) {
        String fileNameRead = instruction[1];
        String readResult = SystemCalls.readFile(fileNameRead);
        currentProcess.setVariable("R", readResult);
        Logger.logln("Read " + readResult + " from " + fileNameRead);
        Logger.logln(currentProcess.getMemoryMap().get("R").toString());
    }

    private static void writeFile(Process currentProcess, String[] instruction) {
        String fileNameWrite = instruction[1];
        String data = instruction[2];
        if (fileNameWrite == null || data == null) {
            Logger.logln(Logger.ANSI_RED + "Error: file name or data is null" + Logger.ANSI_RESET);
            System.exit(1);
        }

        SystemCalls.writeFile(currentProcess, fileNameWrite, data);
        Logger.logln("Writing to file: " + fileNameWrite + " with data: " + data);
    }

    private static void assign(Process currentProcess, String[] instruction) {
        /**
         * assign a to b
         */
        String variable = instruction[1];
        String value = instruction[2];
        if (value == null || variable == null) {
            Logger.logln(Logger.ANSI_RED + "Error: " + instruction[1] + " or " + instruction[2]
                    + " is not a valid variable" + Logger.ANSI_RESET);
            System.exit(1);
        }
        Logger.logln(
                "Assigning " + currentProcess.getMemoryMap().getOrDefault(value, value) + " to " + variable);
        SystemCalls.assign(currentProcess, variable, value);
    }

    private static void printFromTo(Process currentProcess, String[] instruction) {
        String start = instruction[1];
        String end = instruction[2];
        if (start == null || end == null) {
            Logger.logln(
                    Logger.ANSI_RED + "Error: " + instruction[1] + " or " + instruction[2]
                            + " is not a valid variable"
                            + Logger.ANSI_RESET);
            System.exit(1);
        }
        SystemCalls.printFromTo(currentProcess, start, end);
    }

    private static void print(Process currentProcess, String[] instruction) {
        String message = instruction[1];
        if (message == null) {
            Logger.logln(Logger.ANSI_RED + "Error: message is null" + Logger.ANSI_RESET);
            System.exit(1);
        }

        SystemCalls.print(currentProcess, message);
    }

    private static void semSignal(Process currentProcess, HashMap<String, Mutex> mutexMap, Queue<Process> readyQueue,
            String[] instruction) {
        /*
         * check the mutex type
         * if mutex type is output, signal on output mutex
         */
        Mutex postMutex = mutexMap.get(instruction[1]);
        if (postMutex == null) {
            Logger.logln(
                    Logger.ANSI_RED + "Error: " + instruction[1] + " is not a valid mutex" + Logger.ANSI_RESET);
            System.exit(1);
        }
        Logger.logln("Signaling on " + instruction[1]);
        Process nextProcess = SystemCalls.semPost(currentProcess, postMutex);

        if (nextProcess != null) {
            readyQueue.add(nextProcess);
        }
    }

    private static void semWait(Process currentProcess, HashMap<String, Mutex> mutexMap, String[] instruction) {
        /*
         * check the mutex type
         * if mutex type is input, wait on input mutex
         */
        Mutex waitMutex = mutexMap.get(instruction[1]);
        if (waitMutex == null) {
            Logger.logln(
                    Logger.ANSI_RED + "Error: " + instruction[1] + " is not a valid mutex" + Logger.ANSI_RESET);
            System.exit(1);
        }
        Logger.logln("Waiting on " + instruction[1]);
        SystemCalls.semWait(currentProcess, waitMutex);
    }

    private static void input(Process currentProcess) {
        /*
         * input instruction, take input from user
         * get variable name form next instruction
         */
        String variableName = currentProcess.getCurrentInstruction()[1];
        SystemCalls.input(currentProcess, variableName);
    }

}
