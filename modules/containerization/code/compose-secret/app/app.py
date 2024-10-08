from flask import Flask
import os

app = Flask(__name__)

@app.route('/')
def index():
    secret_file_path = '/run/secrets/api_key'  # Percorso del file montato come volume

    # Verifica se il percorso esiste ed è un file
    if os.path.exists(secret_file_path) and os.path.isfile(secret_file_path):
        with open(secret_file_path, 'r') as secret_file:
            api_key = secret_file.read().strip()
        return f"Found the API key!"
    else:
        return "Secret file not found."

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
