# Deny's Project Portfolio Page

## Project: Dextro Student Records System

### Overview
The Dextro app acts as a management system and provides NUS admins a way to manage student records and progression, 
using command line language.

Dextro will track students’ progress and provide insights on how a student is faring by storing the grades and modules
taken by students and calculating metrics like CAP and improvement.


Given below are my contributions to the project.

* **New Feature**: Storage
    * What it does: Deals with loading Students, with their corresponding Modules, from a 
  txt file, DextroStudentList.txt, and saving updated StudentDatabase to the txt file
    * Justification: This feature improves the product because a user can make any changes to the StudentDatabase
  and the program provides a convenient way to store the data, to be accessed again after exiting.
    * Highlights: This enhancement affects existing commands and commands to be added in future. It required an
  in-depth analysis of parsing. Saving the Students and their respective List of Modules and grades via their toString
  functions into a txt file. Then, loading, parsing and extracting the Students from the txt. The implementation
  too was challenging as it required changes to existing commands, understanding existing classes and their
  interactions. Exceptions also had to be handled if the txt file or data folder was missing.
    * Credits: Deny's own ip

* **New Feature**: Search for students with a phone number that contains the keyword given.
    * What it does: Search for students in the StudentDatabase whose phone number contains the keyword input by the 
  user.
    * Justification: This feature improves the product because a user can now also search for specific students, if
  they only know the student's full or partial phone number.
    * Highlights: This enhancement affects existing commands and commands to be added in future. Testcases were also
  added and updated to follow the addition of a new possible search field.
    * Credits: Existing SearchCommand code by agaraNUS in this tp


* **Code contributed**:
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=denyh6&breakdown=true)


* **Contributions to**:
    * User Guide:
        * Added documentation for the features `Storage` and `Search with phone number`
        * Added example output for features as mentioned by testers in PE-D
    * Developer Guide:
        * Added implementation details of the `Storage` and `Search with phone number` feature.
        * Generated the Class diagrams and sequence diagrams for these features using plantuml.
        * Added non-functional requirements
        * Added Instructions for manual testing
    * team-based tasks:
        * Setting up the GitHub team org/repo as a team
        * Tutorial activities
        * Maintaining issue tracker, assigning issues and labelling
        * Updating user/developer docs that are not specific to a feature e.g., documenting non-functional requirements
    * Review contributions:
        * Links to some PRs reviewed: [PR1](https://github.com/AY2526S2-CS2113-T11-4/tp/pull/181), 
      [PR2](https://github.com/AY2526S2-CS2113-T11-4/tp/pull/53),
      [PR3](https://github.com/AY2526S2-CS2113-T11-4/tp/pull/45)


* **Project management**:
  * Managed releases `v1.0` - `v2.1` (3 releases) on Github
