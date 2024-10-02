from flask import Flask, jsonify
import psycopg2
import os

app = Flask(__name__)

DATABASE_URL = os.getenv("DATABASE_URL")

def get_message_from_db():
    conn = psycopg2.connect(DATABASE_URL)
    cur = conn.cursor()
    cur.execute("SELECT 'Hello from the database!'")
    message = cur.fetchone()[0]
    cur.close()
    conn.close()
    return message

@app.route('/api')
def api():
    message = get_message_from_db()
    return jsonify({"message": message})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
