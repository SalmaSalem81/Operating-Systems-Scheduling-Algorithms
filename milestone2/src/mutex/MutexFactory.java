package mutex;

import java.util.HashMap;
import java.util.Queue;
import os.Process;

public class MutexFactory {
    private static MutexFactory instance;
    private Queue<Process> generalBlockedQueue;
    private HashMap<String, Mutex> mutexMap; // map of mutexes <mutex name, mutex>

    private MutexFactory(Queue<Process> generalBlockedQueue, HashMap<String, Mutex> mutexMap) {
        this.generalBlockedQueue = generalBlockedQueue;
        this.mutexMap = mutexMap;
    }

    public static MutexFactory getInstance(Queue<Process> generalBlockedQueue, HashMap<String, Mutex> mutexMap) {
        if (instance == null) {
            instance = new MutexFactory(generalBlockedQueue, mutexMap);
        }
        return instance;

    }

    public void createMutex(String name) {
        Mutex mutex = new Mutex(generalBlockedQueue);
        mutexMap.put(name, mutex);
    }
}
