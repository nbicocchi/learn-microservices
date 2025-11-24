FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY ./scripts ./scripts

ENV PYTHONUNBUFFERED=1 \
    REFERENCE_PATH=/app/data

CMD ["python", "/app/scripts/upload_reference.py"]
