# The Real-World Project We’re Building

In this lesson, we'll get started with the project we'll be using throughout the course.

The relevant module for this lesson is: [real-world-project](https://github.com/nbicocchi/spring-boot-course/tree/module1/real-world-project).

## Creating the Project

Now that we have a broad understanding of Spring, we can start building our project.

The example application we've chosen to serve as the code base for the course is **a task management application**.

We’re going to be implementing everything from scratch, step by step.

Let’s start by creating a simple project. **We’ll use the highly useful** [**Spring Initializr**](https://start.spring.io/) **project,**  a quickstart generator for Spring projects.

We'll choose a Maven project using Java, and we need to fill in the project details:

-   Group: com.baeldung
-   Artifact: task-management-app-lesson

We can also enter a custom package name.

For the dependencies section, we'll choose the “Spring Web” dependency. Even though we won't be focusing on web aspects in the first modules of the course, we include it at this point since it allows us to start the application and keep it running.

**![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://cdn.filestackcontent.com/r4mDDSeSPyXKiotEbu70)**

Now we can click the “Generate” button to download the project, unzip it and import it into an IDE.

**Note**: we'll discuss most of these - Maven and the POM, the exact dependencies, etc - in individual lessons in the course, so don't worry if you don't fully understand these at this point.

## Importing the Project in the IDE

For Eclipse, we can click on File > Import > Maven > Existing Maven Projects, “Browse” the directory where we extracted the Maven Project and select “Finish” to import the project:

**![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://cdn.filestackcontent.com/byypUZopT2Sbi9dfaBr2)**

If you are using IntelliJ, you can import the project by navigating to the main menu, select File > Open, and then navigating to the path where our project is present to add it as a Project.

**![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://cdn.filestackcontent.com/d5Yn7lBQRBm5UuKt3Nvj)**

Let’s make a quick change in the project using the IDE to confirm everything is working as expected before proceeding. Let’s rename the main Boot TaskManagementAppLessonApplication class to simply LsApplication.

For this, we can either:

-   search for the class in the “Project (or Package) Explorer” pane of the IDE, right click on it and go to the Refactor > Rename option
-   or use the following shortcut:

| Eclipse - Windows | Eclipse - Mac | IntelliJ - Windows | IntelliJ - Mac |
| --- | --- | --- | --- |
| Alt + ⇧ + R | ⌥ + ⌘ + R | Shift + F6 | Ctrl + Alt + Shift + T |

In the video you might notice we’re also re-formatting the class using the following shortcut command:

| Eclipse - Windows | Eclipse - Mac | IntelliJ - Windows | IntelliJ - Mac |
| --- | --- | --- | --- |
| Ctrl + Shift + F | Ctrl + Shift + F | Ctrl + Alt + L | ⌘ + L |

## Adding the Basic Persistence and Service Layers

We'll create a simple [persistence layer](https://en.wikipedia.org/wiki/Persistence_(computer_science)#Persistence_layers) under the package _com.baeldung.persistence.model_, by adding a _Project_ class.

To do this, we can either:

-   Right-click on the Package or Project Explorer View where we want to add the new resource and select the New > Class (or Package)
-   Open the class wizard using the following shortcut:

| Eclipse - Windows | Eclipse - Mac | IntelliJ - Windows | IntelliJ - Mac |
| --- | --- | --- | --- |
| Ctrl + N | ⌘ + N | Alt + Insert | ⌘ + N |

```
public class Project {

    private Long id;

    private String name;

    private LocalDate dateCreated;
    
    // constructors, getters, setters, equals, hashCode

}
```
This class will have 3 basic attributes: the _id_, _name_ and _dateCreated_.

Next, we'll add the [repository](https://martinfowler.com/eaaCatalog/repository.html) under the package _com.baeldung.persistence.repository_:

```
public interface IProjectRepository {

    Optional<Project> findById(Long id);

    Project save(Project project);
}
```

Since persistence is the focus of a future module, we’ll now simulate persistence for these simple operations, with an in-memory _ArrayList_ under the package _com.baeldung.persistence.repository.impl_:

```
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    List<Project> projects = new ArrayList<>();

    @Override
    public Optional<Project> findById(Long id) {
        return projects.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public Project save(Project project) {
       // 
    }

}
```

Let’s now implement the save operation covering both create and update semantics:

```
@Override
public Project save(Project project) {
    Project existingProject = findById(project.getId()).orElse(null);
    if (existingProject == null) {
        projects.add(project);
        return project;
    } else {
        projects.remove(existingProject);
        Project newProject = new Project(project);
        projects.add(newProject);
        return project;
    }
}
```

Let’s also had this new, useful _Project_ constructor:

```
public class Project {

    // ...

    public Project(Project project) {
        this(project.getId(), project.getName(), project.getDateCreated());
    }
}
```

Moving on to the [service layer](https://en.wikipedia.org/wiki/Multitier_architecture#Common_layers), we'll add a similar service interface under the package _com.baeldung.service_ and class name as _IProjectService_ with an implementation under the package _com.baeldung.service.impl_:

```
@Service
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private IProjectRepository projectRepository;

    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }
}
```

Here, we’re simply autowiring the Project Repository and implementing the exact same operations and just delegating to the repository.

The _@Repository, @Service_ and _@Autowired_ annotations we've used are Spring annotations that define and connect the 2 classes. We'll go into more details regarding each concept in the next lessons.

## Upgrade Notes

The lesson was recorded with Spring Boot version 2.1.x, but the codebase is constantly updated to the latest version. There is no other difference in the content of the lesson between the 2 versions.

Also, you maybe notice the Initializr interface looks slightly different, as this is also being improved over time. You now have the option to choose the Java version as well. You can use any version equal to or higher than Java 8, as long as you have the specific JDK installed.

## Errata

In the video, the method _findById()_ use the "==" operator for comparison; in the code, the method _equals()_ is used to compare the _Long_ _id_ values.

Also, in the main _Project_ constructor, we want to set a random _id_ if it was not provided as a parameter:

```
public Project(Long id, String name, LocalDate dateCreated) {
    if(Objects.isNull(id)) {
        id = new Random().nextLong();
    }
    this.id = id;
    this.name = name;
    this.dateCreated = dateCreated;
}
```

## Resources

We've seen a simple refactor action in this lesson - moving a class from one package into another.

Over the span of the entire course, we'll naturally focus on a number of these refactoring operations.

Here's a full reference you can always consult, if you need to:

-   [Refactor Operations in Eclipse](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fref-menu-refactor.htm)
-   [Refactor Operations in IntelliJ](https://www.jetbrains.com/help/idea/refactoring-source-code.html)
