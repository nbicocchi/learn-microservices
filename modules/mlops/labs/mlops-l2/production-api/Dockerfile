FROM python:3.13-slim

# Set working directory
WORKDIR /app

# Copy and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app folder and data folder
COPY app/ .

# Expose FastAPI port
EXPOSE 8100

# Run the app
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8100"]
