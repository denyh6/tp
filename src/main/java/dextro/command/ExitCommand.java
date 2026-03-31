package dextro.command;

import dextro.app.Storage;
import dextro.model.record.StudentDatabase;

public class ExitCommand implements Command{
    public ExitCommand() {

    }

    public CommandResult execute(StudentDatabase studentDatabase, Storage storage) {
        return new CommandResult("Goodbye!", true);
    }
}
