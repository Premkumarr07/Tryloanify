package com.example.tryloanify.data.fake

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class FakeDataStore @Inject constructor() {
    var customer: Customer? = null
    var loggedIn: Boolean = false
    val applications = mutableMapOf<String, LoanApplication>()
    val documents = mutableMapOf<String, MutableList<UploadedDocument>>()
    val offers = mutableMapOf<String, LoanOffer>()
    var activeLoan: Loan? = null
    val schedules = mutableMapOf<String, List<RepaymentInstallment>>()
    val transactions = mutableMapOf<String, MutableList<Transaction>>()
    var kycPollCount = mutableMapOf<String, Int>()
    var otpAttempts = 0
    var lastOtpPhone: String? = null
    val validOtp = "123456"
}

@Singleton
class FakeAuthRepository @Inject constructor(
    private val store: FakeDataStore,
) : AuthRepository {
    private val loggedInFlow = MutableStateFlow(false)

    override suspend fun sendOtp(phone: String): Result<Unit> {
        delay(800)
        if (phone.length != 10) return Result.failure(IllegalArgumentException("Enter a valid 10-digit number"))
        store.lastOtpPhone = phone
        store.otpAttempts = 0
        return Result.success(Unit)
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<Unit> {
        delay(600)
        if (otp != store.validOtp) {
            store.otpAttempts++
            if (store.otpAttempts >= 3) {
                return Result.failure(IllegalStateException("Too many attempts. Try again in 10 minutes."))
            }
            return Result.failure(IllegalArgumentException("Invalid OTP"))
        }
        store.loggedIn = true
        loggedInFlow.value = true
        if (store.customer == null || store.customer?.phone != phone) {
            store.customer = Customer(
                id = UUID.randomUUID().toString(),
                fullName = "",
                phone = phone,
                email = "",
                panNumber = "",
            )
        }
        return Result.success(Unit)
    }

    override suspend fun saveProfile(customer: Customer): Result<Customer> {
        delay(400)
        store.customer = customer
        return Result.success(customer)
    }

    override suspend fun getCurrentCustomer(): Customer? = store.customer

    override suspend fun isLoggedIn(): Boolean = store.loggedIn

    override suspend fun logout() {
        store.loggedIn = false
        loggedInFlow.value = false
    }

    override fun observeLoggedIn(): Flow<Boolean> = loggedInFlow.asStateFlow()
}

@Singleton
class FakeApplicationRepository @Inject constructor(
    private val store: FakeDataStore,
) : ApplicationRepository {
    private var trackingCounter = 100001

    override suspend fun createDraft(application: LoanApplication): Result<LoanApplication> {
        delay(300)
        val id = application.id.ifBlank { UUID.randomUUID().toString() }
        val draft = application.copy(
            id = id,
            trackingId = "TRK-2026-${trackingCounter++}",
            status = ApplicationStatus.DRAFT,
        )
        store.applications[id] = draft
        return Result.success(draft)
    }

    override suspend fun updateDraft(application: LoanApplication): Result<LoanApplication> {
        delay(200)
        store.applications[application.id] = application
        return Result.success(application)
    }

    override suspend fun submitApplication(applicationId: String): Result<LoanApplication> {
        delay(1000)
        val app = store.applications[applicationId] ?: return Result.failure(NoSuchElementException())
        val updated = app.copy(status = ApplicationStatus.SUBMITTED, kycStatus = KycStatus.IN_PROGRESS)
        store.applications[applicationId] = updated
        store.kycPollCount[applicationId] = 0
        return Result.success(updated)
    }

    override suspend fun getApplication(applicationId: String): LoanApplication? =
        store.applications[applicationId]

    override suspend fun getApplications(): List<LoanApplication> =
        store.applications.values.sortedByDescending { it.trackingId }

    override suspend fun startKyc(applicationId: String): Result<LoanApplication> {
        delay(500)
        val app = store.applications[applicationId] ?: return Result.failure(NoSuchElementException())
        val updated = app.copy(kycStatus = KycStatus.IN_PROGRESS)
        store.applications[applicationId] = updated
        return Result.success(updated)
    }

    override suspend fun pollKyc(applicationId: String): Result<LoanApplication> {
        delay(1200)
        val app = store.applications[applicationId] ?: return Result.failure(NoSuchElementException())
        val count = (store.kycPollCount[applicationId] ?: 0) + 1
        store.kycPollCount[applicationId] = count
        if (count < 2) {
            return Result.success(app.copy(kycStatus = KycStatus.IN_PROGRESS))
        }
        val verified = app.copy(
            status = ApplicationStatus.KYC_VERIFIED,
            kycStatus = KycStatus.VERIFIED,
        )
        store.applications[applicationId] = verified
        delay(800)
        val reviewed = verified.copy(status = ApplicationStatus.UNDER_REVIEW)
        store.applications[applicationId] = reviewed
        delay(600)
        val approved = reviewed.copy(status = ApplicationStatus.APPROVED)
        store.applications[applicationId] = approved
        val offer = buildOffer(approved)
        store.offers[applicationId] = offer
        return Result.success(approved)
    }

    override suspend fun getOffer(applicationId: String): LoanOffer? = store.offers[applicationId]

    override suspend fun acceptOffer(applicationId: String): Result<LoanOffer> {
        delay(500)
        val offer = store.offers[applicationId] ?: return Result.failure(NoSuchElementException())
        val app = store.applications[applicationId]?.copy(status = ApplicationStatus.OFFER_ACCEPTED)
        if (app != null) store.applications[applicationId] = app
        return Result.success(offer)
    }

    override suspend fun rejectOffer(applicationId: String): Result<Unit> {
        delay(300)
        store.applications[applicationId] = store.applications[applicationId]?.copy(
            status = ApplicationStatus.REJECTED,
        ) ?: return Result.failure(NoSuchElementException())
        return Result.success(Unit)
    }

    override suspend fun completeESign(applicationId: String): Result<LoanApplication> {
        delay(1000)
        val app = store.applications[applicationId] ?: return Result.failure(NoSuchElementException())
        val updated = app.copy(status = ApplicationStatus.ESIGN_COMPLETED)
        store.applications[applicationId] = updated
        return Result.success(updated)
    }

    override suspend fun triggerDisbursement(applicationId: String): Result<Loan> {
        delay(1500)
        val app = store.applications[applicationId] ?: return Result.failure(NoSuchElementException())
        val offer = store.offers[applicationId] ?: return Result.failure(NoSuchElementException())
        val loanId = UUID.randomUUID().toString()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val now = Date()
        val loan = Loan(
            id = loanId,
            applicationId = applicationId,
            customerId = app.customerId,
            principal = offer.sanctionedAmount,
            interestRate = offer.interestRate,
            tenureMonths = offer.tenureMonths,
            emiAmount = offer.emiAmount,
            outstandingPrincipal = offer.sanctionedAmount,
            status = LoanStatus.ACTIVE,
            disbursementDate = dateFormat.format(now),
            nextEmiDate = dateFormat.format(Date(now.time + TimeUnit.DAYS.toMillis(30))),
            coolingOffEndsAt = now.time + TimeUnit.DAYS.toMillis(3),
        )
        store.activeLoan = loan
        store.applications[applicationId] = app.copy(status = ApplicationStatus.DISBURSED)
        store.schedules[loanId] = buildSchedule(loan)
        store.transactions[loanId] = mutableListOf(
            Transaction(
                id = UUID.randomUUID().toString(),
                loanId = loanId,
                amount = offer.sanctionedAmount,
                type = "DISBURSEMENT",
                status = "SUCCESS",
                createdAt = dateFormat.format(now),
            ),
        )
        return Result.success(loan)
    }

    private fun buildOffer(app: LoanApplication): LoanOffer {
        val sanctioned = minOf(app.requestedAmount, app.monthlyIncome * 40)
        val rate = 12.0
        val tenure = app.requestedTenure
        val emi = calculateEmi(sanctioned, rate, tenure)
        return LoanOffer(
            applicationId = app.id,
            sanctionedAmount = sanctioned,
            interestRate = rate,
            apr = 13.2,
            tenureMonths = tenure,
            emiAmount = emi,
            riskGrade = "B",
            kfsContent = KFS_TEMPLATE.format(
                sanctioned.toLong(),
                rate,
                13.2,
                tenure,
                emi.toLong(),
            ),
        )
    }

    private fun calculateEmi(principal: Double, annualRate: Double, tenureMonths: Int): Double {
        val r = annualRate / 12 / 100
        if (r == 0.0) return principal / tenureMonths
        return principal * r * (1 + r).pow(tenureMonths) / ((1 + r).pow(tenureMonths) - 1)
    }

    private fun buildSchedule(loan: Loan): List<RepaymentInstallment> {
        val r = loan.interestRate / 12 / 100
        var balance = loan.principal
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val baseDate = Date()
        return (1..loan.tenureMonths).map { i ->
            val interest = balance * r
            val principal = loan.emiAmount - interest
            balance -= principal
            RepaymentInstallment(
                id = UUID.randomUUID().toString(),
                loanId = loan.id,
                installmentNo = i,
                dueDate = dateFormat.format(Date(baseDate.time + TimeUnit.DAYS.toMillis(30L * i))),
                principalComponent = principal,
                interestComponent = interest,
                totalAmount = loan.emiAmount,
                status = if (i == 1) InstallmentStatus.PENDING else InstallmentStatus.PENDING,
            )
        }
    }

    companion object {
        private val KFS_TEMPLATE = """
            KEY FACT STATEMENT (KFS)
            
            Sanctioned Amount: ₹%d
            Interest Rate: %.1f%% p.a.
            APR (Annual Percentage Rate): %.1f%%
            Tenure: %d months
            EMI Amount: ₹%d
            
            Processing Fee: ₹0 (waived for MVP)
            GST on Processing: ₹0
            Total Cost of Loan: As per repayment schedule
            
            Cooling-off Period: 3 days from disbursement
            Grievance Officer: grievance@tryloanify.com | 1800-123-4567
            RBI Ombudsman: 14448
            
            By accepting this offer you acknowledge viewing this KFS.
        """.trimIndent()
    }
}

@Singleton
class FakeDocumentRepository @Inject constructor(
    private val store: FakeDataStore,
) : DocumentRepository {
    override suspend fun uploadDocument(
        applicationId: String,
        type: DocumentType,
        fileName: String,
    ): Result<UploadedDocument> {
        delay(900)
        val doc = UploadedDocument(
            id = UUID.randomUUID().toString(),
            applicationId = applicationId,
            type = type,
            fileName = fileName,
            uploaded = true,
        )
        val list = store.documents.getOrPut(applicationId) { mutableListOf() }
        list.removeAll { it.type == type }
        list.add(doc)
        return Result.success(doc)
    }

    override suspend fun getDocuments(applicationId: String): List<UploadedDocument> =
        store.documents[applicationId].orEmpty()
}

@Singleton
class FakeLoanRepository @Inject constructor(
    private val store: FakeDataStore,
) : LoanRepository {
    override suspend fun getActiveLoan(): Loan? = store.activeLoan

    override suspend fun getLoan(loanId: String): Loan? =
        if (store.activeLoan?.id == loanId) store.activeLoan else null

    override suspend fun getSchedule(loanId: String): List<RepaymentInstallment> =
        store.schedules[loanId].orEmpty()

    override suspend fun getTransactions(loanId: String): List<Transaction> =
        store.transactions[loanId].orEmpty()

    override suspend fun payEmi(loanId: String, amount: Double): Result<Transaction> {
        delay(1000)
        val schedule = store.schedules[loanId]?.toMutableList() ?: return Result.failure(NoSuchElementException())
        val pending = schedule.indexOfFirst { it.status == InstallmentStatus.PENDING }
        if (pending >= 0) {
            schedule[pending] = schedule[pending].copy(status = InstallmentStatus.PAID)
            store.schedules[loanId] = schedule
        }
        val txn = Transaction(
            id = UUID.randomUUID().toString(),
            loanId = loanId,
            amount = amount,
            type = "REPAYMENT",
            status = "SUCCESS",
            createdAt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
        )
        store.transactions.getOrPut(loanId) { mutableListOf() }.add(txn)
        store.activeLoan = store.activeLoan?.copy(
            outstandingPrincipal = (store.activeLoan?.outstandingPrincipal ?: 0.0) - (amount * 0.7),
        )
        return Result.success(txn)
    }

    override suspend fun cancelWithinCoolingOff(loanId: String): Result<Unit> {
        delay(500)
        if (store.activeLoan?.id != loanId) return Result.failure(NoSuchElementException())
        val endsAt = store.activeLoan?.coolingOffEndsAt ?: return Result.failure(IllegalStateException("No cooling-off"))
        if (System.currentTimeMillis() > endsAt) {
            return Result.failure(IllegalStateException("Cooling-off period has ended"))
        }
        store.activeLoan = null
        return Result.success(Unit)
    }
}
