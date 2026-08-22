package com.pagovoz.app.util

/**
 * Catálogo de apps de pago soportadas.
 * packageName = paquete real de la app en Play Store (Perú).
 * Puedes agregar más bancos/billeteras aquí sin tocar el resto del código.
 */
data class SupportedApp(
    val packageName: String,
    val displayName: String,
    val colorHex: String
)

object SupportedApps {

    val ALL = listOf(
        SupportedApp("com.bcp.innovacxion.yapeapp", "Yape", "#762DD3"),
        SupportedApp("pe.bbva.bbvacontigo", "Plin (BBVA)", "#00A9E0"),
        SupportedApp("com.interbank.mobilebanking", "Plin (Interbank)", "#00A9E0"),
        SupportedApp("com.scotiabank.banking", "Plin (Scotiabank)", "#EE1C2E"),
        SupportedApp("com.bn.appmovilbn", "Plin (BanBif)", "#00A9E0"),
        SupportedApp("com.tunki.wallet", "Tunki", "#F58220"),
        SupportedApp("pe.bim.app", "BIM (Billetera Móvil)", "#0072CE"),
        SupportedApp("com.izipay.plim", "Plim", "#FF5A00"),
        SupportedApp("com.bcp.bank.mibanco", "Mibanco", "#00954C"),
        SupportedApp("pe.com.bbva.mobile", "BBVA App", "#072146"),
        // App genérica: si el usuario agrega manualmente otra app desde Ajustes
        SupportedApp("custom", "Otra app", "#607D8B")
    )

    fun findByPackage(pkg: String): SupportedApp? = ALL.find { it.packageName == pkg }

    fun displayNameFor(pkg: String): String = findByPackage(pkg)?.displayName ?: pkg
}
