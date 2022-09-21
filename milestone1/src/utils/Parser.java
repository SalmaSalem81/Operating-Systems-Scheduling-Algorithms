package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private Parser() {
        throw new IllegalStateException("Utility class");
    }

    public static ArrayList<String[]> parse(String path) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    result.add(line.split(" "));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
