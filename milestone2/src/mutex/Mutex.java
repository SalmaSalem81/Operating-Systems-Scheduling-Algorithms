package mutex;

import logger.Logger;
import os.Process;

import java.util.Queue;

public class Mutex {
    private Queue<Process> waitingQueue;
    private MutexState state; // free or locked
    private int processId;
    private Queue<Process> generalBlockedQueue;

    public Mutex(Queue<Process> generalBlockedQueue) {
        this.state = MutexState.FREE;
        this.processId = -1;
        waitingQueue = new java.util.LinkedList<>();
        this.generalBlockedQueue = generalBlockedQueue;
    }

    public int getProcessId() {
        return processId;
    }

    public void wait(Process process) {
        // the mutex is already taken. Block the process
        if (this.state == MutexState.LOCKED) {
            waitingQueue.add(process);
            generalBlockedQueue.add(process);
            process.setBlocked(true);
            return;
        }
        // safe to take the mutex
        this.state = MutexState.LOCKED;
        this.processId = process.getId();
        Logger.logln("Process (#" + process.getId() + ") got the mutex");
    }

    public Process post(Process process) {
        if (this.processId != process.getId()) {
            /**
             * The process is not the one who has the mutex.
             */
            Logger.logln(Logger.ANSI_RED + "[Error] process (#"
                    + process.getId()
                    + ") tried to post a mutex that is not owned by it"
                    + Logger.ANSI_RESET);
            return null;
        }
        // the process releases the mutex by setting the value to 1
        this.state = MutexState.FREE;
        this.processId = -1;
        /*
         * check if there are processes waiting for the mutex
         */
        if (waitingQueue.isEmpty()) {
            return null;
        }
        /*
         * There's a process waiting for the mutex.
         */
        Process nextProcess = waitingQueue.poll();
        nextProcess.setBlocked(false);
        generalBlockedQueue.remove(nextProcess);

        Logger.logln(Logger.ANSI_GREEN + "Process (#" + nextProcess.getId() +
                ") is woken up" + Logger.ANSI_RESET);

        // give the mutex to the next process
        this.processId = nextProcess.getId();
        this.state = MutexState.LOCKED; // woken process takes the mutex.
        return nextProcess;
    }

}
