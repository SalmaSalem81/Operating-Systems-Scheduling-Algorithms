package logger.destinations;

public interface LogObserver {
    void log(String message);
    void logLn(String message);
}
