package os;

import java.util.ArrayList;
import java.util.HashMap;

import logger.entities.enums.ProcessState;
import utils.Parser;

public class Process {
    private int id;
    private String path;
    private ArrayList<String[]> instructions;
    private HashMap<String, Object> memoryMap;
    private int lastInstructionIndex = 0; // aka pc
    private boolean blocked = false;
    private int arrivalTime;

    public Process(String path, int id, int arrivalTime) {
        this.setPath(path);
        this.id = id;
        setInstructions(Parser.parse(path));
        memoryMap = new HashMap<>();
        this.arrivalTime = arrivalTime;
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
        if (isBlocked()) {
            return ProcessState.BLOCKED;
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
        if (isBlocked()) {
            return ProcessState.BLOCKED;
        }
        return ProcessState.EXECUTING;
    }

    public boolean isArriving(int currentTime) {
        /**
         * check if the process is arriving at the current time
         */
        return currentTime == arrivalTime;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isFinished() {
        /**
         * check if the process is finished executing
         * if the last instruction index is equal or greater than the number of
         * instructions
         */
        return this.lastInstructionIndex >= this.instructions.size();
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLastInstructionIndex() {
        return lastInstructionIndex;
    }

    public void setLastInstructionIndex(int lastInstructionIndex) {
        this.lastInstructionIndex = lastInstructionIndex;
    }

    public HashMap<String, Object> getMemoryMap() {
        return memoryMap;
    }

    public void setMemoryMap(HashMap<String, Object> memoryMap) {
        this.memoryMap = memoryMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String[]> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String[]> instructions) {
        this.instructions = instructions;
    }

    public String[] getCurrentInstruction() {
        return this.instructions.get(lastInstructionIndex);
    }

    public void incrementLastInstructionIndex() {
        this.lastInstructionIndex++;
    }

    public void setVariable(String string, String value) {
        this.memoryMap.put(string, value);
    }

    @Override
    public String toString() {
        return "Process " + this.id;
    }

    public String[] pollInstruction() {
        String[] instruction = this.instructions.get(this.lastInstructionIndex);
        this.lastInstructionIndex++;
        return instruction;
    }
}
