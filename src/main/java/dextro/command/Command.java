package dextro.command;

import dextro.exception.CommandException;
import dextro.app.Storage;
import dextro.model.record.StudentDatabase;

public interface Command {
    CommandResult execute(StudentDatabase db) throws CommandException;

    CommandResult undo(StudentDatabase db) throws CommandException;

    CommandResult execute(StudentDatabase db, Storage storage) throws CommandException;

    CommandResult undo(StudentDatabase db, Storage storage) throws CommandException;

    boolean isUndoable();
}
