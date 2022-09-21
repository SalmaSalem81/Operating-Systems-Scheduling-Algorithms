package os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import logger.Logger;
import logger.entities.enums.ProcessState;
import logger.entities.enums.QueueType;
import mutex.Mutex;
import mutex.MutexFactory;
import utils.Interpreter;

public class Scheduler {

    int clockCycles = -1;
    /**
     * Mutex used for protecting I/O
     */
    Mutex inputMutex;
    Mutex outputMutex;
    Mutex fileMutex;
    HashMap<String, Mutex> mutexMap; // map of mutexes <mutex name, mutex>
    Queue<Process> readyQueue;
    Queue<Process> generalBlockedQueue = new java.util.LinkedList<>();
    ArrayList<Process> processes;
    private int quantumTime; // number of instructions executed in each slice

    public Scheduler(int quantumTime, Queue<Process> readyQueue, ArrayList<Process> processes) {
        this.quantumTime = quantumTime;
        mutexMap = new HashMap<>();
        MutexFactory mutexFactory = MutexFactory.getInstance(generalBlockedQueue, mutexMap);

        mutexFactory.createMutex("userInput");
        mutexFactory.createMutex("userOutput");
        mutexFactory.createMutex("file");

        this.readyQueue = readyQueue;
        this.processes = processes;
    }

    public void schedule(Process currentProcess) {
        /*
         * Round Robin Scheduling
         * give a process a quantum time to execute
         * - if the process is blocked, it will be added to the general blocked queue
         * - if the process is finished, it will be removed from the ready queue
         * - if the process is not finished, it will be added to the ready queue
         *
         */
        int counter = 0;
        while (counter < quantumTime) {
            logInstructionsInfo(currentProcess, clockCycles);

            Interpreter.interrupt(currentProcess, mutexMap, readyQueue);
            counter++;
            if (currentProcess.getStateInScheduler() != ProcessState.EXECUTING && counter < quantumTime) {
                /*
                 * if the process is finished nor blocked (i.e. it is not being executed
                 * anymore), and has still time slices to execute.
                 * ignore the rest of the time slices
                 */
                counter = quantumTime;
            }
            incrementClockCycle();
            // check if a process has arrived.
            processes.stream().filter(p -> p.isArriving(clockCycles)).forEach(p -> readyQueue.add(p));
            processes.removeIf(p -> p.isArriving(clockCycles));
        }

    }

    public void incrementClockCycle() {
        this.clockCycles++;
    }

    private void logInstructionsInfo(Process currentProcess, int timeSlice) {
        Logger.logln(Logger.PP);
        Logger.logln(Logger.ANSI_BOLD_BLACK + "Clock Cycle: " + timeSlice + Logger.ANSI_RESET);
        Logger.logln(new LinkedList<>(generalBlockedQueue), QueueType.BLOCKED);
        Logger.logln(new LinkedList<>(readyQueue), QueueType.READY);
        Logger.logln(currentProcess, ProcessState.CHOSEN);
        Logger.logln("Current Instruction: " + Arrays.toString(currentProcess.getCurrentInstruction()));
    }

    public int getClockCycles() {
        return clockCycles;
    }

    public Queue<Process> getGeneralBlockedQueue() {
        return this.generalBlockedQueue;
    }

}
