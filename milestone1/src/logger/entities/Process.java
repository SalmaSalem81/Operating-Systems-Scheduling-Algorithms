package logger.entities;

import entities.ProcessState;
import logger.Logger;

public class Process implements Trackable {
    private entities.Process process;
    private ProcessState state;

    public Process(entities.Process process, ProcessState state) {
        this.process = process;
        this.state = state;
    }

    public Process(entities.Process process) {
        this.process = process;
    }

    @Override
    public String info() {
        String pInfo = Logger.ANSI_BLUE + "Process (#" + process.getPcb().getId() + ")" + Logger.ANSI_RESET;

        if (state == null) {
            return pInfo;
        }

        return infoState() + ' ' + pInfo;
    }

    private String infoState() {
        return switch (state) {
            case BLOCKED -> Logger.ANSI_RED_BOLD + "[BLOCKED]" + Logger.ANSI_RESET;
            case RUNNING -> Logger.ANSI_BLUE_BOLD + "[CHOSEN]" + Logger.ANSI_RESET;
            case PREEMPTED -> Logger.ANSI_YELLOW_BOLD + "[PREEMPTED]" + Logger.ANSI_RESET;
            case FINISHED -> Logger.ANSI_GREEN_BOLD + "[FINISHED]" + Logger.ANSI_RESET;
            default -> "[" + state + "]";
        };
    }
}
