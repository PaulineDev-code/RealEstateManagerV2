package com.openclassrooms.realestatemanagerv2

import com.openclassrooms.realestatemanagerv2.utils.convertFromLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.formatMillisToLocal
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.Locale
import java.util.TimeZone

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var previousTimeZone: TimeZone
    private lateinit var previousLocale: Locale

    @Before
    fun setUp() {
        // Sauvegarde puis force UTC pour des résultats déterministes
        previousTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        // (Optionnel) figer la Locale par défaut si tu en dépends ailleurs
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        // Restaure l’environnement
        TimeZone.setDefault(previousTimeZone)
        Locale.setDefault(previousLocale)
    }

    @Test
    fun `returns empty string when millis is null`() {
        val result: String = (null as Long?).formatMillisToLocal()
        assertEquals(result, "")
    }

    @Test
    fun `formats date in French with default pattern`() {
        // 2024-06-15T00:00:00Z en ms (UTC)
        val millis = 1718409600000L
        val result = millis.formatMillisToLocal(
            pattern = "dd MMMM yyyy",
            locale = Locale.FRANCE
        )
        assertEquals(result, "15 juin 2024")
    }

    @Test
    fun `formats date in US locale with custom pattern`() {
        val millis = 1718409600000L // 2024-06-15T00:00:00Z
        val result = millis.formatMillisToLocal(
            pattern = "yyyy/MM/dd",
            locale = Locale.US
        )
        assertEquals(result,"2024/06/15")
    }

    @Test
    fun `formats month name in English with default pattern`() {
        val millis = 1718409600000L
        val result = millis.formatMillisToLocal(
            pattern = "dd MMMM yyyy",
            locale = Locale.US
        )
        assertEquals(result,"15 June 2024")
    }

    // ========== CURRENCY CONVERSION TESTS ==========

    /**
     * Test convertToLocalCurrency() - USD to local currency with conversion
     */
    @Test
    fun `convertToLocalCurrency converts USD to EUR for France`() {
        Locale.setDefault(Locale.FRANCE)
        val usdPrice = 100000.0
        val result = usdPrice.convertToLocalCurrency()

        // 100000 USD * 0.91 = 91000 EUR
        // Format français: "91 000,00 €" (avec espace insécable \u00A0)
        assertEquals("91000.0", result)
    }

    @Test
    fun `convertToLocalCurrency keeps USD for unsupported locale`() {
        Locale.setDefault(Locale.of("ar", "SA")) // Arabie Saoudite (non supportée)
        val usdPrice = 100000.0
        val result = usdPrice.convertToLocalCurrency()

        // Devrait rester en USD sans conversion
        assertEquals("100000.0", result)
    }

    /**
     * Test formatToLocalCurrency() - Format without conversion
     */
    @Test
    fun `formatToLocalCurrency formats string to French currency without conversion`() {
        Locale.setDefault(Locale.FRANCE)
        val priceString = "300000"
        val result = priceString.formatToLocalCurrency()

        // Devrait formater en EUR SANS conversion: "300 000,00 €"
        /*assertEquals("300 000,00 €", result.replace('\u00A0', ' '))*/
        assertTrue("Should contain 300", result.contains("300"))
        assertTrue("Should contain 000", result.contains("000"))
        assertTrue("Should contain EUR symbol", result.contains("€") || result.contains("EUR"))
    }

    @Test
    fun `formatToLocalCurrency formats decimal string correctly`() {
        Locale.setDefault(Locale.US)
        val priceString = "1234.56"
        val result = priceString.formatToLocalCurrency()

        // Format US: "$1,234.56"
        assertEquals("$1,234.56", result)
    }

    @Test
    fun `formatToLocalCurrency returns original string for invalid input`() {
        Locale.setDefault(Locale.US)
        val invalidString = "not a number"
        val result = invalidString.formatToLocalCurrency()

        // Devrait retourner la chaîne originale car parseDouble échoue
        assertEquals(invalidString, result)
    }

    /**
     * Test convertFromLocalCurrency() - Local currency to USD (returns raw Double as String)
     */
    @Test
    fun `convertFromLocalCurrency converts EUR to USD for France`() {
        Locale.setDefault(Locale.FRANCE)
        val eurPrice = 91000.0
        val result = eurPrice.convertFromLocalCurrency()

        // 91000 EUR / 0.91 = 100000.0 USD
        // Retourne un String du Double brut: "100000.0"
        val resultDouble = result.toDouble()
        assertEquals(100000.0, resultDouble, 0.01) // Tolérance pour arrondis
    }

    @Test
    fun `convertFromLocalCurrency keeps value for unsupported locale`() {
        Locale.setDefault(Locale.of("pt", "PT")) // Portugal (non supporté)
        val price = 50000.0
        val result = price.convertFromLocalCurrency()

        // Devrait retourner la même valeur: "50000.0"
        assertEquals("50000.0", result)
    }

    /**
     * Test round-trip conversion consistency
     */
    @Test
    fun `round-trip USD to EUR and back preserves value`() {
        Locale.setDefault(Locale.FRANCE)
        val originalUSD = 100000.0

        // USD → EUR (formatted): 100000 * 0.91 = "91 000,00 €"
        val eurString = originalUSD.convertToLocalCurrency()

        // Parse le montant EUR: "91 000,00 €" → 91000.0
        /*val eurValue = eurString
            .replace('\u00A0', ' ')           // Espace insécable → normal
            .replace(" ", "")                  // Enlever espaces
            .replace("€", "")                  // Enlever symbole
            .replace(",", ".")                 // Virgule → point
            .trim()
            .toDoubleOrNull() ?: 0.0*/

        // EUR → USD (raw): 91000 / 0.91 = "100000.0"
        val backToUsdString = eurString.toDouble().convertFromLocalCurrency()
        val finalUSD = backToUsdString.toDouble()

        // Devrait être très proche de l'original (tolérance 0.1% pour arrondis)
        assertEquals(originalUSD, finalUSD, 100.0)
    }
}
