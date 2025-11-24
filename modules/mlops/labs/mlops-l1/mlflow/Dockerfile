# Base image Linux, compatibile con Docker Desktop su Windows
FROM python:3.13-slim

# Copia il file requirements.txt nella root del container
COPY requirements.txt .

# Installa le dipendenze Python
RUN pip install --no-cache-dir --upgrade -r requirements.txt

# Avvia MLflow server, ascolta su tutte le interfacce
# ed accetta connessioni da host.docker.internal
ENTRYPOINT [ "mlflow", "server", "--allowed-hosts", "host.docker.internal:5000,localhost:5000" ]
