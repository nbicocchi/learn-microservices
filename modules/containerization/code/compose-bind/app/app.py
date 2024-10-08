from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return "Hello from Flask and Docker with Bind Mount!"


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug="True",port=5000)
