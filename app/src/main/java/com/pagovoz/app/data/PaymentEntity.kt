package com.pagovoz.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,      // paquete de la app origen (Yape, Plin, etc.)
    val appDisplayName: String,   // nombre bonito para mostrar
    val amount: Double,
    val currencySymbol: String,
    val senderName: String?,
    val rawText: String,          // texto original de la notificación (auditoría)
    val timestamp: Long,          // epoch millis
    val dedupeHash: String        // hash para evitar notificaciones duplicadas
)
