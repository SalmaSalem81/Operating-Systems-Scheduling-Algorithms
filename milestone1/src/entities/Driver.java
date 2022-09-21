package entities;

import memory.MMU;
import memory.Memory;
import utils.Parser;

public class Driver {
    public static void main(String[] args) {
        Disk disk = new Disk(new Memory());

        disk.removeProcess(3);
    }
}
