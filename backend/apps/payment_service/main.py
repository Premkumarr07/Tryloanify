from datetime import datetime, timedelta
from uuid import uuid4

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI(title="TryLoanify Payment Service", version="1.0.0")

loans: dict[str, dict] = {}
schedules: dict[str, list] = {}
transactions: dict[str, list] = {}


class LoanDto(BaseModel):
    id: str
    application_id: str
    customer_id: str
    principal: float
    interest_rate: float
    tenure_months: int
    emi_amount: float
    outstanding_principal: float
    status: str
    disbursement_date: str
    next_emi_date: str


class RepaymentDto(BaseModel):
    id: str
    loan_id: str
    installment_no: int
    due_date: str
    principal_component: float
    interest_component: float
    total_amount: float
    status: str


class TransactionDto(BaseModel):
    id: str
    loan_id: str
    amount: float
    type: str
    status: str
    created_at: str


class DisburseRequest(BaseModel):
    application_id: str
    customer_id: str
    principal: float
    interest_rate: float
    tenure_months: int
    emi_amount: float


def calculate_emi(principal: float, annual_rate: float, tenure: int) -> float:
    r = annual_rate / 12 / 100
    return principal * r * (1 + r) ** tenure / ((1 + r) ** tenure - 1)


def build_schedule(loan_id: str, principal: float, rate: float, tenure: int, emi: float):
    r = rate / 12 / 100
    balance = principal
    schedule = []
    base = datetime.now()
    for i in range(1, tenure + 1):
        interest = balance * r
        principal_part = emi - interest
        balance -= principal_part
        schedule.append(
            {
                "id": str(uuid4()),
                "loan_id": loan_id,
                "installment_no": i,
                "due_date": (base + timedelta(days=30 * i)).strftime("%d %b %Y"),
                "principal_component": principal_part,
                "interest_component": interest,
                "total_amount": emi,
                "status": "PENDING",
            }
        )
    return schedule


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/api/v1/payments/disburse", response_model=LoanDto)
def disburse(request: DisburseRequest):
    loan_id = str(uuid4())
    now = datetime.now()
    loan = {
        "id": loan_id,
        "application_id": request.application_id,
        "customer_id": request.customer_id,
        "principal": request.principal,
        "interest_rate": request.interest_rate,
        "tenure_months": request.tenure_months,
        "emi_amount": request.emi_amount,
        "outstanding_principal": request.principal,
        "status": "ACTIVE",
        "disbursement_date": now.strftime("%d %b %Y"),
        "next_emi_date": (now + timedelta(days=30)).strftime("%d %b %Y"),
    }
    loans[loan_id] = loan
    schedules[loan_id] = build_schedule(
        loan_id, request.principal, request.interest_rate, request.tenure_months, request.emi_amount
    )
    transactions[loan_id] = [
        {
            "id": str(uuid4()),
            "loan_id": loan_id,
            "amount": request.principal,
            "type": "DISBURSEMENT",
            "status": "SUCCESS",
            "created_at": now.strftime("%d %b %Y"),
        }
    ]
    return LoanDto(**loan)


@app.get("/api/v1/loans/active", response_model=LoanDto | None)
def get_active_loan():
    for loan in loans.values():
        if loan["status"] == "ACTIVE":
            return LoanDto(**loan)
    return None


@app.get("/api/v1/loans/{loan_id}", response_model=LoanDto)
def get_loan(loan_id: str):
    loan = loans.get(loan_id)
    if not loan:
        raise HTTPException(status_code=404, detail="Loan not found")
    return LoanDto(**loan)


@app.get("/api/v1/loans/{loan_id}/schedule", response_model=list[RepaymentDto])
def get_schedule(loan_id: str):
    return [RepaymentDto(**s) for s in schedules.get(loan_id, [])]


@app.get("/api/v1/loans/{loan_id}/transactions", response_model=list[TransactionDto])
def get_transactions(loan_id: str):
    return [TransactionDto(**t) for t in transactions.get(loan_id, [])]


@app.post("/api/v1/payments/repay", response_model=TransactionDto)
def repay(body: dict):
    loan_id = body.get("loan_id")
    amount = float(body.get("amount", 0))
    loan = loans.get(loan_id)
    if not loan:
        raise HTTPException(status_code=404, detail="Loan not found")
    schedule = schedules.get(loan_id, [])
    for inst in schedule:
        if inst["status"] == "PENDING":
            inst["status"] = "PAID"
            break
    loan["outstanding_principal"] = max(0, loan["outstanding_principal"] - amount * 0.7)
    txn = {
        "id": str(uuid4()),
        "loan_id": loan_id,
        "amount": amount,
        "type": "REPAYMENT",
        "status": "SUCCESS",
        "created_at": datetime.now().strftime("%d %b %Y"),
    }
    transactions.setdefault(loan_id, []).append(txn)
    return TransactionDto(**txn)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8004)
