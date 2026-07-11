<div align="center">

<img src="docs/assets/banner.svg" alt="TryLoanify" width="100%"/>

<br/>

<a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-5B39F4?style=for-the-badge&labelColor=281C7D" alt="Apache 2.0"/></a>
&nbsp;
<a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/></a>
&nbsp;
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/></a>
&nbsp;
<a href="https://fastapi.tiangolo.com"><img src="https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white" alt="FastAPI"/></a>

<br/><br/>

### Quick, secure loan origination for the Indian market

Native Android client with Jetpack Compose, backed by FastAPI microservices  
for authentication, applications, credit decisioning, documents, and payments.

<br/>

<sub>MVP scaffold for local demos. Live KYC, bureau, and payment credentials are not included.</sub>

<br/>

[View license](LICENSE) · [Open on GitHub](https://github.com/Premkumarr07/Tryloanify)

</div>

---

## Features

- Phone OTP login with manual entry and SMS Retriever hooks
- Profile setup with PAN format validation
- Purpose-specific consent capture (loan processing, bureau pull, optional marketing)
- Multi-step loan application form (amount, tenure, employment, income)
- Document upload flow for PAN, Aadhaar, and income proof
- KYC status tracking and application lifecycle timeline
- Loan offer screen with APR display and scrollable KFS acknowledgement
- E-sign and disbursement status screens
- Active loan dashboard with repayment schedule, EMI payment, and cooling-off cancellation
- Grievance officer contact surfaced in-app

## Architecture

### Android app

- Language: Kotlin
- UI: Jetpack Compose and Material 3
- Pattern: MVVM with Clean Architecture (presentation / domain / data)
- DI: Hilt
- Local storage: Room (application drafts) and EncryptedSharedPreferences
- Networking: Retrofit and OkHttp
- Default mode: fake repositories (`USE_FAKE=true`) for full offline demo of the customer journey

### Backend services


| Service             | Port | Responsibility                                        |
| ------------------- | ---- | ----------------------------------------------------- |
| application_service | 8001 | OTP auth, JWT, customer profile, applications, offers |
| decision_engine     | 8002 | Eligibility rules and scorecard evaluation            |
| document_service    | 8003 | Document upload and e-sign stubs                      |
| payment_service     | 8004 | Disbursement, repayment schedule, EMI collection      |


Infrastructure for local development includes PostgreSQL, Redis, and MinIO via Docker Compose.

## Project structure

```
Tryloanify/
├── app/                    # Android application module
├── backend/                # FastAPI microservices and docker-compose
├── gradle/                 # Version catalog and wrapper
├── build.gradle.kts
└── settings.gradle.kts
```

## Requirements

### Android

- Android Studio (current stable recommended)
- JDK 11 or newer
- Android SDK with `minSdk` 27 and `compileSdk` / `targetSdk` 36
- Optional: physical device or emulator for install and run

### Backend

- Docker and Docker Compose
- Or Python 3.12+ for running services individually

## Getting started

### Clone

```bash
git clone https://github.com/Premkumarr07/Tryloanify.git
cd Tryloanify
```

### Run the Android app

1. Open the project in Android Studio.
2. Sync Gradle and wait for dependencies to resolve.
3. Create or update `local.properties` with your SDK path:

```properties
sdk.dir=/path/to/Android/sdk
```

1. Build and run the `app` configuration, or from the terminal:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.tryloanify/.MainActivity
```

#### Demo credentials

- Phone: any valid 10-digit Indian mobile number (starts with 6–9)
- OTP: `123456`
- Sample PAN: `ABCDE1234F`

### Run the backend

```bash
cd backend
docker compose up --build
```

Service health checks:

- [http://localhost:8001/health](http://localhost:8001/health)
- [http://localhost:8002/health](http://localhost:8002/health)
- [http://localhost:8003/health](http://localhost:8003/health)
- [http://localhost:8004/health](http://localhost:8004/health)

#### Point the Android app at the API

In `app/build.gradle.kts` (or build type `buildConfigField` values):

- Set `USE_FAKE` to `false`
- Set `API_BASE_URL` to `http://10.0.2.2:8001/` for the Android emulator, or your machine LAN IP for a physical device

## Customer journey

1. Splash and OTP authentication
2. Profile and consent
3. Loan application form
4. Document upload and KYC
5. Offer review (amount, rate, EMI, APR, KFS)
6. E-sign and disbursement
7. Loan dashboard and EMI payment

## Compliance notes (MVP UI)

The Android UI includes controls aligned with common digital lending expectations for India:

- KFS review and acknowledgement before offer acceptance
- Separate marketing consent from loan processing consent
- Cooling-off cancellation affordance on the dashboard
- Visible grievance contact details

Production integrations (live KYC, bureau, payment gateway, Aadhaar eSign, and India-region hosting) remain out of scope for this MVP scaffold and must be configured with licensed providers and regulated entity partnerships before launch.

## Configuration


| Setting        | Location               | Description                                         |
| -------------- | ---------------------- | --------------------------------------------------- |
| `USE_FAKE`     | `app/build.gradle.kts` | Use in-memory fake repositories when `true`         |
| `API_BASE_URL` | `app/build.gradle.kts` | Base URL for Application Service                    |
| `JWT_SECRET`   | backend env            | Signing secret for access/refresh tokens            |
| `REDIS_URL`    | backend env            | Redis connection for OTP storage                    |
| `DATABASE_URL` | backend env            | PostgreSQL URL (when wired beyond in-memory stores) |


## Development

- Prefer feature branches and pull requests against `main`.
- Keep secrets out of source control (`local.properties` is ignored).
- Backend Python bytecode (`__pycache__`) should not be committed.

## License

This project is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for the full text.

```
Copyright 2026 TryLoanify Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Repository

[https://github.com/Premkumarr07/Tryloanify](https://github.com/Premkumarr07/Tryloanify)