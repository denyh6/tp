package dextro.command;

import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchCommandTest {

    private StudentDatabase db;

    @BeforeEach
    public void setUp() {
        db = new StudentDatabase();

        // Student 1: John in CS
        Student john = new Student.Builder("John Doe")
                .phone("81234567")
                .email("johndoe@u.nus.edu")
                .address("123 Clementi Road")
                .course("Computer Science")
                .build();
        john.addModule(new Module("CS2113", Grade.A));
        db.addStudent(john);

        // Student 2: Jane in IS
        Student jane = new Student.Builder("Jane Smith")
                .phone("98765432")
                .course("Information Systems")
                .build();
        jane.addModule(new Module("IS1103", Grade.B_PLUS));
        jane.addModule(new Module("CS2113", Grade.B));
        db.addStudent(jane);

        // Student 3: Johnny in BZA
        Student johnny = new Student.Builder("Johnny Appleseed")
                .course("Business Analytics")
                .build();
        db.addStudent(johnny);
    }

    @Test
    public void execute_searchByCourseMatch_success() {
        // Search for course containing "Science" using c/ prefix
        SearchCommand command = new SearchCommand("Science", null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Computer Science";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByCourseNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand("Medicine", null);
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void execute_searchByModuleMatch_success() {
        // Search for module "CS2113" using m/ prefix
        SearchCommand command = new SearchCommand(null, "CS2113");
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, A\n" +
                "2. Jane Smith, B";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByModuleNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand(null, "EE2026");
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void undo_throwsCommandException() {
        SearchCommand command = new SearchCommand("Science", null);

        assertThrows(CommandException.class, () -> command.undo(db));
        assertFalse(command.isUndoable());
    }
}
