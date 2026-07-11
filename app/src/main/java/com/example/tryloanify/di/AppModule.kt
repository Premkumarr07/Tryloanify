package com.example.tryloanify.di

import android.content.Context
import androidx.room.Room
import com.example.tryloanify.BuildConfig
import com.example.tryloanify.data.local.db.LoanDatabase
import com.example.tryloanify.data.local.prefs.SecurePrefs
import com.example.tryloanify.data.remote.api.ApplicationApi
import com.example.tryloanify.data.remote.api.AuthApi
import com.example.tryloanify.data.remote.api.DocumentApi
import com.example.tryloanify.data.remote.api.LoanApi
import com.example.tryloanify.data.remote.interceptor.AuthInterceptor
import com.example.tryloanify.data.repository.ApplicationRepositoryImpl
import com.example.tryloanify.data.repository.AuthRepositoryImpl
import com.example.tryloanify.data.repository.DocumentRepositoryImpl
import com.example.tryloanify.data.repository.LoanRepositoryImpl
import com.example.tryloanify.domain.repository.ApplicationRepository
import com.example.tryloanify.domain.repository.AuthRepository
import com.example.tryloanify.domain.repository.DocumentRepository
import com.example.tryloanify.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindApplicationRepository(impl: ApplicationRepositoryImpl): ApplicationRepository

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindLoanRepository(impl: LoanRepositoryImpl): LoanRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSecurePrefs(@ApplicationContext context: Context): SecurePrefs = SecurePrefs(context)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LoanDatabase =
        Room.databaseBuilder(context, LoanDatabase::class.java, "tryloanify.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideApplicationDraftDao(db: LoanDatabase): com.example.tryloanify.data.local.db.ApplicationDraftDao =
        db.applicationDraftDao()

    @Provides
    fun provideCachedLoanDao(db: LoanDatabase): com.example.tryloanify.data.local.db.CachedLoanDao =
        db.cachedLoanDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(securePrefs: SecurePrefs): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(securePrefs))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideApplicationApi(retrofit: Retrofit): ApplicationApi =
        retrofit.create(ApplicationApi::class.java)

    @Provides
    @Singleton
    fun provideDocumentApi(retrofit: Retrofit): DocumentApi = retrofit.create(DocumentApi::class.java)

    @Provides
    @Singleton
    fun provideLoanApi(retrofit: Retrofit): LoanApi = retrofit.create(LoanApi::class.java)
}
