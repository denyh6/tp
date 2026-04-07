# Chong Kai Jie - Project Portfolio Page

## Overview

Dextro is a desktop application for managing student academic records, optimized for use via a Command Line Interface (CLI) while still having the benefits of a Graphical User Interface (GUI). It helps educators and administrators track student progress, manage course modules, and monitor academic performance efficiently.

## Summary of Contributions

### Code contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=ffluryy&tabRepo=AY2526S2-CS2113-T11-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

Classes written:
```
App
AddCommand
RemoveCommand
Command
CommandResult
CreateCommand
DeleteCommand
ListCommand
Config
CommandException
ParseException
StudentDatabase
Grade
Module
Student
ArgumentTokenizer
Parser
Ui
Main
```

### Enhancements implemented

#### New Feature: Create
A way for users to add a new student record by inputting name and other optional fields.

#### New Feature: Delete
A way for users to delete existing student records based on id number

#### New Feature: List
A way to view the entire database, or ranges of id numbers

#### New Feature: Add
A way to add a module/grade to a student's data

#### New Feature: Remove
A way to remove a module that matches the given code in a student's data

#### Enhancement to ArgumentTokenizer
Improved robustness against badly formed inputs by throwing an exception when 

### Contributions to the User Guide
- Wrote the descriptions and examples to the following functions: ```create, delete, edit, list, find, search, status, undo, sort, exit, add, remove```
- Added Command Format Notes
- Added some examples to Command Summary

### Contributions to team-based tasks
Persuaded the team on multiple occasions to contribute to the project code
