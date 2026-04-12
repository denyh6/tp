package dextro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentTest {

    // ===== Builder =====

    @Test
    void builder_nameOnly_createsStudent() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals("ALICE", s.getName());
    }

    @Test
    void builder_nullName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Student.Builder((String) null));
    }

    @Test
    void builder_blankName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Student.Builder("  "));
    }

    @Test
    void builder_optionalFieldsOmitted_gettersReturnNa() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals("N.A.", s.getPhone());
        assertEquals("N.A.", s.getEmail());
        assertEquals("N.A.", s.getAddress());
        assertEquals("N.A.", s.getCourse());
    }

    @Test
    void builder_allFieldsSet_gettersReturnValues() {
        Student s = new Student.Builder("ALICE")
                .phone("91234567")
                .email("alice@mail.com")
                .address("Orchard Road")
                .course("CS")
                .build();
        assertEquals("91234567", s.getPhone());
        assertEquals("alice@mail.com", s.getEmail());
        assertEquals("Orchard Road", s.getAddress());
        assertEquals("CS", s.getCourse());
    }

    @Test
    void builder_fromExisting_copiesAllFields() {
        Student original = new Student.Builder("ALICE")
                .phone("91234567").email("alice@mail.com").address("Addr").course("CS").build();
        Student copy = new Student.Builder(original).build();
        assertEquals("ALICE", copy.getName());
        assertEquals("91234567", copy.getPhone());
        assertEquals("alice@mail.com", copy.getEmail());
        assertEquals("Addr", copy.getAddress());
        assertEquals("CS", copy.getCourse());
    }

    @Test
    void builderName_blankNameIgnored_keepsPreviousName() {
        Student s = new Student.Builder("ALICE").name("  ").build();
        assertEquals("ALICE", s.getName());
    }

    @Test
    void builderName_nullIgnored_keepsPreviousName() {
        Student s = new Student.Builder("ALICE").name(null).build();
        assertEquals("ALICE", s.getName());
    }

    // ===== toString =====

    @Test
    void toString_allFields_correctFormat() {
        Student s = new Student.Builder("ALICE")
                .phone("91234567").email("alice@mail.com").address("Addr").course("CS").build();
        assertEquals("ALICE/91234567/alice@mail.com/Addr/CS", s.toString());
    }

    @Test
    void toString_missingFields_showsNa() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals("ALICE/N.A./N.A./N.A./N.A.", s.toString());
    }

    // ===== addModule / getModules / removeModule =====

    @Test
    void addModule_singleModule_moduleCountIsOne() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A));
        assertEquals(1, s.getModules().size());
    }

    @Test
    void removeModule_existingModule_returnsTrue() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A));
        assertTrue(s.removeModule("CS2113"));
    }

    @Test
    void removeModule_existingModule_moduleCountDecreases() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A));
        s.removeModule("CS2113");
        assertEquals(0, s.getModules().size());
    }

    @Test
    void removeModule_nonExistentModule_returnsFalse() {
        Student s = new Student.Builder("ALICE").build();
        assertFalse(s.removeModule("CS2113"));
    }

    @Test
    void removeModule_caseInsensitive_removesSuccessfully() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A));
        assertTrue(s.removeModule("cs2113"));
        assertEquals(0, s.getModules().size());
    }

    // ===== calculateCap =====

    @Test
    void calculateCap_noModules_returnsZero() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals(0.0, s.calculateCap());
    }

    @Test
    void calculateCap_singleGradedModule_correct() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 4)); // A = 5.0
        assertEquals(5.0, s.calculateCap(), 0.001);
    }

    @Test
    void calculateCap_satisfactoryGrade_notIncludedInCap() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 4));
        s.addModule(new Module("GEA1000", Grade.SATISFACTORY, 4)); // S not counted
        assertEquals(5.0, s.calculateCap(), 0.001);
    }

    @Test
    void calculateCap_mixedCredits_weightedCorrectly() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 4));     // 5.0 * 4 = 20
        s.addModule(new Module("MA1521", Grade.B, 2));     // 3.5 * 2 = 7
        // total = 27 / 6 = 4.5
        assertEquals(4.5, s.calculateCap(), 0.001);
    }

    @Test
    void calculateCap_allSatisfactory_returnsZero() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.SATISFACTORY, 4));
        assertEquals(0.0, s.calculateCap());
    }

    // ===== getTotalMCs =====

    @Test
    void getTotalMcs_noModules_returnsZero() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals(0, s.getTotalMCs());
    }

    @Test
    void getTotalMcs_gradedModule_counted() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 4));
        assertEquals(4, s.getTotalMCs());
    }

    @Test
    void getTotalMcs_unsatisfactoryGrade_notCounted() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.UNSATISFACTORY, 4));
        assertEquals(0, s.getTotalMCs());
    }

    @Test
    void getTotalMcs_satisfactoryGrade_counted() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.SATISFACTORY, 4));
        assertEquals(4, s.getTotalMCs());
    }

    @Test
    void getTotalMcs_multipleModules_sumCorrect() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 4));
        s.addModule(new Module("MA1521", Grade.B, 4));
        assertEquals(8, s.getTotalMCs());
    }

    // ===== getProgressStatus =====

    @Test
    void getProgressStatus_zeroMcs_justStarted() {
        Student s = new Student.Builder("ALICE").build();
        assertEquals("Just Started", s.getProgressStatus());
    }

    @Test
    void getProgressStatus_fortyMcs_onTrack() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 40));
        assertEquals("On Track", s.getProgressStatus());
    }

    @Test
    void getProgressStatus_eightyMcs_satisfactory() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 80));
        assertEquals("Satisfactory", s.getProgressStatus());
    }

    @Test
    void getProgressStatus_oneTwentyMcs_goodProgress() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 120));
        assertEquals("Good Progress", s.getProgressStatus());
    }

    @Test
    void getProgressStatus_oneSixtyMcs_completed() {
        Student s = new Student.Builder("ALICE").build();
        s.addModule(new Module("CS2113", Grade.A, 160));
        assertEquals("Completed", s.getProgressStatus());
    }
}
