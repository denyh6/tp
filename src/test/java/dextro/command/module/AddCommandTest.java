package dextro.command.module;

import dextro.app.Storage;
import dextro.command.CommandResult;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;



class AddCommandTest {

    @Test
    void execute_validInput_addsModuleSuccessfully() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(1, "CS2113", Grade.A, 4);

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

        AddCommand command = new AddCommand(2, "CS2113", Grade.A, 4); // invalid index

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

        AddCommand command = new AddCommand(1, "CS2113", Grade.A, 4);

        CommandResult result = command.execute(db, storage);

        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_multipleAdds_modulesAccumulate() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand cmd1 = new AddCommand(1, "CS2113", Grade.A, 4);
        AddCommand cmd2 = new AddCommand(1, "MA1521", Grade.B_PLUS, 4);

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

        AddCommand command = new AddCommand(1, "cs2113", Grade.A, 4);

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

        AddCommand command = new AddCommand(1, "cS2113", Grade.A, 4);

        command.execute(db, storage);

        Module module = student.getModules().get(0);
        assertEquals("CS2113", module.getCode());
    }

    // ===== execute(db, storage) =====

    @Test
    void execute_indexBelowOne_returnsInvalidMessage() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(0, "CS1010", Grade.A, null);
        CommandResult result = cmd.execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_indexAboveStudentCount_returnsInvalidMessage() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(db.getStudentCount() + 1, "CS1010", Grade.A, null);
        CommandResult result = cmd.execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }


    @Test
    void undo_afterExecute_resultMessageContainsModuleCode() throws CommandException {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(1, "CS1010", Grade.A, null);
        cmd.execute(db, storage);
        CommandResult result = cmd.undo(db, storage);
        assertTrue(result.getMessage().contains("CS1010"));
    }

    @Test
    void undo_withoutExecute_throwsCommandException() {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(1, "CS1010", Grade.A, null);
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }


    @Test
    void undo_calledTwice_throwsCommandExceptionOnSecondCall() throws CommandException {
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(1, "CS1010", Grade.A, null);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        // wasExecuted is still true but module is gone — second undo should fail
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    // ===== isUndoable / execute(db) / undo(db) =====

    @Test
    void isUndoable_alwaysReturnsTrue() {
        StudentDatabase db = new StudentDatabase();
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(1, "CS1010", Grade.A, null);
        assertTrue(cmd.isUndoable());
    }

    @Test
    void execute_dbOnlyOverload_returnsNull() throws CommandException {
        StudentDatabase db = new StudentDatabase();
        Student student = new Student.Builder("John").build();
        db.addStudent(student);
        AddCommand cmd = new AddCommand(1, "CS1010", Grade.A, null);
        assertNull(cmd.execute(db));
    }
}
