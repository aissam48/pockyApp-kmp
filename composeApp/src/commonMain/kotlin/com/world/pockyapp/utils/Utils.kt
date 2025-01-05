package com.world.pockyapp.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object Utils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun formatCreatedAt(createdAt: String?): String {

        if (createdAt == null){
            return ""
        }

        val formatPattern = "yyyy-MM-dd HH:mm:ss"

        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(formatPattern)
        }
        val parsedDateTime = dateTimeFormat.parse(createdAt)

        val createdInstant = parsedDateTime.toInstant(TimeZone.UTC)
        val createdInSystemZone = createdInstant.toLocalDateTime(TimeZone.currentSystemDefault())


        val currentMoment: Instant = Clock.System.now()
        val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

        val diffInSeconds = datetimeInSystemZone.toInstant(TimeZone.currentSystemDefault())
            .minus(createdInSystemZone.toInstant(TimeZone.currentSystemDefault()))
            .inWholeSeconds

        return when {
            diffInSeconds < 60 -> "just now"
            diffInSeconds < 3600 -> "${diffInSeconds / 60} minutes ago"
            diffInSeconds < 86400 -> "${diffInSeconds / 3600} hours ago"
            diffInSeconds < 604800 -> "${diffInSeconds / 86400} days ago"
            diffInSeconds < 2592000 -> "${diffInSeconds / 604800} weeks ago"
            diffInSeconds < 31536000 -> "${diffInSeconds / 2592000} months ago"
            else -> "${diffInSeconds / 31536000} years ago"
        }

    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = "^[+]?[0-9]{10,15}$"
        return phone.matches(phoneRegex.toRegex())
    }

    fun isValidUsername(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9_]+$"
        return username.matches(usernameRegex.toRegex())
    }
}