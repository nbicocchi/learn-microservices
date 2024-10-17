FROM python:3.8.5
WORKDIR /app
COPY requirements.txt requirements.txt

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

COPY . /app

EXPOSE 5000
CMD ["python", "src/launch.py", "-cfg_pth", "src/config/config.yml"]
