package dextro.parser;

import dextro.exception.ParseException;
import dextro.model.Grade;

public class Validator {
    public static String validateName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name is compulsory for create command. Use \"n/\" prefix");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("Name too long, must be less than 100 chars");
        }

        if (!name.matches("^[A-Za-z ,()/.\\-@']+$")){
            throw new IllegalArgumentException(
                    "Name must contain only alphabets, spaces and special symbols: , ( ) . - / @ '");
        }

        return name.replaceAll("\\s+", " ");
    }

    public static String validatePhone(String phone) throws ParseException {
        if (phone == null || phone.isBlank()) {
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

    public static String validateEmail(String email) throws ParseException {
        if (email == null || email.isBlank()) {
            return null;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
            throw new ParseException("Invalid email format");
        }

        return email.toLowerCase();
    }

    public static String validateAddress(String address) throws ParseException {
        if (address == null || address.isBlank()) {
            return null;
        }

        if (address.length() > 200) {
            throw new ParseException("Address too long, must be less than 200 chars");
        }

        return address.replaceAll("\\s+", " ");
    }

    public static String validateCourse(String course) throws ParseException {
        if (course == null || course.isBlank()) {
            return null;
        }

        if (course.length() > 50) {
            throw new ParseException("Course too long, must be less than 50 chars");
        }

        if (!course.matches("^[A-Za-z ,&()-]+$")) {
            throw new ParseException("Course must contain only alphabets, spaces and special symbols: , & ( ) -");
        }

        return course.replaceAll("\\s+", " ");
    }

    public static String validateModuleCode(String moduleCode) throws ParseException {
        if (!moduleCode.matches("^[A-Z]{2,4}\\d{4}[A-Z0-9]{0,5}$")) {
            throw new ParseException("Invalid module code: " + moduleCode);
        } else {
            return moduleCode;
        }
    }

    public static Grade validateGrade(String grade) throws ParseException {
        if (grade == null) {
            throw new ParseException("Grade not provided");
        }
        return Grade.fromString(grade.toUpperCase());

    }

    public static Integer validateCredits(String credits) throws ParseException {
        int out;
        if (credits == null || credits.isBlank()) {
            return null;
        }
        try {
            out = Integer.parseInt(credits);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid credits value: " +
                    credits +
                    ". Credits must be a positive integer.");
        }
        if (out <= 0) {
            throw new ParseException("Credits must be positive");
        }

        return out;
    }

    public static String[] validateModuleFormat(String input) throws ParseException {
        String[] parts = input.split("/", -1); // -1 to keep trailing empty strings
        if (parts[0].isBlank()) {
            throw new ParseException("Module code cannot be empty (e.g., CS2113/A or CS2113/A/2)");
        }
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new ParseException("Grade cannot be empty (e.g., CS2113/A or CS2113/A/2)");
        }
        if (parts.length == 3 && parts[2].isBlank()) {
            throw new ParseException("Credits cannot be empty if slash is provided (e.g., CS2113/A/2)");
        }
        if (parts.length > 3) {
            throw new ParseException("Module format must be CODE/GRADE[/CREDITS] (e.g., CS2113/A or CS2113/A/2)");
        }

        return parts;
    }

    public static int validateIndex(String raw) throws ParseException {
        int index;
        try {
            index = Integer.parseInt(raw.strip());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid student index: " + raw + ". Index must be an integer");
        }
        if (index <= 0) {
            throw new ParseException("Index must be positive");
        }
        return index;
    }
}
