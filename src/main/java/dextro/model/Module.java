package dextro.model;

public class Module {
    private final String code;
    private final Grade grade;

    public Module(String code, Grade grade) {
        this.code = code;
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public Grade getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return code + "/" + grade; // uses enum toString()
    }
}
