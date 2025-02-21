FROM python:3.10
WORKDIR /app
COPY requirements.txt requirements.txt

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

COPY . /app

EXPOSE 5004
CMD ["python", "src/launch.py", "-cfg_pth", "src/config/template/config.yml"]
