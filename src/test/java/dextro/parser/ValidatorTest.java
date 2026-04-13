package dextro.parser;

import dextro.exception.ParseException;
import dextro.model.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {

    // ===== validateName =====

    @Test
    void validateName_null_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateName(null));
    }

    @Test
    void validateName_blank_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateName("  "));
    }

    @Test
    void validateName_tooLong_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Validator.validateName("A".repeat(101)));
    }

    @Test
    void validateName_invalidChars_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateName("John123"));
    }

    @Test
    void validateName_validName_returnsName() throws Exception {
        assertEquals("JOHN DOE", Validator.validateName("JOHN DOE"));
    }

    @Test
    void validateName_multipleSpaces_collapsedToOne() throws Exception {
        assertEquals("JOHN DOE", Validator.validateName("JOHN  DOE"));
        assertEquals("JOHN DOE", Validator.validateName("JOHN   DOE"));
    }

    @Test
    void validateName_specialCharsAllowed_returnsName() throws Exception {
        assertEquals("O'BRIEN", Validator.validateName("O'BRIEN"));
        assertEquals("ST. JOHN", Validator.validateName("ST. JOHN"));
    }

    // ===== validatePhone =====

    @Test
    void validatePhone_null_returnsNull() throws ParseException {
        assertNull(Validator.validatePhone(null));
    }

    @Test
    void validatePhone_blank_returnsNull() throws ParseException {
        assertNull(Validator.validatePhone("  "));
    }

    @Test
    void validatePhone_validStartingWith8_returnsPhone() throws ParseException {
        assertEquals("81234567", Validator.validatePhone("81234567"));
    }

    @Test
    void validatePhone_validStartingWith9_returnsPhone() throws ParseException {
        assertEquals("91234567", Validator.validatePhone("91234567"));
    }

    @Test
    void validatePhone_startsWithOne_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validatePhone("12345678"));
    }

    @Test
    void validatePhone_tooShort_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validatePhone("9123456"));
    }

    @Test
    void validatePhone_tooLong_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validatePhone("912345678"));
    }

    @Test
    void validatePhone_letters_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validatePhone("9123456a"));
    }

    // ===== validateEmail =====

    @Test
    void validateEmail_null_returnsNull() throws ParseException {
        assertNull(Validator.validateEmail(null));
    }

    @Test
    void validateEmail_blank_returnsNull() throws ParseException {
        assertNull(Validator.validateEmail("  "));
    }

    @Test
    void validateEmail_validEmail_returnsLowercase() throws ParseException {
        assertEquals("john@mail.com", Validator.validateEmail("john@mail.com"));
    }

    @Test
    void validateEmail_noAtSign_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validateEmail("johnmail.com"));
    }

    @Test
    void validateEmail_noDomain_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validateEmail("john@"));
    }

    @Test
    void validateEmail_noDomainDot_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validateEmail("john@mailcom"));
    }

    // ===== validateAddress =====

    @Test
    void validateAddress_null_returnsNull() throws ParseException {
        assertNull(Validator.validateAddress(null));
    }

    @Test
    void validateAddress_blank_returnsNull() throws ParseException {
        assertNull(Validator.validateAddress("  "));
    }

    @Test
    void validateAddress_valid_returnsAddress() throws ParseException {
        assertEquals("Orchard Road", Validator.validateAddress("Orchard Road"));
    }

    @Test
    void validateAddress_tooLong_throwsParseException() {
        assertThrows(ParseException.class,
                () -> Validator.validateAddress("A".repeat(201)));
    }

    @Test
    void validateAddress_multipleSpaces_collapsedToOne() throws ParseException {
        assertEquals("Orchard Road", Validator.validateAddress("Orchard  Road"));
    }

    // ===== validateModuleCode =====

    @Test
    void validateModuleCode_valid_returnsCode() throws ParseException {
        assertEquals("CS2113", Validator.validateModuleCode("CS2113"));
    }

    @Test
    void validateModuleCode_withSuffix_returnsCode() throws ParseException {
        assertEquals("CG1111A", Validator.validateModuleCode("CG1111A"));
    }

    @Test
    void validateModuleCode_tooShortPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validateModuleCode("C2113"));
    }

    @Test
    void validateModuleCode_noNumbers_throwsParseException() {
        assertThrows(ParseException.class, () -> Validator.validateModuleCode("CSMATH"));
    }

    @Test
    void validateModuleCode_lowercase_throwsParseException() {
        // validateModuleCode expects already-uppercased input
        assertThrows(ParseException.class, () -> Validator.validateModuleCode("cs2113"));
    }

    // ===== validateGrade =====

    @Test
    void validateGrade_gradeAPlus_returns() throws ParseException {
        assertEquals(Grade.A_PLUS, Validator.validateGrade("A+"));
    }

    @Test
    void validateGrade_gradeF_returns() throws ParseException {
        assertEquals(Grade.F, Validator.validateGrade("F"));
    }

    @Test
    void validateGrade_gradeS_returnsGrade() throws ParseException {
        assertEquals(Grade.SATISFACTORY, Validator.validateGrade("S"));
    }

    @Test
    void validateGrade_invalid_throwsParseException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateGrade("Z"));
    }

    @Test
    void validateGrade_lowercase_stillWorks() throws ParseException {
        // validateGrade uppercases internally
        assertEquals(Grade.A, Validator.validateGrade("a"));
    }
}
