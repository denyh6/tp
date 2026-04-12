package dextro.command.module;

import dextro.app.Storage;
import dextro.command.CommandResult;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private Student student;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
        student = new Student.Builder("JOHN").build();
        student.addModule(new Module("CS2113", Grade.A));
        student.addModule(new Module("MA1521", Grade.B_PLUS));
        db.addStudent(student);
    }

    // ===== execute(db, storage) — happy path =====

    @Test
    void execute_validModule_moduleRemoved() {
        new RemoveCommand(1, "CS2113").execute(db, storage);
        assertEquals(1, student.getModules().size());
    }

    @Test
    void execute_validModule_correctModuleRemoved() {
        new RemoveCommand(1, "CS2113").execute(db, storage);
        assertEquals("MA1521", student.getModules().get(0).getCode());
    }

    @Test
    void execute_validModule_messageCorrect() {
        CommandResult result = new RemoveCommand(1, "CS2113").execute(db, storage);
        assertEquals("Removed module CS2113 from JOHN", result.getMessage());
    }

    @Test
    void execute_lowercaseModuleCode_removesSuccessfully() {
        new RemoveCommand(1, "cs2113").execute(db, storage);
        assertEquals(1, student.getModules().size());
    }

    @Test
    void execute_mixedCaseModuleCode_removesSuccessfully() {
        new RemoveCommand(1, "Cs2113").execute(db, storage);
        assertEquals(1, student.getModules().size());
    }

    @Test
    void execute_moduleCodeStoredUppercaseInMessage() {
        CommandResult result = new RemoveCommand(1, "cs2113").execute(db, storage);
        assertTrue(result.getMessage().contains("CS2113"));
    }

    @Test
    void execute_removeLastModule_studentHasNoModules() {
        new RemoveCommand(1, "CS2113").execute(db, storage);
        new RemoveCommand(1, "MA1521").execute(db, storage);
        assertEquals(0, student.getModules().size());
    }

    // ===== execute(db, storage) — not found / invalid =====

    @Test
    void execute_moduleNotFound_returnsMessage() {
        CommandResult result = new RemoveCommand(1, "CG2027").execute(db, storage);
        assertEquals("Module CG2027 not found for JOHN", result.getMessage());
    }

    @Test
    void execute_moduleNotFound_doesNotModifyModuleList() {
        new RemoveCommand(1, "CG2027").execute(db, storage);
        assertEquals(2, student.getModules().size());
    }

    @Test
    void execute_invalidIndexZero_returnsInvalidMessage() {
        CommandResult result = new RemoveCommand(0, "CS2113").execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_invalidIndexTooHigh_returnsInvalidMessage() {
        CommandResult result = new RemoveCommand(99, "CS2113").execute(db, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_emptyDb_returnsInvalidMessage() {
        StudentDatabase emptyDb = new StudentDatabase();
        CommandResult result = new RemoveCommand(1, "CS2113").execute(emptyDb, storage);
        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_dbOnlyOverload_returnsNull() throws CommandException {
        assertNull(new RemoveCommand(1, "CS2113").execute(db));
    }

    // ===== undo =====

    @Test
    void undo_afterExecute_moduleRestored() throws CommandException {
        RemoveCommand cmd = new RemoveCommand(1, "CS2113");
        cmd.execute(db, storage);
        assertEquals(1, student.getModules().size());
        cmd.undo(db, storage);
        assertEquals(2, student.getModules().size());
    }

    @Test
    void undo_afterExecute_restoredModuleHasCorrectCode() throws CommandException {
        RemoveCommand cmd = new RemoveCommand(1, "CS2113");
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        boolean found = student.getModules().stream()
                .anyMatch(m -> m.getCode().equals("CS2113"));
        assertTrue(found);
    }

    @Test
    void undo_afterExecute_restoredModuleHasCorrectGrade() throws CommandException {
        RemoveCommand cmd = new RemoveCommand(1, "CS2113");
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        Module restored = student.getModules().stream()
                .filter(m -> m.getCode().equals("CS2113"))
                .findFirst().orElseThrow();
        assertEquals(Grade.A, restored.getGrade());
    }

    @Test
    void undo_afterExecute_messageContainsModuleCode() throws CommandException {
        RemoveCommand cmd = new RemoveCommand(1, "CS2113");
        cmd.execute(db, storage);
        CommandResult result = cmd.undo(db, storage);
        assertTrue(result.getMessage().contains("CS2113"));
    }

    @Test
    void undo_withoutExecute_throwsCommandException() {
        RemoveCommand cmd = new RemoveCommand(1, "CS2113");
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void undo_afterModuleNotFound_throwsCommandException() {
        RemoveCommand cmd = new RemoveCommand(1, "CG2027");
        cmd.execute(db, storage); // module not found, removedModule stays null
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void undo_singleOverload_returnsNull() throws CommandException {
        assertNull(new RemoveCommand(1, "CS2113").undo(db));
    }

    // ===== isUndoable =====

    @Test
    void isUndoable_alwaysReturnsTrue() {
        assertTrue(new RemoveCommand(1, "CS2113").isUndoable());
    }
}
