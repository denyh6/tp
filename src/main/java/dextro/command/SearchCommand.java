package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

import java.util.List;


//Searches for students matching any combination of name, phone, email, address, course, and module code.
public class SearchCommand implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;
    private final String moduleCode;

    public SearchCommand(String name, String phone, String email, String address, String course, String moduleCode) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.course = course;
        this.moduleCode = moduleCode;
    }

    @Override
    public CommandResult execute(StudentDatabase db, Storage storage) throws CommandException {
        // verify the database was successfully passed in
        assert db != null : "StudentDatabase should not be null during SearchCommand execution";

        List<Student> students = db.getAllStudents();

        // verify the database returns a valid list. Even if empty, it shouldn't be null
        assert students != null : "StudentDatabase.getAllStudents() should return a list, not null";

        StringBuilder resultBuilder = new StringBuilder();
        boolean isAnyStudentFound = false;

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);

            if (isStudentMatch(student)) {
                int displayIndex = i + 1; // 1-based index for UI display
                resultBuilder.append(formatStudentDetails(displayIndex, student));
                isAnyStudentFound = true;
            }
        }

        if (!isAnyStudentFound) {
            return new CommandResult("No matching students found.");
        }

        return new CommandResult(resultBuilder.toString().trim(), false);
    }

    @Override
    public CommandResult execute(StudentDatabase db) throws CommandException {
        return this.execute(db, null);
    }

    @Override
    public CommandResult undo(StudentDatabase db) throws CommandException {
        throw new CommandException("Cannot undo search command");
    }

    @Override
    public CommandResult undo(StudentDatabase db, Storage storage) throws CommandException {
        return null;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    // ========================================================================
    // Private Helper Methods
    // ========================================================================


    //Evaluates if a student matches all the provided search criteria.
    private boolean isStudentMatch(Student student) {
        if (!isMatch(student.getName(), this.name)) {
            return false;
        }
        if (!isMatch(student.getPhone(), this.phone)){
            return false;
        }
        if (!isMatch(student.getEmail(), this.email)) {
            return false;
        }
        if (!isMatch(student.getAddress(), this.address)) {
            return false;
        }
        if (!isMatch(student.getCourse(), this.course)) {
            return false;
        }

        if (this.moduleCode != null) {
            return getMatchingModulesString(student) != null;
        }

        return true;
    }

    /**
     * Checks if a student's field matches the search query string.
     * If the query is null, it means the user isn't searching by this field, so it auto-passes.
     */
    private boolean isMatch(String studentField, String query) {
        if (query == null) {
            return true;
        }
        if (studentField == null) {
            return false;
        }
        return studentField.toLowerCase().contains(query.toLowerCase());
    }

    /**
     * Formats the final output string for a matched student, dynamically appending
     * only the fields the user explicitly searched for.
     */
    private String formatStudentDetails(int index, Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append(index).append(". ").append(student.getName());

        if (this.phone != null) {
            sb.append(", Phone: ").append(student.getPhone());
        }
        if (this.email != null) {
            sb.append(", Email: ").append(student.getEmail());
        }
        if (this.address != null) {
            sb.append(", Address: ").append(student.getAddress());
        }
        if (this.course != null) {
            sb.append(", Course: ").append(student.getCourse());
        }

        if (this.moduleCode != null) {
            String matchedModules = getMatchingModulesString(student);
            sb.append(", Modules: [").append(matchedModules).append("]");
        }

        sb.append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Retrieves a formatted string of matching modules (e.g., "CS2113: A | CS2040: B+"),
     * or null if no modules match the search query.
     */
    private String getMatchingModulesString(Student student) {
        StringBuilder mods = new StringBuilder();
        boolean hasMatch = false;

        for (Module m : student.getModules()) {
            if (m.getCode().toLowerCase().contains(this.moduleCode.toLowerCase())) {
                hasMatch = true;
                mods.append(m.getCode())
                    .append(": ")
                    .append(m.getGrade().toString())
                    .append(" | ");
            }
        }

        if (!hasMatch) {
            return null;
        }

        return mods.substring(0, mods.length() - 3);
    }
}
