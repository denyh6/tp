package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.record.StudentDatabase;

public class UndoCommand implements Command {
    private final CommandHistory history;

    public UndoCommand(CommandHistory history) {
        this.history = history;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        if (history.isEmpty()) {
            return new CommandResult("Warning: No command to undo");
        }

        Command lastCommand = history.pop();
        return lastCommand.undo(db, storage);
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        throw new CommandException("Cannot undo an undo command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
