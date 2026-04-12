package dextro.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

class DeleteCommandTest {

    private StudentDatabase db;
    private Storage storage;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");
        db.addStudent(new Student.Builder(
                "ALICE")
                .phone("91234567")
                .email("a@mail.com")
                .address("Addr1")
                .course("Computer Science").build());
        db.addStudent(new Student.Builder(
                "BOB")
                .phone("98765432")
                .email("b@mail.com")
                .address("Addr2")
                .course("Information Technology").build());
        db.addStudent(new Student.Builder(
                "CHARLIE")
                .phone("81234567")
                .email("c@mail.com")
                .address("Addr3")
                .course("Computer Engineering").build());
    }

    // ===== execute(db, storage) =====

    @Test
    void execute_validIndex_studentRemovedFromDb() throws CommandException {
        new DeleteCommand(2).execute(db, storage);
        assertEquals(2, db.getStudentCount());
    }

    @Test
    void execute_validIndex_correctStudentRemoved() throws CommandException {
        new DeleteCommand(2).execute(db, storage);
        assertEquals("ALICE", db.getStudent(0).getName());
        assertEquals("CHARLIE", db.getStudent(1).getName());
    }

    @Test
    void execute_validIndex_messageContainsDeletedStudentName() throws CommandException {
        CommandResult result = new DeleteCommand(2).execute(db, storage);
        assertTrue(result.getMessage().contains("BOB"));
    }

    @Test
    void execute_firstIndex_deletesFirstStudent() throws CommandException {
        new DeleteCommand(1).execute(db, storage);
        assertEquals("BOB", db.getStudent(0).getName());
    }

    @Test
    void execute_lastIndex_deletesLastStudent() throws CommandException {
        new DeleteCommand(3).execute(db, storage);
        assertEquals(2, db.getStudentCount());
        assertEquals("ALICE", db.getStudent(0).getName());
        assertEquals("BOB", db.getStudent(1).getName());
    }

    @Test
    void execute_indexTooHigh_throwsCommandException() {
        CommandException e = assertThrows(CommandException.class,
                () -> new DeleteCommand(5).execute(db, storage));
        assertEquals("The student at index 5 does not exist.", e.getMessage());
    }

    @Test
    void execute_indexZero_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> new DeleteCommand(0).execute(db, storage));
    }

    @Test
    void execute_negativeIndex_throwsCommandException() {
        assertThrows(CommandException.class,
                () -> new DeleteCommand(-1).execute(db, storage));
    }

    @Test
    void execute_emptyDb_throwsCommandException() {
        StudentDatabase emptyDb = new StudentDatabase();
        assertThrows(CommandException.class,
                () -> new DeleteCommand(1).execute(emptyDb, storage));
    }

    @Test
    void execute_dbOnlyOverload_returnsNull() throws CommandException {
        assertNull(new DeleteCommand(1).execute(db));
    }

    // ===== undo =====

    @Test
    void undo_afterExecute_studentCountRestored() throws CommandException {
        DeleteCommand cmd = new DeleteCommand(2);
        cmd.execute(db, storage);
        assertEquals(2, db.getStudentCount());
        cmd.undo(db, storage);
        assertEquals(3, db.getStudentCount());
    }

    @Test
    void undo_afterExecute_studentRestoredAtOriginalPosition() throws CommandException {
        // Delete BOB at index 2, then undo — BOB should be back at index 1 (0-based)
        DeleteCommand cmd = new DeleteCommand(2);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        assertEquals("ALICE", db.getStudent(0).getName());
        assertEquals("BOB", db.getStudent(1).getName());
        assertEquals("CHARLIE", db.getStudent(2).getName());
    }

    @Test
    void undo_afterExecute_restoredStudentFieldsIntact() throws CommandException {
        DeleteCommand cmd = new DeleteCommand(1);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        Student restored = db.getStudent(0);
        assertEquals("ALICE", restored.getName());
        assertEquals("91234567", restored.getPhone());
        assertEquals("a@mail.com", restored.getEmail());
    }

    @Test
    void undo_afterDeleteFirst_restoresAtPositionZero() throws CommandException {
        DeleteCommand cmd = new DeleteCommand(1);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        assertEquals("ALICE", db.getStudent(0).getName());
    }

    @Test
    void undo_afterDeleteLast_restoresAtEnd() throws CommandException {
        DeleteCommand cmd = new DeleteCommand(3);
        cmd.execute(db, storage);
        cmd.undo(db, storage);
        assertEquals("CHARLIE", db.getStudent(2).getName());
    }

    @Test
    void undo_withoutExecute_throwsCommandException() {
        DeleteCommand cmd = new DeleteCommand(1);
        assertThrows(CommandException.class, () -> cmd.undo(db, storage));
    }

    @Test
    void undo_messageContainsStudentName() throws CommandException {
        DeleteCommand cmd = new DeleteCommand(2);
        cmd.execute(db, storage);
        CommandResult result = cmd.undo(db, storage);
        assertTrue(result.getMessage().contains("BOB"));
    }

    @Test
    void undo_singleOverload_returnsNull() throws CommandException {
        assertNull(new DeleteCommand(1).undo(db));
    }

    // ===== isUndoable =====

    @Test
    void isUndoable_alwaysReturnsTrue() {
        assertTrue(new DeleteCommand(1).isUndoable());
    }
}
