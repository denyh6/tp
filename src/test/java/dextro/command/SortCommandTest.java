package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SortCommandTest {

    private StudentDatabase db;
    private Storage storage;
    private Student alice;
    private Student bob;
    private Student charlie;

    @BeforeEach
    public void setUp() {
        db = new StudentDatabase();
        storage = null;

        // Initializing students with "N.A." to match your actual string output format
        alice = new Student.Builder("Alice")
                .phone("N.A.")
                .email("N.A.")
                .address("N.A.")
                .course("CS")
                .build();
        bob = new Student.Builder("Bob")
                .phone("N.A.")
                .email("N.A.")
                .address("N.A.")
                .course("SE")
                .build();
        charlie = new Student.Builder("Charlie")
                .phone("N.A.")
                .email("N.A.")
                .address("N.A.")
                .course("IS")
                .build();

        // Add students out of alphabetical order
        // Original indices: Bob = 1, Charlie = 2, Alice = 3
        db.addStudent(bob);
        db.addStudent(charlie);
        db.addStudent(alice);
    }

    @Test
    public void execute_sortByName_success() throws CommandException {
        // Arrange
        SortCommand sortCommand = new SortCommand("name");

        // Act
        CommandResult result = sortCommand.execute(db, storage);

        // Assert
        String expectedMessage = "Temporary list sorted by name:" + System.lineSeparator() +
                "3. Alice/N.A./N.A./N.A./CS" + System.lineSeparator() +
                "1. Bob/N.A./N.A./N.A./SE" + System.lineSeparator() +
                "2. Charlie/N.A./N.A./N.A./IS";

        assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    public void execute_sortByCourse_success() throws CommandException {
        // Arrange
        SortCommand sortCommand = new SortCommand("course");

        // Act
        CommandResult result = sortCommand.execute(db, storage);

        // Assert
        String expectedMessage = "Temporary list sorted by course:" + System.lineSeparator() +
                "3. Alice/N.A./N.A./N.A./CS" + System.lineSeparator() +
                "2. Charlie/N.A./N.A./N.A./IS" + System.lineSeparator() +
                "1. Bob/N.A./N.A./N.A./SE";

        assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    public void undo_throwsCommandException() {
        // Arrange
        SortCommand sortCommand = new SortCommand("name");

        // Act & Assert
        CommandException exception = assertThrows(CommandException.class, () -> {
            sortCommand.undo(db, storage);
        });

        assertEquals("Cannot undo sort command", exception.getMessage());
    }

    @Test
    public void isUndoable_returnsFalse() {
        // Arrange
        SortCommand sortCommand = new SortCommand("name");

        // Act & Assert
        assertFalse(sortCommand.isUndoable());
    }
}
