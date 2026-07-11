import os
from datetime import datetime, timedelta, timezone
from typing import Optional
from uuid import uuid4

from fastapi import Depends, FastAPI, HTTPException, Header
from fastapi.middleware.cors import CORSMiddleware
from jose import jwt
from pydantic import BaseModel
import redis

app = FastAPI(title="TryLoanify Application Service", version="1.0.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

JWT_SECRET = "dev-secret-change-in-production"
JWT_ALGORITHM = "HS256"
OTP_TTL = 300
OTP_RATE_LIMIT = 3

redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
redis_client = redis.from_url(redis_url, decode_responses=True)

# In-memory stores for MVP
customers: dict[str, dict] = {}
applications: dict[str, dict] = {}
tracking_counter = 100001


class OtpRequest(BaseModel):
    phone: str


class OtpVerifyRequest(BaseModel):
    phone: str
    otp: str


class CustomerDto(BaseModel):
    id: Optional[str] = None
    full_name: str
    phone: str
    email: str = ""
    pan_number: str = ""


class CreateApplicationRequest(BaseModel):
    requested_amount: float
    requested_tenure: int
    employment_type: str
    monthly_income: float


class ApplicationDto(BaseModel):
    id: str
    tracking_id: str
    customer_id: str
    requested_amount: float
    requested_tenure: int
    employment_type: str
    monthly_income: float
    status: str
    kyc_status: Optional[str] = "PENDING"


class LoanOfferDto(BaseModel):
    application_id: str
    sanctioned_amount: float
    interest_rate: float
    apr: float
    tenure_months: int
    emi_amount: float
    risk_grade: str
    kfs_content: str


def create_token(sub: str, role: str = "CUSTOMER", token_type: str = "access") -> str:
    ttl = timedelta(minutes=15) if token_type == "access" else timedelta(days=7)
    payload = {
        "sub": sub,
        "role": role,
        "type": token_type,
        "exp": datetime.now(timezone.utc) + ttl,
    }
    return jwt.encode(payload, JWT_SECRET, algorithm=JWT_ALGORITHM)


def get_current_user(authorization: Optional[str] = Header(None)) -> str:
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Unauthorized")
    token = authorization.split(" ", 1)[1]
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        return payload["sub"]
    except Exception as exc:
        raise HTTPException(status_code=401, detail="Invalid token") from exc


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/api/v1/auth/otp/send")
def send_otp(request: OtpRequest):
    if len(request.phone) != 10 or not request.phone.isdigit():
        raise HTTPException(status_code=400, detail="Invalid phone number")
    rate_key = f"otp_rate:{request.phone}"
    count = int(redis_client.get(rate_key) or 0)
    if count >= OTP_RATE_LIMIT:
        raise HTTPException(status_code=429, detail="Rate limit exceeded")
    redis_client.setex(rate_key, 600, count + 1)
    otp = "123456"
    redis_client.setex(f"otp:{request.phone}", OTP_TTL, otp)
    return {"message": "OTP sent", "demo_otp": otp}


@app.post("/api/v1/auth/otp/verify")
def verify_otp(request: OtpVerifyRequest):
    stored = redis_client.get(f"otp:{request.phone}")
    if stored != request.otp:
        raise HTTPException(status_code=400, detail="Invalid OTP")
    customer_id = next(
        (cid for cid, c in customers.items() if c["phone"] == request.phone),
        str(uuid4()),
    )
    if customer_id not in customers:
        customers[customer_id] = {
            "id": customer_id,
            "full_name": "",
            "phone": request.phone,
            "email": "",
            "pan_number": "",
        }
    access = create_token(customer_id, token_type="access")
    refresh = create_token(customer_id, token_type="refresh")
    redis_client.delete(f"otp:{request.phone}")
    return {"access_token": access, "refresh_token": refresh}


@app.post("/api/v1/auth/profile", response_model=CustomerDto)
def save_profile(customer: CustomerDto, user_id: str = Depends(get_current_user)):
    customers[user_id] = {
        "id": user_id,
        "full_name": customer.full_name,
        "phone": customer.phone,
        "email": customer.email,
        "pan_number": customer.pan_number,
    }
    return CustomerDto(**customers[user_id])


@app.get("/api/v1/auth/me", response_model=CustomerDto)
def get_me(user_id: str = Depends(get_current_user)):
    if user_id not in customers:
        raise HTTPException(status_code=404, detail="Customer not found")
    return CustomerDto(**customers[user_id])


@app.post("/api/v1/applications", response_model=ApplicationDto)
def create_application(
    request: CreateApplicationRequest,
    user_id: str = Depends(get_current_user),
):
    global tracking_counter
    app_id = str(uuid4())
    tracking_id = f"TRK-2026-{tracking_counter}"
    tracking_counter += 1
    app_data = {
        "id": app_id,
        "tracking_id": tracking_id,
        "customer_id": user_id,
        "requested_amount": request.requested_amount,
        "requested_tenure": request.requested_tenure,
        "employment_type": request.employment_type,
        "monthly_income": request.monthly_income,
        "status": "DRAFT",
        "kyc_status": "PENDING",
    }
    applications[app_id] = app_data
    return ApplicationDto(**app_data)


@app.put("/api/v1/applications/{app_id}", response_model=ApplicationDto)
def update_application(
    app_id: str,
    request: CreateApplicationRequest,
    user_id: str = Depends(get_current_user),
):
    app_data = applications.get(app_id)
    if not app_data or app_data["customer_id"] != user_id:
        raise HTTPException(status_code=404, detail="Application not found")
    app_data.update(
        {
            "requested_amount": request.requested_amount,
            "requested_tenure": request.requested_tenure,
            "employment_type": request.employment_type,
            "monthly_income": request.monthly_income,
        }
    )
    return ApplicationDto(**app_data)


@app.post("/api/v1/applications/{app_id}/submit", response_model=ApplicationDto)
def submit_application(app_id: str, user_id: str = Depends(get_current_user)):
    app_data = applications.get(app_id)
    if not app_data or app_data["customer_id"] != user_id:
        raise HTTPException(status_code=404, detail="Application not found")
    app_data["status"] = "SUBMITTED"
    app_data["kyc_status"] = "IN_PROGRESS"
    return ApplicationDto(**app_data)


@app.get("/api/v1/applications/{app_id}", response_model=ApplicationDto)
def get_application(app_id: str, user_id: str = Depends(get_current_user)):
    app_data = applications.get(app_id)
    if not app_data or app_data["customer_id"] != user_id:
        raise HTTPException(status_code=404, detail="Application not found")
    return ApplicationDto(**app_data)


@app.get("/api/v1/applications", response_model=list[ApplicationDto])
def list_applications(user_id: str = Depends(get_current_user)):
    return [
        ApplicationDto(**a)
        for a in applications.values()
        if a["customer_id"] == user_id
    ]


def calculate_emi(principal: float, annual_rate: float, tenure: int) -> float:
    r = annual_rate / 12 / 100
    if r == 0:
        return principal / tenure
    return principal * r * (1 + r) ** tenure / ((1 + r) ** tenure - 1)


@app.get("/api/v1/applications/{app_id}/offer", response_model=LoanOfferDto)
def get_offer(app_id: str, user_id: str = Depends(get_current_user)):
    app_data = applications.get(app_id)
    if not app_data or app_data["customer_id"] != user_id:
        raise HTTPException(status_code=404, detail="Application not found")
    sanctioned = min(app_data["requested_amount"], app_data["monthly_income"] * 40)
    rate = 12.0
    tenure = app_data["requested_tenure"]
    emi = calculate_emi(sanctioned, rate, tenure)
    kfs = (
        f"KEY FACT STATEMENT\nSanctioned: ₹{sanctioned:,.0f}\n"
        f"Interest: {rate}% p.a.\nAPR: 13.2%\nEMI: ₹{emi:,.0f}"
    )
    return LoanOfferDto(
        application_id=app_id,
        sanctioned_amount=sanctioned,
        interest_rate=rate,
        apr=13.2,
        tenure_months=tenure,
        emi_amount=emi,
        risk_grade="B",
        kfs_content=kfs,
    )


@app.post("/api/v1/applications/{app_id}/accept", response_model=LoanOfferDto)
def accept_offer(app_id: str, user_id: str = Depends(get_current_user)):
    app_data = applications.get(app_id)
    if not app_data or app_data["customer_id"] != user_id:
        raise HTTPException(status_code=404, detail="Application not found")
    app_data["status"] = "OFFER_ACCEPTED"
    return get_offer(app_id, user_id)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8001)
