import os
import shutil
import uuid
import subprocess
from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from typing import List, Dict
from pymongo import MongoClient
from docker import DockerClient
from docker.errors import DockerException
import csv
import json
import time

app = FastAPI()

# MongoDB setup
client = MongoClient("mongodb+srv://sujalgoeldav:RwzfJlkOkMENXIZw@cluster0.uaamx.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")
db = client.leetcode_system
problems_collection = db.problems
test_cases_collection = db.test_cases
driver_codes_collection = db.driver_codes

# Docker client setup
docker_client = DockerClient(base_url='unix://var/run/docker.sock')

class FileComponent:
    def display(self):
        pass

class FileLeaf(FileComponent):
    def __init__(self, name: str, content: str):
        self.name = name
        self.content = content

    def display(self):
        print(f"File: {self.name}")

class DirectoryComposite(FileComponent):
    def __init__(self, name: str):
        self.name = name
        self.children = []

    def add(self, component: FileComponent):
        self.children.append(component)

    def display(self):
        print(f"Directory: {self.name}")
        for child in self.children:
            child.display()

class Problem(BaseModel):
    problem_statement: str
    difficulty: str

class TestCase(BaseModel):
    problem_id: str
    input: str
    output: str

class DriverCode(BaseModel):
    problem_interface: FileLeaf
    problem_id: str
    language: str
    main_file: FileLeaf
    class Config:
        arbitrary_types_allowed = True

def parse_file_component(data: Dict) -> FileComponent:
    if data["type"] == "file":
        return FileLeaf(data["name"], data["content"])
    else:
        dir = DirectoryComposite(data["name"])
        for child in data["children"]:
            dir.add(parse_file_component(child))
        return dir

def extract_files(directory):
    """Recursively extract FileLeaf objects from a DirectoryComposite"""
    files = []
    for item in directory.children:
        if isinstance(item, FileLeaf):
            files.append(item)
        elif isinstance(item, DirectoryComposite):
            files.extend(extract_files(item))  # Recursive call for subdirectories
    return files

def find_main_file(component: FileComponent) -> FileLeaf:
    if isinstance(component, FileLeaf) and component.name == "Main.java":
        return component
    elif isinstance(component, DirectoryComposite):
        for child in component.children:
            result = find_main_file(child)
            if result:
                return result
    return None

def find_problem_interface(component: FileComponent) -> FileLeaf:
    if isinstance(component, FileLeaf) and component.name == "ProblemInterface.java":
        return component
    elif isinstance(component, DirectoryComposite):
        for child in component.children:
            result = find_problem_interface(child)
            if result:
                return result
    return None

@app.post("/store-problem")
async def store_problem(
    json_file: UploadFile = File(...),
    input_txt: UploadFile = File(...)
):
    try:
        json_content = await json_file.read()
        data = json.loads(json_content.decode("utf-8"))

        # Generate unique problem ID
        problem_id = str(uuid.uuid4())

        # Read input.txt file
        txt_content = await input_txt.read()
        txt_content = txt_content.decode("utf-8").strip()

        # Splitting test cases based on empty lines
        input_data = []
        current_case = []

        for line in txt_content.split("\n"):
            if line.strip() == "":  # Empty line indicates separation between test cases
                if current_case:
                    input_data.append(current_case)  # Store the completed test case
                    current_case = []
            else:
                current_case.append(line)

        if current_case:
            input_data.append(current_case)  # Store the last test case if any

        # Save problem to MongoDB
        problem = Problem(
            problem_statement=data["problemStatement"],
            difficulty="EASY",
        )
        problems_collection.insert_one(problem.dict())

        # Parse file components
        ideal_solution = parse_file_component(data["idealSolution"])
        driver_code = parse_file_component(data["driverCode"])

        driver_code_entry = {
            "problem_id": problem_id,
            "language": "JAVA",
            "main_file": vars(find_main_file(driver_code)),  # Convert to dict
            "problem_interface": vars(find_problem_interface(driver_code))  # Convert to dict
        }
        driver_codes_collection.insert_one(driver_code_entry)

        # Start Docker container
        container = docker_client.containers.run(
            image="code-sandbox",
            network_mode="none",
            mem_limit="256m",
            cpu_period=100000,
            cpu_quota=50000,
            detach=True,
            stdin_open=True,
            tty=True,
            command=["tail", "-f", "/dev/null"]  # Keeps container running
        )

        # Wait for the container to start
        time.sleep(5)  # Add a delay to ensure the container is running
        container.reload()  # Refresh container status

        if container.status != "running":
            raise HTTPException(status_code=500, detail="Docker container failed to start")

        # Copy Java files to the container
        for file in extract_files(ideal_solution) + extract_files(driver_code):
            dest_path = f"/app/{file.name}"
            container.exec_run(f"mkdir -p {os.path.dirname(dest_path)}")
            container.exec_run(f"bash -c 'echo \"{file.content}\" > {dest_path}'")
        container.exec_run(f"bash -c 'echo \"{txt_content}\" > /app/input.txt'")
        # Execute entrypoint.sh
        exec_result = container.exec_run(["/bin/bash", "/app/entrypoint.sh"])
        print("Execution Output:", exec_result.output.decode())

        # Check for errors
        if exec_result.exit_code != 0:
            raise HTTPException(status_code=500, detail=f"Execution failed: {exec_result.output.decode()}")

        # Read output.txt
        exit_code, output = container.exec_run("cat /app/output.txt")
        print(output)
        output_text = output.decode().strip().split("\n")

        # Stop and remove the container
        container.stop()


        # Splitting outputs based on empty lines separating test cases
        output_cases = []
        current_case = []
        print(output_text)
        for line in output_text:
            if line.strip() == "":  # Empty line denotes separation between test cases
                if current_case:
                    output_cases.append("\n".join(current_case))  # Store the grouped output
                    current_case = []
            else:
                current_case.append(line)

        if current_case:
            output_cases.append("\n".join(current_case))  # Store the last case if any

        # Ensure input_data and output_cases are correctly matched
        if len(input_data) != len(output_cases):
            print(input_data)
            print(output_cases)
            raise HTTPException(status_code=500, detail="Mismatch between input and output test cases")

        # Save test cases
        for input_row, output in zip(input_data, output_cases):
            test_case = TestCase(
                problem_id=problem_id,
                input="\n".join(input_row),  # Join input lines appropriately
                output=output  # Multi-line output handled correctly
            )
            test_cases_collection.insert_one(test_case.dict())

        return {"output": output_cases}



    except DockerException as e:
        raise HTTPException(status_code=500, detail=f"Docker error: {str(e)}")

    except Exception as e:
        return {"message": f"Error: {str(e)}"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
