package dextro.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

class StatusCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private Student student1;
    private Student student2;
    private Student student3;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");

        student1 = new Student.Builder("Alice")
                .phone("91234567")
                .email("alice@test.com")
                .address("123 Street")
                .course("Computer Science")
                .build();

        student2 = new Student.Builder("Bob")
                .phone("98765432")
                .email("bob@test.com")
                .address("456 Avenue")
                .course("Computer Engineering")
                .build();

        student3 = new Student.Builder("Charlie")
                .phone("87654321")
                .email("charlie@test.com")
                .address("789 Road")
                .course("Information Systems")
                .build();

        db.addStudent(student1);
        db.addStudent(student2);
        db.addStudent(student3);
    }

    @Test
    void execute_validIndex_returnsCorrectStatus() throws CommandException {
        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        String expectedMessage = "Index 1: Alice, Computer Science, Cap 0.0, 0/160 MCs completed. "
                + "Status: Just Started.\n"
                + "No modules added yet.";
        assertEquals(expectedMessage, result.getMessage());
        assertFalse(result.shouldExit());
    }

    @Test
    void execute_validIndexWithModules_calculatesCorrectCap() throws CommandException {
        student1.addModule(new Module("CS2113", Grade.A));
        student1.addModule(new Module("CS2101", Grade.B_PLUS));

        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("Cap 4.5"));
        assertTrue(result.getMessage().contains("8/160 MCs completed"));
        assertTrue(result.getMessage().contains("Modules and Grades:"));
        assertTrue(result.getMessage().contains("CS2113: A (4 MCs)"));
        assertTrue(result.getMessage().contains("CS2101: B+ (4 MCs)"));
    }

    @Test
    void execute_studentWithMultipleModules_showsCorrectProgressStatus() throws CommandException {
        for (int i = 0; i < 10; i++) {
            student2.addModule(new Module("MOD" + i, Grade.A));
        }

        StatusCommand cmd = new StatusCommand(2);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("40/160 MCs"));
        assertTrue(result.getMessage().contains("Status: On Track"));
    }

    @Test
    void execute_studentWith80MCs_showsSatisfactoryStatus() throws CommandException {
        for (int i = 0; i < 20; i++) {
            student3.addModule(new Module("MOD" + i, Grade.B));
        }

        StatusCommand cmd = new StatusCommand(3);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("80/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Satisfactory"));
    }

    @Test
    void execute_studentWith120MCs_showsGoodProgressStatus() throws CommandException {
        for (int i = 0; i < 30; i++) {
            student1.addModule(new Module("MOD" + i, Grade.A_MINUS));
        }

        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("120/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Good Progress"));
    }

    @Test
    void execute_studentWith160MCs_showsCompletedStatus() throws CommandException {
        for (int i = 0; i < 40; i++) {
            student2.addModule(new Module("MOD" + i, Grade.A_PLUS));
        }

        StatusCommand cmd = new StatusCommand(2);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("160/160 MCs"));
        assertTrue(result.getMessage().contains("Status: Completed"));
    }

    @Test
    void execute_invalidIndexZero_throwsCommandException() {
        StatusCommand cmd = new StatusCommand(0);
        CommandException exception = assertThrows(CommandException.class, () ->
            cmd.execute(db, storage)
        );

        assertEquals("Invalid index: 0", exception.getMessage());
    }

    @Test
    void execute_invalidIndexNegative_throwsCommandException() {
        StatusCommand cmd = new StatusCommand(-1);
        CommandException exception = assertThrows(CommandException.class, () ->
            cmd.execute(db, storage)
        );

        assertEquals("Invalid index: -1", exception.getMessage());
    }

    @Test
    void execute_indexTooLarge_throwsCommandException() {
        StatusCommand cmd = new StatusCommand(4);
        CommandException exception = assertThrows(CommandException.class, () ->
            cmd.execute(db, storage)
        );

        assertEquals("Invalid index: 4", exception.getMessage());
    }

    @Test
    void execute_indexOneBoundary_worksCorrectly() throws CommandException {
        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("Index 1: Alice"));
    }

    @Test
    void execute_indexMaxBoundary_worksCorrectly() throws CommandException {
        StatusCommand cmd = new StatusCommand(3);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("Index 3: Charlie"));
    }

    @Test
    void execute_emptyDatabase_throwsCommandException() {
        StudentDatabase emptyDb = new StudentDatabase();
        StatusCommand cmd = new StatusCommand(1);

        assertThrows(CommandException.class, () ->
            cmd.execute(emptyDb, storage)
        );
    }

    @Test
    void execute_withoutStorage_returnsNull() throws CommandException {
        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db);

        assertNull(result);
    }

    @Test
    void isUndoable_always_returnsFalse() {
        StatusCommand cmd = new StatusCommand(1);
        assertFalse(cmd.isUndoable());
    }

    @Test
    void undo_withoutStorage_returnsNull() throws CommandException {
        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.undo(db);

        assertNull(result);
    }

    @Test
    void undo_withStorage_throwsCommandException() {
        StatusCommand cmd = new StatusCommand(1);
        CommandException exception = assertThrows(CommandException.class, () ->
            cmd.undo(db, storage)
        );

        assertEquals("Cannot undo status command", exception.getMessage());
    }

    @Test
    void execute_studentWithMixedGrades_calculatesCorrectAverage() throws CommandException {
        student1.addModule(new Module("CS2113", Grade.A_PLUS));
        student1.addModule(new Module("CS2101", Grade.B));
        student1.addModule(new Module("CS2040", Grade.A));

        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        double expectedCap = (5.0 + 3.5 + 5.0) / 3.0;
        assertTrue(result.getMessage().contains(String.format("Cap %.1f", expectedCap)));
    }

    @Test
    void execute_studentWithNoModules_showsZeroCap() throws CommandException {
        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        assertTrue(result.getMessage().contains("Cap 0.0"));
        assertTrue(result.getMessage().contains("0/160 MCs"));
    }

    @Test
    void execute_multipleCallsSameStudent_returnsConsistentResults() throws CommandException {
        student1.addModule(new Module("CS2113", Grade.A));

        StatusCommand cmd1 = new StatusCommand(1);
        CommandResult result1 = cmd1.execute(db, storage);

        StatusCommand cmd2 = new StatusCommand(1);
        CommandResult result2 = cmd2.execute(db, storage);

        assertEquals(result1.getMessage(), result2.getMessage());
    }

    @Test
    void execute_allGradeTypes_calculatesCorrectly() throws CommandException {
        student1.addModule(new Module("MOD1", Grade.A_PLUS));
        student1.addModule(new Module("MOD2", Grade.A));
        student1.addModule(new Module("MOD3", Grade.A_MINUS));
        student1.addModule(new Module("MOD4", Grade.B_PLUS));
        student1.addModule(new Module("MOD5", Grade.B));
        student1.addModule(new Module("MOD6", Grade.B_MINUS));
        student1.addModule(new Module("MOD7", Grade.C));
        student1.addModule(new Module("MOD8", Grade.F));
        student1.addModule(new Module("MOD9", Grade.A));
        student1.addModule(new Module("MOD10", Grade.B));

        StatusCommand cmd = new StatusCommand(1);
        CommandResult result = cmd.execute(db, storage);

        assertFalse(result.shouldExit());
        assertTrue(result.getMessage().contains("40/160 MCs"));
    }
}
