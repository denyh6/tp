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
        SearchCommand command = new SearchCommand("Science", null, null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Computer Science";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByCourseNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand("Medicine", null, null);
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void execute_searchByModuleMatch_success() {
        // Search for module "CS2113" using m/ prefix
        SearchCommand command = new SearchCommand(null, "CS2113", null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, CS2113: A\n" +
                "2. Jane Smith, CS2113: B";

        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByModuleMultipleMatches_success() {
        // Add another CS module to John to test multiple matches for a single student
        Student john = db.getAllStudents().get(0);
        john.addModule(new Module("CS2040", Grade.B_PLUS));

        // Search for "CS", which should match both CS2113 and CS2040 for John
        SearchCommand command = new SearchCommand(null, "CS", null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, CS2113: A\n" +
                "1. John Doe, CS2040: B+\n" +
                "2. Jane Smith, CS2113: B";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByModuleNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand(null, "EE2026", null);
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void undo_throwsCommandException() {
        SearchCommand command = new SearchCommand("Science", null, null);

        assertThrows(CommandException.class, () -> command.undo(db));
        assertFalse(command.isUndoable());
    }

    @Test
    public void execute_searchByPhoneMatch_success() {
        // Search for course containing "Science" using c/ prefix
        SearchCommand command = new SearchCommand(null, null, "812");
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, 81234567";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByPhoneMultipleMatches_success() {
        // Search for "2", which should match both 81234567 for John and 98765432 for Jane
        SearchCommand command = new SearchCommand(null, null, "2");
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, 81234567\n" +
                "2. Jane Smith, 98765432";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByPhoneNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand(null, null, "000");
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

}
