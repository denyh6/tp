# Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}

---

### Implementation: Matthias

#### Status Command

The `StatusCommand` allows users to view detailed information about a specific student, including their CAP, total MCs completed, and progress status.

### Implementation: Agara

### Sort Command 

The `SortCommand` allows users to view a temporary list of all users sorted by certain categories selected by the user, such as name, course, and mcs.

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

The target user is an Administrative Staff member (Admin) at the National University of Singapore (NUS), specifically those managing the CS2113 modules. These users are:
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
