# Working with Events and Listeners

In this module, we’re going to focus on the events support in Spring.

The relevant module for this lesson is: [working-with-events-and-listeners-end](https://github.com/nbicocchi/spring-boot-course/tree/module8/working-with-events-and-listeners-end)

## Overview

**Simply put,** **events allow us to write** [**loosely coupled**](https://en.wikipedia.org/wiki/Loose_coupling) **components** **that don’t have to be closely connected.**

It’s important to understand that these events are still entirely [synchronous](https://stackoverflow.com/a/748189/6661361) as they’re sent and processed in the same thread.

Of course, asynchronous processing can be enabled but this is not the default behavior.

It’s also critical to comprehend that the entire solution runs fully in-process; it doesn’t leave the JVM it’s running on. That means, just to be clear, that this is not a message broker or a message bus and therefore it wouldn't help communication in a distributed system.

## The Event and the Listener API

**An Event is just a regular class**, and therefore can contain as little or as much data as it needs to.

**The event listener, on the other hand, needs to be a Spring bean. It gets notified when the event is fired** and we can, of course, have multiple listeners registered and listening for the same event.

**To fire or trigger the event, we can use the Event publishing API.** The main _ApplicationContext_ provides this capability as well, as we'll see in the example shown in the next sections.

Spring will then take care then of notifying all the registered event listeners of that particular event.

## Creating a Simple Event

As with much of the other Spring functionality we explored up until this point, the Event framework is part of Spring core. As a result, we already have access to everything we need, without having to add any special dependency.

Let’s say **we want to run some logic when a new _Project_ is created**. We'll get started by creating a simple Event for when that happens.

The _ProjectCreatedEvent_ will contain a field to hold the Project Id, the respective getters and setters, and a constructor to assign the id. In a nutshell, a simple [POJO](https://www.martinfowler.com/bliki/POJO.html) with a _projectId_ field:

```
public class ProjectCreatedEvent {

    private Long projectId;

    // constructor, getters, setters

}
```

That’s it, this is our event.

Now, let’s make sure we send out or publish this event when the project actually gets created.

## Publishing the Event

To do that we need to:

-   **inject the _ApplicationEventPublisher_ in our _ProjectController_ class**
-   obtain a reference to the saved entity, which is retrieved by the service layer
-   **fire off the event using the _publishEvent_ API**

Finally, we'll redirect to the projects page. Let's see the whole process:

```
public class ProjectController {

    @Autowired
    private ApplicationEventPublisher publisher;
    
    // ...
    
    @PostMapping
    public String addProject(ProjectDto project) {
        Project newProject = projectService.save(convertToEntity(project));
        publisher.publishEvent(new ProjectCreatedEvent(newProject.getId()));
        return "redirect:/projects";
    }
}
```

And we’re done, a simple one-liner to send out the event here.

## Creating the Event Listener

Finally let’s create the event listener annotated with _@Component_, and **a method where we intend to handle the event**. The method will, of course, receive the actual event object as a parameter, indicated in its signature.

**The way we register the listener to listen for a specific event is via the _@EventListener_ annotation:**

```
@Component
public class ProjectCreatedEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectCreatedEventListener.class);

    @EventListener
    public void handleProjectCreatedEvent(ProjectCreatedEvent projectCreatedEvent) {
        LOG.info("New Project Created with Id {}", projectCreatedEvent.getProjectId());
    }
}
```

As you can see, the listener implementation is quite simple as well.

For the logic in our listener, we used a simple example of adding a log statement.

If we now run the application, and create a new _Project_ entity using the _/projects/new_ endpoint we'll be able to see the log entry in the console:

_c.b.l.e.ProjectCreatedEventListener   : New Project Created with Id 4_

## Resources
- [Spring Events](https://www.baeldung.com/spring-events)
