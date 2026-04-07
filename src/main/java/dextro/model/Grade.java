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
    S("S", 0.0, false, true),
    U("U", 0.0, false, false),
    CS("CS", 0.0, false, true),
    CU("CU", 0.0, false, false),
    IC("IC", 0.0, false, false),
    IP("IP", 0.0, false, false),
    AUD("AUD", 0.0, false, false),
    EXE("EXE", 0.0, false, false),
    W("W", 0.0, false, false),
    WU("WU", 0.0, false, false),
    DT("DT", 5.0, true, true),
    M("M", 5.0, true, true),
    PASS("Pass", 5.0, true, true),
    FAIL("Fail", 5.0, true, true);

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

    public String getLabel() {
        return label;
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
