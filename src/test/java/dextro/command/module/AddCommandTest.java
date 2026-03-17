package dextro.command.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import dextro.command.CommandResult;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;
import org.junit.jupiter.api.Test;

class AddCommandTest {

    @Test
    void execute_validInput_addsModuleSuccessfully() {
        // Setup
        StudentDatabase db = new StudentDatabase();
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(1, "CS2113", Grade.A);

        // Execute
        CommandResult result = command.execute(db);

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
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand command = new AddCommand(2, "CS2113", Grade.A); // invalid index

        // Execute
        CommandResult result = command.execute(db);

        // Assert
        assertEquals("Invalid student index", result.getMessage());

        // Ensure nothing added
        assertEquals(0, student.getModules().size());
    }

    @Test
    void execute_emptyDatabase_returnsError() {
        StudentDatabase db = new StudentDatabase();

        AddCommand command = new AddCommand(1, "CS2113", Grade.A);

        CommandResult result = command.execute(db);

        assertEquals("Invalid student index", result.getMessage());
    }

    @Test
    void execute_multipleAdds_modulesAccumulate() {
        StudentDatabase db = new StudentDatabase();
        Student student = new Student.Builder("John").build();
        db.addStudent(student);

        AddCommand cmd1 = new AddCommand(1, "CS2113", Grade.A);
        AddCommand cmd2 = new AddCommand(1, "MA1521", Grade.B_PLUS);

        cmd1.execute(db);
        cmd2.execute(db);

        assertEquals(2, student.getModules().size());
    }
}
