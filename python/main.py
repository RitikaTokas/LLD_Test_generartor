import requests
import base64
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

class UploadFileRequest(BaseModel):
    code: str
    repo_name: str
    branch: str = "main"
    file_path: str
    file_content: str

app = FastAPI()

CLIENT_ID = "Ov23lik7pTkdZwv1nLLn"
CLIENT_SECRET = "10662dd19ba483434b09da8a9fd8e755d95ad32d"

TOKEN_URL = "https://github.com/login/oauth/access_token"
REPO_URL = "https://api.github.com/user/repos"

@app.post("/github/upload-file/")
def upload_file(request: UploadFileRequest):
    headers = {"Accept": "application/json"}
    token_data = {
        "client_id": CLIENT_ID,
        "client_secret": CLIENT_SECRET,
        "code": request.code,
    }

    response = requests.post(TOKEN_URL, headers=headers, data=token_data)
    if response.status_code != 200:
        raise HTTPException(status_code=400, detail="Failed to retrieve access token")

    token_response = response.json()
    print("token_response:", token_response)
    access_token = token_response.get("access_token")
    if not access_token:
        raise HTTPException(status_code=400, detail="Access token not found")

    headers["Authorization"] = f"Bearer {access_token}"

    user_response = requests.get("https://api.github.com/user", headers=headers)
    if user_response.status_code != 200:
        raise HTTPException(status_code=400, detail="Failed to retrieve user info")

    username = user_response.json().get("login")
    if not username:
        raise HTTPException(status_code=400, detail="Failed to retrieve username")

    # Check if the repo exists.
    repo_url = f"https://api.github.com/repos/{username}/{request.repo_name}"
    repo_check = requests.get(repo_url, headers=headers)

    # If the repo does not exist, create it.
    if repo_check.status_code == 404:
        create_repo_data = {"name": request.repo_name, "private": False}
        create_repo = requests.post(REPO_URL, headers=headers, json=create_repo_data)
        if create_repo.status_code != 201:
            raise HTTPException(status_code=400, detail="Failed to create repository")

    encoded_content = base64.b64encode(request.file_content.encode()).decode()
    file_url = f"https://api.github.com/repos/{username}/{request.repo_name}/contents/{request.file_path}"

    file_data = {
        "message": "Uploading file via API",
        "content": encoded_content,
        "branch": request.branch,
    }

    file_response = requests.put(file_url, headers=headers, json=file_data)
    if file_response.status_code not in [200, 201]:
        raise HTTPException(status_code=400, detail="File upload failed")

    return {
        "message": "File uploaded successfully",
        "file_url": file_response.json()["content"]["html_url"]
    }