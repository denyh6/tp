package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.record.StudentDatabase;

public class EditCommand implements Command {
    private final int index;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;
    private final String moduleCode;
    private final Grade grade;

    public EditCommand(int index, String name, String phone, String email,
                       String address, String course, String moduleCode, Grade grade) {
        this.index = index;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.course = course;
        this.moduleCode = moduleCode;
        this.grade = grade;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException{
        int studentCount = db.getStudentCount();
        if (index > studentCount || index < 0) {
            throw new CommandException("Index should be within range.");
        }
        Student existing = db.getStudent(index);

        // validate moduleCode exists before making any changes
        if (moduleCode != null && existing.getModules().stream()
            .noneMatch(m -> m.getCode().equalsIgnoreCase(moduleCode))) {
            return new CommandResult("Module " + moduleCode + " not found for this student.");
        }

        // rebuild student with updated basic fields
        Student updatedStudent = new Student.Builder(existing)
            .name(name)
            .phone(phone)
            .email(email)
            .address(address)
            .course(course)
            .build();

        // copy modules, replacing grade for the matched module
        for (Module m : existing.getModules()) {
            if (moduleCode != null && m.getCode().equalsIgnoreCase(moduleCode)) {
                updatedStudent.addModule(new Module(m.getCode(), grade));
            } else {
                updatedStudent.addModule(m);
            }
        }

        db.updateStudent(index, updatedStudent);
        storage.saveStudentList(db);
        return new CommandResult("Student updated successfully.");
    }
}
