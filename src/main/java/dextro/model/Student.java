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
        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Module module : modules) {
            Grade grade = module.getGrade();

            if (grade.getCountsToGpa()) {
                int credits = module.getCredits();
                totalPoints += grade.getCap() * credits;
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        return totalPoints / totalCredits;
    }

    public int getTotalMCs() {
        int total = 0;

        for (Module module : modules) {
            if (module.getGrade().getCountsToCompletion()) {
                total += module.getCredits();
            }
        }

        return total;
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
        private String name; // compulsory
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

        public Builder(Student existing) {
            this.name = existing.name;     
            this.phone = existing.phone;
            this.email = existing.email;
            this.address = existing.address;
            this.course = existing.course;
        }

        public Builder name(String name) {
            if (name != null && !name.isBlank()) {
                this.name = name;  
            }
            return this;
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
