package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortCommand implements Command {

    private final String category;

    public SortCommand(String category) {
        this.category = category.toLowerCase();
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        List<Student> students = db.getAllStudents();

        if (students.isEmpty()) {
            return new CommandResult("No students to sort.", false);
        }

        // Create a temporary copy so we don't mutate the actual database order
        List<Student> sortedList = new ArrayList<>(students);

        switch (category) {
        case "name":
            sortedList.sort(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
            break;
        case "course":
            sortedList.sort(Comparator.comparing(Student::getCourse, String.CASE_INSENSITIVE_ORDER));
            break;
        case "cap":
            // numerically descending (highest CAP first)
            sortedList.sort(Comparator.comparingDouble(Student::calculateCap).reversed());
            break;
        case "mcs":
            // numerically descending (highest MCs first)
            sortedList.sort(Comparator.comparingInt(Student::getTotalMCs).reversed());
            break;
        default:
            throw new CommandException("Invalid sort category. Available categories: name, course, cap, mcs");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Temporary list sorted by ").append(category).append(":").append(System.lineSeparator());

        for (Student student : sortedList) {
            // Retrieve the 1-based index from the original, unmutated list
            int originalIndex = students.indexOf(student) + 1;

            sb.append(originalIndex)
                    .append(". ")
                    .append(student.toString());

            // Append the numerical values for clarity if sorting by them
            if (category.equals("cap")) {
                sb.append(String.format(" (CAP: %.1f)", student.calculateCap()));
            } else if (category.equals("mcs")) {
                sb.append(String.format(" (MCs: %d)", student.getTotalMCs()));
            }
            sb.append(System.lineSeparator());
        }

        return new CommandResult(sb.toString().trim(), false);
    }

    /**
     * Executes the sort command using the provided database and storage.
     *
     * @param db      The student database.
     * @param storage The storage component.
     * @return The result of the command execution.
     * @throws CommandException If an error occurs during execution.
     */
    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        // Sort doesn't need storage, so just pass it to your existing logic
        return execute(db);
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        throw new CommandException("Cannot undo sort command");
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        // Route to the existing undo logic
        return undo(db);
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
