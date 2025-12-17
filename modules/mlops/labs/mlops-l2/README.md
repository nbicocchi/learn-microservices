# Prepare
```
pyenv install 3.13.3
pyenv local 3.13.3
pyenv virtualenv mlops
pyenv activate mlops
python -m pip install --upgrade pip
pip install -r requirements.txt
```

```
cd inference-bentoml
bentoml build <-- this outputs a name (e.g., spaceflight_service:h3f3atgjikqxhigt) 
bentoml containerize spaceflight_service:h3f3atgjikqxhigt <-- update this
```

update docker-compose.yml

```
  inference:
    image: spaceflight_service:h3f3atgjikqxhigt   # <- your generated image name:tag here
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

```
docker compose up --build -d
```

# Use

Eventually, try manual experiments with training-pipeline/notebooks

Run the automatic training pipeline to produce a model

```
curl -X POST http://localhost:8005/run-pipeline \
     -H "Content-Type: application/json"
```

Check if a new model appears in model registry: http://localhost:5000/#/models

Ask the inference engine to load a model

```
curl -X POST http://localhost:3000/import_model \
  -H "Content-Type: application/json" \
  -d '{
        "model_uri": {                     
            "model_name": "spaceflights-kedro",
            "model_version": 2
        }
      }
```

Make a test prediction on the inference engine

```
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

