package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class DeleteCommand implements Command {
    private final int index;
    private Student deletedStudent = null;
    private int deletedIndex = -1;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        try {
            deletedIndex = index - 1;
            deletedStudent = db.removeStudent(deletedIndex);
            storage.saveStudentList(db);
            return new CommandResult("Successfully deleted student:\n" + deletedStudent.toString());

        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("The student at index " + index + " does not exist.");
        }
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        if (deletedStudent == null || deletedIndex == -1) {
            throw new CommandException("Cannot undo: delete command was not executed");
        }
        db.addStudent(deletedStudent);
        return new CommandResult("Undone: Student deletion of " + deletedStudent.getName());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
