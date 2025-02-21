import argparse
import os
import subprocess
import signal
import sys
from time import sleep
import threading
import requests

processes = []
stop_event = threading.Event()


def start_nodes(num_nodes):
    log_dir = "logs"
    os.makedirs(log_dir, exist_ok=True)

    base_port = 8000
    cluster_nodes = ','.join([f"localhost:{base_port + i}" for i in range(num_nodes)])

    for i in range(num_nodes):
        port = base_port + i
        node_id = f"node{i + 1}"
        log_file = os.path.join(log_dir, f"{node_id}.log")

        with open(log_file, "w") as f:
            command = [
                "mvn", "spring-boot:run",
                "-DskipTests",  # Skip tests
                f"-Dspring-boot.run.arguments=--server.port={port} --node.id={node_id} --node.cluster-nodes={cluster_nodes}"
            ]

            if args.print_command:
                print(f"# Node {i + 1} command:")
                print(" ".join(command))
                print("\n")
                continue

            process = subprocess.Popen(command, stdout=f, stderr=subprocess.STDOUT)
            processes.append(process)
            print(f"Started {node_id} on port {port}, logging to {log_file} [http://localhost:{port}/monitor]")


def stop_nodes():
    print("\nStopping all nodes...")
    for process in processes:
        process.terminate()

    for process in processes:
        process.wait()
        print(f"Node with PID {process.pid} has stopped.")


def check_nodes_status(num_nodes):
    base_port = 8000
    inactive_nodes = set(range(num_nodes))
    while True:
        all_active = True
        for i in range(num_nodes):
            port = base_port + i
            try:
                response = requests.get(f"http://localhost:{port}/raft/status", timeout=2)
                if response.status_code != 200:
                    all_active = False
                    inactive_nodes.add(i)
                else:
                    inactive_nodes.discard(i)
            except requests.exceptions.RequestException:
                all_active = False
                break
        if not all_active:
            print("Inactive nodes:", inactive_nodes)

        if stop_event.is_set():
            return
        sleep(5)


def parse_logs():
    log_dir = "logs"
    for log_file in os.listdir(log_dir):
        full_path = os.path.join(log_dir, log_file)
        with open(full_path, "r") as f:
            try:
                for line in f:
                    if "ERROR" in line or "Exception" in line:
                        print(f"Error in {log_file}: {line.strip()}")

                    if "MY_DEBUG" in line:
                        print(f"Debug in {log_file}: {line.strip()}")
            except UnicodeDecodeError:
                pass


def signal_handler(sig, frame):
    stop_event.set()
    stop_nodes()
    sys.exit(0)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Start multiple Spring Boot nodes")
    parser.add_argument("num_nodes", type=int, help="Number of nodes to start")
    # Option that only print the command to copy and paste (without starting the nodes)
    parser.add_argument("--print-command", action="store_true", help="Print the command to copy and paste",
                        required=False, default=False)

    args = parser.parse_args()

    signal.signal(signal.SIGINT, signal_handler)

    # Clear logs folder
    os.system("rm -rf logs/*")

    start_nodes(args.num_nodes)

    if args.print_command:
        sys.exit(0)

    sleep(10)  # Wait for nodes to start

    # Start a thread to monitor node statuses
    threading.Thread(target=check_nodes_status, args=(args.num_nodes,), daemon=True).start()

    # Start a thread to parse logs
    # threading.Thread(target=parse_logs, daemon=True).start()

    try:
        while True:
            sleep(0.2)
    except KeyboardInterrupt:
        stop_event.set()
    finally:
        stop_nodes()
