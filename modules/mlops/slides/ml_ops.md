# MLOPS

## Introduction
Nowadays, **Machine Learning** (*ML*) systems are widely adopted by companies and are now part of people's daily lives.
If once these systems remained almost "confined" in research laboratories, now they are placed in *production environments*, therefore having to be maintained in order to avoid performance drops and poor service to users.

In the traditional **ML pipeline**, the goal is often to perform experiments, not caring about the *inference* phase. For this reason, even the chosen programming environment refers to *Notebooks* and non-engineered codes.

These limitations have allowed the birth of the *MLOps*, that defines a series of practice to design, develop and then maintain an *ML model* into a *production environment* throughout its life-cycle.

### DevOps

The so called figure of **MLOps engineer**, born in recent years, is based on the **DevOps** concept but applied to *Artificial Intelligence* (*AI*) systems.

*DevOps* is a movement that defines a methodology based on continuous software development (**CI**/**CD**), people, communication and technologies, to well deploy and maintain a software project.

Based on *DevOps*, *MLOps* helps the companies to serve new *ML models* to the users, relying on specific standardized process and tools.


## MLOps Principle

### Iterative-Incremental Process
*MLOps* process can be splitted in 3 different and fundamental phases:
- **design**: dedicated to *business understanding*, identifying potential users, design and methods to evaluate the project development;
- **model development**: where a step of *data engineering* is performed, the *ML model* is choosen, and the *ML model* is finally tested and validated;
- **operations**: practices that regolate the *ML model* deployment, following *CI*/*CD* pipelines, keeping the *ML model* monitored once it is put into the *production environment*.

### Automation
In this context the concept of **maturity** is defined, that represents the automation level reached by the project, in the *training* and releasing updated *ML models*. It is found 3 levels:
- level 0: **manual process**;
- level 1: **ML pipeline automation**;
- level 2: **CI**/**CD** **pipeline automation**.

### Continuous X
The deployment of a *ML model* rely on many different practices:
- **Continuous Integration** (**CI**): *ML model* *testing* and *validation* phases; 
- **Continuous Delivery** (**CD**): delivery a *training* pipeline to automatically deploy the *ML model*;
- **Continuous Training** (**CT**): practice to automatically re-train and re-deploy the *ML model*;
- **Continuous Monitoring** (**CM**): monitor the *ML model* performance with new real *production data*.

### Versioning
The goal of *versioning* is to strongly track the *ML model* and *dataset*, in order to facilitate the *reproducibility* and possible switch to new or better (also older) versions of the *ML model*.
In this step the *ML model* needs to be accompained also by *training* and *testing* data, specification, documentation, *hyper-parameters* and configurations, with all the essential artifacts to replicate results and deploy the *ML model* in a safety way.

### ML-based Software Delivery Metrics
In the deployment and delivery phases, 4 principle metrics are taken into account to measure and improve the software delivery:
- **deployment frequency**: how often the code is deployed and released to the users;
- **lead time for changes**: time from commit changes to release into *production environment*;
- **mean time to restore**: time to restore service when an incident occurs;
- **change fail percentage**: how often a change results in degrated service.


## MLOps Pillars

### Data Engineering

#### Data Ingestion
In this stage the data are collected from various sources, typically having different formats.
The collected raw data are then transformed into **features** through a *feature engineering* process.
Finally, the data are saved and stored into a **DB**, to compose the so called *dataset*.

#### Exploration and Validation
In this stage (also called *Exploratory Data Analysis*, **EDA**), the dataset is scanned and analyzed by the user to ensure the quality of the data, also having a better understanding of the data it-self.

Typically a further step to clean the data is performed (**data cleaning**), in order to correct errors that could be present in the dataset (*missng values*, *duplicate samples*, *out-liers*, etc...).

#### Data Splitting
The *dataset* is divided into more subsets, to feed the *ML model*. Are typically defined:
- *training* set;
- *validation* set;
- *testing* set.


### Model Engineering

#### Model Training
The *ML model* is *trained* on the *training* set. In this step all the different *hyper-parameters* are *fine-tuned* by the user, wanting to find the best configuration.
The *ML model* can be trained in 2 main ways:
- *offline training*: trained with already collecting data;
- *online training*: re-trained with updated (*production*) data;

#### Model Testing
Before to put the *ML model* into a *production environment*, some tests are performed to validate the measurements with respect to the expected requirements.
The metrics refers to 2 main aspects:
- *statistical*: tied to the *ML model* performance scores (*accuracy*, *precision*, *F1-score*, etc...);
- *computational*: tied to resource usage (*CPU*, *GPU*, *latency*, etc...).

#### Model Packaging
The *ML model* is finally exported into a specific format, making easier the distribution and the usage of the *ML model* by the *business application*.


### Model Deployment
#### Model Serving
In this step the *ML model* is ready to be deployed into the *production environment* to be accessed and used by the final users from its specific *API*.

#### Model Monitoring
In this last crucial stage the *ML model* is observed and monitored, to ensure that his performance remains consistent, if not, a signal for a *re-training* is typically triggered.

Usually some *logs* are generated to provide useful information about the *ML model* state.
