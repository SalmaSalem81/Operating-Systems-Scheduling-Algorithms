package logger.entities;

import logger.Logger;
import logger.entities.enums.ProcessState;

public class Process implements Trackable {
    private os.Process process;
    private ProcessState state;

    public Process(os.Process process, ProcessState state) {
        this.process = process;
        this.state = state;
    }

    public Process(os.Process process) {
        this.process = process;
    }

    @Override
    public String info() {
        String pInfo = "Process (#" + process.getId() + ")";

        if (state == null) {
            return pInfo;
        }

        return infoState() + ' ' + pInfo;
    }

    private String infoState() {
        return switch (state) {
            case BLOCKED -> Logger.ANSI_RED + "[BLOCKED]" + Logger.ANSI_RESET;
            case CHOSEN -> Logger.ANSI_BLUE + "[CHOSEN]" + Logger.ANSI_RESET;
            case PREEMPTED -> Logger.ANSI_YELLOW + "[PREEMPTED]" + Logger.ANSI_RESET;
            case FINISHED -> Logger.ANSI_GREEN + "[FINISHED]" + Logger.ANSI_RESET;
            default -> "[" + state + "]";
        };
    }
}
