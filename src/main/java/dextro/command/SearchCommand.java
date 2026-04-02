package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

import java.util.List;

public class SearchCommand implements Command {

    private final String course;
    private final String moduleCode;

    public SearchCommand(String course, String moduleCode) {
        this.course = course;
        this.moduleCode = moduleCode;
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        List<Student> students = db.getAllStudents();
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            int originalIndex = i + 1; // 1-based index for display

            if (course != null) {
                if (student.getCourse() != null && student.getCourse().toLowerCase().contains(course.toLowerCase())) {
                    sb.append(originalIndex).append(". ")
                            .append(student.getName()).append(", ")
                            .append(student.getCourse()).append("\n");
                    found = true;
                }
            } else if (moduleCode != null) {
                for (Module m : student.getModules()) {
                    if (m.getCode().toLowerCase().contains(moduleCode.toLowerCase())) {
                        sb.append(originalIndex).append(". ")
                                .append(student.getName()).append(", ")
                                .append(m.getGrade().toString()).append("\n");
                        found = true;
                        break; // Move to the next student once a module matches
                    }
                }
            }
        }

        if (!found) {
            return new CommandResult("No matching students found.");
        }

        return new CommandResult(sb.toString().trim(), false);
    }

    /**
     * @param db
     * @param storage
     * @return
     * @throws CommandException
     */
    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        return null;
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        throw new CommandException("Cannot undo search command");
    }

    /**
     * @param db
     * @param storage
     * @return
     * @throws CommandException
     */
    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        return null;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}