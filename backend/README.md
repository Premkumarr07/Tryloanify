# TryLoanify Backend

FastAPI microservices monorepo for the Loan MVP.

## Services

| Service | Port | Description |
|---------|------|-------------|
| application_service | 8001 | Auth OTP, customers, applications |
| decision_engine | 8002 | Credit rules R001-R009, scorecard |
| document_service | 8003 | Document upload, e-sign stubs |
| payment_service | 8004 | Disbursement, repayment, schedule |

## Quick Start

```bash
cd backend
docker compose up --build
```

## Android Integration

Set `USE_FAKE=false` in `app/build.gradle.kts` and point `API_BASE_URL` to `http://10.0.2.2:8001/` for emulator.

Demo OTP: `123456`
