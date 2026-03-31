package dextro.command.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dextro.app.Storage;
import dextro.command.CommandResult;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.Test;

class RemoveCommandTest {

    @Test
    void execute_validModule_removesSuccessfully() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();

        student.addModule(new Module("CS2113", Grade.A));
        db.addStudent(student);

        RemoveCommand command = new RemoveCommand(1, "CS2113");

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert message
        assertEquals(
                "Removed module CS2113 from John",
                result.getMessage()
        );

        // Assert module removed
        assertEquals(0, student.getModules().size());
    }

    @Test
    void execute_moduleNotFound_returnsMessage() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();

        student.addModule(new Module("CS2113", Grade.A));
        db.addStudent(student);

        RemoveCommand command = new RemoveCommand(1, "MA1521");

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert
        assertEquals(
                "Module MA1521 not found for John",
                result.getMessage()
        );

        // Ensure original module still exists
        assertEquals(1, student.getModules().size());
    }

    @Test
    void execute_invalidIndex_returnsError() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        RemoveCommand command = new RemoveCommand(2, "CS2113");

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_emptyDatabase_returnsError() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");

        RemoveCommand command = new RemoveCommand(1, "CS2113");

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_caseInsensitiveMatch_removesSuccessfully() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();

        student.addModule(new Module("CS2113", Grade.A));
        db.addStudent(student);

        RemoveCommand command = new RemoveCommand(1, "cs2113"); // lowercase

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert
        assertEquals(0, student.getModules().size());
        assertEquals("Removed module CS2113 from John", result.getMessage());
    }
}
