package entities;

import memory.Memory;
import java.util.ArrayList;

public class Process {
    private String path;
    private ArrayList<String[]> instructions;
    private Object[] variables;
    private int arrivalTime;
    private PCB pcb;

    public Process(String path, int id, int arrivalTime) {
        this.setPath(path);
        variables = new Object[Memory.VARIABLE_COUNT];
        this.arrivalTime = arrivalTime;
        pcb = new PCB();
        pcb.setId(id);
        instructions = new ArrayList<>();
    }

    public Process() {
        pcb = new PCB();
        variables = new Object[Memory.VARIABLE_COUNT];
        instructions = new ArrayList<>();
        arrivalTime = -1;
    }

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public boolean isArriving(int currentTime) {
        /**
         * check if the process is arriving at the current time
         */
        return currentTime == arrivalTime;
    }

    public boolean isFinished() {
        /**
         * check if the process is finished executing
         * if the last instruction index is equal or greater than the number of
         * instructions
         */
        return this.getPcb().getProgramCounter() >= this.instructions.size();
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String[]> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String[]> instructions) {
        this.instructions = instructions;
    }

    public void setVariableValue(int key, Object value) {
        this.variables[key] = value;
    }

    public Object getVariable(int key) {
        return this.variables[key];
    }

    public ProcessState getState() {
        /**
         * should be only called while
         * the kernel is taking over,
         * so it's never can be `EXECUTING`
         */
        if (isFinished()) {
            return ProcessState.FINISHED;
        }
        return ProcessState.PREEMPTED;
    }

    public ProcessState getStateInScheduler() {
        /**
         * should be only called
         * in the scheduler,
         * so it's never can be `PREEMPTED`
         */
        if (isFinished()) {
            return ProcessState.FINISHED;
        }
        return ProcessState.RUNNING;
    }
}
