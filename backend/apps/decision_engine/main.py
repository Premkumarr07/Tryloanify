from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI(title="TryLoanify Decision Engine", version="1.0.0")

RULES = [
    {"id": "R001", "condition": lambda d: d.credit_score < 650, "action": "REJECT"},
    {"id": "R002", "condition": lambda d: 650 <= d.credit_score < 720, "action": "MANUAL_REVIEW"},
    {"id": "R003", "condition": lambda d: d.credit_score >= 720, "action": "AUTO_APPROVE"},
    {"id": "R006", "condition": lambda d: d.monthly_income < 15000, "action": "REJECT"},
]


class EvaluateRequest(BaseModel):
    application_id: str
    credit_score: int = 750
    monthly_income: float
    requested_amount: float
    requested_tenure: int
    employment_type: str = "SALARIED"


class DecisionResponse(BaseModel):
    application_id: str
    decision: str
    sanctioned_amount: float
    interest_rate: float
    emi_amount: float
    risk_grade: str
    rules_fired: list[str]


def calculate_emi(principal: float, annual_rate: float, tenure: int) -> float:
    r = annual_rate / 12 / 100
    return principal * r * (1 + r) ** tenure / ((1 + r) ** tenure - 1)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/api/v1/decisions/evaluate", response_model=DecisionResponse)
def evaluate(request: EvaluateRequest):
    fired = []
    decision = "APPROVED"
    for rule in RULES:
        if rule["condition"](request):
            fired.append(rule["id"])
            if rule["action"] == "REJECT":
                decision = "REJECTED"
                break
            if rule["action"] == "MANUAL_REVIEW":
                decision = "MANUAL_REVIEW"

    if decision == "REJECTED":
        return DecisionResponse(
            application_id=request.application_id,
            decision="REJECTED",
            sanctioned_amount=0,
            interest_rate=0,
            emi_amount=0,
            risk_grade="D",
            rules_fired=fired,
        )

    rate_map = {800: 10.5, 750: 12.0, 720: 14.0, 650: 16.5}
    rate = 12.0
    for threshold, r in sorted(rate_map.items(), reverse=True):
        if request.credit_score >= threshold:
            rate = r
            break
    sanctioned = min(request.requested_amount, request.monthly_income * 40)
    emi = calculate_emi(sanctioned, rate, request.requested_tenure)
    grade = "A" if request.credit_score >= 800 else "B" if request.credit_score >= 750 else "C"

    return DecisionResponse(
        application_id=request.application_id,
        decision=decision,
        sanctioned_amount=sanctioned,
        interest_rate=rate,
        emi_amount=emi,
        risk_grade=grade,
        rules_fired=fired,
    )


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8002)
