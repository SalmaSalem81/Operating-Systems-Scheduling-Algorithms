package entities;

import logger.Logger;
import logger.entities.enums.QueueType;
import logger.services.GenerateTableService;
import memory.MMU;
import memory.Memory;

import java.util.*;

public class Scheduler {

    int clockCycles = -1;
    Queue<Process> readyQueue;
    ArrayList<Process> processes;
    private int quantumTime; // number of instructions executed in each slice
    private MMU mmu;

    public Scheduler(int quantumTime, Queue<Process> readyQueue, ArrayList<Process> processes, MMU mmu) {
        this.quantumTime = quantumTime;

        this.readyQueue = readyQueue;
        this.processes = processes;
        this.mmu = mmu;
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
        int processSlot = mmu.getMemory().getProcessSlot(currentProcess.getPcb().getId());
        currentProcess.getPcb().setState(ProcessState.RUNNING);
        mmu.getMemory().set(20 * processSlot + 1, currentProcess.getPcb().getState());
        int counter = 0;
        while (counter < quantumTime) {
            logCycleInfo(currentProcess, clockCycles);

            counter++;
            currentProcess.getPcb().setProgramCounter(currentProcess.getPcb().getProgramCounter() + 1);
            if (currentProcess.getStateInScheduler() != ProcessState.RUNNING && counter < quantumTime) {
                /*
                 * if the process is finished nor blocked (i.e. it is not being executed
                 * anymore), and has still time slices to execute.
                 * ignore the rest of the time slices
                 */
                counter = quantumTime;
            }

            incrementClockCycle();
            // check if a process has arrived.
            processes.stream().filter(p -> p.isArriving(clockCycles)).forEach(p -> {
                p.getPcb().setState(ProcessState.NEW);
                readyQueue.add(p);
                // if (mmu.getMemory().hasEmptySlots()) mmu.addProcess(p);
                mmu.addProcess(p);
            });
            processes.removeIf(p -> p.isArriving(clockCycles));
        }

        currentProcess.getPcb().setState(ProcessState.PREEMPTED);
    }

    public void incrementClockCycle() {
        this.clockCycles++;
    }

    private void logCycleInfo(Process currentProcess, int timeSlice) {
        Logger.logln(Logger.PP);
        Logger.logln(Logger.ANSI_BLACK_BOLD + "Clock Cycle: " + timeSlice + Logger.ANSI_RESET);
        Logger.logln(new LinkedList<>(readyQueue), QueueType.READY);
        Logger.logln(currentProcess, ProcessState.RUNNING);
        Logger.logln(Logger.ANSI_GREEN_BOLD + "> " + String.join(" ", currentProcess.getInstructions().get(currentProcess.getPcb().getProgramCounter())) + Logger.ANSI_RESET);

        Logger.logln('\n' + Logger.ANSI_BLACK_BOLD + "================ MEMORY ===============" + Logger.ANSI_RESET);
        Logger.logln(getMemoryTable());
    }

    private String getMemoryTable() {
        Memory memory = mmu.getMemory();

        List<String> headers = Arrays.asList("Address", "Content");

        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < Memory.MEMORY_SIZE; i++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i));
            try {
                if (memory.get(i) instanceof String[]) {
                    row.add(String.join(" ", (String[]) memory.get(i)));
                } else if (memory.get(i) == null) {
                    row.add("-");
                } else {
                    row.add(String.valueOf(memory.get(i)));
                }
            } catch (Exception e) {
                row.add("-");
            }
            rows.add(row);
        }

        return GenerateTableService.execute(headers, rows);
    }

    public int getClockCycles() {
        return clockCycles;
    }

}
