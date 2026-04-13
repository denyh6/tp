package dextro.command;

import dextro.app.Storage;
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
        return null;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        assert db != null : "StudentDatabase should not be null";
        assert storage != null : "Storage should not be null";

        if (index <= 0 || index > db.getAllStudents().size()) {
            throw new CommandException("Invalid index: " + index);
        }

        Student student = db.getAllStudents().get(index - 1);
        assert student != null : "Student should not be null";

        double cap = student.calculateCap();
        assert cap >= 0.0 && cap <= 5.0 : "CAP should be between 0.0 and 5.0";

        int totalMCs = student.getTotalMCs();
        assert totalMCs >= 0 : "Total MCs should not be negative";

        String status = student.getProgressStatus();
        assert status != null && !status.isEmpty() : "Progress status should not be null or empty";

        StringBuilder result = new StringBuilder();
        result.append(String.format("Index %d: %s, %s, Cap %.1f, %d/160 MCs completed. Status: %s.",
                index,
                student.getName(),
                student.getCourse(),
                cap,
                totalMCs,
                status));

        // Add module and grade details
        if (!student.getModules().isEmpty()) {
            result.append("\nModules and Grades:");
            for (dextro.model.Module module : student.getModules()) {
                result.append(String.format("\n  - %s: %s (%d MCs)",
                        module.getCode(),
                        module.getGrade(),
                        module.getCredits()));
            }
        } else {
            result.append("\nNo modules added yet.");
        }

        String resultString = result.toString();
        assert resultString != null && !resultString.isEmpty() : "Result message should not be null or empty";
        return new CommandResult(resultString, false);
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        throw new CommandException("Cannot undo status command");
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
