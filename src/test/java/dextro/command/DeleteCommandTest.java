package dextro.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteCommandTest {

    private StudentDatabase db;
    private Storage storage;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        storage = new Storage("./data/DextroStudentList.txt");

        // Use CreateCommand (builder pattern internally)
        new CreateCommand("Alice", "123", "a@mail.com", "Addr1", "CS")
                .execute(db, storage);
        new CreateCommand("Bob", "456", "b@mail.com", "Addr2", "IT")
                .execute(db, storage);
    }

    @Test
    void execute_validIndex_deletesStudentSuccessfully() {
        DeleteCommand command = new DeleteCommand(1);

        CommandResult result = command.execute(db, storage);

        // Check message
        assertEquals(
                "Successfully deleted student:\nAlice/123/a@mail.com/Addr1/CS",
                result.getMessage()
        );

        // Check database updated
        assertEquals(1, db.getStudentCount());
        assertEquals("Bob", db.getStudent(0).getName());
    }

    @Test
    void execute_invalidIndex_throwsCommandException() {
        DeleteCommand command = new DeleteCommand(5);

        CommandException exception = assertThrows(
                CommandException.class,
                () -> command.execute(db, storage)
        );

        assertEquals(
                "The student at index 5 does not exist.",
                exception.getMessage()
        );
    }
}
