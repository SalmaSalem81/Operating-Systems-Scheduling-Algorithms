package memory;

import entities.PCB;
import entities.Process;
import entities.ProcessState;
import utils.Parser;

import java.util.HashMap;
import java.util.TreeMap;

public class Memory {
    public static final int MEMORY_SIZE = 40; // 40 words
    public static final int MEMORY_SLOTS_COUNT = 2;
    public static final int VARIABLE_COUNT = 3;
    public static int currentInsertion = 0;
    private final Object[] memory;
    private boolean[] memorySlots;
    private TreeMap<Integer, Process> processesInsertionMap;

    public Memory() {
        memory = new Object[MEMORY_SIZE];
        memorySlots = new boolean[MEMORY_SLOTS_COUNT];
        processesInsertionMap = new TreeMap<>();
    }

    public void storeProcess(int slot, Process process) {
        if (slot < 0 || slot >= MEMORY_SLOTS_COUNT) {
            throw new IllegalArgumentException("Invalid slot");
        }

        process.getPcb().setBeginIndex(slot * 20);
        process.getPcb().setEndIndex(slot * 20 + 19);

        int idx = 0;
        // store PCB
        memory[idx++ + slot * 20] = process.getPcb().getId();
        memory[idx++ + slot * 20] = process.getPcb().getState();
        memory[idx++ + slot * 20] = process.getPcb().getProgramCounter();
        memory[idx++ + slot * 20] = process.getPcb().getBeginIndex(); // 0 or 20
        memory[idx++ + slot * 20] = process.getPcb().getEndIndex(); // 19 or 39

        // store variables
        for (int j = 0; j < VARIABLE_COUNT; j++, idx++) {
            HashMap<Object, Object> mp = new HashMap<>();
            mp.put("var" + j, process.getVariable(j));
            memory[idx + slot * 20] = "var" + j;
        }

        boolean empty = process.getInstructions().isEmpty();
        // store raw code lines
        if (empty) {

            for (String[] line : Parser.parse(process.getPath())) {
                memory[idx++ + slot * 20] = line;
                process.getInstructions().add(line);
            }
        } else {
            for (String[] line : process.getInstructions()) {

                memory[idx++ + slot * 20] = line;
            }

        }
        memorySlots[slot] = true;
        processesInsertionMap.put(currentInsertion++, process);
    }

    public Process getProcess(int slot) {
        if (slot < 0 || slot >= MEMORY_SLOTS_COUNT) {
            throw new IllegalArgumentException("Invalid slot");
        }

        Process process = new Process();

        int idx = 0;
        // get PCB
        process.getPcb().setId((int) memory[idx++ + slot * 20]);
        process.getPcb().setState((ProcessState) memory[idx++ + slot * 20]);
        process.getPcb().setProgramCounter((int) memory[idx++ + slot * 20]);
        process.getPcb().setBeginIndex((int) memory[idx++ + slot * 20]);
        process.getPcb().setEndIndex((int) memory[idx++ + slot * 20]);

        // get variables
        for (int j = 0; j < VARIABLE_COUNT; j++) {
            process.setVariableValue(j, memory[idx + j + slot * 20]);
        }
        idx += VARIABLE_COUNT;

        // get raw code lines
        int instructionSize = process.getPcb().getEndIndex() - process.getPcb().getBeginIndex() - PCB.PCB_SIZE
                - VARIABLE_COUNT;
        for (int j = 0; j < instructionSize; j++) {
            process.getInstructions().add((String[]) memory[idx + j + slot * 20]);
        }

        return process;
    }

    public void setProcessVariable(int slot, int index, HashMap<String, Object> variable) {
        if (index < 0 || index >= 3) {
            throw new IllegalArgumentException("Invalid variable index");
        }

        memory[PCB.PCB_SIZE + index + slot * 20] = variable;
    }

    public HashMap<String, Object> getProcessVariable(int slot, String key) {
        for (int i = 0; i < VARIABLE_COUNT; i++) {
            HashMap<String, Object> variable = (HashMap<String, Object>) memory[PCB.PCB_SIZE + i + slot * 20];
            if (variable.containsKey(key)) {
                return variable;
            }
        }

        return null;
    }

    public int getProcessSlot(int id) {
        for (int i = 0; i < MEMORY_SLOTS_COUNT; i++) {
            if (memory[i * 20] != null && memory[i * 20].equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasEmptySlots() {
        for (boolean slot : memorySlots) {
            if (!slot) {
                return true;
            }
        }
        return false;
    }

    public Object get(int index) {
        return memory[index];
    }

    public void set(int index, Object value) {
        memory[index] = value;
    }

    public Object[] getMemory() {
        return memory;
    }

    public boolean[] getMemorySlots() {
        return memorySlots;
    }

    public void setMemorySlots(boolean[] memorySlots) {
        this.memorySlots = memorySlots;
    }

    public TreeMap<Integer, Process> getProcessesInsertionMap() {
        return processesInsertionMap;
    }
}
