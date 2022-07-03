package manager;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final String pathToSave = "/Users/valleyker/Desktop/Tasks.csv";

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(pathToSave));
    }
}