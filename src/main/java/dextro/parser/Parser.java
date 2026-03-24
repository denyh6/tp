package dextro.parser;

import dextro.command.Command;
import dextro.command.CreateCommand;
import dextro.command.DeleteCommand;
import dextro.command.ExitCommand;
import dextro.command.ListCommand;
import dextro.command.StatusCommand;
import dextro.command.module.AddCommand;
import dextro.command.module.RemoveCommand;
import dextro.config.Config;
import dextro.exception.ParseException;
import dextro.model.Grade;

public class Parser {

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
        case Config.CMD_EXIT -> new ExitCommand();
        default -> throw new ParseException("Unknown command: " + commandWord);
        };
    }

    private Command parseCreate(String args) throws ParseException {
        ArgumentTokenizer tokenizer = new ArgumentTokenizer(args, "n/", "p/", "e/", "a/", "c/");
        String name = tokenizer.getValue("n/");
        if (name == null || name.isBlank()) {
            throw new ParseException("Name is compulsory for create command");
        }
        String phone = tokenizer.getValue("p/");
        String email = tokenizer.getValue("e/");
        String address = tokenizer.getValue("a/");
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

    private Command parseStatus(String args) throws ParseException {
        try {
            int index = Integer.parseInt(args);
            return new StatusCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid index for status: " + args);
        }
    }
}
