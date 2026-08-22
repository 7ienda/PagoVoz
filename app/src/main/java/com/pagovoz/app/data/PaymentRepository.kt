package com.pagovoz.app.data

import android.content.Context
import java.security.MessageDigest

class PaymentRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).paymentDao()

    // Ventana de tiempo para considerar una notificación "duplicada" (Android suele
    // re-emitir la misma notificación varias veces en pocos segundos).
    private val DEDUPE_WINDOW_MILLIS = 15_000L

    fun makeHash(packageName: String, rawText: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest("$packageName|$rawText".toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Devuelve true si el pago fue insertado (es decir, NO era duplicado).
     */
    suspend fun insertIfNotDuplicate(payment: PaymentEntity): Boolean {
        val since = payment.timestamp - DEDUPE_WINDOW_MILLIS
        val duplicates = dao.countRecentDuplicates(payment.dedupeHash, since)
        if (duplicates > 0) return false
        dao.insert(payment)
        return true
    }

    fun getAllLive() = dao.getAllLive()
    fun getByAppLive(pkg: String) = dao.getByAppLive(pkg)
    fun getRecentLive(limit: Int = 20) = dao.getRecentLive(limit)
    fun getTotalSince(sinceMillis: Long) = dao.getTotalSince(sinceMillis)
    fun getTotalSinceForApp(sinceMillis: Long, pkg: String) = dao.getTotalSinceForApp(sinceMillis, pkg)
    suspend fun getDistinctApps() = dao.getDistinctApps()
    suspend fun clearAll() = dao.clearAll()
    suspend fun getAllForExport() = dao.getAllForExport()
}
