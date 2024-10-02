# MLModelMonitoring


This repo allows to emulate and monitor an **ML model** in a *distributed production environment*, simulating *training*, *testing* and *production* data.

The system is composed by 4 fundamental containerized *micro-services*:
- **MySQL DB**: to *store* and *query* necessary data;
- **simulator**: to simulate *training*, *testing* and *production* data;
- **ML model**: a trainable model that can be tested on *data batches*;
- **monitoring**: to observe the model, allowing to compute *reports* and *tests* and trigger model *re-training*.


## :grey_question: Index
- [MLModelMonitoring](#MLModelMonitoring)
    - [Index](#index)
    - [Installation](#installation)
    - [Dependencies](#dependencies)
    - [Usage](#usage)
    - [References](#references)


## :receipt: Installation
```
git clone git@github.com:semUni17/MLModelMonitoring.git
cd MLModelMonitoring
```


## :package: Dependencies
To create a general virtual environment:
```
conda env create -f environment.yml
```
Anyway, all micro-services have their own `requirements.txt` file, a `config.yml` configuration file and a `Dockerfile` to run the `Docker` container.


## :zap: Usage
To stop and run all the containers in *background*:
- `./launch.sh`

To test all the micro-services *API*, please download and install `Postman` to send requests.


## :notebook: References
- [Docker](https://www.docker.com/)
- [Postman](https://www.postman.com/)