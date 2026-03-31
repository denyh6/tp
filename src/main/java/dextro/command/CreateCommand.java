package dextro.command;

import dextro.app.Storage;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class CreateCommand implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;

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

        String message = String.format("Student created: %s", student.getName());
        return new CommandResult(message);
    }
}
