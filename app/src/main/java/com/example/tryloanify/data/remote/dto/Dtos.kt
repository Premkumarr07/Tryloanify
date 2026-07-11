package com.example.tryloanify.data.remote.dto

data class OtpRequest(val phone: String)
data class OtpVerifyRequest(val phone: String, val otp: String)
data class AuthResponse(val access_token: String, val refresh_token: String)

data class CustomerDto(
    val id: String?,
    val full_name: String,
    val phone: String,
    val email: String,
    val pan_number: String,
)

data class CreateApplicationRequest(
    val requested_amount: Double,
    val requested_tenure: Int,
    val employment_type: String,
    val monthly_income: Double,
)

data class ApplicationDto(
    val id: String,
    val tracking_id: String,
    val customer_id: String,
    val requested_amount: Double,
    val requested_tenure: Int,
    val employment_type: String,
    val monthly_income: Double,
    val status: String,
    val kyc_status: String?,
)

data class LoanOfferDto(
    val application_id: String,
    val sanctioned_amount: Double,
    val interest_rate: Double,
    val apr: Double,
    val tenure_months: Int,
    val emi_amount: Double,
    val risk_grade: String,
    val kfs_content: String,
)

data class DocumentDto(
    val id: String,
    val application_id: String,
    val doc_type: String,
    val file_name: String,
)

data class LoanDto(
    val id: String,
    val application_id: String,
    val customer_id: String,
    val principal: Double,
    val interest_rate: Double,
    val tenure_months: Int,
    val emi_amount: Double,
    val outstanding_principal: Double,
    val status: String,
    val disbursement_date: String,
    val next_emi_date: String,
)

data class RepaymentDto(
    val id: String,
    val loan_id: String,
    val installment_no: Int,
    val due_date: String,
    val principal_component: Double,
    val interest_component: Double,
    val total_amount: Double,
    val status: String,
)

data class TransactionDto(
    val id: String,
    val loan_id: String,
    val amount: Double,
    val type: String,
    val status: String,
    val created_at: String,
)
