package com.example.tryloanify.data.local.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.tryloanify.domain.model.EmploymentType

@Entity(tableName = "application_drafts")
data class ApplicationDraftEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val requestedAmount: Double,
    val requestedTenure: Int,
    val employmentType: String,
    val monthlyIncome: Double,
    val trackingId: String,
    val updatedAt: Long,
)

@Entity(tableName = "cached_loans")
data class CachedLoanEntity(
    @PrimaryKey val id: String,
    val applicationId: String,
    val principal: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val outstandingPrincipal: Double,
    val status: String,
    val disbursementDate: String,
    val nextEmiDate: String,
    val jsonPayload: String,
)

@Dao
interface ApplicationDraftDao {
    @Query("SELECT * FROM application_drafts ORDER BY updatedAt DESC")
    suspend fun getAll(): List<ApplicationDraftEntity>

    @Query("SELECT * FROM application_drafts WHERE id = :id")
    suspend fun getById(id: String): ApplicationDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(draft: ApplicationDraftEntity)

    @Query("DELETE FROM application_drafts WHERE id = :id")
    suspend fun delete(id: String): Int
}

@Dao
interface CachedLoanDao {
    @Query("SELECT * FROM cached_loans LIMIT 1")
    suspend fun getActive(): CachedLoanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(loan: CachedLoanEntity)
}

@Database(
    entities = [ApplicationDraftEntity::class, CachedLoanEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class LoanDatabase : RoomDatabase() {
    abstract fun applicationDraftDao(): ApplicationDraftDao
    abstract fun cachedLoanDao(): CachedLoanDao
}

fun ApplicationDraftEntity.toEmploymentType(): EmploymentType =
    EmploymentType.valueOf(employmentType)
