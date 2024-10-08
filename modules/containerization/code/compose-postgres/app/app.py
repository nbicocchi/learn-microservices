from flask import Flask, jsonify
from flask_sqlalchemy import SQLAlchemy
import os

app = Flask(__name__)

# Percorsi ai file secrets
db_user_file = '/run/secrets/db_user'
db_password_file = '/run/secrets/db_password'

# Legge il nome utente e la password dal file secrets
with open(db_user_file) as f:
    db_user = f.read().strip()

with open(db_password_file) as f:
    db_password = f.read().strip()

# Configura il database utilizzando PostgreSQL
app.config['SQLALCHEMY_DATABASE_URI'] = f'postgresql://{db_user}:{db_password}@db:5432/mydatabase'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

@app.route('/')
def index():
    return jsonify(message="Connected to PostgreSQL using Docker Secrets!")

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
