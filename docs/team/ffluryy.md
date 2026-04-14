# Chong Kai Jie - Project Portfolio Page

## Overview

Dextro is a desktop application for managing student academic records, optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI). It helps educators and administrators track student progress, manage course modules, and monitor academic performance efficiently.

## Summary of Contributions

### Code contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=ffluryy&tabRepo=AY2526S2-CS2113-T11-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

I wrote most of v1.0, which was important for setting up the project structure. Most of my code has to do with input validation and integration. 
### Enhancements implemented

#### New Feature: Create (duplicate detection and confirmation flow)
Added duplicate-entry detection to `CreateCommand` before any student is written to the database. `CreateCommand.execute()` calls `StudentDatabase.findDuplicateFields()` — also added as part of this work — which returns a map of conflicting students to the fields they share (name, phone, email, or address). If conflicts are found, the command returns a `CommandResult` carrying a `ForceCreateCommand` as its `pendingCommand`, using a new `CommandResult` constructor overload added to support this flow. `App.run()` was updated to check `requiresConfirmation()` and re-prompt the user; entering `y` causes `App` to execute the `ForceCreateCommand`, which bypasses the duplicate check and delegates directly to `CreateCommand.doCreate()`. The undo history records the `ForceCreateCommand` rather than the original `CreateCommand`, so undo works correctly regardless of which path was taken.

#### New Feature: List
Implemented `ListCommand` from scratch as one of the first commands in the project. It retrieves all students from `StudentDatabase` and formats them as a numbered list. Returns a `"No students found."` message instead of blank output when the database is empty.

#### New Feature: Add
Implemented `AddCommand` and its full lifecycle: parsing in `Parser`, execution against `StudentDatabase` and `Storage`, and undo support. Later extended it to accept an optional credit count parameter (defaulting to 4 MCs), requiring updates to `Parser`, `Module`, `Storage` (serialisation format), and `Student.calculateCap()` and `getTotalMCs()` to use per-module MC values. Also added a duplicate module code check to `Student.addModule()`, which throws `IllegalArgumentException` if the student already has the same module code; `AddCommand` catches this and returns it as a user-facing error message.

#### New Feature: Remove
Implemented `RemoveCommand` for removing a module by code from a student's record. The command performs a case-insensitive match and saves a reference to the full `Module` object — including grade and credit count — before removing it, so that `undo` can re-add the exact module rather than reconstructing it from partial data.

#### New Feature: Help
Added `HelpCommand`, which prints a formatted list of all available commands with their syntax, and a link to the user guide. Wired `CMD_HELP` in `Config` and the corresponding `case` in `Parser`.

#### Enhancement to ArgumentTokenizer
Rewrote `ArgumentTokenizer` from a simple `indexOf`-based scanner into a cursor-based parser. The rewrite added two robustness improvements: prefix matching now requires the prefix to be preceded by whitespace (preventing partial matches within field values, e.g. `n/John/p/` not being misread as two separate prefixes), and duplicate prefix detection now throws a `ParseException` with a user-friendly message instead of silently overwriting the earlier value.

#### Enhancement to Edit
Extended `EditCommand` to support updating both personal fields and module grades in a single command, with any combination of fields being optional. Later added support for editing module credits alongside grade. The command saves the full previous `Student` object before modification so that `undo` can restore the student exactly as it was.

#### Enhancement to Validator and Normalizer
Extracted all field validation logic from `Parser` into a dedicated `Validator` class, and created a `Normalizer` class to handle canonicalisation (uppercasing names, lowercasing emails, stripping whitespace, normalising module codes to uppercase) before validation. This separation means `Parser` only calls normalise-then-validate per field, with no inline validation logic. Added comprehensive `ValidatorTest` and `NormalizerTest` suites covering valid inputs, boundary cases, and expected exceptions.

---

### Contributions to the User Guide
- Wrote the descriptions and examples for the following commands: `create`, `delete`, `edit`, `list`, `find`, `search`, `status`, `undo`, `sort`, `exit`, `add`, `remove`
- Wrote Command Format Notes
- Added examples to Command Summary
- Updated syntax to use `UPPER_CASE` parameter convention throughout
- Updated examples to reflect actual output format

---

### Contributions to the Developer Guide
- Design:
  - Main
  - Config
  - Exceptions: `CommandException` / `ParseException`
  - Model: `Student` / `StudentDatabase`, `Grade` / `Module`
  - UI
  - Parser / `ArgumentTokenizer`
  - Storage
  - `Command`
- Implementation:
  - `AddCommand`
  - `RemoveCommand`
  - `CreateCommand`
  - `DeleteCommand`
  - `ListCommand`

---

### Contributions to team-based tasks
- Caught bugs for other team members
- Reminded team members on deadlines
- Suggested new features
- Approved PRs
- Released v2.0.1
- Sorted and assigned all bugs from PE-D to the respective developers
