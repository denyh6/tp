package dextro.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dextro.exception.StorageException;
import dextro.model.Grade;
import dextro.model.Module;
import dextro.model.Student;
import dextro.model.record.StudentDatabase;

/**
 * Represents the file used to store address book data.
 */
public class Storage {

    /**
     * Default file path used if the user doesn't provide the file name.
     */
    public static final String DEFAULT_STORAGE_FOLDERPATH = "./data";
    public static final String DEFAULT_STORAGE_FILEPATH = "./data/DextroStudentList.txt";
    public final String filePath;

    /**
     * Constructs a Storage instance of the specified file.
     *
     * @param filePath File path of the wing.txt file.
     * @throws StorageException if the given file path is invalid
     */
    public Storage(String filePath) throws StorageException {
        if (!isValidPath(filePath)) {
            throw new StorageException("wing.storage.Storage file should end with '.txt'");
        }

        this.filePath = DEFAULT_STORAGE_FILEPATH;
    }

    /**
     * Returns true if the given path is acceptable as a storage file.
     * The file path is considered acceptable if it ends with '.txt'
     *
     * @param filePath File path of wing.txt.
     * @return true, if filePath ends with ".txt".
     */
    private static boolean isValidPath(String filePath) {
        return filePath.endsWith(".txt");
    }

    /**
     * Saves the {@code students} data to the storage file.
     *
     * @param db current StudentList to be saved to wing.txt.
     * @throws StorageException if there are errors converting and/or storing data to file.
     */
    public void saveStudentList(StudentDatabase db) throws StorageException {
        try {
            FileWriter fw = new FileWriter(DEFAULT_STORAGE_FILEPATH);
            for (Student student : db.getAllStudents()) {
                fw.write(student.toString());
                // name + "/" + getPhone() + "/" + getEmail() + "/" + getAddress() + "/" + getCourse();
                fw.write("/");
                fw.write(student.getModules().toString());
                // [code + "/" + grade, code + "/" + grade, ...]
                fw.write(System.lineSeparator());
            }
            fw.close();
        } catch (IOException ioe) {
            throw new StorageException("Error writing to file: " + filePath);
        }
    }

    /**
     * Loads the {@code Storage} data from this storage file, and then returns it as an ArrayList of Students.
     * Returns an empty {@code ArrayList} if the file or directory folder does not exist.
     *
     * @return ArrayList of Students extracted from wing.txt.
     * @throws IOException if there were errors reading and/or converting data from file, or creating it.
     */
    public ArrayList<Student> loadStudentList() throws IOException {
        File dir = new File(DEFAULT_STORAGE_FOLDERPATH);
        if (!dir.exists()) {
            boolean isFolderCreated = dir.mkdir();
            if (!isFolderCreated) {
                throw new IOException("Unable to create folder: " + DEFAULT_STORAGE_FOLDERPATH);
            }
        }

        File file = new File(DEFAULT_STORAGE_FILEPATH);
        if (!file.exists()) {
            boolean isFileCreated = file.createNewFile();
            if (!isFileCreated) {
                throw new IOException("Unable to create file: " + DEFAULT_STORAGE_FILEPATH);
            }
            return new ArrayList<>();
        }

        ArrayList<Student> loadedStudentList = new ArrayList<>();
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
            Student loadedStudent = decodeLineToStudent(s.nextLine());
            loadedStudentList.add(loadedStudent);
        }
        return loadedStudentList;
    }

    private static Student decodeLineToStudent(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Input line is null or blank.");
        }

        int openIndex = line.indexOf('[');
        int closeIndex = line.lastIndexOf(']');

        if (openIndex == -1 || closeIndex < openIndex) {
            throw new IllegalArgumentException(
                    "Module block [...] is missing or malformed. Input: " + line);
        }

        String prefix = line.substring(0, openIndex - 1);
        String moduleBlock = line.substring(openIndex + 1, closeIndex);

        String[] fields = prefix.split("/", 5);
        if (fields.length != 5) {
            throw new IllegalArgumentException(
                    "Expected 5 fields (name/phone/email/address/course), found "
                            + fields.length + " in DextroStudentList.txt file line. Input: " + prefix);
        }

        String name = nullOrTrimmed(fields[0]);
        String phone = nullOrTrimmed(fields[1]);
        String email = nullOrTrimmed(fields[2]);
        String address = nullOrTrimmed(fields[3]);
        String course = nullOrTrimmed(fields[4]);

        Student studentToBeAdded = new Student.Builder(name)
                .phone(phone)
                .email(email)
                .address(address)
                .course(course)
                .build();

        List<Module> modules = parseModules(moduleBlock);
        for (Module module : modules) {
            studentToBeAdded.addModule(module);
        }
        return studentToBeAdded;
    }

    private static List<Module> parseModules(String moduleBlock) {
        List<Module> modules = new ArrayList<>();

        if (moduleBlock.isBlank()) {
            return modules;
        }

        for (String entry : moduleBlock.split(",")) {
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("/");
            if (parts.length < 2 || parts.length > 3) {
                throw new IllegalArgumentException(
                        "Invalid module entry (expected code/grade or code/grade/credits): " + trimmed);
            }

            String code = parts[0].trim();
            Grade grade = Grade.fromString(parts[1].trim());

            if (parts.length == 3) {
                try {
                    int credits = Integer.parseInt(parts[2].trim());
                    modules.add(new Module(code, grade, credits));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid credits value in module entry: " + trimmed);
                }
            } else {
                modules.add(new Module(code, grade)); // defaults to 4
            }
        }

        return modules;
    }

    private static String nullOrTrimmed(String field) {
        String trimmed = field.trim();
        return (trimmed.isEmpty() || trimmed.equals("null")) ? null : trimmed;
    }

}
