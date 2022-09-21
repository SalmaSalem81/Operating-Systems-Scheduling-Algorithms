package entities;

import java.util.ArrayList;
import java.util.Queue;

import memory.MMU;

public class Kernel {
    public static final String DIR_PATH = "src/programs/";
    ArrayList<Process> arrivingProcesses;
    Scheduler scheduler;
    Queue<Process> readyQueue;
    MMU mmu;

    public Kernel(ArrayList<Process> arrivingProcesses, Scheduler scheduler, Queue<Process> readyQueue, MMU mmu) {
        this.arrivingProcesses = arrivingProcesses;
        this.scheduler = scheduler;
        this.readyQueue = readyQueue;
        this.mmu = mmu;
    }

    public void run() {
        while (!arrivingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            /*
             * if there are processes arriving and there is no process in the ready queue,
             * wait for the next process to arrive
             * else, execute the next process in the ready queue
             */

            // Logger.logln("Waiting for arriving processes...");
            scheduler.incrementClockCycle();

            arrivingProcesses.stream().filter(p -> p.isArriving(scheduler.getClockCycles()))
                    .forEach(
                            p -> {
                                p.getPcb().setState(ProcessState.NEW);
                                readyQueue.add(p);
                                // if (mmu.getMemory().hasEmptySlots())
                                    mmu.addProcess(p);
                            });

            arrivingProcesses.removeIf(p -> p.isArriving(scheduler.getClockCycles()));

            while (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();
                // currentProcess.getPcb().setState(ProcessState.NEW);
                if (!mmu.isProcessInMemory(currentProcess.getPcb().getId())) {
                    mmu.addProcess(currentProcess);
                }
                scheduler.schedule(currentProcess);

                ProcessState state = currentProcess.getState();
                // Logger.logln(currentProcess, state);

                if (state == ProcessState.PREEMPTED) {
                    // return the process to the tail of the ready queue
                    readyQueue.add(currentProcess);
                    int processSlot = mmu.getMemory().getProcessSlot(currentProcess.getPcb().getId());
                    currentProcess.getPcb().setState(ProcessState.PREEMPTED);
                    mmu.getMemory().set(20 * processSlot + 1, currentProcess.getPcb().getState());
                } else {
                    mmu.removeProcess(currentProcess.getPcb().getId());
                }
            }
            if (arrivingProcesses.isEmpty() && readyQueue.isEmpty()) {
                /*
                 * if there are no more processes arriving and no more processes in the ready
                 * queue,
                 * the simulation is finished
                 */
                return;
            }
        }
    }

}
