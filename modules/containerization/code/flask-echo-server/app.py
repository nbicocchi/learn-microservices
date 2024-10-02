from flask import Flask, request, jsonify

app = Flask(__name__)

# Route to echo the received data
@app.route('/echo', methods=['POST'])
def echo():
    data = request.json
    return jsonify({"echoed_data": data})

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=5000)