# Developer Guide

## Acknowledgements
SE-EDU Addressbook 3 Developer Guide: https://se-education.org/addressbook-level3/DeveloperGuide.html

## Design & implementation

### Design:


![ArchitectureDiagram](images/ArchitectureDiagram.png)

The **Architecture Diagram** above explains the high-level design of the application.

**Arrow notation:**
- **Solid arrows (`-->`)** represent structural dependencies (one component uses or depends on another).
- **Dashed arrows (`..>`)** represent instantiation or creation (one component creates instances of another).

Given below is a quick overview of the main components and how they interact with each other.

**Main components of the architecture**

`Main` is the entry point of the application. It is responsible for initialising all components in the correct sequence and connecting them to each other at launch. At shutdown, it terminates the application by exiting the `App` run loop.

The bulk of the app's work is done by the following five components:

- `UI` : Handles all console input and output.
- `App` : Orchestrates the main run loop, coordinating all other components.
- `Parser` : Interprets raw user input into executable `Command` objects.
- `Model` : Holds the student data in memory (`StudentDatabase`, `Student`, `Module`, `Grade`).
- `Storage` : Reads data from, and writes data to, the hard disk.

`Config` holds application-wide constants (command keywords) used across components.

**How the architecture components interact with each other**

The sequence diagram below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

![ArchitectureSequence](images/ArchitectureSequence.png)
 
---

#### UI Component

The `Ui` class handles all console input and output. It reads user commands from standard input via a `Scanner` and displays messages surrounded by separator lines.

![UiClassDiagram](images/UiClassDiagram.png)

The `UI` component:
- is instantiated once by `Main` and passed into `App`.
- exposes `readCommand()` (instance method) which `App` calls each loop iteration to obtain the next line of input using the instance's `Scanner`.
- exposes the static methods `Ui.show(String)` and `Ui.line()` to allow display of command results and error messages from anywhere in the application without requiring a `Ui` reference.
- separates concerns: input reading requires state (Scanner) and uses instance methods; output display is stateless and uses static methods for convenience.

---

#### App Component

The `App` component is responsible for continuously running the program until a flag to stop is received from a `CommandResult`. It coordinates all major components — `Parser`, `Ui`, `StudentDatabase`, `Storage`, and `CommandHistory` — within its main loop.

![AppClassDiagram](images/AppClassDiagram.png)

The `App` component:
- depends on `Parser` to parse user input into `Command` objects.
- calls `Command.execute(db, storage)` and receives a `CommandResult`.
- uses `Ui` to read input and display output.
- uses `CommandHistory` to track undoable commands, pushing each onto the stack after successful execution.
- terminates the loop when `CommandResult.shouldExit()` returns `true`.
- catches `ParseException` and `CommandException` in its main loop and displays the error message via `Ui.show()`.

---

#### Parser Component

The `Parser` component is responsible for interpreting raw user input into executable `Command` objects. It splits input into a command keyword and arguments, delegates argument tokenisation to `ArgumentTokenizer`, validates field values, and constructs the appropriate `Command`.

![ParserClassDiagram](images/ParserClassDiagram.png)

The `Parser` component:
- uses `ArgumentTokenizer` to tokenise argument strings into key-value maps for commands that take named fields (e.g. `n/`, `p/`, `e/`).
- uses constants from `Config` in a switch expression to route the command keyword to the correct private `parseX()` method.
- holds a reference to `CommandHistory` (injected by `App`) which it passes into `UndoCommand`.
- throws `ParseException` when the input is malformed or a field fails validation.

`ArgumentTokenizer` scans a raw argument string for recognised prefixes and extracts the substring between each prefix and the next as its value. It throws `ParseException` if a duplicate prefix is detected.
 
---

#### Model Component

The Model component holds all student data in memory.

![StudentClassDiagram](images/StudentClassDiagram.png)

The `Model` component:
- uses `StudentDatabase` to hold the in-memory `List<Student>` and expose CRUD operations (`addStudent`, `removeStudent`, `getStudent`, `getAllStudents`).
- represents each student as a `Student` object, constructed via the inner `Student.Builder` class which enforces that `name` is always provided. Each `Student` holds personal fields (`name`, `phone`, `email`, `address`, `course`) and a `List<Module>`.
- represents each enrolled module as a `Module` object carrying a module code, a `Grade`, and a credit count (defaulting to 4 MCs).
- uses the `Grade` enum to represent all possible grade values, where each constant carries a grade-point value and flags for GPA and completion counting, used by `Student.calculateCap()` and `Student.getTotalMCs()`.

The `Model` component does not depend on `Storage` or `Parser` — it represents pure data and business logic.
 
---

#### Storage Component

The `Storage` component handles reading from and writing to the flat-file database at `./data/DextroStudentList.txt`.

The `Storage` component:
- saves the full `StudentDatabase` to disk after every mutating command, serialising each `Student` and its `Module` list as a delimited string.
- loads the student list at startup, parsing each line back into `Student` and `Module` objects. If the file or directory does not exist, it creates them and returns an empty list.
- throws `StorageException` on file I/O errors, which `Main` catches and handles by starting with an empty `StudentDatabase`.

---

#### Common Classes

The following classes are used across multiple components:

- `Command` — interface defining `execute(StudentDatabase, Storage)`, `undo(StudentDatabase, Storage)`, and `isUndoable()`, implemented by all command classes.
- `CommandResult` — encapsulates the outcome of a command execution, carrying the display message and an optional exit flag read by `App`.
- `CommandException` — unchecked exception thrown by command implementations when execution cannot proceed (e.g. out-of-bounds index).
- `ParseException` — unchecked exception thrown by `Parser` and `ArgumentTokenizer` when input is malformed.
- `Config` — `final` utility class with only static String constants for command keywords (e.g. `CMD_CREATE`, `CMD_DELETE`), referenced by `Parser`.
 
---

### Implementation: Kai Jie

#### Create Command

The `CreateCommand` allows users to create a new student record with a required name and optional phone, email, address, and course fields. It supports undo by storing the index of the created student and removing it on `undo()`.

##### Class Diagram

![CreateCommandClassDiagram](images/CreateCommandClassDiagram.png)

The class diagram shows the relationship between `CreateCommand` and other components:
- `CreateCommand` implements the `Command` interface.
- It uses `Student.Builder` to construct a `Student` and `StudentDatabase.addStudent()` to persist it.
- It calls `Storage.saveStudentList()` to write changes to disk.
- It returns a `CommandResult` containing a confirmation message with the student's name.

##### Sequence Diagram

![CreateCommandSequence](images/CreateCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User inputs the create command with arguments (e.g., `create n/Alice p/91234567 e/alice@u.nus.edu c/CS`).
2. `Parser.parse()` identifies the `create` keyword and delegates to `Parser.parseCreate()`.
3. `Parser.parseCreate()` constructs an `ArgumentTokenizer` with prefixes `n/`, `p/`, `e/`, `a/`, `c/` and calls `validateName()`, `validatePhone()`, `validateEmail()`, `validateAddress()`.
4. A `CreateCommand` is constructed with the validated fields.
5. `App` calls `CreateCommand.execute(db, storage)`.
6. `CreateCommand` builds a `Student` via `Student.Builder`, calls `db.addStudent(student)`, then `storage.saveStudentList(db)`.
7. A `CommandResult` is returned with the message `"Student created: <name>"`.

---

#### Delete Command

The `DeleteCommand` allows users to remove an existing student record by 1-based index. It supports undo by storing the deleted `Student` and its original index for reinsertion.

##### Class Diagram

![DeleteCommandClassDiagram](images/DeleteCommandClassDiagram.png)

The class diagram shows the relationship between `DeleteCommand` and other components:
- `DeleteCommand` implements the `Command` interface.
- It calls `StudentDatabase.removeStudent(index)` and stores the returned `Student` for potential undo.
- It calls `Storage.saveStudentList()` to persist the change.
- It throws `CommandException` if the index is out of bounds.
- It returns a `CommandResult` containing the deleted student's details.

##### Sequence Diagram

![DeleteCommandSequence](images/DeleteCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User inputs the delete command (e.g., `delete 1`).
2. `Parser.parse()` identifies the `delete` keyword and calls `Parser.parseDelete()`, which parses the integer index.
3. A `DeleteCommand` is constructed with the index.
4. `App` calls `DeleteCommand.execute(db, storage)`.
5. If the index is out of bounds, `CommandException` is thrown and `App` displays the error.
6. Otherwise, `db.removeStudent(index - 1)` is called, the returned `Student` is stored, `storage.saveStudentList(db)` is called, and a `CommandResult` with the student's details is returned.

---

#### List Command

The `ListCommand` retrieves and displays all student records currently held in `StudentDatabase`. It is not undoable as it performs no mutations.

##### Class Diagram

![ListCommandClassDiagram](images/ListCommandClassDiagram.png)

The class diagram shows the relationship between `ListCommand` and other components:
- `ListCommand` implements the `Command` interface.
- It calls `StudentDatabase.getAllStudents()` to retrieve the full student list.
- It formats each student using `Student.toString()` with a 1-based index prefix.
- It returns a `CommandResult` with the formatted list, or a `"No students found."` message if the list is empty.
- `isUndoable()` returns `false`; `undo()` throws `CommandException`.

##### Sequence Diagram

![ListCommandSequence](images/ListCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User inputs `list`.
2. `Parser.parse()` matches the `list` keyword and returns `new ListCommand()` directly (no argument parsing required).
3. `App` calls `ListCommand.execute(db, storage)`.
4. `ListCommand` calls `db.getAllStudents()` and checks if the list is empty.
5. If empty, a `CommandResult("No students found.")` is returned.
6. Otherwise, `ListCommand` iterates over all students, calls `student.toString()`, and builds a numbered output string.
7. A `CommandResult` containing the formatted list is returned and displayed via `Ui`.

---

#### Add Command

The `AddCommand` adds a `Module` to an existing student's module list, identified by 1-based index. The module is specified as `CODE/GRADE` or `CODE/GRADE/CREDITS`. It supports undo by removing the added module.

##### Class Diagram

![AddCommandClassDiagram](images/AddCommandClassDiagram.png)

The class diagram shows the relationship between `AddCommand` and other components:
- `AddCommand` implements the `Command` interface, residing in the `dextro.command.module` subpackage.
- It retrieves the target `Student` from `StudentDatabase` by index.
- It constructs a `Module` with the given code, `Grade`, and optional credit count.
- It calls `student.addModule(module)` and `storage.saveStudentList(db)`.
- `undo()` calls `student.removeModule(moduleCode)` to reverse the addition.

##### Sequence Diagram

![AddCommandSequence](images/AddCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User inputs the add command (e.g., `add 1 CS2113/A`).
2. `Parser.parse()` identifies the `add` keyword and calls `Parser.parseAdd()`.
3. `Parser.parseAdd()` splits the index from the module string, validates the module code via `validateModuleCode()` and the grade via `validateGrade()` (which calls `Grade.fromString()`).
4. An `AddCommand` is constructed with the index, module code, `Grade`, and optional credits.
5. `App` calls `AddCommand.execute(db, storage)`.
6. If the index is invalid, a `CommandResult("Invalid student index")` is returned immediately.
7. Otherwise, the student is retrieved, a `Module` is constructed, `student.addModule(module)` is called, `storage.saveStudentList(db)` is called, and a `CommandResult` confirming the addition is returned.

---

#### Remove Command

The `RemoveCommand` removes a named `Module` from an existing student's module list, identified by 1-based index. It supports undo by storing the removed `Module` reference for reinsertion.

##### Class Diagram

![RemoveCommandClassDiagram](images/RemoveCommandClassDiagram.png)

The class diagram shows the relationship between `RemoveCommand` and other components:
- `RemoveCommand` implements the `Command` interface, residing in the `dextro.command.module` subpackage.
- It retrieves the target `Student` from `StudentDatabase` by index.
- It iterates over `student.getModules()` to locate and store a reference to the module before removal (for undo support).
- It calls `student.removeModule(moduleCode)` and `storage.saveStudentList(db)`.
- `undo()` calls `student.addModule(removedModule)` to reinsert the saved module.
- `CommandException` is thrown by `undo()` if the command was never executed or the module was not found.

##### Sequence Diagram

![RemoveCommandSequence](images/RemoveCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User inputs the remove command (e.g., `remove 1 CS2113`).
2. `Parser.parse()` identifies the `remove` keyword and calls `Parser.parseRemove()`, which splits the index and module code.
3. A `RemoveCommand` is constructed with the index and module code (uppercased).
4. `App` calls `RemoveCommand.execute(db, storage)`.
5. If the index is invalid, a `CommandResult("Invalid student index")` is returned immediately.
6. Otherwise, the student is retrieved and `student.getModules()` is iterated to find and store the matching `Module`.
7. `student.removeModule(moduleCode)` is called; if successful, `storage.saveStudentList(db)` is called and a confirmation `CommandResult` is returned.
8. If the module code is not found, a `CommandResult` indicating the module was not found is returned.

---

### Implementation: Matthias

#### Status Command

The `StatusCommand` allows users to view detailed information about a specific student, including their CAP, total MCs completed, and progress status.

##### Class Diagram

![StatusCommandClassDiagram](images/StatusCommandClassDiagram.png)

The class diagram shows the relationship between `StatusCommand` and other components:
- `StatusCommand` implements the `Command` interface
- It uses with `StudentDatabase` to retrieve student information, and Storage to save the changes.
- Returns a `CommandResult` containing the status information

##### Sequence Diagram

![StatusCommandSequence](images/StatusCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User executes the status command with a student index
2. `StatusCommand.execute()` is called with the `StudentDatabase` and `Storage`
3. The command validates the index and retrieves the student
4. Student information (CAP, MCs, status) is calculated and formatted
5. A `CommandResult` is returned with the formatted status message

#### Undo Command

The `UndoCommand` allows users to revert the last undoable command that modified the student database.

##### Class Diagram

![UndoCommandClassDiagram](images/UndoCommandClassDiagram.png)

The class diagram shows the relationship between `UndoCommand` and other components:
- `UndoCommand` implements the `Command` interface
- It maintains a reference to `CommandHistory` which tracks executed commands
- It interacts with `StudentDatabase` and `Storage` to perform the undo operation
- Returns a `CommandResult` indicating the undo operation result

##### Sequence Diagram

![UndoCommandSequence](images/UndoCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User executes the undo command
2. `UndoCommand.execute()` is called with the `StudentDatabase` and `Storage`
3. The command checks if there are any commands to undo in the `CommandHistory`
4. If available, the last command is popped from the history
5. The `undo()` method of the last command is invoked
6. A `CommandResult` is returned indicating the success or failure of the undo operation

---

### Implementation: Wen Yuan

#### Edit Command

The `EditCommand` allows users to alter attributes of the student records.

##### Class Diagram

![EditCommandClassDiagram](images/EditCommandClass.png)

The class diagram shows the relationship between `EditCommand` and other components:
- `EditCommand` implements the `Command` interface
- `EditCommand` needs references from Storage and Student class, and has an aggregation relationship with StudentDatabase. 
- It interacts with `StudentDatabase` to retrieve/manipulate student information
- It uses the `Storage` component for persistence operations
- Returns a `CommandResult` containing the status information
- All methods listed in `Command` is implemented in `EditCommand` class as there is no further child class from `EditCommand`.

##### Sequence Diagram

![StatusCommandSequence](images/EditCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User executes the edit command with attribute flags(e.g n/ for name and a/ for address)
2. `Parser` class parse user input and determine which eactly which record is of concern and which attribute needs to be edited based on the flags
3. `EditCommand` class constructs an object with all attributes attach to it.
4. The exact `Student` object is extracted out of `StudentDatabase`
5. The `Student` object is then rebuilt with updated attributes.
6. If module need to updated, `EditCommand` will search through the list of modules in `Student`. If Module does not exists, it will add the module in the `Student`.
7. `saveStudentList()` is called to save all updates in the database.

#### Find Command

The `FindCommand` allows users to find user-defined keyword and pinpoint to student record containing that keyword. More than one record can be queried if there is a common keyword.

##### Class Diagram

![FindCommandClassDiagram](images/FindCommandClass.png)

The class diagram shows the relationship between `FindCommand` and other components:
- Similar to `EditCommand`, `FindCommand` also implements the `Command` interface
- `FindCommand` needs to reference of `Storage` and `StudentDatabase`. 
- `FindCommand` compose of `CommandException`. 
- It interacts with `StudentDatabase` to retrieve relevant student records.
- Although `Storage` is not used in execute method under `FindCommand`, the `Command` require it to be there. So the method signature must match `Command`.
- All methods listed in `Command` is implemented in `FindCommand` class as there is no further child class from `FindCommand`.

##### Sequence Diagram

![StatusCommandSequence](images/EditCommandSequence.png)

The sequence diagram illustrates the execution flow:
1. User executes the find command with string that he/she wants to find.
2. `Parser` class parse user input and determine the command(find) and the keyword(string).
3. `FindCommand` then retrieve all student information from `StudentDatabase` .
4. A loop will check if any of the student information correspond to the keyword defined by the user.
5. The `CommandResult` will display if the student records are found.

---

### Implementation: Agara

##### Sort Command

The `SortCommand` allows users to view a temporary list of all users sorted by certain categories selected by the user, such as name, course, and mcs.

##### Class Diagram

![SortCommandClassDiagram](images/SortCommandClass.png)

The class diagram shows the relationship between `SortCommand` and other components:
- `SortCommand` implements the `Command` interface
- `SortCommand` needs to reference of `Storage` and `StudentDatabase`. 
- It interacts with `StudentDatabase` to retrieve all student records and sort them based on the specified category.
- Although `Storage` is not used in execute method under `SortCommand`, the `Command` require it to be there. So the method signature must match `Command`.
- All methods listed in `Command` is implemented in `SortCommand` class as there is no further child class from `SortCommand`.
- Returns a `CommandResult` containing the sorted list of students based on the specified category.

##### Sequence Diagram

![SortCommandSequence](images/SortCommandSequence.png)

The sequence diagram illustrates the execution flow:
- User executes the sort command with a specified category (e.g., name, course, mcs)
- `Parser` class parses user input and determines the command (sort) and the category
- `SortCommand.execute()` is called with the `StudentDatabase` and `Storage`
- The command retrieves all student records from the `StudentDatabase`
- A `CommandResult` is returned containing the sorted list of students based on the specified category

##### Search Command

The `SearchCommand` allows users to search for students using certain categories, such as name, course, and mcs.

##### Class Diagram

![SearchCommandClassDiagram](images/SearchCommandClass.png)

The class diagram shows the relationship between `SearchCommand` and other components:
- `SearchCommand` implements the `Command` interface
- `SearchCommand` needs to reference of `Storage` and `StudentDatabase`. 
- It interacts with `StudentDatabase` to retrieve relevant student records based on the specified search criteria
- Although `Storage` is not used in execute method under `SearchCommand`, the `Command` require it to be there. So the method signature must match `Command`.
- All methods listed in `Command` is implemented in `SearchCommand` class as there is no further child class from `SearchCommand`.
- Returns a `CommandResult` containing the search results based on the specified search criteria

##### Sequence Diagram

The sequence diagram illustrates the execution flow:
- User executes the search command with specified search criteria (e.g., name, course, mcs)
- `Parser` class parses user input and determines the command (search) and the search criteria
- `SearchCommand.execute()` is called with the `StudentDatabase` and `Storage`
- The command retrieves relevant student records from the `StudentDatabase` based on the specified search criteria
- A `CommandResult` is returned containing the search results based on the specified search criteria

## Product scope
### Target user profile

The target user is an Administrative Staff member (Admin) at the National University of Singapore (NUS). These users are:
- Responsible for managing large cohorts of students and their academic progression
- Comfortable using Command Line Interfaces (CLI) for fast data entry and retrieval
- In need of a centralised, local system to manage student contact details and module grades without the overhead of a heavy web-based GUI.

### Value proposition

Student Records information may be stored in a fragemented fashion, with academic history in one system and progress tracking (GPA, Module Code) in another. The Student Record Data Management System (SRDMS) provides a streamlined, keyboard-centric workflow for maintaining student databases. By using a CLI-based approach, it enables admins to perform batch-like updates and quick searches significantly faster than traditional spreadsheet or form-based systems.

## User Stories

| Version | As a ... | I want to ...                            | So that I can ...                                                       |
|---------|----------|------------------------------------------|-------------------------------------------------------------------------|
| v1.0    | Admin    | create a new student record              | add new enrollees to the system record system.                               |
| v1.0    | Admin    | list all students                        | see a high-level overview of the current student population.            |
| v1.0    | Admin    | delete a student entry                   | remove records of students who have withdrawn or graduated.             |
| v2.0    | Admin    | edit student details (name, email, etc.) | ensure the database remains accurate as student information changes.    |
| v2.0    | Admin    | add or remove modules for a student      | track their academic history and specific module completions.           |
| v2.0    | Admin    | check the status of a student            | quickly see a student's CAP, total MCs, and overall academic standing.  |
| v2.0    | Admin    | search students by course or module      | filter the database to find specific groups for administrative actions. |


## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
