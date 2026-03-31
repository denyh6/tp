package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;
import dextro.model.Student;

import java.util.List;

public class ListCommand implements Command {

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) {
        List<Student> students = db.getAllStudents();

        if (students.isEmpty()) {
            return new CommandResult("No students found.", false);
        }

        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Student student : students) {
            sb.append(index++)
                    .append(": ")
                    .append(student.toString())
                    .append("\n");
        }

        return new CommandResult(sb.toString().trim(), false);
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        throw new CommandException("Cannot undo list command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
