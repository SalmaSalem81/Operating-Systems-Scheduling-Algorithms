package utils;

import java.util.ArrayList;

import logger.Logger;
import os.Kernel;
import os.Process;

public class ProcessFactory {
    private ArrayList<Process> processes;
    private static ProcessFactory instance = new ProcessFactory(Kernel.DIR_PATH);
    private int currentId = 1;
    private String directory;

    private ProcessFactory(String directory) {
        this.directory = directory;
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(ArrayList<Process> arrivingProcesses) {
        this.processes = arrivingProcesses;
    }

    public static ProcessFactory getInstance() {
        return instance;
    }

    public void createProcess(String fileName, int arrivalTime) {
        Process process = new Process(directory + fileName, currentId++, arrivalTime);
        if (process.getInstructions().isEmpty()) {
            Logger.logln("Process (#" + process.getId() + ") is empty");
            return;
        }
        instance.getProcesses().add(process);
    }
}
