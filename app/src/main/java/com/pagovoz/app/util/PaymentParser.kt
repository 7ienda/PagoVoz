package com.pagovoz.app.util

import java.util.Locale
import java.util.regex.Pattern

/**
 * Resultado de analizar el texto de una notificación de pago.
 */
data class ParsedPayment(
    val amount: Double,
    val currencySymbol: String,
    val senderName: String?
)

/**
 * Extrae el monto y (si es posible) el nombre de quien envía el pago,
 * a partir del título + texto de una notificación push de apps como
 * Yape, Plin, Tunki, BIM, etc.
 *
 * Ejemplos reales de texto que llegan en notificaciones:
 *  "Yape! Te llegó un pago de S/ 50.00 de Juan Pérez"
 *  "Recibiste S/25.50 de MARIA LOPEZ"
 *  "Plin: Has recibido S/ 120.00 de Carlos R."
 */
object PaymentParser {

    // Soporta S/, S/., PEN, y opcionalmente USD ($)
    private val AMOUNT_PATTERN: Pattern = Pattern.compile(
        "(S/\\.?|PEN|\\$)\\s?([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Intenta capturar "de <Nombre>" al final del texto
    private val SENDER_PATTERN: Pattern = Pattern.compile(
        "\\bde\\s+([A-ZÑÁÉÍÓÚ][A-Za-zÑñÁÉÍÓÚáéíóú.\\-]*(?:\\s+[A-ZÑÁÉÍÓÚ][A-Za-zÑñÁÉÍÓÚáéíóú.\\-]*){0,3})\\s*$"
    )

    fun parse(fullText: String): ParsedPayment? {
        val matcher = AMOUNT_PATTERN.matcher(fullText)
        if (!matcher.find()) return null

        val symbolRaw = matcher.group(1) ?: "S/"
        val numberRaw = matcher.group(2) ?: return null

        val normalized = numberRaw
            .replace(".", "#")
            .replace(",", ".")
            .replace("#", "")
            // Si el separador decimal original era punto (formato peruano estándar 50.00)
            .let {
                // Reconstruir de forma segura: preferimos el patrón peruano "1234.56"
                numberRaw.replace(",", "")
            }

        val amount = normalized.toDoubleOrNull() ?: return null

        val symbol = when {
            symbolRaw.contains("$") -> "$"
            else -> "S/"
        }

        val senderMatcher = SENDER_PATTERN.matcher(fullText)
        val sender = if (senderMatcher.find()) senderMatcher.group(1)?.trim() else null

        return ParsedPayment(amount = amount, currencySymbol = symbol, senderName = sender)
    }

    fun formatAmount(amount: Double, symbol: String = "S/"): String {
        return String.format(Locale("es", "PE"), "%s %.2f", symbol, amount)
    }
}
