package dextro.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String course;

    private final List<Module> modules;

    private Student(Builder builder) {
        this.name = builder.name;
        this.phone = builder.phone == null ? "" : builder.phone;
        this.email = builder.email == null ? "" : builder.email;
        this.address = builder.address == null ? "" : builder.address;
        this.course = builder.course == null ? "" : builder.course;
        this.modules = new ArrayList<>();
    }

    // Getters return "N.A." if empty
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone.isEmpty() ? "N.A." : phone;
    }

    public String getEmail() {
        return email.isEmpty() ? "N.A." : email;
    }

    public String getAddress() {
        return address.isEmpty() ? "N.A." : address;
    }

    public String getCourse() {
        return course.isEmpty() ? "N.A." : course;
    }

    @Override
    public String toString() {
        return name + "/" +
                getPhone() + "/" +
                getEmail() + "/" +
                getAddress() + "/" +
                getCourse();
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public boolean removeModule(String moduleCode) {
        return modules.removeIf(m -> m.getCode().equalsIgnoreCase(moduleCode));
    }

    public List<Module> getModules() {
        return modules;
    }

    public double calculateCap() {
        if (modules.isEmpty()) {
            return 0.0;
        }
        double totalPoints = 0.0;
        int count = 0;
        for (Module module : modules) {
            totalPoints += module.getGrade().getCap();
            count++;
        }
        return totalPoints / count;
    }

    public int getTotalMCs() {
        // Assuming each module is 4 MCs as per the example
        return modules.size() * 4;
    }

    public String getProgressStatus() {
        int totalMCs = getTotalMCs();
        if (totalMCs >= 160) {
            return "Completed";
        } else if (totalMCs >= 120) {
            return "Good Progress";
        } else if (totalMCs >= 80) {
            return "Satisfactory";
        } else if (totalMCs >= 40) {
            return "On Track";
        } else {
            return "Just Started";
        }
    }

    // Builder class
    public static class Builder {
        private final String name; // compulsory
        private String phone;
        private String email;
        private String address;
        private String course;

        public Builder(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name is compulsory");
            }
            this.name = name;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder course(String course) {
            this.course = course;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

}
