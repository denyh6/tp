package dextro.command.module;

import dextro.app.Storage;
import dextro.command.Command;
import dextro.command.CommandResult;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class AddCommand implements Command {

    private final int index;
    private final String moduleCode;
    private final Grade grade;

    public AddCommand(int index, String moduleCode, Grade grade) {
        this.index = index;
        this.moduleCode = moduleCode.toUpperCase();
        this.grade = grade;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) {
        if (index < 1 || index > db.getStudentCount()) {
            return new CommandResult("Invalid student index");
        }

        Student student = db.getStudent(index-1);

        Module module = new Module(moduleCode, grade);
        student.addModule(module);
        storage.saveStudentList(db);

        return new CommandResult(
                "Added module " + moduleCode + " (" + grade + ") to " + student.getName()
        );
    }
}
