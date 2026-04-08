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
    private final Integer credits;
    private Student previousStudent = null;

    public EditCommand(int index, String name, String phone, String email,
                       String address, String course, String moduleCode, Grade grade, Integer credits) {
        this.index = index;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.course = course;
        this.moduleCode = moduleCode;
        this.grade = grade;
        this.credits = credits;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        int studentCount = db.getStudentCount();
        if (index > studentCount || index < 0) {
            throw new CommandException("Index should be within range.");
        }
        Student existing = db.getStudent(index);
        previousStudent = existing;

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

        boolean editedModule = false;
        // copy modules, replacing grade for the matched module
        for (Module m : existing.getModules()) {
            if (m.getCode().equalsIgnoreCase(moduleCode)) {
                if (credits != null) {
                    updatedStudent.addModule(new Module(m.getCode(), grade, credits));
                } else {
                    updatedStudent.addModule(new Module(m.getCode(), grade));
                }
                editedModule = true;
            } else {
                updatedStudent.addModule(m);
            }
        }

        db.updateStudent(index, updatedStudent);
        storage.saveStudentList(db);
        if (name == null && phone == null && email == null && address == null && course == null && !editedModule) {
            return new CommandResult("No changes made.");
        }
        return new CommandResult("Student updated successfully.");
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        if (previousStudent == null) {
            throw new CommandException("Cannot undo: edit command was not executed");
        }
        if (index >= db.getStudentCount() || index < 0) {
            throw new CommandException("Cannot undo: invalid index");
        }
        db.updateStudent(index, previousStudent);
        return new CommandResult("Undone: Student edit of " + previousStudent.getName());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
