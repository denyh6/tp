package dextro.command;

import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

public class StatusCommand implements Command {

    private final int index;

    public StatusCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        if (index <= 0 || index > db.getAllStudents().size()) {
            throw new CommandException("Invalid index: " + index);
        }

        Student student = db.getAllStudents().get(index - 1);
        double cap = student.calculateCap();
        int totalMCs = student.getTotalMCs();
        String status = student.getProgressStatus();

        String result = String.format("Index %d: %s, %s, Cap %.1f, %d/160 MCs completed. Status: %s.",
                index,
                student.getName(),
                student.getCourse(),
                cap,
                totalMCs,
                status);

        return new CommandResult(result, false);
    }
}
