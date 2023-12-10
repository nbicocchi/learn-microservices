# A Simple Development Environment

In this lesson, we'll have a look at how to set up our development environment for Java and Spring.

We'll also look at the 2 most popular Java [IDEs](https://en.wikipedia.org/wiki/Integrated_development_environment): Eclipse and IntelliJ.


## Preparing the Environment

First, let’s make sure the environment is set up properly.

### Java

Let’s check that Java is installed locally. We can do this by opening the command line window and running the following commands:

```
$ java
Usage: java [options] <mainclass> [args...]
           (to execute a class)
   or  java [options] -jar <jarfile> [args...]
           (to execute a jar file)
   or  java [options] -m <module>[/<mainclass>] [args...]
       java [options] --module <module>[/<mainclass>] [args...]
           (to execute the main class in a module)
   or  java [options] <sourcefile> [args]
           (to execute a single source-file program)
   ...
   
$ java -version
java version "17.0.7" 2023-04-18 LTS
```

This shows that Java is installed correctly.

We won't go over installing a JDK here because, while we’re starting from scratch with Spring, the course here assumes you’ve done Java development before. **If you don't have Java installed**, check out the [official download page](https://www.oracle.com/java/technologies/downloads/).

### Git

Let’s also check that Git is available. For this, you can type the following commands:

```
$ git
usage: git [-v | --version] [-h | --help] [-C <path>] [-c <name>=<value>]
           [--exec-path[=<path>]] [--html-path] [--man-path] [--info-path]
           [-p | --paginate | -P | --no-pager] [--no-replace-objects] [--bare]
           [--git-dir=<path>] [--work-tree=<path>] [--namespace=<name>]
           [--super-prefix=<path>] [--config-env=<name>=<envvar>]
           <command> [<args>]
           ...
           
$ git --version
git version 2.39.2 (Apple Git-143)
```

We’re going to need this later while cloning our project from GitHub.

**If you don't have Git installed**, have a look at the [](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)[official installation guide](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

### Maven

First, note that the IDE we choose will already contain a Maven plugin, so **you can use Maven from the IDE** without a separate installation.

Therefore installing a local instance of Maven is entirely optional. This is useful if you want to run Maven commands from the command line, or change the default installation in the IDE. Check out this guide on [Installing Maven](https://www.baeldung.com/install-maven-on-windows-linux-mac) for more detailed instructions on this.

## Checkout the Course Codebase

Next, let's set up the course [repository](https://en.wikipedia.org/wiki/Repository_(version_control)) on the local machine.

First, let's go to the project on GitHub, select the 'clone' option, and copy the URL [https://github.com/nbicocchi/spring-boot-course).

Then in a command line/PowerShell, let's move to the directory where we want to clone our repository (e.g., "_/$HOME/Desktop_") and execute the corresponding git command:

```
$ git clone https://github.com/nbicocchi/spring-boot-course
```

Next, let's move to the repository directory and **checkout the _module1_ branch**:

```
$ cd learn-spring 
$ git status 
$ git checkout module1
```


## Eclipse IDE

Now, let’s download Eclipse. For this course, **we'll use the** [**Eclipse STS (Spring Tools)**](https://spring.io/tools) **version**, which is a version of Eclipse with additional built-in support for easier Spring development.

Browse the link above and download the Eclipse bundle for your specific OS. Depending on the OS, you might have to:

-   unzip the file and execute the application file (Linux)
-   install the application right away (Mac)
-   open the self-extracting file and run the application file afterwards (Windows)

There are a lot of interesting features provided by Eclipse to help in the development process. Here are a few that you should try out.

### Perspectives and Views

Eclipse organizes the different development aspects using Views (e.g., ‘Package Explorer”, “JUnit”, “Console”, etc.) and Perspectives, which are well-defined collections of Views (e.g., for “Java” development or for “Debug” purposes).

We can clean up the perspective by moving, hiding, or removing the functional views based on our personal preferences.

### Save Action Effects

You can indicate the IDE to automatically organize imports or format the source code whenever you save changes in a file. You can access this by navigating to the Preferences menu option > Java > Editor > Save Action.

### JDK and Maven Installations

You can configure which JDK and Maven instances the IDE should use.

To add a JDK to the setup, we can navigate to Preferences > Java > Installed JREs > Add > Standard VM > browse the JDK Home directory > click on “Finish”. Then you can select which instance you want to activate.

For Maven, we can proceed similarly, Preferences > Maven > Installations > Add > search the Maven Home directory for the instance you downloaded > Finish.

### Formatters and Code Styles

As with most IDEs, Eclipse allows setting up a formatter to apply and maintain a consistent code style. You can navigate to Java > Code Style > Formatter. You can import the formatter from our code case.

### Import, Build, and Launch Projects

Now we can **import our first lesson project as a Maven project** by following these steps:

-   click on File > Import > Maven > Existing Maven Projects
-   “Browse” the repo directory (it will show up the Maven modules for each lesson)
-   and select “Finish” to import the projects

Let’s run a simple Maven build on it: _right-click on the project_ (using the “Package Explorer” or the “Project Explorer” views) > _Run As_ > _Maven build_. You should be able to see the operation output in the “Console” View.

Finally, we can run the application using the Boot Dashboard.

A quick heads up, we’ll be exploring the project in another lesson, so you can disregard the actual implementation and functionality at this stage.

In each lesson, I'll use common **shortcuts in the IDE** to ease development. Check out this guide with the most useful [shortcuts for Eclipse](https://www.baeldung.com/eclipse-shortcuts).

## IntelliJ IDE

[IntelliJ](https://www.jetbrains.com/idea/download/#section=linux) is also a very popular Java IDE.

The codebase of this course and all my other courses is simply a Maven project, so it works absolutely fine with any IDE. I personally use both IntelliJ and Eclipse and, for recording, happen to use Eclipse mostly for continuity.

Let’s see how we can install IntelliJ. We'll google it and get the community edition, as that's sufficient at this point. Unlike Eclipse, this needs to be installed, not just un-archived.

Now let’s see how to import a project. We can select 'Import Project', then select the actual lesson from 'external sources - Maven'. We'll check: 'import Maven project automatically', add a JDK, and we're done.

For more tips & tricks on using IntelliJ, this guide to [IntelliJ Basics](https://www.baeldung.com/intellij-basics) will get you started with the most common setup steps and useful shortcuts.

...

Finally, **we can run the project by right-clicking the main _LsApp class > Run ‘LsApp’_ option**. And we're done; the full environment is up and running.

Same as Eclipse, **IntelliJ provides shortcuts** to speed up development. You can find the most popular ones in this guide: [Common Shortcuts in IntelliJ IDEA](https://www.baeldung.com/intellij-idea-shortcuts).

## Resources
- [Eclipse STS](https://spring.io/tools)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=linux)
- [Install Java on Ubuntu](https://www.baeldung.com/ubuntu-install-jdk)
- [Install Java on Windows](https://www.java.com/en/download/help/windows_manual_download.html)
- [Install Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
- [Install Maven](https://www.baeldung.com/install-maven-on-windows-linux-mac)
