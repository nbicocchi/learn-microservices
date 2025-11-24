from fastapi import FastAPI, BackgroundTasks
import subprocess
import logging
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Kedro Trigger API")


@app.post("/run-pipeline")
def run_pipeline(background_tasks: BackgroundTasks):
    """
    Trigger the Kedro pipeline to run.
    """
    def kedro_task():
        subprocess.run(["kedro", "run"], check=True)

    background_tasks.add_task(kedro_task)
    return {"status": "started", "message": "Pipeline is running in background"}
