# Base image Linux, compatibile con Docker Desktop su Windows
FROM python:3.13-slim

# Setta la working directory nel container
WORKDIR /app

# Copia i requirements
COPY requirements.txt .

# Aggiorna pip e installa le dipendenze
RUN pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt

# Esponi la porta predefinita di MLflow
EXPOSE 5000

ENTRYPOINT ["mlflow", "server", "--host", "0.0.0.0", "--port", "5000", "--allowed-hosts", "mlflow:5000,localhost:5000,host.docker.internal:5000"]
