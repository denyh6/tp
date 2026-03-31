package dextro.command.module;

import dextro.app.Storage;
import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class RemoveCommand implements Command {

    private final int index;
    private final String moduleCode;

    public RemoveCommand(int index, String moduleCode) {
        this.index = index;
        this.moduleCode = moduleCode.toUpperCase();
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) {

        if (index < 1 || index > db.getStudentCount()) {
            return new CommandResult("Invalid student index");
        }

        Student student = db.getStudent(index - 1);

        boolean removed = student.removeModule(moduleCode);
        if (removed) {
            return new CommandResult("Removed module " + moduleCode + " from " + student.getName());
        }
        storage.saveStudentList(db);

        return new CommandResult(
                "Module " + moduleCode + " not found for " + student.getName()
        );


    }
}
