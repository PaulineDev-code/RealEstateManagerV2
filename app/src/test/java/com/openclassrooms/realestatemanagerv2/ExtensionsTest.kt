package com.openclassrooms.realestatemanagerv2

import com.openclassrooms.realestatemanagerv2.utils.convertFromLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.formatMillisToLocal
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone

/**
 * Unit tests for the utility extension functions defined in Extensions.kt.
 *
 * These tests verify:
 * 1. Date formatting across different locales and patterns.
 * 2. Currency conversion (USD to Local and Local to USD).
 * 3. Robustness against null or invalid inputs.
 */
class ExtensionsTest {
    private lateinit var previousTimeZone: TimeZone
    private lateinit var previousLocale: Locale

    @Before
    fun setUp() {
        previousTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        TimeZone.setDefault(previousTimeZone)
        Locale.setDefault(previousLocale)
    }

    @Test
    fun `returns empty string when millis is null`() {
        val result: String = (null as Long?).formatMillisToLocal()
        assertEquals("", result )
    }

    @Test
    fun `formats date in French with default pattern`() {
        // 2024-06-15T00:00:00Z en ms (UTC)
        val millis = 1718409600000L
        val result = millis.formatMillisToLocal(
            locale = Locale.FRANCE
        )
        assertEquals("15 juin 2024", result )
    }

    @Test
    fun `formats date in US locale with short pattern`() {
        val millis = 1718409600000L // 2024-06-15T00:00:00Z
        val result = millis.formatMillisToLocal(
            dateStyle = FormatStyle.SHORT,
            locale = Locale.US
        )
        assertEquals("6/15/24", result)
    }

    @Test
    fun `formats month name in American with long pattern`() {
        val millis = 1718409600000L
        val result = millis.formatMillisToLocal(
            dateStyle = FormatStyle.LONG,
            locale = Locale.US
        )
        assertEquals("June 15, 2024", result)
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
        assertEquals(91000.0, result, 0.001)
    }

    @Test
    fun `convertToLocalCurrency keeps USD for unsupported locale`() {
        val saudiLocale = Locale.Builder()
            .setLanguage("ar")
            .setRegion("SA")
            .build()

        Locale.setDefault(saudiLocale)
        val usdPrice = 100000.0
        val result = usdPrice.convertToLocalCurrency()

        assertEquals(100000.0, result, 0.001)
    }

    /**
     * Test formatToLocalCurrency() - Format without conversion
     */
    @Test
    fun `formatToLocalCurrency formats string to French currency without conversion`() {
        Locale.setDefault(Locale.FRANCE)
        val priceString = "300000"
        val result = priceString.formatToLocalCurrency()

        assertTrue("Should contain 300", result.contains("300"))
        assertTrue("Should contain 000", result.contains("000"))
        assertTrue("Should contain EUR symbol", result.contains("€") || result.contains("EUR"))
    }

    @Test
    fun `formatToLocalCurrency formats decimal string correctly`() {
        Locale.setDefault(Locale.US)
        val priceString = "1234.56"
        val result = priceString.formatToLocalCurrency()

        assertEquals("$1,234.56", result)
    }

    @Test
    fun `formatToLocalCurrency returns original string for invalid input`() {
        Locale.setDefault(Locale.US)
        val invalidString = "not a number"
        val result = invalidString.formatToLocalCurrency()

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
        assertEquals(100000.0, result, 0.01) // Tolérance pour arrondis
    }

    @Test
    fun `convertFromLocalCurrency keeps value for unsupported locale`() {
        val portugalLocale = Locale.Builder()
            .setLanguage("pt")
            .setRegion("PT")
            .build()

        Locale.setDefault(portugalLocale) // Portugal
        val price = 50000.0
        val result = price.convertFromLocalCurrency()

        assertEquals(50000.0, result, 0.001)
    }

    /**
     * Test round-trip conversion consistency
     */
    @Test
    fun `round-trip USD to EUR and back preserves value`() {
        Locale.setDefault(Locale.FRANCE)
        val originalUSD = 100000.0

        // USD → EUR (formatted): 100000 * 0.91 = "91 000,00 €"
        val eurDouble = originalUSD.convertToLocalCurrency()

        // EUR → USD (raw): 91000 / 0.91 = "100000.0"
        val finalUSD = eurDouble.convertFromLocalCurrency()

        assertEquals(originalUSD, finalUSD, 10.0)
    }
}
