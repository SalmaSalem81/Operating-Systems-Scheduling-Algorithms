package os;

/*
 *   OS class is used to simulate the operating system.

 *   __authors__ = [
 *       "Ibrahim",
 *       "Muhammed",
 *       "Salma",
 *       "Farah",
         "Shehab"
 *   ]
 *   __version__ = "1.0"
 *   __date__ = "2022-5-12"
 *   __copyright__ = "Copyright (c) 2022"
 *   __license__ = "MIT"
 *   __maintainer__ = "Ibrahim"
 *   __email__ = "ibrahimabouelenein@student.guc.edu.eg"
 *   __status__ = "Development"
 *   __last_modified__ = "2022-5-12"
 *   __description__ = "This is the main class of the operating system."
 */

import java.util.ArrayList;
import java.util.Queue;

import logger.Logger;
import logger.entities.enums.ProcessState;

public class Kernel {
    public static final String DIR_PATH = "src/programs/";
    ArrayList<Process> arrivingProcesses;
    Scheduler scheduler;
    Queue<Process> readyQueue;

    public Kernel(ArrayList<Process> arrivingProcesses, Scheduler scheduler, Queue<Process> readyQueue) {
        this.arrivingProcesses = arrivingProcesses;
        this.scheduler = scheduler;
        this.readyQueue = readyQueue;
    }

    public void run() {
        while (!arrivingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            /*
             * if there are processes arriving and there is no process in the ready queue,
             * wait for the next process to arrive
             * else, execute the next process in the ready queue
             */

            Logger.logln("Waiting for arriving processes...");
            scheduler.incrementClockCycle();

            arrivingProcesses.stream().filter(p -> p.isArriving(scheduler.getClockCycles()))
                    .forEach(readyQueue::add);

            arrivingProcesses.removeIf(p -> p.isArriving(scheduler.getClockCycles()));

            while (!readyQueue.isEmpty()) {

                Process currentProcess = readyQueue.poll();
                scheduler.schedule(currentProcess);

                ProcessState state = currentProcess.getState();
                Logger.logln(currentProcess, state);

                if (state == ProcessState.PREEMPTED) {
                    // return the process to the tail of the ready queue
                    readyQueue.add(currentProcess);
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
