package logger.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateLogFileService {
    static LocalDateTime current = LocalDateTime.now();
    static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    private static final String LOG_FILE_NAME = "log-" + format.format(
            current) + ".txt";
    private static final String LOG_FILE_PATH = "./src/logger/outputs/" + LOG_FILE_NAME;

    private CreateLogFileService() {
        throw new IllegalStateException("Utility class");
    }

    public static BufferedWriter execute() {
        try {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            return new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
