package dextro.model.record;

import dextro.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StudentDatabaseTest {

    private StudentDatabase db;
    private Student alice;
    private Student bob;

    @BeforeEach
    void setUp() {
        db = new StudentDatabase();
        alice = new Student.Builder(
                "ALICE")
                .phone("91234567")
                .email("alice@mail.com")
                .address("Addr1")
                .course("Computer Science").build();
        bob   = new Student.Builder(
                "BOB")
                .phone("98765432")
                .email("bob@mail.com")
                .address("Addr2")
                .course("Computer Engineering").build();
    }

    // ===== addStudent / getStudent / getStudentCount =====

    @Test
    void addStudent_singleStudent_countIsOne() {
        db.addStudent(alice);
        assertEquals(1, db.getStudentCount());
    }

    @Test
    void addStudent_twoStudents_countIsTwo() {
        db.addStudent(alice);
        db.addStudent(bob);
        assertEquals(2, db.getStudentCount());
    }

    @Test
    void getStudent_validIndex_returnsCorrectStudent() {
        db.addStudent(alice);
        assertEquals("ALICE", db.getStudent(0).getName());
    }

    @Test
    void getStudent_invalidIndex_throwsIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> db.getStudent(0));
    }

    @Test
    void getStudentCount_emptyDb_returnsZero() {
        assertEquals(0, db.getStudentCount());
    }

    // ===== removeStudent =====

    @Test
    void removeStudent_validIndex_returnedStudentCorrect() {
        db.addStudent(alice);
        Student removed = db.removeStudent(0);
        assertEquals("ALICE", removed.getName());
    }

    @Test
    void removeStudent_validIndex_countDecreases() {
        db.addStudent(alice);
        db.addStudent(bob);
        db.removeStudent(0);
        assertEquals(1, db.getStudentCount());
    }

    @Test
    void removeStudent_removesFromMiddle_remainingOrderCorrect() {
        Student charlie = new Student.Builder("CHARLIE").build();
        db.addStudent(alice);
        db.addStudent(bob);
        db.addStudent(charlie);
        db.removeStudent(1);
        assertEquals("ALICE", db.getStudent(0).getName());
        assertEquals("CHARLIE", db.getStudent(1).getName());
    }

    @Test
    void removeStudent_invalidIndex_throwsIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> db.removeStudent(0));
    }

    // ===== updateStudent =====

    @Test
    void updateStudent_replacesStudentAtIndex() {
        db.addStudent(alice);
        Student updated = new Student.Builder("ALICE UPDATED").build();
        db.updateStudent(0, updated);
        assertEquals("ALICE UPDATED", db.getStudent(0).getName());
    }

    @Test
    void updateStudent_countUnchanged() {
        db.addStudent(alice);
        db.updateStudent(0, bob);
        assertEquals(1, db.getStudentCount());
    }

    // ===== getAllStudents =====

    @Test
    void getAllStudents_emptyDb_returnsEmptyList() {
        assertTrue(db.getAllStudents().isEmpty());
    }

    @Test
    void getAllStudents_returnsAllInOrder() {
        db.addStudent(alice);
        db.addStudent(bob);
        List<Student> all = db.getAllStudents();
        assertEquals(2, all.size());
        assertEquals("ALICE", all.get(0).getName());
        assertEquals("BOB", all.get(1).getName());
    }

    // ===== findDuplicateFields =====

    @Test
    void findDuplicateFields_noConflicts_returnsEmptyMap() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", "81234567", "bob@mail.com", "Addr2");
        assertTrue(result.isEmpty());
    }

    @Test
    void findDuplicateFields_nameMatch_returnsNameConflict() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("ALICE", "81234567", "other@mail.com", "Other Addr");
        assertFalse(result.isEmpty());
        assertTrue(result.get(alice).contains("name"));
    }

    @Test
    void findDuplicateFields_phoneMatch_returnsPhoneConflict() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", "91234567", null, null);
        assertTrue(result.get(alice).contains("phone"));
    }

    @Test
    void findDuplicateFields_emailMatch_returnsEmailConflict() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", null, "alice@mail.com", null);
        assertTrue(result.get(alice).contains("email"));
    }

    @Test
    void findDuplicateFields_addressMatch_returnsAddressConflict() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", null, null, "Addr1");
        assertTrue(result.get(alice).contains("address"));
    }

    @Test
    void findDuplicateFields_multipleFieldsMatch_allReported() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("ALICE", "91234567", "alice@mail.com", "Addr1");
        List<String> matched = result.get(alice);
        assertTrue(matched.contains("name"));
        assertTrue(matched.contains("phone"));
        assertTrue(matched.contains("email"));
        assertTrue(matched.contains("address"));
    }

    @Test
    void findDuplicateFields_multipleStudentsConflict_allReturnedInMap() {
        Student charlie = new Student.Builder("CHARLIE").phone("91234567").build();
        db.addStudent(alice);
        db.addStudent(charlie);
        // same phone as both alice and charlie
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", "91234567", null, null);
        assertEquals(2, result.size());
    }

    @Test
    void findDuplicateFields_nullPhone_notChecked() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void findDuplicateFields_blankPhone_notChecked() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", "  ", null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void findDuplicateFields_naPhone_notChecked() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("BOB", "N.A.", null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void findDuplicateFields_nameMatchCaseInsensitive() {
        db.addStudent(alice);
        Map<Student, List<String>> result = db.findDuplicateFields("alice", null, null, null);
        assertFalse(result.isEmpty());
        assertTrue(result.get(alice).contains("name"));
    }

    @Test
    void findDuplicateFields_emptyDb_returnsEmptyMap() {
        Map<Student, List<String>> result = db.findDuplicateFields("ALICE", "91234567", "alice@mail.com", "Addr1");
        assertTrue(result.isEmpty());
    }
}
