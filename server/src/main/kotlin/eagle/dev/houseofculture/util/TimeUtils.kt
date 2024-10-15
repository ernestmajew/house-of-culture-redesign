package eagle.dev.houseofculture.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Instant.atDefaultZone() =
    atZone(ZoneId.systemDefault())

fun Instant.toLocalDate() =
    atDefaultZone().toLocalDate()

fun Instant.toOffsetDateTime() =
    atDefaultZone().toOffsetDateTime()

fun Instant.toFormattedOffsetDateTime(): String {
    val offsetDateTime = atDefaultZone().toOffsetDateTime()
    return offsetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}
fun LocalDate.toInstant() =
    atStartOfDay(ZoneOffset.UTC).toInstant()

fun Instant.noZone() =
    atZone(ZoneId.of("Europe/London"))

fun Instant.toLocalDateWithoutZone() =
    noZone().toLocalDate()

fun Instant.toOffsetDateTimeWithoutZone() =
    noZone().toOffsetDateTime()