package dextro.command;

import dextro.app.Storage;
import dextro.exception.CommandException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            // Sort modules by grade (highest to lowest), then by code
            List<Module> sortedModules = student.getModules().stream()
                    .sorted(Comparator.comparing((Module m) -> m.getGrade().getCap()).reversed()
                            .thenComparing(Module::getCode))
                    .collect(Collectors.toList());

            result.append("\nModules and Grades:");
            for (Module module : sortedModules) {
                result.append(String.format("\n  - %s: %s (%d MCs)",
                        module.getCode(),
                        module.getGrade(),
                        module.getCredits()));
            }

            // Add module statistics
            result.append(generateModuleStatistics(student.getModules()));
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

    /**
     * Generates module statistics including grade distribution, highest/lowest grades, and average.
     *
     * @param modules List of modules to analyze
     * @return Formatted string with module statistics
     */
    private String generateModuleStatistics(List<Module> modules) {
        StringBuilder stats = new StringBuilder();
        stats.append("\n\nModule Statistics:");

        // Count modules by grade
        Map<Grade, Integer> gradeCount = new HashMap<>();
        Grade highestGrade = null;
        Grade lowestGrade = null;
        double totalGradePoints = 0.0;
        int gradedModuleCount = 0;

        for (Module module : modules) {
            Grade grade = module.getGrade();
            gradeCount.put(grade, gradeCount.getOrDefault(grade, 0) + 1);

            // Track highest and lowest grades (only for graded modules)
            if (grade.getCountsToGpa()) {
                if (highestGrade == null || grade.getCap() > highestGrade.getCap()) {
                    highestGrade = grade;
                }
                if (lowestGrade == null || grade.getCap() < lowestGrade.getCap()) {
                    lowestGrade = grade;
                }
                totalGradePoints += grade.getCap();
                gradedModuleCount++;
            }
        }

        // Display grade distribution (sorted by grade from highest to lowest)
        stats.append("\n  Grade Distribution: ");
        boolean first = true;
        List<Map.Entry<Grade, Integer>> sortedGrades = gradeCount.entrySet().stream()
                .sorted((e1, e2) -> {
                    // First sort by CAP (descending)
                    int capCompare = Double.compare(e2.getKey().getCap(), e1.getKey().getCap());
                    if (capCompare != 0) {
                        return capCompare;
                    }
                    // If CAP is equal (e.g., A+ and A both 5.0), sort by enum ordinal
                    // This ensures A+ comes before A
                    return Integer.compare(e1.getKey().ordinal(), e2.getKey().ordinal());
                })
                .collect(Collectors.toList());

        for (Map.Entry<Grade, Integer> entry : sortedGrades) {
            if (!first) {
                stats.append(", ");
            }
            int count = entry.getValue();
            String gradeName = entry.getKey().toString();
            stats.append(String.format("%d %s%s", count, gradeName, count > 1 ? "'s" : ""));
            first = false;
        }

        // Display highest and lowest grades
        if (highestGrade != null && lowestGrade != null) {
            stats.append(String.format("\n  Highest Grade: %s (%.1f)", highestGrade, highestGrade.getCap()));
            stats.append(String.format("\n  Lowest Grade: %s (%.1f)", lowestGrade, lowestGrade.getCap()));
        }

        return stats.toString();
    }
}
