package entities;

import java.util.ArrayList;

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
        instance.getProcesses().add(process);
    }
}
