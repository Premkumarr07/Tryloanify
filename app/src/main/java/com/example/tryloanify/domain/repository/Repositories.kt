package com.example.tryloanify.domain.repository

import com.example.tryloanify.domain.model.Customer
import com.example.tryloanify.domain.model.Loan
import com.example.tryloanify.domain.model.LoanApplication
import com.example.tryloanify.domain.model.LoanOffer
import com.example.tryloanify.domain.model.RepaymentInstallment
import com.example.tryloanify.domain.model.Transaction
import com.example.tryloanify.domain.model.UploadedDocument
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<Unit>
    suspend fun saveProfile(customer: Customer): Result<Customer>
    suspend fun getCurrentCustomer(): Customer?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
    fun observeLoggedIn(): Flow<Boolean>
}

interface ApplicationRepository {
    suspend fun createDraft(application: LoanApplication): Result<LoanApplication>
    suspend fun updateDraft(application: LoanApplication): Result<LoanApplication>
    suspend fun submitApplication(applicationId: String): Result<LoanApplication>
    suspend fun getApplication(applicationId: String): LoanApplication?
    suspend fun getApplications(): List<LoanApplication>
    suspend fun startKyc(applicationId: String): Result<LoanApplication>
    suspend fun pollKyc(applicationId: String): Result<LoanApplication>
    suspend fun getOffer(applicationId: String): LoanOffer?
    suspend fun acceptOffer(applicationId: String): Result<LoanOffer>
    suspend fun rejectOffer(applicationId: String): Result<Unit>
    suspend fun completeESign(applicationId: String): Result<LoanApplication>
    suspend fun triggerDisbursement(applicationId: String): Result<Loan>
}

interface DocumentRepository {
    suspend fun uploadDocument(
        applicationId: String,
        type: com.example.tryloanify.domain.model.DocumentType,
        fileName: String,
    ): Result<UploadedDocument>
    suspend fun getDocuments(applicationId: String): List<UploadedDocument>
}

interface LoanRepository {
    suspend fun getActiveLoan(): Loan?
    suspend fun getLoan(loanId: String): Loan?
    suspend fun getSchedule(loanId: String): List<RepaymentInstallment>
    suspend fun getTransactions(loanId: String): List<Transaction>
    suspend fun payEmi(loanId: String, amount: Double): Result<Transaction>
    suspend fun cancelWithinCoolingOff(loanId: String): Result<Unit>
}
