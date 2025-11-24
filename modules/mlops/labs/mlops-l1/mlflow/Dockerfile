#FROM python:3.13-slim

#RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

#COPY requirements.txt .
#RUN pip install --no-cache-dir --upgrade -r requirements.txt

#ENTRYPOINT [ "mlflow", "server" ]



# Base image Linux, compatibile con Docker Desktop su Windows
FROM python:3.13-slim

# Copia il file requirements.txt nella root del container
COPY requirements.txt .

# Installa le dipendenze Python
RUN pip install --no-cache-dir --upgrade -r requirements.txt

# Imposta MLflow come entrypoint e ascolta tutte le interfacce
ENTRYPOINT ["mlflow", "server"]
