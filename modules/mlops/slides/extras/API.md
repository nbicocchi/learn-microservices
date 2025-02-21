# API Documentation

This document describes the REST API endpoints available in the MLModel Tracking and Monitoring system. The system consists of four main microservices, each with its own set of endpoints.

## Base URLs

- ML Model Service: `http://localhost:5001/root/ml`
- Tracking Service: `http://localhost:5002/root/tracking`
- Monitoring Service: `http://localhost:5003/root/monitoring`
- Simulator Service: `http://localhost:5004/root/simulator`

## Simulator Service

### GET /root/simulator
Check simulator status.

**Response**
```json
{
    "name": "simulator_name",
    "message": "Simulator"
}
```

### POST /root/simulator
Generate and load dataset.

**Parameters**
```json
{
    "set_name": "string"  // Options: "training", "testing", "production"
}
```

**Response**
```json
{
    "message": "Load {set_name} set"
}
```

## ML Model Service

### GET /root/ml
Get current model information.

**Response**
```json
{
    "name": "model_name",
    "message": "ML model"
}
```

### POST /root/ml
Evaluate model on a dataset.

**Parameters**
```json
{
    "dataset": "string"  // Options: "training", "testing", "production"
}
```

**Response**
```json
{
    "message": "Score on {dataset} set: {score}%"
}
```

### POST /root/ml/set_model
Set up a new model with specified parameters.

**Parameters**
```json
{
    "model_name": "string",  // Options: "RandomForest", "SVC", "LogisticRegression"
    "hyperparameters": {
        // Model-specific parameters
    }
}
```

**Response**
```json
{
    "message": "Model successfully uploaded"
}
```

## Tracking Service

### GET /root/tracking/experiments
Get all experiments.

**Response**
```json
{
    "message": "Experiments retrieved successfully",
    "data": [
        {
            "experiment_id": "string",
            "name": "string",
            "lifecycle_stage": "string",
            "run_count": "integer"
        }
    ]
}
```

### POST /root/tracking/experiments
Get specific experiment information.

**Parameters**
```json
{
    "experiment_id": "string",
    "request_information": "string",  // Options: "general", "best_model", "statistics"
    "filter": ["string"]  // Optional: List of metrics to filter by
}
```

**Response**
```json
{
    "message": "Information Retrieved",
    "data": {
        // Experiment-specific information
    }
}
```

### DELETE /root/tracking/experiments
Delete an experiment.

**Parameters**
```
?experiment_id=string
```

### GET /root/tracking/runs
Get runs for an experiment.

**Parameters**
```
?experiment_id=string
```

### POST /root/tracking/runs
Get specific run information.

**Parameters**
```json
{
    "experiment_id": "string",
    "run_id": "string",
    "request_information": "string",  // Options: "parameters", "metrics"
    "type": "string"  // For parameters: "data", "grid_search", "model"; For metrics: "system", "model"
}
```

### DELETE /root/tracking/runs
Delete a run.

**Parameters**
```
?experiment_id=string&run_id=string
```

### POST /root/tracking/model_management
Manage model optimization and settings. This endpoint has two modes of operation based on the `request_information` parameter.
- Optimization mode: Performs hyperparameter tuning across multiple models using grid search and cross-validation, logging results to MLflow
- Setting mode: Deploys a specific model version from a previous MLflow run for use in production

**Parameters for Optimization Mode**
```json
{
    "request_information": "optimization",
    "models": ["string"],  // List of model names to optimize
    "hyperparameters": {
        "model_name": {
            // Model-specific parameters
        }
    },
    "scoring": "string",  // Scoring metric to use
    "cv": "integer",  // Number of cross-validation folds
    "experiment_name": "string",
    "experiment_description": "string",
    "experiment_owner": "string"
}
```

**Response for Optimization Mode**
```json
{
    "message": "Experiments retrieved successfully",
    "data": {
        // Experiment and model IDs
    }
}
```

**Parameters for Setting Mode**
```json
{
    "request_information": "setting",
    "run_id": "string"  // ID of the run containing the model to set
}
```

**Response for Setting Mode**
```json
{
    "message": "Model successfully uploaded"
}
```

## Monitoring Service

### GET /root/monitoring
Get monitoring status.

**Response**
```json
{
    "name": "monitoring_name",
    "message": "Monitoring"
}
```

### POST /root/monitoring
Compute monitoring metrics.

**Parameters**
```json
{
    "metric": "string",  // Options: "report", "tests", "summary"
    "tests": {
        // Optional: Test configurations
    }
}
```

**Response**
```json
{
    "message": "string"  // Result of monitoring computation
}
```

## Error Responses

All endpoints may return the following error responses:

```json
{
    "error": "string",
    "message": "string"
}
```

Common HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 404: Not Found
- 500: Internal Server Error
