FROM python:3.11

ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1

WORKDIR /app

# Installa dipendenze di sistema
#RUN apt-get update && apt-get install -y build-essential && rm -rf /var/lib/apt/lists/*

COPY pyproject.toml requirements.txt ./

# Installa dipendenze Python
RUN pip install --no-cache-dir -r requirements.txt

COPY src ./src
COPY conf ./conf
COPY data ./data
COPY app.py ./

# Avvia FastAPI con Uvicorn
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8005"]
