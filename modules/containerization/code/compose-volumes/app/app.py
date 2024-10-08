from flask import Flask, request, jsonify
import datetime
import os

app = Flask(__name__)

LOG_FILE_PATH = '/data/log.txt'

@app.route('/echo', methods=['POST'])
def echo():
    data = request.json 
    with open(LOG_FILE_PATH, 'a') as f:
        f.write(f"I echoed the message  {data} at: {datetime.datetime.now()}</br>\n")
    return jsonify({"echoed_data": data})


@app.route('/logs')
def read_logs():
    if os.path.exists(LOG_FILE_PATH):
        with open(LOG_FILE_PATH, 'r') as f:
            log_content = f.read()
        return log_content
    else:
        return "Log file not found!", 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=5000)