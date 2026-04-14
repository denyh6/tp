package dextro.parser;

import dextro.command.Command;
import dextro.command.CommandHistory;
import dextro.command.CreateCommand;
import dextro.command.DeleteCommand;
import dextro.command.EditCommand;
import dextro.command.ExitCommand;
import dextro.command.FindCommand;
import dextro.command.HelpCommand;
import dextro.command.SearchCommand;
import dextro.command.SortCommand;
import dextro.command.ListCommand;
import dextro.command.StatusCommand;
import dextro.command.UndoCommand;
import dextro.command.module.AddCommand;
import dextro.command.module.RemoveCommand;
import dextro.config.Config;
import dextro.exception.ParseException;
import dextro.model.Grade;

public class Parser {
    private CommandHistory history;

    public void setCommandHistory(CommandHistory history) {
        this.history = history;
    }

    public Command parse(String userInput) throws ParseException {
        if (userInput.isBlank()) {
            throw new ParseException("Input cannot be empty");
        }

        if (userInput.contains("|")) {
            throw new ParseException("Input cannot contain the '|' character.");
        }

        String[] split = userInput.trim().split("\\s+", 2);
        String commandWord = split[0].toLowerCase();
        String arguments = split.length > 1 ? split[1].trim() : "";

        return switch (commandWord) {
        case Config.CMD_CREATE -> parseCreate(arguments);
        case Config.CMD_DELETE -> parseDelete(arguments);
        case Config.CMD_ADD -> parseAdd(arguments);
        case Config.CMD_REMOVE -> parseRemove(arguments);
        case Config.CMD_LIST -> parseList(arguments);
        case Config.CMD_STATUS -> parseStatus(arguments);
        case Config.CMD_UNDO -> parseUndo();
        case Config.CMD_SEARCH -> parseSearch(arguments);
        case Config.CMD_SORT -> parseSort(arguments);
        case Config.CMD_FIND -> parseFind(arguments);
        case Config.CMD_EXIT -> new ExitCommand();
        case Config.CMD_EDIT -> parseEdit(arguments);
        case Config.CMD_HELP -> new HelpCommand();
        default -> throw new ParseException("Unknown command: " + commandWord +
                ". For info on available commands, try \"help\".");
        };
    }

    private Command parseCreate(String args) throws ParseException {
        ArgumentTokenizer tokenizer = new ArgumentTokenizer(args, "n/", "p/", "e/", "a/", "c/");

        String name    = Validator.validateName(Normalizer.normalizeName(tokenizer.getValue("n/")));
        String phone   = Validator.validatePhone(Normalizer.normalizePhone(tokenizer.getValue("p/")));
        String email   = Validator.validateEmail(Normalizer.normalizeEmail(tokenizer.getValue("e/")));
        String address = Validator.validateAddress(Normalizer.normalizeAddress(tokenizer.getValue("a/")));
        String course  = Validator.validateCourse(Normalizer.normalizeCourse(tokenizer.getValue("c/")));

        return new CreateCommand(name, phone, email, address, course);
    }

    private Command parseDelete(String args) throws ParseException {
        if (args.strip().isBlank()) {
            throw new ParseException("Delete requires an integer index");
        }
        String[] tokens = args.strip().split("\\s+", 2);
        if (tokens.length > 1) {
            throw new ParseException("Delete only takes one argument (e.g., delete 3)");
        }
        int index = Validator.validateIndex(tokens[0]);
        return new DeleteCommand(index);
    }

    private Command parseAdd(String args) throws ParseException {
        String[] tokens = args.split("\\s+", 2);

        if (tokens.length < 2) {
            throw new ParseException("Add requires: index + CODE/GRADE[/CREDITS] (e.g., 3 CS2113/A or 3 CS2113/A/2)");
        }

        int index = Validator.validateIndex(tokens[0]);
        String[] parts = Validator.validateModuleFormat(tokens[1]);

        String moduleCode = Validator.validateModuleCode(Normalizer.normalizeModuleCode(parts[0]));
        Grade grade = Validator.validateGrade(Normalizer.normalizeGrade(parts[1]));

        if (parts.length == 2) {
            return new AddCommand(index, moduleCode, grade, null);
        }

        return new AddCommand(index, moduleCode, grade, Validator.validateCredits(parts[2]));
    }

    private Command parseRemove(String args) throws ParseException {
        String[] tokens = args.split("\\s+", 2);
        if (tokens.length < 2) {
            throw new ParseException("Remove requires an index and module code (e.g., 3 CS2113)");
        }
        if (tokens[1].contains(" ")) {
            throw new ParseException("Remove only takes one module code (e.g., remove 1 CS2113)");
        }

        int index = Validator.validateIndex(tokens[0]);
        String moduleCode = Validator.validateModuleCode(Normalizer.normalizeModuleCode(tokens[1]));
        return new RemoveCommand(index, moduleCode);
    }

    private Command parseList(String args) throws ParseException {
        if (args.strip().isBlank()) {
            return new ListCommand();
        } else {
            throw new ParseException("List command does not take arguments");
        }
    }

    private Command parseEdit(String args) throws ParseException {
        if (args == null || args.isEmpty()) {
            throw new ParseException("Index is compulsory for edit command");
        }
        int index;
        try {
            int sepIndex = args.indexOf(" ");
            String indexStr = (sepIndex == -1) ? args : args.substring(0, sepIndex);
            index = Integer.parseInt(indexStr.trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid index to edit.");
        }

        int sepIndex = args.indexOf(" ");
        String attributes = (sepIndex == -1) ? "" : args.substring(sepIndex + 1);

        if (attributes.isEmpty()) {
            throw new ParseException("Edit requires at least one field (e.g., n/John or m/CS2113/A).");
        }

        if (!attributes.matches("^(n/|p/|e/|a/|c/|m/).*")) {
            throw new ParseException(
                    "Invalid formatting. Use prefixes: n/, p/, e/, a/, c/, m/. Example: edit 1 m/CS2113/A");
        }

        ArgumentTokenizer tokenizer = new ArgumentTokenizer(attributes, "n/", "p/", "e/", "a/", "c/", "m/");

        String rawName    = tokenizer.getValue("n/");
        String rawPhone   = tokenizer.getValue("p/");
        String rawEmail   = tokenizer.getValue("e/");
        String rawAddress = tokenizer.getValue("a/");
        String rawCourse  = tokenizer.getValue("c/");
        String moduleValue = tokenizer.getValue("m/");

        String name    = rawName    != null ?
                Validator.validateName(Normalizer.normalizeName(rawName))           : null;
        String phone   = rawPhone   != null ?
                Validator.validatePhone(Normalizer.normalizePhone(rawPhone))      : null;
        String email   = rawEmail   != null ?
                Validator.validateEmail(Normalizer.normalizeEmail(rawEmail))        : null;
        String address = rawAddress != null ?
                Validator.validateAddress(Normalizer.normalizeAddress(rawAddress))  : null;
        String course  = rawCourse  != null ?
                Validator.validateCourse(Normalizer.normalizeCourse(rawCourse))    : null;

        String moduleCode = null;
        Grade grade = null;
        Integer credits = null;
        if (moduleValue != null) {
            String[] parts = Validator.validateModuleFormat(moduleValue);
            moduleCode = Validator.validateModuleCode(parts[0].strip().toUpperCase());
            grade = Validator.validateGrade(parts[1].strip().toUpperCase());

            if (parts.length >= 3) {
                credits = Validator.validateCredits(parts[2].strip());
            }
        }

        return new EditCommand(index - 1, name, phone, email, address, course, moduleCode, grade, credits);
    }

    private Command parseStatus(String args) throws ParseException {
        try {
            int index = Integer.parseInt(args);
            return new StatusCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid index for status: " + args);
        }
    }

    private Command parseSearch(String args) throws ParseException {
        if (args == null || args.isBlank()) {
            throw new ParseException("Search query cannot be empty.");
        }

        ArgumentTokenizer tokenizer = new ArgumentTokenizer(args, "n/", "p/", "e/", "a/", "c/", "m/");
        String name = tokenizer.getValue("n/");
        String phone = tokenizer.getValue("p/");
        String email = tokenizer.getValue("e/");
        String address = tokenizer.getValue("a/");
        String course = tokenizer.getValue("c/");
        String module = tokenizer.getValue("m/");

        // Count how many categories the user is trying to search by
        int count = 0;
        if (name != null) {
            count++;
        }
        if (phone != null) {
            count++;
        }
        if (email != null) {
            count++;
        }
        if (address != null) {
            count++;
        }
        if (course != null) {
            count++;
        }
        if (module != null) {
            count++;
        }

        /*if (count > 1) {
            throw new ParseException("Cannot search by multiple categories at the same time.");
        }*/

        if (count == 0) {
            throw new ParseException("I'm sorry, I think you meant to use the find function? " +
                    "The search function only works if you input a valid prefix " +
                    "(e.g., n/John, p/8123, e/nus, a/clementi, c/CS, m/CS2113).");
        }

        // Prevent empty prefix searches like "search n/"
        if ((name != null && name.isBlank()) ||
            (phone != null && phone.isBlank()) ||
            (email != null && email.isBlank()) ||
            (address != null && address.isBlank()) ||
            (course != null && course.isBlank()) ||
            (module != null && module.isBlank())) {
            throw new ParseException("Search query cannot be empty." +
                    "The search function only works if you input a valid prefix" +
                    "(e.g., n/John, p/8123, e/nus, a/clementi, c/CS, m/CS2113).");
        }

        return new SearchCommand(
                name != null ? name.strip() : null,
                phone != null ? phone.strip() : null,
                email != null ? email.strip() : null,
                address != null ? address.strip() : null,
                course != null ? course.strip() : null,
                module != null ? module.strip() : null
        );
    }

    private Command parseSort(String args) throws ParseException {
        if (args == null || args.isBlank()) {
            throw new ParseException("Sort category cannot be empty. Usage: sort [name/course/cap/mcs]");
        }

        String category = args.trim().toLowerCase();
        if (!category.equals("name") && !category.equals("course")
                && !category.equals("cap") && !category.equals("mcs")) {
            throw new ParseException("Invalid category. Available categories: name, course, cap, mcs");
        }

        return new SortCommand(category);
    }

    private Command parseFind(String args) throws ParseException {
        if (args == null || args.isBlank()) {
            throw new ParseException("Find query cannot be empty.");
        }
        return new FindCommand(args.strip());
    }

    private Command parseUndo() throws ParseException {
        if (history == null) {
            throw new ParseException("Command history not initialized");
        }
        return new UndoCommand(history);
    }
}
