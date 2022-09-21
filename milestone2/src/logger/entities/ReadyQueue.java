package logger.entities;

import os.Process;

public class ReadyQueue extends Queue {
    public ReadyQueue(java.util.Queue<Process> processQueue) {
        super(processQueue);
    }

    @Override
    public String info() {
        return "Ready Queue contains: " + super.info();
    }
}
