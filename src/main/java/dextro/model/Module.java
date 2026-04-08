package dextro.model;

public class Module {
    private final String code;
    private final Grade grade;
    private final int credits;

    public Module(String code, Grade grade, int credits) {
        this.code = code;
        this.grade = grade;
        this.credits = credits;
    }

    public Module(String code, Grade grade) {
        this.code = code;
        this.grade = grade;
        this.credits = 4; //default 4 MCs
    }

    public String getCode() {
        return code;
    }

    public Grade getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return code + "/" + grade + "/" + credits;
    }

    public int getCredits() {
        return credits;
    }
}
