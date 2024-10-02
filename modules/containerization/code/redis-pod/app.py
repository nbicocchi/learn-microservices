from flask import Flask, request, jsonify
import redis

app = Flask(__name__)

# Connect to Redis
r = redis.Redis(host='redis', port=6379, db=0)

@app.route('/set/<key>', methods=['POST'])
def set_key(key):
    value = request.json.get('value')
    r.set(key, value)
    return jsonify({'message': f'Successfully set {key} = {value}'}), 200

@app.route('/get/<key>', methods=['GET'])
def get_key(key):
    value = r.get(key)
    if value:
        return jsonify({'key': key, 'value': value.decode('utf-8')}), 200
    else:
        return jsonify({'message': f'Key {key} not found'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
