package cloud.macca.microservices.grades.dto;

public class Student {
    int id;
    String name,
        surname;

    public Student(int id, String name, String surname){
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }
}
