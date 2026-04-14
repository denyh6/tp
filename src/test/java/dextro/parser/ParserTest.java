package dextro.parser;

import dextro.command.CommandHistory;
import dextro.command.CreateCommand;
import dextro.command.DeleteCommand;
import dextro.command.FindCommand;
import dextro.command.HelpCommand;
import dextro.command.ListCommand;
import dextro.command.SortCommand;
import dextro.command.SearchCommand;
import dextro.command.ExitCommand;
import dextro.command.module.AddCommand;
import dextro.command.module.RemoveCommand;
import dextro.exception.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
        parser.setCommandHistory(new CommandHistory());
    }

    // ===== create =====

    @Test
    void parse_createWithName_returnsCreateCommand() throws ParseException {
        assertInstanceOf(CreateCommand.class, parser.parse("create n/John Doe"));
    }

    @Test
    void parse_createWithAllFields_returnsCreateCommand() throws ParseException {
        assertInstanceOf(CreateCommand.class, parser.parse(
                "create n/John Doe p/91234567 e/john@mail.com a/Orchard Road c/CS"));
    }

    @Test
    void parse_createMissingName_throwsParseException() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> parser.parse("create p/91234567"));
    }

    @Test
    void parse_createEmptyArgs_throwsParseException() {
        assertThrows(java.lang.IllegalArgumentException.class, () -> parser.parse("create"));
    }

    @Test
    void parse_createInvalidPhone_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("create n/John p/12345678"));
    }

    @Test
    void parse_createInvalidEmail_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("create n/John e/notanemail"));
    }

    @Test
    void parse_createDuplicatePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("create n/John n/Jane"));
    }

    @Test
    void parse_createNameNormalisedToUppercase() throws ParseException {
        // Parser normalises name, no exception expected
        assertInstanceOf(CreateCommand.class, parser.parse("create n/john doe"));
    }

    // ===== delete =====

    @Test
    void parse_deleteValidIndex_returnsDeleteCommand() throws ParseException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
    }

    @Test
    void parse_deleteNonNumeric_throwsParseException() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("delete abc"));
        assertTrue(e.getMessage().contains("Invalid student index: abc. Index must be an integer"));
    }

    @Test
    void parse_deleteNoArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("delete"));
    }

    @Test
    void parse_deleteCaseInsensitive_returnsDeleteCommand() throws ParseException {
        assertInstanceOf(DeleteCommand.class, parser.parse("DELETE 1"));
    }

    // ===== add =====

    @Test
    void parse_addValidInput_returnsAddCommand() throws ParseException {
        assertInstanceOf(AddCommand.class, parser.parse("add 1 CS2113/A"));
    }

    @Test
    void parse_addWithCredits_returnsAddCommand() throws ParseException {
        assertInstanceOf(AddCommand.class, parser.parse("add 1 CS2113/A/4"));
    }

    @Test
    void parse_addMissingGrade_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add 1 CS2113"));
    }

    @Test
    void parse_addInvalidModuleCode_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add 1 INVALID/A"));
    }

    @Test
    void parse_addInvalidGrade_throwsParseException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("add 1 CS2113/Z"));
    }

    @Test
    void parse_addNonNumericIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add abc CS2113/A"));
    }

    @Test
    void parse_addNoArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add"));
    }

    @Test
    void parse_addZeroCredits_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add 1 CS2113/A/0"));
    }

    @Test
    void parse_addNegativeCredits_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("add 1 CS2113/A/-1"));
    }

    // ===== remove =====

    @Test
    void parse_removeValidInput_returnsRemoveCommand() throws ParseException {
        assertInstanceOf(RemoveCommand.class, parser.parse("remove 1 CS2113"));
    }

    @Test
    void parse_removeLowercaseModuleCode_returnsRemoveCommand() throws ParseException {
        assertInstanceOf(RemoveCommand.class, parser.parse("remove 1 cs2113"));
    }

    @Test
    void parse_removeNonNumericIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("remove abc CS2113"));
    }

    @Test
    void parse_removeMissingModuleCode_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("remove 1"));
    }

    @Test
    void parse_removeNoArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("remove"));
    }

    // ===== list =====

    @Test
    void parse_listNoArgs_returnsListCommand() throws ParseException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
    }

    @Test
    void parse_listWithArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("list something"));
    }

    @Test
    void parse_listCaseInsensitive_returnsListCommand() throws ParseException {
        assertInstanceOf(ListCommand.class, parser.parse("LIST"));
    }

    // ===== find =====

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws ParseException {
        assertInstanceOf(FindCommand.class, parser.parse("find John"));
    }

    @Test
    void parse_findEmptyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_findBlankArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("find   "));
    }

    // ===== search =====

    @Test
    void parse_searchByCourse_returnsSearchCommand() throws ParseException {
        assertInstanceOf(SearchCommand.class, parser.parse("search c/CS"));
    }

    @Test
    void parse_searchByModule_returnsSearchCommand() throws ParseException {
        assertInstanceOf(SearchCommand.class, parser.parse("search m/CS2113"));
    }

    @Test
    void parse_searchMultipleCategories_returnsSearchCommand() throws ParseException {
        // Updated: This should now successfully return a SearchCommand instead of throwing an exception
        assertInstanceOf(SearchCommand.class, parser.parse("search c/CS m/CS2113"));
    }

    @Test
    void parse_searchNoPrefixes_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("search John"));
    }

    @Test
    void parse_searchEmptyArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("search"));
    }

    @Test
    void parse_searchEmptyCourseValue_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("search c/"));
    }

    @Test
    void parse_searchEmptyModuleValue_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("search m/"));
    }

    // ===== sort =====

    @Test
    void parse_sortByName_returnsSortCommand() throws ParseException {
        assertInstanceOf(SortCommand.class, parser.parse("sort name"));
    }

    @Test
    void parse_sortByCourse_returnsSortCommand() throws ParseException {
        assertInstanceOf(SortCommand.class, parser.parse("sort course"));
    }

    @Test
    void parse_sortByCap_returnsSortCommand() throws ParseException {
        assertInstanceOf(SortCommand.class, parser.parse("sort cap"));
    }

    @Test
    void parse_sortByMcs_returnsSortCommand() throws ParseException {
        assertInstanceOf(SortCommand.class, parser.parse("sort mcs"));
    }

    @Test
    void parse_sortInvalidCategory_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("sort invalid"));
    }

    @Test
    void parse_sortNoArgs_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("sort"));
    }

    @Test
    void parse_sortCategoryUppercase_returnsSortCommand() throws ParseException {
        // sort args are lowercased before comparison
        assertInstanceOf(SortCommand.class, parser.parse("sort NAME"));
    }

    // ===== help =====

    @Test
    void parse_help_returnsHelpCommand() throws ParseException {
        assertInstanceOf(HelpCommand.class, parser.parse("help"));
    }

    @Test
    void parse_helpCaseInsensitive_returnsHelpCommand() throws ParseException {
        assertInstanceOf(HelpCommand.class, parser.parse("HELP"));
    }

    // ===== exit =====

    @Test
    void parse_exit_returnsExitCommand() throws ParseException {
        assertInstanceOf(ExitCommand.class, parser.parse("exit"));
    }

    // ===== edit =====

    @Test
    void parse_editWithName_succeeds() throws ParseException {
        parser.parse("edit 1 n/Jane Doe");
    }

    @Test
    void parse_editWithPhone_succeeds() throws ParseException {
        parser.parse("edit 1 p/91234567");
    }

    @Test
    void parse_editWithModule_succeeds() throws ParseException {
        parser.parse("edit 1 m/CS2113/A");
    }

    @Test
    void parse_editWithModuleAndCredits_succeeds() throws ParseException {
        parser.parse("edit 1 m/CS2113/A/4");
    }

    @Test
    void parse_editNoFields_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1"));
    }

    @Test
    void parse_editNoIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit"));
    }

    @Test
    void parse_editNonNumericIndex_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit abc n/Jane"));
    }

    @Test
    void parse_editInvalidModuleFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1 m/CS2113"));
    }

    @Test
    void parse_editInvalidPrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1 z/something"));
    }

    @Test
    void parse_editInvalidPhone_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("edit 1 p/12345678"));
    }

    // ===== general =====

    @Test
    void parse_unknownCommand_throwsParseExceptionWithHelpHint() {
        ParseException e = assertThrows(ParseException.class, () -> parser.parse("blah"));
        assertTrue(e.getMessage().contains("help"));
    }

    @Test
    void parse_emptyInput_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(""));
    }

    @Test
    void parse_blankInput_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("   "));
    }

    @Test
    void parse_commandCaseInsensitive_succeeds() throws ParseException {
        assertInstanceOf(ListCommand.class, parser.parse("LiSt"));
    }
}
