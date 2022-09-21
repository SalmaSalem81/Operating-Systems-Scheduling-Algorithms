import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import entities.Disk;
import entities.Kernel;
import entities.Scheduler;
import memory.MMU;
import entities.Process;
import entities.ProcessFactory;
// import logger.entities.enums.QueueType;
// import utils.ProcessFactory;
import memory.Memory;

public class OS {
    public static void main(String[] args) {
        int quantumTime = 2;

        ArrayList<Process> arrivingProcesses = new ArrayList<>();
        ProcessFactory processFactory = ProcessFactory.getInstance();
        processFactory.setProcesses(arrivingProcesses);

        processFactory.createProcess("Program_1.txt", 0);
        processFactory.createProcess("Program_2.txt", 1);
        processFactory.createProcess("Program_3.txt", 4);

        Queue<Process> readyQueue = new java.util.LinkedList<>();
        Memory memory = new Memory();
        Disk disk = new Disk(memory);
        MMU mmu = new MMU(memory, disk);

        Scheduler scheduler = new Scheduler(quantumTime, readyQueue, arrivingProcesses, mmu);

        Kernel finix = new Kernel(arrivingProcesses, scheduler, readyQueue, mmu);

        // starts the simulation until there are no more processes arriving.
        finix.run();

        // Logger.logln(Logger.ANSI_GREEN + "[EXIT] PROGRAM FINISHED SUCCESSFULLY!" +
        // Logger.ANSI_RESET);
    }
}
