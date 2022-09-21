package logger;

import entities.ProcessState;
import logger.entities.enums.QueueType;
import logger.services.InitLoggerService;
import entities.Process;

import java.util.Queue;

public class Logger {
    public static final String PP = "\n===============================================================\n";
    public static final String ANSI_RESET       = "\033[0m";
    public static final String ANSI_RED         = "\033[0;31m";
    public static final String ANSI_GREEN       = "\033[0;32m";
    public static final String ANSI_YELLOW      = "\033[0;33m";
    public static final String ANSI_BLUE        = "\033[0;34m";
    public static final String ANSI_BLACK_BOLD  = "\033[1;30m";
    public static final String ANSI_RED_BOLD    = "\033[1;31m";
    public static final String ANSI_GREEN_BOLD  = "\033[1;32m";
    public static final String ANSI_YELLOW_BOLD = "\033[1;33m";
    public static final String ANSI_BLUE_BOLD   = "\033[1;34m";
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
        logSubject.notifyObservers(new logger.entities.ReadyQueue(queue).info(), true);
    }

    public static void logln(Process process, ProcessState state) {
        logSubject.notifyObservers(new logger.entities.Process(process, state).info(), true);
    }
}
