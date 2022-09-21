package entities;

import logger.Logger;
import memory.Memory;

import java.io.*;
import java.util.ArrayList;

public class Disk {
    private final String DISK_PATH = "src/programs/disk.txt";
    private final String TMP_PATH = "src/programs/tmp.txt";
    private final String PROCESS_SEPARATOR = "#";
    private BufferedWriter diskWriter;
    private BufferedReader diskReader;
    private BufferedWriter tmpWriter;
    private BufferedReader tmpReader;
    private File diskFile;
    private File tmpFile;
    private Memory memory;

    public Disk(Memory memory) {
        this.memory = memory;

        try {
            diskFile = new File(DISK_PATH);
            tmpFile = new File(TMP_PATH);
            diskFile.createNewFile();
            tmpFile.createNewFile();
            diskWriter = new BufferedWriter(new FileWriter(diskFile, true));
            diskReader = new BufferedReader(new FileReader(diskFile));
            tmpWriter = new BufferedWriter(new FileWriter(tmpFile));
            tmpReader = new BufferedReader(new FileReader(tmpFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDiskFile() {
        return diskFile;
    }

    public File getTmpFile() {
        return tmpFile;
    }

    public void writeProcess(int slot) {
        try {
            Process process = memory.getProcess(slot);

            diskWriter.write(PROCESS_SEPARATOR + '\n');

            diskWriter.write("id " + process.getPcb().getId() + '\n');
            diskWriter.write("state " + (process.getPcb().getState() == null ? ProcessState.NEW : process.getPcb().getState()) + '\n');
            diskWriter.write("pc " + process.getPcb().getProgramCounter() + '\n');
            diskWriter.write("beginIndex " + process.getPcb().getBeginIndex() + '\n');
            diskWriter.write("endIndex " + process.getPcb().getEndIndex() + '\n');

            for (int i = 0; i < Memory.VARIABLE_COUNT; i++) {
                diskWriter.write("var" + i + " " + process.getVariable(i) + '\n');
            }

            for (String[] line : process.getInstructions()) {
                if (line != null) diskWriter.write(String.join(" ", line) + '\n');
            }

            Logger.logln(Logger.ANSI_YELLOW_BOLD + "Process " + process.getPcb().getId() + " written to disk" + Logger.ANSI_RESET);
            diskWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeProcess(int id) { // remove 3
        try {
            resetDiskReader();
            resetTmpReader();

            boolean firstLine = true;
            String line;
            while ((line = diskReader.readLine()) != null) { // skips separator or first line
                if (firstLine) {
                    line = diskReader.readLine(); // id
                }

                if (Integer.parseInt(line.split(" ")[1]) != id) {
                    writeToTmp(PROCESS_SEPARATOR);
                    writeToTmp(line);
                    while ((line = diskReader.readLine()) != null && !line.startsWith(PROCESS_SEPARATOR)) {
                        writeToTmp(line);
                    }
                } else {
                    seekNextHash();
                }
                firstLine = false;
            }

            Logger.logln(Logger.ANSI_RED_BOLD + "Process " + id + " loaded from disk" + Logger.ANSI_RESET);

            tmpFile.renameTo(diskFile);
            tmpFile.delete();
            tmpFile = new File(TMP_PATH);
            tmpFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Process readProcess(int id) {
        Process process = new Process();
        try {
            resetDiskReader();

            String line = diskReader.readLine(); // skip separator
            line = diskReader.readLine(); // id
            while (Integer.parseInt(line.split(" ")[1]) != id) {
                seekNextHash();
                line = diskReader.readLine();
            }
            process.getPcb().setId(Integer.parseInt(line.split(" ")[1]));

            line = diskReader.readLine(); // state
            process.getPcb().setState(ProcessState.valueOf(line.split(" ")[1]));

            line = diskReader.readLine(); // pc
            process.getPcb().setProgramCounter(Integer.parseInt(line.split(" ")[1]));

            line = diskReader.readLine(); // beginIndex
            process.getPcb().setBeginIndex(Integer.parseInt(line.split(" ")[1]));

            line = diskReader.readLine(); // endIndex
            process.getPcb().setEndIndex(Integer.parseInt(line.split(" ")[1]));

            for (int i = 0; i < Memory.VARIABLE_COUNT; i++) {
                line = diskReader.readLine(); // var
                process.setVariableValue(i, line.split(" ")[1]);
            }

            ArrayList<String[]> instructions = new ArrayList<>();
            while (!line.equals(PROCESS_SEPARATOR) && (line = diskReader.readLine()) != null) {
                instructions.add(line.split(" "));
            }
            process.setInstructions(instructions);

//            Logger.logln(Logger.ANSI_YELLOW_BOLD + "Process " + process.getPcb().getId() + " read from disk" + Logger.ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeProcess(id);

        return process;
    }

    public boolean isProcessOnDisk(int id) {
        try {
            resetDiskReader();
            String line;
            while ((line = diskReader.readLine()) != null) {
                if (line.contains("id " + id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeToTmp(String line) {
        try {
            tmpWriter.write(line + '\n');
            tmpWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void seekNextHash() {
        try {
            String line;
            while ((line = diskReader.readLine()) != null) {
                if (line.equals(PROCESS_SEPARATOR)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetDiskReader() {
        try {
            diskReader.close();
            diskReader = new BufferedReader(new FileReader(DISK_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetTmpReader() {
        try {
            tmpReader.close();
            tmpReader = new BufferedReader(new FileReader(TMP_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
