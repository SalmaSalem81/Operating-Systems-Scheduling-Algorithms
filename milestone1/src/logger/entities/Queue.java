package logger.entities;

import entities.Process;

public abstract class Queue implements Trackable {
    private java.util.Queue<Process> processQueue;

    public Queue(java.util.Queue<Process> processQueue) {
        this.processQueue = processQueue;
    }

    @Override
    public String info() {
        if (processQueue.isEmpty()) {
            return "[]\n";
        }

        StringBuilder sb = new StringBuilder("\n");
        int index = 1;
        while (!processQueue.isEmpty()) {
            Process p = processQueue.poll();
            sb.append(index++).append(". ").append(new logger.entities.Process(p).info()).append("\n");
        }

        return sb.toString();
    }
}
