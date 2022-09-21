package entities;

public class PCB {
    public static final int PCB_SIZE = 5;
    private int id;
    private int programCounter;
    private int beginIndex;
    private int endIndex;
    private ProcessState state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }
}
