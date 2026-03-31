package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class CreateCommand implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;
    private int createdIndex = -1;

    public CreateCommand(String name, String phone, String email, String address, String course) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.course = course;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) {
        Student student = new Student.Builder(name)
                .phone(phone)
                .email(email)
                .address(address)
                .course(course)
                .build();

        db.addStudent(student);
        storage.saveStudentList(db);
        createdIndex = db.getStudentCount() - 1;

        String message = String.format("Student created: %s", student.getName());
        return new CommandResult(message);
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        if (createdIndex == -1) {
            throw new CommandException("Cannot undo: create command was not executed");
        }
        if (createdIndex >= db.getStudentCount()) {
            throw new CommandException("Cannot undo: student no longer exists at the created index");
        }
        Student removed = db.removeStudent(createdIndex);
        return new CommandResult("Undone: Student creation of " + removed.getName());
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
