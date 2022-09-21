package logger.services;

import memory.Memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogEntityService {
    private LogEntityService() {
        throw new IllegalStateException("Utility class");
    }

    public static String logMainMemory(Memory memory, int startAddress, int endAddress) {
        List<String> headers = Arrays.asList("Address", "Value");

        List<List<String>> rows = new ArrayList<>();
        for (int i = startAddress; i <= endAddress; i++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i));
            row.add(memory.get(i).toString());
            rows.add(row);
        }

        return GenerateTableService.execute(headers, rows);
    }
}
