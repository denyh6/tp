package dextro.model;

public enum Grade {
    A_PLUS("A+", 5.0, true, true),
    A("A", 5.0, true, true),
    A_MINUS("A-", 4.5, true, true),
    B_PLUS("B+", 4.0, true, true),
    B("B", 3.5, true, true),
    B_MINUS("B-", 3.0, true, true),
    C_PLUS("C+", 2.5, true, true),
    C("C", 2.0, true, true),
    D_PLUS("D+", 1.5, true, true),
    D("D", 1.0, true, true),
    F("F", 0.0, true, true),
    SATISFACTORY("S", 0.0, false, true),
    UNSATISFACTORY("U", 0.0, false, false),
    COMPLETED_SATISFACTORY("CS", 0.0, false, true),
    COMPLETED_UNSATISFACTORY("CU", 0.0, false, false),
    INCOMPLETE("IC", 0.0, false, false),
    IN_PROGRESS("IP", 0.0, false, false),
    AUDIT("AUD", 0.0, false, false),
    EXEMPTED("EXE", 0.0, false, false),
    WITHDRAWN("W", 0.0, false, false),
    WITHDRAWAL_FROM_UNIVERSITY("WU", 0.0, false, false),
    DISTINCTION("DT", 5.0, true, true),
    MERIT("M", 4.0, true, true),
    PASS("Pass", 3.0, true, true),
    FAIL("Fail", 0.0, true, true);

    private final String label;
    private final double cap;
    private final boolean countsToGpa;
    private final boolean countsToCompletion;

    Grade(String label, double cap, boolean counts, boolean countsToCompletion) {
        this.label = label;
        this.cap = cap;
        this.countsToGpa = counts;
        this.countsToCompletion = countsToCompletion;
    }

    public double getCap() {
        return cap;
    }

    public boolean getCountsToGpa() {
        return countsToGpa;
    }

    public boolean getCountsToCompletion() {
        return countsToCompletion;
    }

    public static Grade fromString(String input) {
        for (Grade g : Grade.values()) {
            if (g.label.equals(input)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid grade: " + input);
    }

    @Override
    public String toString() {
        return label;
    }
}
