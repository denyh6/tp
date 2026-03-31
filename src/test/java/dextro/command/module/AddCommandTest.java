package dextro.command.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dextro.app.Storage;
import dextro.command.CommandResult;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.Test;

class AddCommandTest {

    @Test
    void execute_validInput_addsModuleSuccessfully() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(1, "CS2113", Grade.A);

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert message
        assertEquals(
                "Added module CS2113 (A) to John",
                result.getMessage()
        );

        // Assert module added
        assertEquals(1, student.getModules().size());

        Module module = student.getModules().get(0);
        assertEquals("CS2113", module.getCode());
        assertEquals(Grade.A, module.getGrade());
    }

    @Test
    void execute_invalidIndex_returnsErrorMessage() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(2, "CS2113", Grade.A); // invalid index

        // Execute
        CommandResult result = command.execute(db, storage);

        // Assert
        assertEquals("Invalid student index", result.getMessage());

        // Ensure nothing added
        assertEquals(0, student.getModules().size());
    }

    @Test
    void execute_emptyDatabase_returnsError() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");

        AddCommand command = new AddCommand(1, "CS2113", Grade.A);

        CommandResult result = command.execute(db, storage);

        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_multipleAdds_modulesAccumulate() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand cmd1 = new AddCommand(1, "CS2113", Grade.A);
        AddCommand cmd2 = new AddCommand(1, "MA1521", Grade.B_PLUS);

        cmd1.execute(db, storage);
        cmd2.execute(db, storage);

        assertEquals(2, student.getModules().size());
    }

    @Test
    void execute_lowercaseModuleCode_convertedToUppercase() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(1, "cs2113", Grade.A);

        // Execute
        command.execute(db, storage);

        // Assert module stored in uppercase
        assertEquals(1, student.getModules().size());

        Module module = student.getModules().get(0);
        assertEquals("CS2113", module.getCode()); // should be uppercase
    }

    @Test
    void execute_mixedCaseModuleCode_convertedToUppercase() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(1, "cS2113", Grade.A);

        command.execute(db, storage);

        Module module = student.getModules().get(0);
        assertEquals("CS2113", module.getCode());
    }
}
