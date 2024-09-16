package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import screens.tools.CalendarDataSource
import screens.tools.CalendarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * This calendar implementation is heavily based on the example in this example:
 *      https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-v2-b7311bd6e331
 * The github code is here:
 *      https://github.com/mzennis/MyCalendar/tree/feature/calendarv2?source=post_page-----b7311bd6e331--------------------------------
 * We heavily modified the code for our unique purposes and to fit our app
 * This particular file is directly sourced from here:
 *      https://github.com/mzennis/MyCalendar/blob/feature/calendarv2/app/src/main/java/com/pandaways/mycalendar/ui/CalendarViewModel.kt
 */
class CalendarViewModel : ViewModel() {

    private val dataSource by lazy { CalendarDataSource() }

    private val _uiState = MutableStateFlow(CalendarUiState.Init)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    dates = dataSource.getDates(currentState.yearMonth)
                )
            }
        }
    }

    fun toNextMonth(nextMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    yearMonth = nextMonth,
                    dates = dataSource.getDates(nextMonth)
                )
            }
        }
    }

    fun toPreviousMonth(prevMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    yearMonth = prevMonth,
                    dates = dataSource.getDates(prevMonth)
                )
            }
        }
    }

}