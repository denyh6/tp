package dextro.command.module;

import dextro.app.Storage;
import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class AddCommand implements Command {

    private final int index;
    private final String moduleCode;
    private final Grade grade;
    private final Integer credits;

    private boolean wasExecuted = false;

    public AddCommand(int index, String moduleCode, Grade grade, Integer credits) {
        this.index = index;
        this.moduleCode = moduleCode.toUpperCase();
        this.grade = grade;
        this.credits = credits;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) {
        if (index < 1 || index > db.getStudentCount()) {
            return new CommandResult("Invalid student index");
        }

        Student student = db.getStudent(index-1);
        Module module;
        if (credits != null) {
            module = new Module(moduleCode, grade, credits);
        } else {
            module = new Module(moduleCode, grade);
        }
        student.addModule(module);
        storage.saveStudentList(db);
        wasExecuted = true;

        return new CommandResult(
                "Added module " + moduleCode + " (" + grade + ") to " + student.getName()
        );
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        if (!wasExecuted) {
            throw new CommandException("Cannot undo: add command was not executed");
        }
        if (index < 1 || index > db.getStudentCount()) {
            throw new CommandException("Cannot undo: invalid student index");
        }

        Student student = db.getStudent(index - 1);
        boolean removed = student.removeModule(moduleCode);

        if (!removed) {
            throw new CommandException("Cannot undo: module not found");
        }

        return new CommandResult("Undone: Module addition of " + moduleCode + " for " + student.getName());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
