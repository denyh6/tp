package dextro.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dextro.app.Storage;
import dextro.model.record.StudentDatabase;
import dextro.model.Student;

class CreateCommandTest {

    @Test
    void execute_validInput_studentAddedAndMessageCorrect() {
        // Arrange
        StudentDatabase db = new StudentDatabase();
        Storage storage = new Storage("./data/DextroStudentList.txt");
        CreateCommand cmd = new CreateCommand(
                "John", "12345678", "john@mail.com", "house", "CS"
        );

        CommandResult result = cmd.execute(db, storage);

        assertEquals(1, db.getStudentCount());

        Student student = db.getStudent(0);
        assertEquals("John", student.getName());
        assertEquals("12345678", student.getPhone());
        assertEquals("john@mail.com", student.getEmail());
        assertEquals("house", student.getAddress());
        assertEquals("CS", student.getCourse());

        assertEquals("Student created: John", result.getMessage());
    }
}
