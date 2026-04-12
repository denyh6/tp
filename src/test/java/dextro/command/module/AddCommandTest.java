package dextro.command.module;

import dextro.app.Storage;
import dextro.command.CommandResult;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private Student student;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
        student = new Student.Builder("JOHN").build();
        db.addStudent(student);
    }

    // ===== execute(db, storage) — happy path =====

    @Test
    void execute_validInput_moduleAddedToStudent() {
        new AddCommand(1, "CS2113", Grade.A, 4).execute(db, storage);
        assertEquals(1, student.getModules().size());
    }

    @Test
    void execute_validInput_messageCorrect() {
        CommandResult result = new AddCommand(1, "CS2113", Grade.A, 4).execute(db, storage);
        assertEquals("Added module CS2113 (A) to JOHN", result.getMessage());
    }

    @Test
    void execute_validInput_moduleCodeStoredUppercase() {
        new AddCommand(1, "cs2113", Grade.A, 4).execute(db, storage);
        assertEquals("CS2113", student.getModules().get(0).getCode());
    }

    @Test
    void execute_withCredits_creditsStoredCorrectly() {
        new AddCommand(1, "CS2113", Grade.A, 6).execute(db, storage);
        assertEquals(6, student.getModules().get(0).getCredits());
    }

    @Test
    void execute_nullCredits_defaultsFourCredits() {
        new AddCommand(1, "CS2113", Grade.A, null).execute(db, storage);
        assertEquals(4, student.getModules().get(0).getCredits());
    }

    @Test
    void execute_gradeStoredCorrectly() {
        new AddCommand(1, "CS2113", Grade.B_PLUS, 4).execute(db, storage);
        assertEquals(Grade.B_PLUS, student.getModules().get(0).getGrade());
    }

    @Test
    void execute_multipleModules_allAccumulate() {
        new AddCommand(1, "CS2113", Grade.A, 4).execute(db, storage);
        new AddCommand(1, "MA1521", Grade.B_PLUS, 4).execute(db, storage);
        assertEquals(2, student.getModules().size());
    }

    @Test
    void execute_duplicateModule_addedAgain() {
        // duplicate modules are allowed for retakes
        new AddCommand(1, "CS2113", Grade.A, 4).execute(db, storage);
        new AddCommand(1, "CS2113", Grade.B, 4).execute(db, storage);
        assertEquals(2, student.getModules().size());
    }

    @Test
    void execute_correctStudentSelected_whenMultipleStudents() {
        Student second = new Student.Builder("JANE").build();
        db.addStudent(second);
        new AddCommand(2, "MA1521", Grade.A, 4).execute(db, storage);
        assertEquals(0, student.getModules().size());
        assertEquals(1, second.getModules().size());
    }

    // ===== execute(db, storage) — invalid index =====

    @Test
    void execute_indexZero_returnsInvalidMessage() {
        CommandResult result = new AddCommand(0, "CS2113", Grade.A, null).execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_indexTooHigh_returnsInvalidMessage() {
        CommandResult result = new AddCommand(99, "CS2113", Grade.A, null).execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_emptyDb_returnsInvalidMessage() {
        StudentDatabase emptyDb = new StudentDatabase();
        CommandResult result = new AddCommand(1, "CS2113", Grade.A, null).execute(emptyDb, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_invalidIndex_noModuleAdded() {
        new AddCommand(99, "CS2113", Grade.A, null).execute(db, storage);
        assertEquals(0, student.getModules().size());
    }

    // ===== undo =====

    @Test
    void undo_afterExecute_moduleRemoved() throws CommandException {
        AddCommand cmd = new AddCommand(1, "CS2113", Grade.A, 4);
        cmd.execute(db, storage);
        assertEquals(1, student.getModules().size());
        cmd.undo(db, storage);
        assertEquals(0, student.getModules().size());
    }

    @Test
    void undo_afterExecute_messageContainsModuleCode() throws CommandException {
        AddCommand cmd = new AddCommand(1, "CS2113", Grade.A, 4);
        cmd.execute(db, storage);
        CommandResult result = cmd.undo(db, storage);
        assertTrue(result.getMessage().contains("CS2113"));
    }

    @Test
    void undo_withoutExecute_throwsCommandException() {
        AddCommand cmd = new AddCommand(1, "CS2113", Grade.A, null);
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void undo_calledTwice_throwsCommandExceptionOnSecond() throws CommandException {
        AddCommand cmd = new AddCommand(1, "CS2113", Grade.A, null);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void undo_singleOverload_returnsNull() throws CommandException {
        assertNull(new AddCommand(1, "CS2113", Grade.A, null).undo(db));
    }

    @Test
    void undo_undoesCorrectModuleWhenMultipleExist() throws CommandException {
        // Add two modules, undo the second
        AddCommand cmd1 = new AddCommand(1, "CS2113", Grade.A, 4);
        AddCommand cmd2 = new AddCommand(1, "MA1521", Grade.B, 4);
        cmd1.execute(db, storage);
        cmd2.execute(db, storage);
        cmd2.undo(db, storage);
        assertEquals(1, student.getModules().size());
        assertEquals("CS2113", student.getModules().get(0).getCode());
    }

    // ===== isUndoable / execute(db) =====

    @Test
    void isUndoable_alwaysReturnsTrue() {
        assertTrue(new AddCommand(1, "CS2113", Grade.A, null).isUndoable());
    }

    @Test
    void execute_dbOnlyOverload_returnsNull() throws CommandException {
        assertNull(new AddCommand(1, "CS2113", Grade.A, null).execute(db));
    }
}
