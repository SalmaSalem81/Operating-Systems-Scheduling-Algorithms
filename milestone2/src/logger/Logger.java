package logger;

import logger.entities.enums.ProcessState;
import logger.entities.enums.QueueType;
import logger.services.InitLoggerService;
import os.Process;

import java.util.Queue;

public class Logger {
    public static final String PP = "\n===============================================================\n";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BOLD_BLACK = "\u001B[1m\u001B[30m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Logger instance = new Logger();
    private static LogSubject logSubject;


    private Logger() {
        logSubject = InitLoggerService.execute();
    }

    public static Logger getInstance() {
        return instance;
    }

    public static void log(String message) {
        logSubject.notifyObservers(message, false);
    }

    public static void logln(String message) {
        logSubject.notifyObservers(message, true);
    }

    public static void logln(Queue<Process> queue, QueueType type) {
        if (type == QueueType.BLOCKED) {
            logSubject.notifyObservers(new logger.entities.BlockedQueue(queue).info(), true);
        } else if (type == QueueType.READY) {
            logSubject.notifyObservers(new logger.entities.ReadyQueue(queue).info(), true);
        }
    }

    public static void logln(Process process, ProcessState state) {
        logSubject.notifyObservers(new logger.entities.Process(process, state).info(), true);
    }
}
