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
bento build
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

# builds a model

```
curl -X POST http://localhost:8005/run-pipeline \
     -H "Content-Type: application/json"
```

# try manual experiments with jupiter-lab

# check models on mlflow
http://localhost:5000/

# check inference engine
http://localhost:3000/


