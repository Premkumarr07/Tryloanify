from uuid import uuid4

from fastapi import FastAPI, File, Form, UploadFile
from pydantic import BaseModel

app = FastAPI(title="TryLoanify Document Service", version="1.0.0")
documents: dict[str, dict] = {}


class DocumentDto(BaseModel):
    id: str
    application_id: str
    doc_type: str
    file_name: str


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/api/v1/documents/upload", response_model=DocumentDto)
async def upload_document(
    application_id: str = Form(...),
    doc_type: str = Form(...),
    file: UploadFile = File(...),
):
    doc_id = str(uuid4())
    doc = {
        "id": doc_id,
        "application_id": application_id,
        "doc_type": doc_type,
        "file_name": file.filename or "document.jpg",
    }
    documents[doc_id] = doc
    return DocumentDto(**doc)


@app.post("/api/v1/documents/{doc_id}/esign")
def initiate_esign(doc_id: str):
    return {"sign_url": f"https://esign.tryloanify.com/sign/{doc_id}", "status": "PENDING"}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8003)
