package dextro.command;

import dextro.app.Storage;
import dextro.model.record.StudentDatabase;

public interface Command {
    CommandResult execute(StudentDatabase db, Storage storage);
}
