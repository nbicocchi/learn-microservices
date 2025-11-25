FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt ./
RUN pip install --default-timeout=100 --no-cache-dir -r requirements.txt

COPY ./scripts ./scripts
COPY ./data ./data

CMD ["sh", "-c", "while true; do python ./scripts/production.py && python ./scripts/monitoring.py; sleep 300; done"]