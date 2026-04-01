package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public class ExitCommand implements Command{
    public ExitCommand() {

    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    public CommandResult execute(StudentDatabase studentDatabase, Storage storage) {
        return new CommandResult("Goodbye!", true);
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        throw new CommandException("Cannot undo exit command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
