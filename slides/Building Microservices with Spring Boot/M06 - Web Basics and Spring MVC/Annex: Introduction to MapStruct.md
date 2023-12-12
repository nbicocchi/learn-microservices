# Introduction to MapStruct

In this tutorial, we’ll explore the use of [MapStruct](http://www.mapstruct.org/), which is, simply put, a Java Bean mapper.
This API contains functions that automatically map between two Java Beans. With MapStruct, we only need to create the interface, and the library will automatically create a concrete implementation during compile time.

The relevant module for this lesson is: [mapstruct-introduction-end](https://github.com/nbicocchi/spring-boot-course/tree/module6/mapstruct-introduction-end)


## MapStruct and Transfer Object Pattern

For most applications, you’ll notice a lot of boilerplate code converting POJOs to other POJOs. For example, a common type of conversion happens between persistence-backed entities and DTOs that go out to the client-side.

So, that is the problem that MapStruct solves: manually creating bean mappers is time-consuming. But the library **can generate bean mapper classes automatically.**

## Maven

Let’s add the below dependency into our Maven _pom.xml_:

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0.Beta1</version>
</dependency>
```

Let’s also add the _annotationProcessorPaths_ section to the configuration part of the _maven-compiler-plugin_ plugin.

The _mapstruct-processor_ is used to generate the mapper implementation during the build:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.0.Beta1</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Basic Mapping

### POJOs

Let’s first create a simple Java POJO:

```
public class SimpleSource {
    private String name;
    private String description;
    // getters and setters
}

public class SimpleDestination {
    private String name;
    private String description;
    // getters and setters
}
```

### The Mapper Interface

```
@Mapper(componentModel = "spring")
public interface SimpleSourceDestinationMapper {
    SimpleDestination sourceToDestination(SimpleSource source);
    SimpleSource destinationToSource(SimpleDestination destination);
}
```

Notice we did not create an implementation class for our _SimpleSourceDestinationMapper —_ because MapStruct creates it for us.


### The Mapper Implementation (generated)

We can trigger the MapStruct processing by executing an _mvn clean install_.

This will generate the implementation class under _/target/generated-sources/annotations/_.

Here is the class that MapStruct auto-creates for us:

```
@Component
public class SimpleSourceDestinationMapperImpl implements SimpleSourceDestinationMapper {

    @Override
    public SimpleDestination sourceToDestination(SimpleSource source) {
        if ( source == null ) {
            return null;
        }

        SimpleDestination simpleDestination = new SimpleDestination();

        simpleDestination.setName( source.getName() );
        simpleDestination.setDescription( source.getDescription() );

        return simpleDestination;
    }

    @Override
    public SimpleSource destinationToSource(SimpleDestination destination) {
        if ( destination == null ) {
            return null;
        }

        SimpleSource simpleSource = new SimpleSource();

        simpleSource.setName( destination.getName() );
        simpleSource.setDescription( destination.getDescription() );

        return simpleSource;
    }
}

```

### Test Case

Finally, with everything generated, let’s write a test case showing that values in _SimpleSource_ match values in _SimpleDestination_:

```
@SpringBootTest
class SimpleSourceDestinationMapperTest {
    @Autowired
    SimpleSourceDestinationMapper mapper;

    @Test
    void sourceToDestination() {
        SimpleSource source = new SimpleSource("a","b");
        SimpleDestination destination = mapper.sourceToDestination(source);

        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getDescription(), destination.getDescription());
    }
}
```

## Mapping Fields With Different Field Names

### POJOs

```
public class EmployeeDTO {

    private int employeeId;
    private String employeeName;
    // getters and setters
}
```

```
public class Employee {

    private int id;
    private String name;
    // getters and setters
}
```

### The Mapper Interface

```
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    EmployeeDTO employeeToEmployeeDTO(Employee entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    Employee employeeDTOtoEmployee(EmployeeDTO dto);
}
```

### Test Case

```
@SpringBootTest
class EmployeeMapperTest {
    @Autowired
    EmployeeMapper mapper;

    @Test
    void employeeToEmployeeDTO() {
        Employee entity = new Employee(1, "John");
        EmployeeDTO dto = mapper.employeeToEmployeeDTO(entity);

        assertEquals(dto.getEmployeeId(), entity.getId());
        assertEquals(dto.getEmployeeName(), entity.getName());

    }
}
```

## Mapping Beans With Child Beans

### POJOs

```
public class EmployeeWithDivisionDTO {
    private int employeeId;
    private String employeeName;
    private DivisionDTO division;
    // getters and setters omitted
}
```

```
public class EmployeeWithDivision {
    private int id;
    private String name;
    private Division division;
    // getters and setters omitted
}
```

```
public class DivisionDTO {
    private int id;
    private String name;
    // default constructor, getters and setters omitted
}
```

```
public class Division {
    private int id;
    private String name;
    // default constructor, getters and setters omitted
}
```

### The Mapper Interface

```
@Mapper(componentModel = "spring")
public interface DivisionMapper {
    DivisionDTO divisionToDivisionDTO(Division entity);

    Division divisionDTOToDivision(DivisionDTO dto);
}
```

```
@Mapper(componentModel = "spring", uses = DivisionMapper.class)
public interface EmployeeWithDivisionMapper {
    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    @Mapping(target = "divisionDTO", source = "entity.division")
    EmployeeWithDivisionDTO employeeToEmployeeDTO(EmployeeWithDivision entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    @Mapping(target = "division", source = "dto.divisionDTO")
    EmployeeWithDivision employeeDTOtoEmployee(EmployeeWithDivisionDTO dto);
}
```

### Test Case

```
@SpringBootTest
class EmployeeWithDivisionMapperTest {
    @Autowired
    EmployeeWithDivisionMapper mapper;

    @Test
    public void employeeWithDivisionToEmployeeWithDivisionDTO() {
        EmployeeWithDivision entity = new EmployeeWithDivision(1, "a", new Division(1, "Division1"));
        EmployeeWithDivisionDTO dto = mapper.employeeToEmployeeDTO(entity);
        assertEquals(dto.getDivisionDTO().getId(), entity.getDivision().getId());
        assertEquals(dto.getDivisionDTO().getName(), entity.getDivision().getName());
    }
}
```

## Mapping With Type Conversion

### POJOs

```
public class EmployeeWithDate {
    private int id;
    private String name;
    private Date date;
    // default constructor, getters and setters omitted
}
```

```
public class EmployeeWithDateDTO {
    private int employeeId;
    private String employeeName;
    private String date;
    // default constructor, getters and setters omitted
}
```

### The Mapper Interface

```
@Mapper(componentModel = "spring")
public interface EmployeeWithDateMapper {
    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    @Mapping(target = "date", source = "entity.date", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EmployeeWithDateDTO employeeWithDateToEmployeeWithDateDTO(EmployeeWithDate entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    @Mapping(target = "date", source = "dto.date", dateFormat = "dd-MM-yyyy HH:mm:ss")
    EmployeeWithDate employeeWithDateDTOToEmployeeWithDate(EmployeeWithDateDTO dto);
}
```

### Test Case

```
@SpringBootTest
class EmployeeWithDateMapperTest {
    @Autowired
    EmployeeWithDateMapper mapper;

    @Test
    void employeeWithDateToEmployeeWithDateDTO() throws ParseException {
        EmployeeWithDate entity =
                new EmployeeWithDate(1, "John", new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01"));
        EmployeeWithDateDTO dto = mapper.employeeWithDateToEmployeeWithDateDTO(entity);

        assertEquals(dto.getEmployeeId(), entity.getId());
        assertEquals(dto.getEmployeeName(), entity.getName());
        assertEquals(dto.getDate(), "01-01-2000 00:00:00");
    }
}
```

## Mapping With Ignored Fields

### POJOs
```
public class Document {
    private int id;
    private String title;
    private String text;
    private Date modificationTime;
    // default constructor, getters and setters omitted
}
```

```
public class DocumentDTO {
    private int id;
    private String title;
    private String text;
    private List<String> comments;
    private String author;
    // default constructor, getters and setters omitted
}
```


### The Mapper Interface
```
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", ignore = true)
    DocumentDTO documentToDocumentDTO(Document entity);

    @Mapping(target = "modificationTime", ignore = true)
    Document documentDTOToDocument(DocumentDTO dto);
}
```

### Test Case
```
@SpringBootTest
class DocumentMapperTest {
    @Autowired
    DocumentMapper mapper;

    @Test
    void documentoToDocumentDTO() throws ParseException {
        Document entity = new Document(1, "a", "b", new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01"));
        DocumentDTO dto = mapper.documentToDocumentDTO(entity);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getText(), entity.getText());
        assertNull(dto.getComments());
        assertNull(dto.getAuthor());
    }
}
```


## Resources
- [Custom Mapper with MapStruct](https://www.baeldung.com/mapstruct-custom-mapper)
- [Ignoring Unmapped Properties with MapStruct](https://www.baeldung.com/mapstruct-ignore-unmapped-properties)
- [Using Multiple Source Objects with MapStruct](https://www.baeldung.com/mapstruct-multiple-source-objects)
