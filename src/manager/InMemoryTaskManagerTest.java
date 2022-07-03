package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}