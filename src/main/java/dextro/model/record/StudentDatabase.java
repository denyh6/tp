package dextro.model.record;

import dextro.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDatabase {
    private final List<Student> studentList;

    public StudentDatabase() {
        studentList = new ArrayList<>();
    }

    public StudentDatabase(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public Student getStudent(int index) {
        return studentList.get(index);
    }

    public Student removeStudent(int index) {
        return studentList.remove(index);
    }

    public int getStudentCount() {
        return studentList.size();
    }

    public List<Student> getAllStudents() {
        return studentList;
    }

    public void updateStudent(int index, Student updatedStudent) {
        studentList.set(index, updatedStudent);
    }
}
