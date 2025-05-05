package com.openclassrooms.realestatemanagerv2.utils

import androidx.compose.ui.text.intl.Locale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.text.format

class DateUtils() {
    companion object {
        fun formatToIso8601(dateMillis: Long?): String? {
            return dateMillis?.let {
                val instant = Instant.ofEpochMilli(it)
                val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                val formatter = DateTimeFormatter.ISO_DATE
                localDate.format(formatter)
            }
        }

        fun formatToLocal(isoDate: String?): String? {
            return isoDate?.let {
                val localDate = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(
                    java.util.Locale.getDefault()
                )
                localDate.format(formatter)
            }
        }
    }
}