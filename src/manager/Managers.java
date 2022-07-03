package manager;

import http.HTTPTaskManager;



public class Managers {
    private final static HistoryManager historyManager = new InMemoryHistoryManager();
    private final static TaskManager taskManager;

    static {
        taskManager = new HTTPTaskManager(false);
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }
}