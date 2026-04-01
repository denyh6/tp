package dextro.parser;

import dextro.command.Command;
import dextro.command.CommandHistory;
import dextro.command.CreateCommand;
import dextro.command.DeleteCommand;
import dextro.command.EditCommand;
import dextro.command.ExitCommand;
import dextro.command.SearchCommand;
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

        String[] split = userInput.trim().split("\\s+", 2);
        String commandWord = split[0].toLowerCase();
        String arguments = split.length > 1 ? split[1].trim() : "";

        return switch (commandWord) {
        case Config.CMD_CREATE -> parseCreate(arguments);
        case Config.CMD_DELETE -> parseDelete(arguments);
        case Config.CMD_ADD -> parseAdd(arguments);
        case Config.CMD_REMOVE -> parseRemove(arguments);
        case Config.CMD_LIST -> new ListCommand();
        case Config.CMD_STATUS -> parseStatus(arguments);
        case Config.CMD_UNDO -> parseUndo();
        case Config.CMD_SEARCH -> parseSearch(arguments);
        case Config.CMD_EXIT -> new ExitCommand();
        case Config.CMD_EDIT -> parseEdit(arguments);
        default -> throw new ParseException("Unknown command: " + commandWord);
        };
    }

    private String validateName(String name) throws ParseException {
        if (name == null || name.isBlank()) {
            throw new ParseException("Name is compulsory for create command");
        }

        if (name.length() > 100) {
            throw new ParseException("Name too long, must be less than 100 chars");
        }

        return name;
    }

    private String validatePhone(String phone) throws ParseException {
        if (phone == null) {
            return null;
        }

        if (!phone.matches("\\d{8}")) {
            throw new ParseException("Phone number must be 8 numerical digits");
        }

        int firstDigit = phone.charAt(0) - '0';
        if (firstDigit < 8 || firstDigit > 9) {
            throw new ParseException("Phone number is not a valid Singapore mobile number");
        }

        return phone;
    }

    private String validateEmail(String email) throws ParseException {
        if (email == null) {
            return null;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
            throw new ParseException("Invalid email format");
        }

        return email;
    }

    private String validateAddress(String address) throws ParseException {
        if (address == null) {
            return null;
        }

        if (address.length() > 200) {
            throw new ParseException("Address too long, must be less than 200 chars");
        }

        return address;
    }
    private Command parseCreate(String args) throws ParseException {
        ArgumentTokenizer tokenizer = new ArgumentTokenizer(args, "n/", "p/", "e/", "a/", "c/");

        String name = validateName(tokenizer.getValue("n/"));
        String phone = validatePhone(tokenizer.getValue("p/"));
        String email = validateEmail(tokenizer.getValue("e/"));

        String address = validateAddress(tokenizer.getValue("a/"));
        String course = tokenizer.getValue("c/");

        return new CreateCommand(name, phone, email, address, course);
    }

    private Command parseDelete(String args) throws ParseException {
        try {
            int index = Integer.parseInt(args);
            return new DeleteCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid index for delete: " + args);
        }
    }

    private Command parseAdd(String args) throws ParseException {
        String[] tokens = args.split("\\s+", 2);

        if (tokens.length < 2) {
            throw new ParseException(
                    "Add requires: index + CODE/GRADE (e.g., 3 CS2113/A)"
            );
        }

        // Parse index
        int index;
        try {
            index = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid student index: " + tokens[0]);
        }

        // Parse module/grade
        String moduleGrade = tokens[1];

        if (!moduleGrade.contains("/")) {
            throw new ParseException(
                    "Module format must be CODE/GRADE (e.g., CS2113/A)"
            );
        }

        String[] parts = moduleGrade.split("/");

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new ParseException(
                    "Module format must be CODE/GRADE (e.g., CS2113/A)"
            );
        }

        String moduleCode = parts[0];
        Grade grade;

        try {
            grade = Grade.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Invalid grade: " + parts[1]);
        }

        return new AddCommand(index, moduleCode, grade);
    }

    private Command parseRemove(String args) throws ParseException {
        String[] tokens = args.split("\\s+", 2);
        if (tokens.length < 2) {
            throw new ParseException("Remove requires an index and module code (e.g., 3 CS2113)");
        }
        int index;
        try {
            index = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid student index: " + tokens[0]);
        }
        String moduleCode = tokens[1]; // e.g., CS2113
        return new RemoveCommand(index, moduleCode);
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

        ArgumentTokenizer tokenizer = new ArgumentTokenizer(attributes, "n/", "p/", "e/", "a/", "c/", "m/");
        String name = tokenizer.getValue("n/");
        String phone = tokenizer.getValue("p/");
        String email = tokenizer.getValue("e/");
        String address = tokenizer.getValue("a/");
        String course = tokenizer.getValue("c/");
        String moduleValue = tokenizer.getValue("m/");

        // parse moduleCode and grade out of "CODE/GRADE"
        String moduleCode = null;
        Grade grade = null;
        if (moduleValue != null) {
            String[] parts = moduleValue.split("/", 2);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new ParseException("Module format must be CODE/GRADE (e.g., m/CS2113/A)");
            }
            moduleCode = parts[0].trim();
            try {
                grade = Grade.fromString(parts[1].trim());
            } catch (IllegalArgumentException e) {
                throw new ParseException("Invalid grade: " + parts[1]);
            }
        }

        return new EditCommand(index - 1, name, phone, email, address, course, moduleCode, grade);
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

        ArgumentTokenizer tokenizer = new ArgumentTokenizer(args, "c/", "m/");
        String course = tokenizer.getValue("c/");
        String module = tokenizer.getValue("m/");

        if (course != null && module != null) {
            throw new ParseException("Cannot search by both course and module at the same time.");
        }

        if (course != null) {
            if (course.isBlank()) {
                throw new ParseException("Course search query cannot be empty.");
            }
            return new SearchCommand(null, course, null);
        } else if (module != null) {
            if (module.isBlank()) {
                throw new ParseException("Module search query cannot be empty.");
            }
            return new SearchCommand(null, null, module);
        } else {
            // If neither prefix is found, treat the entire argument as a general keyword search
            return new SearchCommand(args.trim(), null, null);
        }
    }

    private Command parseUndo() throws ParseException {
        if (history == null) {
            throw new ParseException("Command history not initialized");
        }
        return new UndoCommand(history);
    }
}
