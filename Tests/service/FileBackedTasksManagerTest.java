package service;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    void init() {
        taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(), Paths.get("test.csv"));
    }
}