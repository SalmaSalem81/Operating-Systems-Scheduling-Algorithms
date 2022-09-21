package memory;

import entities.Disk;
import entities.Process;
import entities.ProcessState;
import logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class MMU {
    private Memory memory;
    private Disk disk;

    public MMU(Memory memory, Disk disk) {
        this.memory = memory;
        this.disk = disk;
    }

    public Disk getDisk() {
        return disk;
    }

    public Memory getMemory() {
        return memory;
    }

    public void addProcess(Process process) {
        if (disk.isProcessOnDisk(process.getPcb().getId())) {
            Process p = disk.readProcess(process.getPcb().getId());
            for (int i = 0; i < memory.getMemorySlots().length; i++) {
                if (!memory.getMemorySlots()[i]) {
                    memory.storeProcess(i, p);
                    return;
                }
            }
        }

        for (int i = 0; i < memory.getMemorySlots().length; i++) {
            if (!memory.getMemorySlots()[i]) {
                memory.storeProcess(i, process);
                return;
            }
        }

        swap(process);
    }

    public int setVariableInMemory(Process process, int index, String variable, int value) {
        if (checkWriteVariablePermission(process, index)) {
            memory.set(index, new HashMap<>() {
                {
                    put(variable, value);
                }
            });

            return 0;
        }

        return -1;
    }

    private boolean checkWriteVariablePermission(Process process, int index) {
        return (index < 20 && process.getPcb().getId() == 0) || (index >= 20 && process.getPcb().getId() == 1);
    }

    public boolean isProcessInMemory(int id) {
        return memory.getProcessSlot(id) != -1;
    }

    private void swap(Process process) {
        var processesInMemory = memory.getProcessesInsertionMap();

        for (Map.Entry<Integer, Process> entry : processesInMemory.entrySet()) {
            if (entry.getValue().getPcb().getState() != ProcessState.RUNNING) {
                int processSlot = memory.getProcessSlot(entry.getValue().getPcb().getId());

                disk.writeProcess(processSlot);
                processesInMemory.remove(entry.getKey());

                memory.storeProcess(processSlot, process);

                Logger.logln(Logger.ANSI_BLUE_BOLD + "Process " + process.getPcb().getId() + " swapped with process "
                        + entry.getValue().getPcb().getId() + Logger.ANSI_RESET);

                return;
            }
        }
    }

    public void removeProcess(int id) {
        int processSlot = memory.getProcessSlot(id);
        for (int i = processSlot * 20; i < Memory.MEMORY_SIZE / 2; i++) {
            memory.set(i, null);
        }
        memory.getMemorySlots()[processSlot] = false;
        for (Map.Entry<Integer, Process> entry : memory.getProcessesInsertionMap().entrySet()) {
            if (entry.getValue().getPcb().getId() == id) {
                memory.getProcessesInsertionMap().remove(entry.getKey());
                break;
            }
        }
    }
}
