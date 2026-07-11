package com.example.tryloanify.domain.model

enum class EmploymentType {
    SALARIED,
    SELF_EMPLOYED,
    BUSINESS,
}

enum class ApplicationStatus {
    DRAFT,
    SUBMITTED,
    KYC_VERIFIED,
    KYC_FAILED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    OFFER_ACCEPTED,
    ESIGN_COMPLETED,
    DISBURSED,
}

enum class DocumentType {
    PAN,
    AADHAAR,
    INCOME_PROOF,
    BANK_STATEMENT,
}

enum class KycStatus {
    PENDING,
    IN_PROGRESS,
    VERIFIED,
    FAILED,
}

enum class LoanStatus {
    ACTIVE,
    CLOSED,
    DEFAULTED,
}

enum class InstallmentStatus {
    PENDING,
    PAID,
    OVERDUE,
}

data class Customer(
    val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val panNumber: String,
)

data class LoanApplication(
    val id: String,
    val trackingId: String,
    val customerId: String,
    val requestedAmount: Double,
    val requestedTenure: Int,
    val employmentType: EmploymentType,
    val monthlyIncome: Double,
    val status: ApplicationStatus,
    val kycStatus: KycStatus = KycStatus.PENDING,
)

data class LoanOffer(
    val applicationId: String,
    val sanctionedAmount: Double,
    val interestRate: Double,
    val apr: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val riskGrade: String,
    val kfsContent: String,
)

data class UploadedDocument(
    val id: String,
    val applicationId: String,
    val type: DocumentType,
    val fileName: String,
    val uploaded: Boolean,
)

data class Loan(
    val id: String,
    val applicationId: String,
    val customerId: String,
    val principal: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val outstandingPrincipal: Double,
    val status: LoanStatus,
    val disbursementDate: String,
    val nextEmiDate: String,
    val coolingOffEndsAt: Long?,
)

data class RepaymentInstallment(
    val id: String,
    val loanId: String,
    val installmentNo: Int,
    val dueDate: String,
    val principalComponent: Double,
    val interestComponent: Double,
    val totalAmount: Double,
    val status: InstallmentStatus,
)

data class Transaction(
    val id: String,
    val loanId: String,
    val amount: Double,
    val type: String,
    val status: String,
    val createdAt: String,
)
