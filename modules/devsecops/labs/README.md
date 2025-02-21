# Labs

## Lab 1: Setting Up GitHub Actions for Continuous Integration
**Objective:** Learn to configure GitHub Actions to automate the build and testing process of a Maven-based Java project.

**Instructions:**
- Create a new Maven-based Java project and push it to a GitHub repository.
- Navigate to the repository's **Actions** tab and set up a new workflow for Java with Maven.
- Customize the workflow file to include steps for:
    - Checking out the repository.
    - Setting up Java and Maven environments.
    - Building the project using Maven.
    - Running unit tests.
- Commit the workflow file and verify that the pipeline runs on each push or pull request.


## Lab 2: Automating Dependency Checks with GitHub Actions
**Objective:** Use GitHub Actions to ensure the project's dependencies are up-to-date and secure.

**Instructions:**
- Add the **OWASP Dependency-Check** plugin to the Maven project for vulnerability scanning.
- Extend the GitHub Actions workflow to include a step that runs the Maven `dependency-check:check` goal.
- Configure the workflow to fail if vulnerabilities are detected.
- Push a commit with a vulnerable dependency and observe how the pipeline reacts.


## Lab 3: Automating Code Quality Checks with GitHub Actions
**Objective:** Integrate static code analysis tools into a GitHub Actions pipeline for quality assurance.

**Instructions:**
- Add the **SpotBugs Maven Plugin** to the project for static code analysis.
- Update the GitHub Actions workflow to include a step that runs `mvn spotbugs:check` as part of the build process.
- Push code with intentional code smells or errors and observe how the pipeline flags them.
- Customize the workflow to generate and archive SpotBugs reports as artifacts.


## Lab 4: Deploying a Maven Project Using GitHub Actions
**Objective:** Learn to deploy a Maven-based Java project to an artifact repository or a cloud platform using GitHub Actions.

**Instructions:**
- Set up a free Nexus Repository or a Docker Hub account to host your application's artifacts.
- Modify the GitHub Actions workflow to include a deployment step that:
    - Builds the project.
    - Packages it into a `.jar` file.
    - Pushes the artifact to Docker Hub.
- Configure secrets in the GitHub repository for secure authentication during deployment.
- Push a release commit and verify that the deployment step executes successfully.


# Questions
Here are 10 questions without bold formatting:

1. What are the key steps in the process of building and publishing a new version of a container image to a registry?
2. How does the DevOps model differ from the traditional Waterfall and Agile models?
3. List and explain the seven stages of the DevOps lifecycle.
4. What are the main benefits of DevOps automation, and which tools are commonly used to support it?
5. What are the 7 C's of DevOps, and how do they contribute to the overall success of DevOps practices?
6. What are the main KPIs used to measure the effectiveness of a DevOps pipeline, and what does each metric track?
7. In what ways does DevSecOps integrate security practices into the CI/CD pipeline, and what are its benefits?
8. What challenges are typically faced when implementing DevSecOps in an organization?
9. Explain the role of security testing and monitoring within the DevSecOps process.
10. What is the significance of the 'Mean Time to Recovery' (MTTR) metric in DevOps, and how can it be reduced to improve service reliability?