package logger.destinations;

import java.io.BufferedWriter;
import java.io.IOException;

public class FileLogger implements LogObserver {

    private final BufferedWriter bufferedWriter;

    public FileLogger(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    public void log(String message) {
        try {
            message = removeColor(message);
            bufferedWriter.write(message);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logLn(String message) {
        try {
            message = removeColor(message);
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String removeColor(String message) {
        return message.replaceAll("(\\033\\[[0-1];3[0-9]m)|(\033\\[0m)", "");
    }
}
