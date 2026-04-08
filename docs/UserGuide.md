# User Guide

## Introduction

The Dextro app acts as a management system and provides NUS admins a way to manage student records and progression, using command line language.

Dextro will track students' progress and provide insights on how a student is faring by storing the grades and modules taken by students and calculating metrics like CAP and improvement.

## Quick Start

1. Ensure Java 17 is installed. Mac users: Ensure you have the precise JDK version prescribed here.
1. Download latest `dextro.jar` file.
1. Copy the file to the folder you want to use as the home folder for Dextro.
1. Open a command terminal, cd into the folder you put the jar file in, and use the `java -jar dextro.jar` command to run the application.
1. Proceed to execute commands, refer to the Features below for details of each command.

## Features

1. Creating a new student record
2. Listing all existing students along with individual summaries
3. Directly edit student records
4. Add new module to the student record
5. Delete an entry
6. View details of selected student by id
7. Filter student records based on tutorial/major
8. Find students based on personal details
9. View Status of a student
10. Undo Feature for written commands



### `create`
**Description:** Creates a new student.

**Syntax:**
```
create n/<name> [p/<phone_number>] [e/<email_address>] [a/<address>] [c/<course>]
```

**Example:**
```
create n/John Doe p/87654321 e/john_doe@hmail.com a/Orchard Road block 20 #23-11 c/Computer Science
```
Optional fields not provided or provided as blank will be stored blank. NAME is compulsory, while the rest are optional.
Repeated fields not allowed. Order of fields does not matter.

Prefixes must be separated from text before with a space. Example:
```
create n/John/ p/87654321
```
The above command does not trigger an error for duplicate prefixes as the name is parsed as John/.

Entries with duplicate details, i.e. same name or address (including exact duplicates) are allowed.

---

### `delete`
**Description:** Deletes a student's database record.

**Syntax:**
```
delete <student_id>
```

**Example:**
```
delete 1
```

---

### `edit`
**Description:** Edits an existing student.

**Syntax:**
```
edit <student_id> [n/<name>] [p/<phone_number>] [e/<email_address>] [a/<address>] [c/<course>] [m/<module_code>/<grade>[/<credits>]]
```

**Example:**
```
edit 1 n/Jane Doe p/98765432 m/CG2027/B/2
```
Minimum of one field must be provided. Repeated fields not allowed. Order of fields does not matter.

If the requested edit does not modify anything, the program will output a successful result anyways.

Prefixes must be separated from text before with a space. Example:
```
edit 2 n/John/ p/87654321
```
The above command does not trigger an error for duplicate prefixes as the name is parsed as John/. 

When editing a student's module entry:

- If the module does not exist, the program will output an error message saying that the module is not found for the student.
- Module credits are optional. Program will default to 4 credits if not provided.
- Module code and grade are case-insensitive

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
1: Bronathan Binglebong/999/N.A./N.A./DSA
2: Charlie Chocolate/67676767/willywonka@jmail.com/Lakseside/N.A.
3: Dalton Dog/83463726/clifford_big_red@dogmail.com/Tampines St 82 Blk 853 #04-67/CS
----------------------------------------------------------------------------------------------------
```
---

### `find`
**Description:** Finds students by info across all fields

**Syntax:**
```
find <keywords>
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
---

### `search`
**Description:** Searches students either by a specific field

**Syntax:**
```
search [n/name] [p/<phone_number>] [e/<email>] [a/<address>] [c/<course>] [m/<module_code>/<grade>]
```
Only one field can be provided. Repeated fields not allowed.

Prefixes must be separated from text before with a space.

---

### `status`
**Description:** Shows the GPA, degree completion progress and summary of a student.

**Syntax:**
```
status <student_id>
```

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
sort <criterion>
```
Criteria: `name, course, cap, mcs`

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
add <student_id> m/<module_code>/<grade>[/credits]
```

**Example:**
```
add 1 m/CS101/A
add 1 m/MA1511/B+/2
```
Adding duplicate modules under the same student is allowed, to accommodate module retakes.


---

### `remove`
**Description:** Removes all modules matching the input from a student.

**Syntax:**
```
remove <student_id> m/<module_code>
```

**Example:**
```
remove 1 m/Cs101
remove 1 m/cG1111a
```

module code is not case sensitive.

---

##  Command Format Notes
- `n/` → Name
- `p/` → Phone number
- `e/` → Email address
- `a/` → Home address
- `c/` → Course
- `m/` → Module
- `<student_id>` → Index shown in the list

---


## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Navigate to /data/DextroStudentList.txt and replace the target computer's file.

## Command Summary

* Add student named John Lim Jun Jie with a phone number 88664422:
```
create n/John Lim Jun Jie p/88664422
```
* Add module CS2113/B+ to John's info:
```
find John Lim Jun Jie
add <student_id> m/CS2113/B+
```
