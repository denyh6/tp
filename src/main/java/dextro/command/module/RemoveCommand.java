package dextro.command.module;

import dextro.app.Storage;
import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.exception.CommandException;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class RemoveCommand implements Command {

    private final int index;
    private final String moduleCode;
    private Module removedModule = null;

    public RemoveCommand(int index, String moduleCode) {
        this.index = index;
        this.moduleCode = moduleCode.toUpperCase();
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

        Student student = db.getStudent(index - 1);

        // Find the module before removing it
        for (Module m : student.getModules()) {
            if (m.getCode().equalsIgnoreCase(moduleCode)) {
                removedModule = m;
                break;
            }
        }

        boolean removed = student.removeModule(moduleCode);
        if (removed) {
            storage.saveStudentList(db);
            return new CommandResult("Removed module " + moduleCode + " from " + student.getName());
        }

        return new CommandResult(
                "Module " + moduleCode + " not found for " + student.getName()
        );


    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        if (removedModule == null) {
            throw new CommandException("Cannot undo: remove command was not executed or module was not found");
        }
        if (index < 1 || index > db.getStudentCount()) {
            throw new CommandException("Cannot undo: invalid student index");
        }

        Student student = db.getStudent(index - 1);
        student.addModule(removedModule);

        return new CommandResult("Undone: Module removal of " + moduleCode + " for " + student.getName());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
