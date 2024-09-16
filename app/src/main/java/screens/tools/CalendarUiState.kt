package screens.tools

import java.time.YearMonth
import java.time.LocalDate

/**
 * This calendar implementation is heavily based on the example in this example:
 *      https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-v2-b7311bd6e331
 * The github code is here:
 *      https://github.com/mzennis/MyCalendar/tree/feature/calendarv2?source=post_page-----b7311bd6e331--------------------------------
 * We heavily modified the code for our unique purposes and to fit our app
 * This particular file is sourced from here:
 *      https://github.com/mzennis/MyCalendar/blob/feature/calendarv2/app/src/main/java/com/pandaways/mycalendar/data/CalendarUiState.kt
 */
data class CalendarUiState(
    val yearMonth: YearMonth,
    val dates: List<Date>
) {
    companion object {
        val Init = CalendarUiState(
            yearMonth = YearMonth.now(),
            dates = emptyList()
        )
    }
    data class Date(
        val dayOfMonth: String,
        val isSelected: Boolean,
        val fullDate: LocalDate
    ) {
        companion object {
            val Empty = Date("", false, LocalDate.MIN)
        }
    }
}