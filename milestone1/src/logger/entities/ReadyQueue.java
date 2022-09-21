package logger.entities;

import entities.Process;
import logger.Logger;

public class ReadyQueue extends Queue {
    public ReadyQueue(java.util.Queue<Process> processQueue) {
        super(processQueue);
    }

    @Override
    public String info() {
        return Logger.ANSI_BLUE_BOLD + "Ready Queue contains: " + Logger.ANSI_RESET +
                Logger.ANSI_BLUE + super.info() + Logger.ANSI_RESET;
    }
}
