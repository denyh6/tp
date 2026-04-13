# User Guide

## Introduction

The Dextro app acts as a management system and provides NUS admins a way to manage student records and progression, using command line language.

Dextro will track students' progress and provide insights on how a student is faring by storing the grades and modules taken by students and calculating metrics like CAP and improvement.

## Quick Start

1. Ensure Java 17 is installed. Mac users: Ensure you have the precise JDK version prescribed here.
2. Download latest `dextro.jar` file.
3. Copy the file to the folder you want to use as the home folder for Dextro.
4. Open a command terminal, cd into the folder you put the jar file in, and use the `java -jar dextro.jar` command to run the application.
5. Proceed to execute commands, refer to the Features below for details of each command.

## Features

### `create`
**Description:** Creates a new student.

**Syntax:**
```
create n/NAME [p/PHONE] [e/EMAIL] [a/ADDRESS] [c/COURSE]
```

**Example:**
```
> create n/John Doe p/87654321 e/john@hmail.com a/20 Orchard Road #23-11 c/Computer Science
----------------------------------------------------------------------------------------------------
Student created: John Doe
----------------------------------------------------------------------------------------------------
```
Optional fields not provided or provided as blank will be stored as `N.A.`. NAME is compulsory, while the rest are optional.
Repeated fields not allowed. Order of fields does not matter.

The following command is valid for the reasons explained below:
```
create n/John/ p/87654321 e/
```
- Since prefixes must be separated from previous text using at least one space, the above command does not trigger an error for duplicate prefixes as the name is parsed as John/.
- Leaving optional fields empty will not trigger an error, neither does not including the corresponding prefix.

**Duplicate entries:**

Creating a new entry with a phone number, email and address that matches an existing student will result in a confirmation prompt.
Users can then input "y" to confirm 


---

### `delete`
**Description:** Deletes a student's database record.

**Syntax:**
```
delete INDEX
```

**Example:**
```
> delete 1
----------------------------------------------------------------------------------------------------
Successfully deleted student:
John Doe/87654321/john_doe@hmail.com/N.A./N.A.
----------------------------------------------------------------------------------------------------
```



---

### `edit`
**Description:** Edits an existing student.

**Syntax:**
```
edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [c/COURSE] [m/CODE/GRADE[/CREDITS]]
```

**Example:**
```
edit 1 n/Jane Doe p/98765432 m/CG2027/B/2
```
Minimum of one field must be provided. Repeated fields not allowed. Order of fields does not matter.

If the requested edit does not modify anything, the program will output a successful result anyway.

Prefixes must be separated from text before with a space. Example:
```
edit 2 n/John/ p/87654321
```
The above command does not trigger an error for duplicate prefixes as the name is parsed as John/, similar to `create`.



---

### `list`
**Description:** Lists all students.

**Syntax:**
```
list
```
Example output:
```
> list
----------------------------------------------------------------------------------------------------
1: Bronathan Binglebong/98765432/N.A./N.A./Data Science and Analytics
2: Charlie Chocolate/87654321/willywonka@jmail.com/Lakseside/N.A.
3: Dalton Dog/83463726/clifford@red.com/Tampines St 83/Computer Science
----------------------------------------------------------------------------------------------------
```
---

### `find`
**Description:** Finds students by info across all fields

**Syntax:**
```
find KEYWORD
```

**Example:**
```
> find John
----------------------------------------------------------------------------------------------------
Here are the matching students in your list:
1. John/87654321/N.A./N.A./N.A.
----------------------------------------------------------------------------------------------------
```
```
> find 8765
----------------------------------------------------------------------------------------------------
Here are the matching students in your list:
1. John/87654321/N.A./N.A./N.A.
----------------------------------------------------------------------------------------------------
```
```
> find n.a
----------------------------------------------------------------------------------------------------
Here are the matching students in your list:
1. JOHN/N.A./N.A./N.A./N.A.
2. janny/N.A./janny@gmail.com/N.A./N.A.
3. John doe/91234678/email@gmail.com/bukit batok/N.A.
----------------------------------------------------------------------------------------------------
```
User can use command: find n.a to query all records of null/missing value for manual validation or filtering if required.
---

### `search`
**Description:** Searches students either by a specific field

**Syntax:**
```
search [c/COURSE] [m/CODE/GRADE]
```
Only one field can be provided. Repeated fields not allowed.

Prefixes must be separated from text before with a space.

---

### `status`
**Description:** Shows the GPA, degree completion progress and summary of a student. Displays all modules sorted by grade (highest to lowest) along with module statistics including grade distribution, highest grade, and lowest grade.

**Syntax:**
```
status INDEX
```

**Example:**
```
> status 1
----------------------------------------------------------------------------------------------------
Index 1: Alice, Computer Science, Cap 4.8, 16/160 MCs completed. Status: Just Started.
Modules and Grades:
  - CG1111A: A+ (4 MCs)
  - CS2113: A (4 MCs)
  - CS9999: A (4 MCs)
  - CS2101: B+ (4 MCs)

Module Statistics:
  Grade Distribution: 1 A+, 2 A's, 1 B+
  Highest Grade: A+ (5.0)
  Lowest Grade: B+ (4.0)
----------------------------------------------------------------------------------------------------
```

**Notes:**
- Modules are automatically sorted by grade from highest to lowest
- Module statistics exclude S/U (Satisfactory/Unsatisfactory) grades
- Grade distribution shows the count of each grade obtained
- If no modules have been added, displays "No modules added yet."

---

### `undo`
**Description:** Reverts the last command.

**Syntax:**
```
undo
```

---

### `sort`
**Description:** Displays a sorted list of the existing database entries

**Syntax:**
```
sort [name|course|cap|mcs]
```

Displays a temporary list. Does not affect the order of the database entries. 

---


### `exit`
**Description:** Exits the application.

**Syntax:**
```
exit
```

---

## 4. Module Commands

### `add`
**Description:** Adds a module to a student.

**Syntax:**
```
add INDEX CODE/GRADE[/CREDITS]
```

**Example:**
```
> add 1 CS2113/A
----------------------------------------------------------------------------------------------------
Added module CS2113 (A) to john
----------------------------------------------------------------------------------------------------
> add 1 MA1511/B+/2
----------------------------------------------------------------------------------------------------
Added module MA1511 (B+) to john
----------------------------------------------------------------------------------------------------
> add 1 MA1511/B
----------------------------------------------------------------------------------------------------
Error: Module MA1511 already exists for this student.
----------------------------------------------------------------------------------------------------
```
Adding duplicate modules under the same student is not allowed.

Modules are validated against a pattern that fits all existing NUS module codes; non-existent modules that follow the same format are allowed.

**Note:** If CREDITS is not specified, the module will default to 4 MCs (e.g., `add 1 CS2113/A` will add CS2113 with 4 MCs).

---

### `remove`
**Description:** Removes all modules matching the input from a student.

**Syntax:**
```
remove INDEX CODE
```

**Example:**
```
remove 1 Cs101
remove 1 cG1111a
```

CODE is case-insensitive

---

##  Field Constraints
- `n/` → Name
  - Must be less than 100 characters long.
  - Must contain only letters, spaces, and the following special symbols: `, ( ) . - / @ '`
  - Case is stored as given.
- `p/` → Phone number
  - Only valid Singaporean mobile number is allowed, i.e. begins with 8 or 9.
  - Must consist of only 8 digits.
- `e/` → Email address
  - Must follow valid email address format:
    - Must contain a `@` symbol
    - Local part allows letters, numbers and `. _ % + -`
    - Domain must contain at least one `.`
  - Case-insensitive, converted to lowercase when stored and displayed.
- `a/` → Home address
  - Must be less than 200 characters long.
  - Must contain only alphanumeric symbols, spaces and the following special symbols: `, . # - / ( ) &`
  - Case is stored as given.
- `c/` → Course
  - Must be less than 50 characters long.
  - Must contain only letters, spaces and the following special symbols: `, ( ) & -`
  - Case is stored as given.
- `m/` → Module
  - CODE must follow the same format as actual NUS modules.
  - CODE must consist only of alphanumeric symbols.
  - CODE and GRADE case-insensitive, converted to uppercase when stored and displayed
- `INDEX` → Index shown in the list

---

##  Command Format Notes
- Command keywords are case-insensitive. For example, `create`, `CREATE`, and `Create` will all invoke the same command.
- Prefixes (e.g., `n/`, `p/`, `e/`) remain case-sensitive unless otherwise specified.
- Data fields (e.g., names, addresses) preserve the case entered by the user unless stated otherwise. See Data Field Info.
- Prefixes must be preceded by a space to be recognized as separate fields.

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Navigate to /data/DextroStudentList.txt and replace the target computer's file.

**Q**: Will my data be saved automatically?

**A**: Yes. All changes are saved automatically to the data file after each command.

**Q**: How do I clear all data?

**A**: Delete the `/data/DextroStudentList.txt` file and restart the application.

**Q**: Why is my command not working?

**A**: Ensure that:
- Prefixes (e.g., n/, p/) are separated by a space
- Required fields are provided
- No duplicate prefixes are used

**Q**: Why am I seeing a duplicate warning?

**A**: The system detected another student with the same phone, email, or address. Enter `y` to confirm creation, or any other input to cancel.

**Q**: Can I undo all commands?

**A**: Most commands can be undone using `undo`. Some commands (e.g., sort) may not be undoable.

**Q**: Why is my input being parsed incorrectly?

**A**: Prefixes must be preceded by a space. Otherwise, they will be treated as part of the previous field.

**Q**: Why can’t I add a module?

**A**: Ensure the format is `CODE/GRADE[/CREDITS]` (e.g., `CS2113/A` or `CS2113/A/4`).

**Q**: What is the difference between `find` and `search`?

**A**:
- `find` searches across all fields using a keyword
- `search` filters by a specific field (e.g., course or module)

**Q**: Why does it say the index is invalid?

**A**: Ensure the index is a valid number shown in the current list.


## Command Summary

* Create a student with full details:
```
create n/John Doe p/91234567 e/john@u.nus.edu a/PGP Block 12 #03-123 c/Computer Science
```

* Create a student with only a name:
```
create n/Jane Tan
```

* List all students:
```
list
```

* Find students by keyword:
```
find John
```

* Search students by course:
```
search c/Computer Science
```

* Search students by module:
```
search m/CS2113
```

* Edit a student’s details:
```
edit 1 n/John Upgraded p/98765432
```

* Add a module to a student:
```
add 1 CS2113/A
```

* Add a module with credits:
```
add 1 MA1512/B+/2
```

* Remove a module:
```
remove 1 CS2113
```

* Delete a student:
```
delete 1
```

* View student status:
```
status 1
```

* Sort students by CAP:
```
sort cap
```

* Undo the last action:
```
undo
```

* Exit the application:
```
exit
```
