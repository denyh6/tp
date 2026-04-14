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
    public void execute_searchByNameMatch_success() {
        // Search for name containing "John"
        SearchCommand command = new SearchCommand("John", null, null, null, null, null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe" + System.lineSeparator() +
                "3. Johnny Appleseed";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByPhoneMatch_success() {
        // Search for phone containing "8123"
        SearchCommand command = new SearchCommand(null, "8123", null, null, null, null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Phone: 81234567";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByEmailMatch_success() {
        // Search for email containing "nus.edu"
        SearchCommand command = new SearchCommand(null, null, "nus.edu", null, null, null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Email: johndoe@u.nus.edu";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByCourseMatch_success() {
        // Search for course containing "Science" using c/ prefix
        SearchCommand command = new SearchCommand(null, null, null, null, "Science", null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Course: Computer Science";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByModuleMatch_success() {
        // Search for module "CS2113" using m/ prefix
        SearchCommand command = new SearchCommand(null, null, null, null, null, "CS2113");
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Modules: [CS2113: A]" + System.lineSeparator() +
                "2. Jane Smith, Modules: [CS2113: B]";

        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByModuleMultipleMatches_success() {
        // Add another CS module to John to test multiple matches for a single student
        Student john = db.getAllStudents().get(0);
        john.addModule(new Module("CS2040", Grade.B_PLUS));

        // Search for "CS", which should match both CS2113 and CS2040 for John
        SearchCommand command = new SearchCommand(null, null, null, null, null, "CS");
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Modules: [CS2113: A | CS2040: B+]" + System.lineSeparator() +
                "2. Jane Smith, Modules: [CS2113: B]";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByMultipleCategoriesMatch_success() {
        // Search for Name "John" AND Course "Science"
        // This should match John Doe, but filter out Johnny Appleseed (wrong course)
        SearchCommand command = new SearchCommand("John", null, null, null, "Science", null);
        CommandResult result = command.execute(db);

        String expectedOutput = "1. John Doe, Course: Computer Science";
        assertEquals(expectedOutput, result.getMessage());
    }

    @Test
    public void execute_searchByMultipleCategoriesNoMatch_showsNotFound() {
        // Search for Name "Jane" AND Course "Science"
        // Jane exists, but her course is "Information Systems", so this should fail
        SearchCommand command = new SearchCommand("Jane", null, null, null, "Science", null);
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void execute_searchByCourseNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand(null, null, null, null, "Medicine", null);
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void execute_searchByModuleNoMatch_showsNotFound() {
        SearchCommand command = new SearchCommand(null, null, null, null, null, "EE2026");
        CommandResult result = command.execute(db);

        assertEquals("No matching students found.", result.getMessage());
    }

    @Test
    public void undo_throwsCommandException() {
        SearchCommand command = new SearchCommand(null, null, null, null, "Science", null);

        assertThrows(CommandException.class, () -> command.undo(db));
        assertFalse(command.isUndoable());
    }
}
