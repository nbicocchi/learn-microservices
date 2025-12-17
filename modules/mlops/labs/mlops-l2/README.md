# Setup

## Python environment

Create and activate a dedicated Python environment using **pyenv**:

```bash
pyenv install 3.13.3
pyenv local 3.13.3
pyenv virtualenv mlops
pyenv activate mlops

python -m pip install --upgrade pip
pip install -r requirements.txt
```

---

## Build the inference service (BentoML)

Move to the BentoML inference project and build the service:

```bash
cd inference-bentoml
bentoml build
```

This command outputs a service identifier, for example:

```
spaceflight_service:h3f3atgjikqxhigt
```

Use the generated identifier to build the container image:

```bash
bentoml containerize spaceflight_service:h3f3atgjikqxhigt
```

> ⚠️ Replace the example tag with the one generated on your machine.

---

## Update `docker-compose.yml`

Edit the `inference` service definition to reference the newly built image:

```yaml
inference:
  image: spaceflight_service:h3f3atgjikqxhigt   # ← your generated image name:tag
  pull_policy: never
  environment:
    - MLFLOW_TRACKING_URI=${MLFLOW_TRACKING_URI}
  depends_on:
    - mlflow
  ports:
    - "3000:3000"
  healthcheck:
    test: [ "CMD", "curl", "-f", "http://localhost:3000/healthz" ]
    interval: 20s
    timeout: 15s
    retries: 10
```

Start the full stack:

```bash
docker compose up --build -d
```

---

# Usage

## Model training

Optionally, explore and experiment manually using the notebooks in:

```
training-pipeline/notebooks
```

To run the **automatic training pipeline** and produce a new model:

```bash
curl -X POST http://localhost:8005/run-pipeline \
     -H "Content-Type: application/json"
```

Verify that a new model appears in the MLflow Model Registry:

```
http://localhost:5000/#/models
```

---

## Load a model into the inference engine

Ask the inference service to load a specific model version:

```bash
curl -X POST http://localhost:3000/import_model \
  -H "Content-Type: application/json" \
  -d '{
        "model_uri": {
          "model_name": "spaceflights-kedro",
          "model_version": 2
        }
      }'
```

---

## Run a test prediction

Send a sample prediction request to the inference service:

```bash
curl -X POST http://localhost:3000/predict \
  -H "Content-Type: application/json" \
  -d '{
        "input_data": {
          "engines": 2.0,
          "passenger_capacity": 5,
          "crew": 1.0,
          "d_check_complete": true,
          "moon_clearance_complete": true,
          "iata_approved": false,
          "company_rating": 4.5,
          "review_scores_rating": 4.7
        }
      }'
```

---

## Dataset generation and analysis

### Generate a reference dataset

Creates a reference dataset with predictions.
Input data are read from:

```
production-api/data/reference.csv
```

Classification labels are produced by the **inference-bentoml** service.

```bash
curl -X GET http://localhost:8100/reference
```

---

### Generate a current dataset

Creates a current dataset with predictions.
Input data are read from the same CSV file but are optionally drifted, depending on the `drift` parameter.

```bash
curl -X POST http://localhost:8100/current \
  -H "Content-Type: application/json" \
  -d '{
        "drift": true
      }'
```

---

### Analyze data drift and performance changes

Run the analysis comparing reference and current datasets:

```bash
curl -X GET http://localhost:8100/analyze
```

Finally, restart the **Evidently AI** service and inspect the results in the UI.
