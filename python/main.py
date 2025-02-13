import requests
import base64
import json
from fastapi import FastAPI, HTTPException, Query

app = FastAPI()

# GitHub OAuth Credentials (replace with actual values)
CLIENT_ID = "Ov23lik7pTkdZwv1nLLn"
CLIENT_SECRET = "10662dd19ba483434b09da8a9fd8e755d95ad32d"

@app.post("/github/upload-file/")
def upload_file(
        code: str = Query(...),
        repo_name: str = Query(...),
        branch: str = Query("main"),
        file_path: str = Query(...),
        file_content: str = Query(...)
):
    """
    Single API call to:
    1. Exchange the GitHub OAuth code for an access token.
    2. Create a repository if it does not exist.
    3. Upload a file to the repository on the given branch.
    """

    # Step 1: Get Access Token
    headers = {"Accept": "application/json"}
    token_data = {
        "client_id": CLIENT_ID,
        "client_secret": CLIENT_SECRET,
        "code": code,
    }

    response = requests.post(TOKEN_URL, headers=headers, data=token_data)
    if response.status_code != 200:
        raise HTTPException(status_code=400, detail="Failed to retrieve access token")

    access_token = response.json().get("access_token")
    if not access_token:
        raise HTTPException(status_code=400, detail="Access token not found")

    headers["Authorization"] = f"Bearer {access_token}"

    # Step 2: Check if Repository Exists, Else Create It
    repo_url = f"https://api.github.com/repos/{{repo_name}}"
    repo_check = requests.get(repo_url, headers=headers)

    if repo_check.status_code == 404:  # Repo doesn't exist, create it
        create_repo_data = {"name": repo_name, "private": False}
        create_repo = requests.post(REPO_URL, headers=headers, json=create_repo_data)
        if create_repo.status_code != 201:
            raise HTTPException(status_code=400, detail="Failed to create repository")

    # Step 3: Upload File to Repository
    encoded_content = base64.b64encode(file_content.encode()).decode()
    file_url = f"https://api.github.com/repos/{repo_name}/contents/{file_path}"

    file_data = {
        "message": "Uploading file via API",
        "content": encoded_content,
        "branch": branch,
    }

    file_response = requests.put(file_url, headers=headers, json=file_data)
    if file_response.status_code not in [200, 201]:
        raise HTTPException(status_code=400, detail="File upload failed")

    return {
        "message": "File uploaded successfully",
        "file_url": file_response.json()["content"]["html_url"]
    }
