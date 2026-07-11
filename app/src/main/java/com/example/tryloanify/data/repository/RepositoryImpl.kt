package com.example.tryloanify.data.repository

import com.example.tryloanify.BuildConfig
import com.example.tryloanify.data.fake.FakeApplicationRepository
import com.example.tryloanify.data.fake.FakeAuthRepository
import com.example.tryloanify.data.fake.FakeDocumentRepository
import com.example.tryloanify.data.fake.FakeLoanRepository
import com.example.tryloanify.data.local.db.ApplicationDraftDao
import com.example.tryloanify.data.local.db.ApplicationDraftEntity
import com.example.tryloanify.data.local.prefs.SecurePrefs
import com.example.tryloanify.data.remote.api.ApplicationApi
import com.example.tryloanify.data.remote.api.AuthApi
import com.example.tryloanify.data.remote.api.DocumentApi
import com.example.tryloanify.data.remote.api.LoanApi
import com.example.tryloanify.data.remote.dto.CreateApplicationRequest
import com.example.tryloanify.data.remote.dto.CustomerDto
import com.example.tryloanify.data.remote.dto.OtpRequest
import com.example.tryloanify.data.remote.dto.OtpVerifyRequest
import com.example.tryloanify.domain.model.ApplicationStatus
import com.example.tryloanify.domain.model.Customer
import com.example.tryloanify.domain.model.DocumentType
import com.example.tryloanify.domain.model.EmploymentType
import com.example.tryloanify.domain.model.InstallmentStatus
import com.example.tryloanify.domain.model.KycStatus
import com.example.tryloanify.domain.model.Loan
import com.example.tryloanify.domain.model.LoanApplication
import com.example.tryloanify.domain.model.LoanOffer
import com.example.tryloanify.domain.model.LoanStatus
import com.example.tryloanify.domain.model.RepaymentInstallment
import com.example.tryloanify.domain.model.Transaction
import com.example.tryloanify.domain.model.UploadedDocument
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.domain.repository.AuthRepository
import com.example.tryloanify.domain.repository.DocumentRepository
import com.example.tryloanify.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val fake: FakeAuthRepository,
    private val authApi: AuthApi,
    private val securePrefs: SecurePrefs,
) : AuthRepository {
    private val loggedInFlow = MutableStateFlow(securePrefs.isLoggedIn)

    override suspend fun sendOtp(phone: String): Result<Unit> =
        if (BuildConfig.USE_FAKE) fake.sendOtp(phone) else runCatching {
            authApi.sendOtp(OtpRequest(phone))
        }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Unit> =
        if (BuildConfig.USE_FAKE) {
            fake.verifyOtp(phone, otp).also { result ->
                if (result.isSuccess) securePrefs.isLoggedIn = true
                loggedInFlow.value = securePrefs.isLoggedIn
            }
        } else {
            runCatching {
                val response = authApi.verifyOtp(OtpVerifyRequest(phone, otp))
                securePrefs.accessToken = response.access_token
                securePrefs.refreshToken = response.refresh_token
                securePrefs.isLoggedIn = true
                loggedInFlow.value = true
            }
        }

    override suspend fun saveProfile(customer: Customer): Result<Customer> =
        if (BuildConfig.USE_FAKE) fake.saveProfile(customer) else runCatching {
            val dto = authApi.saveProfile(
                CustomerDto(
                    id = customer.id,
                    full_name = customer.fullName,
                    phone = customer.phone,
                    email = customer.email,
                    pan_number = customer.panNumber,
                ),
            )
            Customer(
                id = dto.id.orEmpty(),
                fullName = dto.full_name,
                phone = dto.phone,
                email = dto.email,
                panNumber = dto.pan_number,
            )
        }

    override suspend fun getCurrentCustomer(): Customer? =
        if (BuildConfig.USE_FAKE) fake.getCurrentCustomer() else runCatching {
            val dto = authApi.getMe()
            Customer(
                id = dto.id.orEmpty(),
                fullName = dto.full_name,
                phone = dto.phone,
                email = dto.email,
                panNumber = dto.pan_number,
            )
        }.getOrNull()

    override suspend fun isLoggedIn(): Boolean =
        if (BuildConfig.USE_FAKE) fake.isLoggedIn() else securePrefs.isLoggedIn

    override suspend fun logout() {
        if (BuildConfig.USE_FAKE) fake.logout() else securePrefs.clear()
        loggedInFlow.value = false
    }

    override fun observeLoggedIn(): Flow<Boolean> =
        if (BuildConfig.USE_FAKE) fake.observeLoggedIn() else loggedInFlow.asStateFlow()
}

@Singleton
class ApplicationRepositoryImpl @Inject constructor(
    private val fake: FakeApplicationRepository,
    private val api: ApplicationApi,
    private val draftDao: ApplicationDraftDao,
) : ApplicationRepository {
    override suspend fun createDraft(application: LoanApplication): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) {
            fake.createDraft(application).also { result ->
                result.getOrNull()?.let { cacheDraft(it) }
            }
        } else runCatching { mapDto(api.create(application.toRequest())) }

    override suspend fun updateDraft(application: LoanApplication): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) {
            fake.updateDraft(application).also { result ->
                result.getOrNull()?.let { cacheDraft(it) }
            }
        } else runCatching { mapDto(api.update(application.id, application.toRequest())) }

    override suspend fun submitApplication(applicationId: String): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) fake.submitApplication(applicationId)
        else runCatching { mapDto(api.submit(applicationId)) }

    override suspend fun getApplication(applicationId: String): LoanApplication? =
        if (BuildConfig.USE_FAKE) fake.getApplication(applicationId)
        else runCatching { mapDto(api.get(applicationId)) }.getOrNull()

    override suspend fun getApplications(): List<LoanApplication> =
        if (BuildConfig.USE_FAKE) fake.getApplications()
        else runCatching { api.list().map { mapDto(it) } }.getOrDefault(emptyList())

    override suspend fun startKyc(applicationId: String): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) fake.startKyc(applicationId)
        else Result.failure(UnsupportedOperationException("Remote KYC not wired"))

    override suspend fun pollKyc(applicationId: String): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) fake.pollKyc(applicationId)
        else Result.failure(UnsupportedOperationException("Remote KYC not wired"))

    override suspend fun getOffer(applicationId: String): LoanOffer? =
        if (BuildConfig.USE_FAKE) fake.getOffer(applicationId)
        else runCatching { mapOffer(api.getOffer(applicationId)) }.getOrNull()

    override suspend fun acceptOffer(applicationId: String): Result<LoanOffer> =
        if (BuildConfig.USE_FAKE) fake.acceptOffer(applicationId)
        else runCatching { mapOffer(api.acceptOffer(applicationId)) }

    override suspend fun rejectOffer(applicationId: String): Result<Unit> =
        if (BuildConfig.USE_FAKE) fake.rejectOffer(applicationId)
        else Result.failure(UnsupportedOperationException("Remote reject not wired"))

    override suspend fun completeESign(applicationId: String): Result<LoanApplication> =
        if (BuildConfig.USE_FAKE) fake.completeESign(applicationId)
        else Result.failure(UnsupportedOperationException("Remote e-sign not wired"))

    override suspend fun triggerDisbursement(applicationId: String): Result<Loan> =
        if (BuildConfig.USE_FAKE) fake.triggerDisbursement(applicationId)
        else Result.failure(UnsupportedOperationException("Remote disbursement not wired"))

    private suspend fun cacheDraft(app: LoanApplication) {
        draftDao.upsert(
            ApplicationDraftEntity(
                id = app.id,
                customerId = app.customerId,
                requestedAmount = app.requestedAmount,
                requestedTenure = app.requestedTenure,
                employmentType = app.employmentType.name,
                monthlyIncome = app.monthlyIncome,
                trackingId = app.trackingId,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    private fun LoanApplication.toRequest() = CreateApplicationRequest(
        requested_amount = requestedAmount,
        requested_tenure = requestedTenure,
        employment_type = employmentType.name,
        monthly_income = monthlyIncome,
    )

    private fun mapDto(dto: com.example.tryloanify.data.remote.dto.ApplicationDto) = LoanApplication(
        id = dto.id,
        trackingId = dto.tracking_id,
        customerId = dto.customer_id,
        requestedAmount = dto.requested_amount,
        requestedTenure = dto.requested_tenure,
        employmentType = EmploymentType.valueOf(dto.employment_type),
        monthlyIncome = dto.monthly_income,
        status = ApplicationStatus.valueOf(dto.status),
        kycStatus = dto.kyc_status?.let { KycStatus.valueOf(it) } ?: KycStatus.PENDING,
    )

    private fun mapOffer(dto: com.example.tryloanify.data.remote.dto.LoanOfferDto) = LoanOffer(
        applicationId = dto.application_id,
        sanctionedAmount = dto.sanctioned_amount,
        interestRate = dto.interest_rate,
        apr = dto.apr,
        tenureMonths = dto.tenure_months,
        emiAmount = dto.emi_amount,
        riskGrade = dto.risk_grade,
        kfsContent = dto.kfs_content,
    )
}

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val fake: FakeDocumentRepository,
) : DocumentRepository {
    override suspend fun uploadDocument(
        applicationId: String,
        type: DocumentType,
        fileName: String,
    ): Result<UploadedDocument> = fake.uploadDocument(applicationId, type, fileName)

    override suspend fun getDocuments(applicationId: String): List<UploadedDocument> =
        fake.getDocuments(applicationId)
}

@Singleton
class LoanRepositoryImpl @Inject constructor(
    private val fake: FakeLoanRepository,
    private val api: LoanApi,
) : LoanRepository {
    override suspend fun getActiveLoan(): Loan? =
        if (BuildConfig.USE_FAKE) fake.getActiveLoan()
        else runCatching { api.getActive()?.let { mapLoan(it) } }.getOrNull()

    override suspend fun getLoan(loanId: String): Loan? =
        if (BuildConfig.USE_FAKE) fake.getLoan(loanId)
        else runCatching { mapLoan(api.get(loanId)) }.getOrNull()

    override suspend fun getSchedule(loanId: String): List<RepaymentInstallment> =
        if (BuildConfig.USE_FAKE) fake.getSchedule(loanId)
        else runCatching {
            api.getSchedule(loanId).map {
                RepaymentInstallment(
                    id = it.id,
                    loanId = it.loan_id,
                    installmentNo = it.installment_no,
                    dueDate = it.due_date,
                    principalComponent = it.principal_component,
                    interestComponent = it.interest_component,
                    totalAmount = it.total_amount,
                    status = InstallmentStatus.valueOf(it.status),
                )
            }
        }.getOrDefault(emptyList())

    override suspend fun getTransactions(loanId: String): List<Transaction> =
        if (BuildConfig.USE_FAKE) fake.getTransactions(loanId)
        else runCatching {
            api.getTransactions(loanId).map {
                Transaction(
                    id = it.id,
                    loanId = it.loan_id,
                    amount = it.amount,
                    type = it.type,
                    status = it.status,
                    createdAt = it.created_at,
                )
            }
        }.getOrDefault(emptyList())

    override suspend fun payEmi(loanId: String, amount: Double): Result<Transaction> =
        if (BuildConfig.USE_FAKE) fake.payEmi(loanId, amount)
        else runCatching {
            val dto = api.repay(mapOf("loan_id" to loanId, "amount" to amount))
            Transaction(
                id = dto.id,
                loanId = dto.loan_id,
                amount = dto.amount,
                type = dto.type,
                status = dto.status,
                createdAt = dto.created_at,
            )
        }

    override suspend fun cancelWithinCoolingOff(loanId: String): Result<Unit> =
        if (BuildConfig.USE_FAKE) fake.cancelWithinCoolingOff(loanId)
        else Result.failure(UnsupportedOperationException("Remote cancel not wired"))

    private fun mapLoan(dto: com.example.tryloanify.data.remote.dto.LoanDto) = Loan(
        id = dto.id,
        applicationId = dto.application_id,
        customerId = dto.customer_id,
        principal = dto.principal,
        interestRate = dto.interest_rate,
        tenureMonths = dto.tenure_months,
        emiAmount = dto.emi_amount,
        outstandingPrincipal = dto.outstanding_principal,
        status = LoanStatus.valueOf(dto.status),
        disbursementDate = dto.disbursement_date,
        nextEmiDate = dto.next_emi_date,
        coolingOffEndsAt = null,
    )
}
