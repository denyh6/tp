package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class DeleteCommand implements Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        try {
            Student deletedStudent = db.removeStudent(index - 1);
            storage.saveStudentList(db);

            return new CommandResult("Successfully deleted student:\n" + deletedStudent.toString());

        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("The student at index " + index + " does not exist.");
        }
    }
}
