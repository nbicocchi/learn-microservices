Project structure
```
/flask_postgresql_docker
├── app
│   ├── app.py                 # Flask app
│   ├── Dockerfile             # Dockerfile for Flask app
│   ├── requirements.txt       # Python dependencies
├── docker-compose.yml         # Docker Compose file
├── db_password.txt            # Secret file for DB password
└── db_user.txt                # Secret file for DB user
```

This example uses SQLalchemy to authenticate in the db.
