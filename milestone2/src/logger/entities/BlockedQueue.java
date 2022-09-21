package logger.entities;

import os.Process;

public class BlockedQueue extends Queue {
    public BlockedQueue(java.util.Queue<Process> processQueue) {
        super(processQueue);
    }

    @Override
    public String info() {
        return "General Blocked Queue contains: " + super.info();
    }
}
