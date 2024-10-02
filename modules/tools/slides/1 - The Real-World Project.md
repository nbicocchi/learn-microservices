# The Real-World Project We’re Building

## Creating the Project

The example application we've chosen to serve as the code base for the course is a task management application. [**Spring Initializr**](https://start.spring.io/) is useful a quickstart generator for Spring projects.

We'll choose a Maven project using Java, and we need to fill in the project details:

-   Group: com.baeldung
-   Artifact: real-world-project

For the dependencies section, we'll choose the "Spring Web" dependency. Even though we won't be focusing on web aspects in this module, we include it since it allows us to start the application and keep it running.

![](images/m1-start-spring-io.png)

Now we can click the "Generate" button to download the project, unzip it and import it into an IDE.

If you are using IntelliJ, you can import the project by navigating to the main menu, select File > Open, and then navigating to the path where our project is present to add it as a Project.

## Adding the Persistence Layer
We'll create a simple [persistence layer](https://en.wikipedia.org/wiki/Persistence_(computer_science)#Persistence_layers) under the package _com.baeldung.persistence.model_, by adding a _Project_ class.

```
public class Project {

    private Long id;

    private String name;

    private LocalDate dateCreated;

    public Project() {
    }

    public Project(Long id, String name, LocalDate dateCreated) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
    }
    
    public Project(String name, LocalDate dateCreated) {
        this(null, name, dateCreated);
    }

    public Project(Project project) {
        this(project.getId(), project.getName(), project.getDateCreated());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name) && Objects.equals(dateCreated, project.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateCreated);
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", name='" + name + '\'' + ", dateCreated=" + dateCreated + '}';
    }
}
```
This class will have 3 basic attributes: the _id_, _name_ and _dateCreated_.

Next, we'll add the [repository](https://martinfowler.com/eaaCatalog/repository.html) under the package _com.baeldung.persistence.repository_:

```
public interface IProjectRepository {
    Optional<Project> findById(Long id);
    Collection<Project> findAll();
    Project save(Project project);
}
```

For now, we can simulate persistence for these simple operations, with an in-memory _ArrayList_ under the package _com.baeldung.persistence.repository.impl_:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    private final List<Project> projects = new ArrayList<>();

    @Override
    public Optional<Project> findById(Long id) {
        return projects.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Collection<Project> findAll() {
        return projects;
    }

    @Override
    public Project save(Project project) {
        Project toSave = new Project(project);
        if (Objects.isNull(toSave.getId())) {
            toSave.setId(new Random().nextLong(1_000_000L));
        }
        Optional<Project> existingProject = findById(project.getId());
        if (existingProject.isPresent()) {
            projects.remove(existingProject);
        }
        projects.add(toSave);
        return toSave;
    }
}
```

## Adding the Service Layer
Moving on to the [service layer](https://en.wikipedia.org/wiki/Multitier_architecture#Common_layers), we'll add a similar service interface under the package _com.baeldung.service_ and class name as _IProjectService_ with an implementation under the package _com.baeldung.service.impl_:

```
public interface IProjectService {
    Optional<Project> findById(Long id);
    Collection<Project> findAll();
    Project save(Project project);
}
```

```
@Service
public class ProjectServiceImpl implements IProjectService {

    private IProjectRepository projectRepository;

    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Collection<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }
}
```

Here, we’re simply autowiring the Project Repository and implementing the exact same operations and just delegating to the repository.

The _@Repository, @Service_ and _@Autowired_ annotations we've used are Spring annotations that define and connect the 2 classes. 

## Adding the Presentation Layer
Moving on to the [presentation layer](), we'll add a similar service interface under the package _com.baeldung.controller_ and class name as _IProjectController_ with an implementation under the package _com.controller.service.impl_:

```
@RequestMapping(value = "/projects")
public interface IProjectController {
    @GetMapping(value = "/{id}")
    Project findOne(@PathVariable Long id);

    @GetMapping
    Collection<Project> findAll();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Project create(@RequestBody Project project);
}
```

```
@RestController
public class ProjectController implements IProjectController {
    IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public Project findOne(Long id) {
        return projectService.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @Override
    public Collection<Project> findAll() {
        return projectService.findAll();
    }

    @Override
    public Project create(Project project) {
        return projectService.save(project);
    }
}
```

## _ApplicationRunner_ and _CommandLineRunner_ Interfaces

In Spring Boot, _CommandLineRunner_ and _ApplicationRunner_ are two interfaces that allow you to execute code when a Spring Boot application starts. They are typically used to perform some initialization or setup tasks before the application starts processing requests. Both interfaces have a single run method that you need to implement.

```
@SpringBootApplication
public class LsApp implements ApplicationRunner {
    IProjectRepository projectRepository;

    public LsApp(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(LsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        projectRepository.save(new Project("P1", LocalDate.now()));
        projectRepository.save(new Project("P2", LocalDate.now()));
        projectRepository.save(new Project("P3", LocalDate.now()));
    }
}
```

## Resources
- [Refactor Operations in Eclipse](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fref-menu-refactor.htm)
- [Refactor Operations in IntelliJ](https://www.jetbrains.com/help/idea/refactoring-source-code.html)
