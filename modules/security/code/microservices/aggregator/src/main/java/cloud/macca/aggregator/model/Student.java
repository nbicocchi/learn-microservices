package cloud.macca.aggregator.model;

public class Student {
    public final int id;
    public final String name, surname;
    public Student(
            int id,
            String name,
            String surname
    ){
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
}
