package com.pagovoz.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(payment: PaymentEntity): Long

    @Query("SELECT COUNT(*) FROM payments WHERE dedupeHash = :hash AND timestamp > :sinceMillis")
    suspend fun countRecentDuplicates(hash: String, sinceMillis: Long): Int

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllLive(): LiveData<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE packageName = :pkg ORDER BY timestamp DESC")
    fun getByAppLive(pkg: String): LiveData<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLive(limit: Int = 20): LiveData<List<PaymentEntity>>

    @Query("SELECT DISTINCT packageName, appDisplayName FROM payments")
    suspend fun getDistinctApps(): List<AppSummary>

    @Query("SELECT SUM(amount) FROM payments WHERE timestamp >= :sinceMillis")
    fun getTotalSince(sinceMillis: Long): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE timestamp >= :sinceMillis AND packageName = :pkg")
    fun getTotalSinceForApp(sinceMillis: Long, pkg: String): LiveData<Double?>

    @Query("DELETE FROM payments")
    suspend fun clearAll()

    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    suspend fun getAllForExport(): List<PaymentEntity>
}

data class AppSummary(
    val packageName: String,
    val appDisplayName: String
)
