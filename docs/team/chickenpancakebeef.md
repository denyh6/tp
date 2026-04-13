# Matthias Lim - Project Portfolio Page

## Overview

Dextro is a desktop application for managing student academic records, optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI). It helps educators and administrators track student progress, manage course modules, and monitor academic performance efficiently.

## Summary of Contributions

### Code contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=zoom&zA=ChickenPancakeBeef&zR=AY2526S2-CS2113-T11-4%2Ftp%5Bmaster%5D&zACS=226.76727272727274&zS=2026-02-20T00%3A00%3A00&zFS=&zU=2026-04-03T23%3A59%3A59&zMG=false&zFTF=commit&zFGS=groupByRepos&zFR=false)

### Enhancements implemented

#### New Feature: Status Command
- **What it does**: Allows users to view detailed information about a specific student, including their CAP (Cumulative Average Point), total MCs (Modular Credits) completed, and progress status.
- **Justification**: This feature provides quick access to comprehensive student information without having to view all student details. It helps educators make informed decisions about student academic standing.
- **Highlights**: The status command calculates and displays:
  - Student's current CAP
  - Total MCs completed out of 160 required
  - Progress status (Just Started, On Track, Satisfactory, Good Progress, Completed)
- **Code**: `src/main/java/dextro/command/StatusCommand.java:8-57`

#### New Feature: Undo Command
- **What it does**: Allows users to revert the last undoable command that modified the student database.
- **Justification**: This feature prevents data loss from accidental modifications and provides users with confidence when making changes to student records.
- **Highlights**: The undo command integrates with the CommandHistory system to track and reverse operations. It properly handles edge cases such as attempting to undo when there are no commands in history.
- **Code**: `src/main/java/dextro/command/UndoCommand.java:7-43`

#### New Feature: Command History System
- **What it does**: Maintains a stack-based history of executed commands that can be undone.
- **Justification**: This is the underlying infrastructure that enables the undo feature. It tracks all undoable commands and manages the command stack.
- **Highlights**: Implemented using a Stack data structure for efficient LIFO (Last In First Out) operations. Integrates seamlessly with the Command interface through the `isUndoable()` method.
- **Code**: `src/main/java/dextro/command/CommandHistory.java:5-23`

#### Enhancement to Existing Features
- **CAP Calculation**: Implemented the `calculateCap()` method in the Student class to compute the Cumulative Average Point across all modules (`src/main/java/dextro/model/Student.java:67-78`)
- **Progress Status Tracking**: Implemented the `getProgressStatus()` method to categorize student progress based on MCs completed (`src/main/java/dextro/model/Student.java:85-98`)
- **Command Interface Enhancement**: Extended the Command interface to support undo operations by adding `undo()` and `isUndoable()` methods

### Contributions to the User Guide
- Added documentation for the Status command with usage examples
- Added documentation for the Undo command with usage examples
- Updated User Guide to reflect the new features and their integration with existing commands

### Contributions to the Developer Guide
- Created comprehensive documentation for the Status Command feature including:
  - Class diagram showing relationships with other components
  - Sequence diagram illustrating the execution flow
- Created comprehensive documentation for the Undo Command feature including:
  - Class diagram showing the CommandHistory integration
  - Sequence diagram illustrating the undo execution flow
- Added implementation details and design considerations for both features

### Contributions to team-based tasks
- Reviewed and merged pull requests from team members
- Contributed to v1.0 release preparation and JAR packaging
- Updated project documentation and diagrams

