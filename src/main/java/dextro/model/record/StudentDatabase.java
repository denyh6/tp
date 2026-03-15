package dextro.model.record;

import dextro.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentDatabase {
    private final List<Student> studentList = new ArrayList<>();

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public Student getStudent(int index) {
        return studentList.get(index);
    }

    public int getStudentCount() {
        return studentList.size();
    }

    public List<Student> getAllStudents() {
        return studentList;
    }
}
