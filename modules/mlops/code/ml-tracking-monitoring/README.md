# MLModel Tracking and Monitoring

This repository supports tracking and optimizing machine learning experiments in a distributed production environment, 
with a primary focus on parameter optimization through a *Grid Search* approach. It enables emulation and monitoring of 
an ML model across various stages in production, simulating workflows with *training*, *testing*, and *production* data.

The system integrates with **MLflow** for:

- Logging model parameters and metrics via API,
- Accessing experiment data for analysis and comparison,
- Selecting the best-performing model and deploying it in a simulated production environment.

**Evidently AI** is also leveraged to test and monitor both data and the ML model in a production environment, evaluating and tracking the quality of data and predictions.

## Core Components

The system comprises five primary containerized microservices:

- **PostgreSQL Database**: Stores and organizes all relevant experiment data;
- **Simulator**: Generates simulated *training*, *testing*, and *production* datasets;
- **ML Model Service**: Manages a trainable ML model that can be evaluated on simulated data batches;
- **Tracking Service**: Handles experiment optimization, logging, and retrieval of metadata and artifacts;
- **Monitoring Service**: To observe the model, allowing to compute reports and tests and trigger model re-training.

This setup enables comprehensive tracking and monitoring of machine learning models in a production-like environment, supporting both experimentation and long-term model maintenance.

Here is the workflow of the system:
```
Training Data → Multiple Experiments → Track Results → Select Best Model → Deploy → Monitor
```

## Index
- [MLModelTrackingMonitoring](#ML Model Tracking and Monitoring)
    - [Dependencies](#dependencies)
    - [Usage](#usage)
    - [References](#references)

## Dependencies
To create a virtual environment and install all required dependencies:

```
python -m venv venv && source venv/bin/activate && pip install -r requirements.txt
```
Each microservice also has its own `requirements.txt` file, along with a `config.yml` configuration file and a `Dockerfile` to build the corresponding Docker container.

## Usage
Please ensure that you have Docker installed on your machine before proceeding and that the Docker daemon is running.

To start and stop all containers in background mode:

```
chmod +x launch.sh
./launch.sh
```

If you encounter a platform-specific issue, you may need to change the platform in the docker-compose.yml file.

The system will start the following services:
- PostgreSQL Database (port 5432)
- ML Model Service (port 5001)
- Tracking Service (port 5002)
- Monitoring Service (port 5003)
- Simulator Service (port 5004)

To test the microservices APIs, use Postman to send requests. A complete Postman collection is provided in the `postman` directory.
Each postman request corresponds to a specific step in the [5 - Project Emulation Steps.md](../../slides/5%20-%20Project%20Emulation%20Steps.md) file.

For detailed API documentation, see the [API Documentation.md](../../slides/extras/API.md) file.


## References
- [Docker](https://www.docker.com/)
- [Postman](https://www.postman.com/)
- [MLflow](https://www.mlflow.org/)
- [Evidently AI](https://evidentlyai.com/)