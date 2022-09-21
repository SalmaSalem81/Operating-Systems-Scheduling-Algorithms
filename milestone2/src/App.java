import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import logger.Logger;
import logger.entities.enums.QueueType;
import os.Kernel;
import os.Process;
import os.Scheduler;
import utils.ProcessFactory;

public class App {
    public static void main(String[] args) {
        int quantumTime = 2;

        ArrayList<Process> arrivingProcesses = new ArrayList<>();
        ProcessFactory processFactory = ProcessFactory.getInstance();
        processFactory.setProcesses(arrivingProcesses);

        processFactory.createProcess("Program_1.txt", 0);
        processFactory.createProcess("Program_2.txt", 1);
        processFactory.createProcess("Program_3.txt", 4);

        Queue<Process> readyQueue = new java.util.LinkedList<>();

        Scheduler scheduler = new Scheduler(quantumTime, readyQueue, arrivingProcesses);

        Kernel finix = new Kernel(arrivingProcesses, scheduler, readyQueue);

        // starts the simulation until there are no more processes arriving.
        finix.run();

        // is there any process left in the general blocked queue?
        if (!scheduler.getGeneralBlockedQueue().isEmpty()) {
            Logger.logln(Logger.ANSI_RED + "\nThere are processes in the general blocked queue." + Logger.ANSI_RESET);
            Logger.logln(new LinkedList<>(scheduler.getGeneralBlockedQueue()), QueueType.BLOCKED);
        } else {
            Logger.logln(Logger.ANSI_GREEN + "\nAll processes are finished." + Logger.ANSI_RESET);
        }

        Logger.logln(Logger.ANSI_GREEN + "[EXIT] Program finished successfully!" + Logger.ANSI_RESET);
    }
}
