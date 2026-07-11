package com.example.tryloanify.data.remote.api

import com.example.tryloanify.data.remote.dto.ApplicationDto
import com.example.tryloanify.data.remote.dto.AuthResponse
import com.example.tryloanify.data.remote.dto.CreateApplicationRequest
import com.example.tryloanify.data.remote.dto.CustomerDto
import com.example.tryloanify.data.remote.dto.DocumentDto
import com.example.tryloanify.data.remote.dto.LoanDto
import com.example.tryloanify.data.remote.dto.LoanOfferDto
import com.example.tryloanify.data.remote.dto.OtpRequest
import com.example.tryloanify.data.remote.dto.OtpVerifyRequest
import com.example.tryloanify.data.remote.dto.RepaymentDto
import com.example.tryloanify.data.remote.dto.TransactionDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface AuthApi {
    @POST("api/v1/auth/otp/send")
    suspend fun sendOtp(@Body request: OtpRequest)

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): AuthResponse

    @POST("api/v1/auth/profile")
    suspend fun saveProfile(@Body customer: CustomerDto): CustomerDto

    @GET("api/v1/auth/me")
    suspend fun getMe(): CustomerDto
}

interface ApplicationApi {
    @POST("api/v1/applications")
    suspend fun create(@Body request: CreateApplicationRequest): ApplicationDto

    @PUT("api/v1/applications/{id}")
    suspend fun update(@Path("id") id: String, @Body request: CreateApplicationRequest): ApplicationDto

    @POST("api/v1/applications/{id}/submit")
    suspend fun submit(@Path("id") id: String): ApplicationDto

    @GET("api/v1/applications/{id}")
    suspend fun get(@Path("id") id: String): ApplicationDto

    @GET("api/v1/applications")
    suspend fun list(): List<ApplicationDto>

    @POST("api/v1/applications/{id}/accept")
    suspend fun acceptOffer(@Path("id") id: String): LoanOfferDto

    @GET("api/v1/applications/{id}/offer")
    suspend fun getOffer(@Path("id") id: String): LoanOfferDto
}

interface DocumentApi {
    @Multipart
    @POST("api/v1/documents/upload")
    suspend fun upload(
        @Part("application_id") applicationId: RequestBody,
        @Part("doc_type") docType: RequestBody,
        @Part file: MultipartBody.Part,
    ): DocumentDto
}

interface LoanApi {
    @GET("api/v1/loans/active")
    suspend fun getActive(): LoanDto?

    @GET("api/v1/loans/{id}")
    suspend fun get(@Path("id") id: String): LoanDto

    @GET("api/v1/loans/{id}/schedule")
    suspend fun getSchedule(@Path("id") id: String): List<RepaymentDto>

    @GET("api/v1/loans/{id}/transactions")
    suspend fun getTransactions(@Path("id") id: String): List<TransactionDto>

    @POST("api/v1/payments/repay")
    suspend fun repay(@Body body: Map<String, Any>): TransactionDto
}
