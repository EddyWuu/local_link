package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
//import androidx.compose.material.MaterialTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import screens.tools.CustomTopAppBar
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.layout.*
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TabRow
import androidx.compose.material.Tab
import androidx.compose.material.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import viewmodels.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import screens.tools.CalendarDataSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.time.format.FormatStyle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.TabRowDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import viewmodels.CalendarViewModel
import screens.tools.DateUtil
import screens.tools.CalendarUiState
import screens.tools.getDisplayName

@Composable
fun MyActivitiesScreen(viewModel: HomeViewModel) {

    //We make selectedDate and setSelectedDate state variables to allow the screen to react
    //to changes in these variables
    val (selectedDate, setSelectedDate) = remember { mutableStateOf(LocalDate.now()) }
    val events by viewModel.events.collectAsState()
    val registeredEvents by viewModel.registeredEvents.collectAsState()

    LaunchedEffect(Unit) {
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Text(
                            text = "My Activities",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                },
                backgroundColor = Color(0xFF1d2856),
                contentColor = Color.White,
                modifier = Modifier.height(60.dp)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFdbf1fc), Color(0xFF87CEEB))
                        )
                    )
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Calendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date: LocalDate ->
                        setSelectedDate(date)
                    },
                    events = events,
                    registeredEvents = registeredEvents
                )
            }
        }
    )
}

/**
 * This calendar implementation is heavily based on the example in this example:
 * https://medium.com/@meytataliti/android-simple-calendar-with-jetpack-compose-v2-b7311bd6e331
 * The github code is here:
 * https://github.com/mzennis/MyCalendar/tree/feature/calendarv2?source=post_page-----b7311bd6e331--------------------------------
 * We heavily modified the code for our unique purposes and to fit our app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(
    viewModel: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    events: List<models.Event>,
    registeredEvents: MutableList<models.Event>
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 65.dp)
    ) {
        item {
            CalendarWidget(
                days = DateUtil.daysOfWeek,
                yearMonth = uiState.yearMonth,
                dates = uiState.dates,
                onPreviousMonthButtonClicked = { prevMonth ->
                    viewModel.toPreviousMonth(prevMonth)
                },
                onNextMonthButtonClicked = { nextMonth ->
                    viewModel.toNextMonth(nextMonth)
                },
                selectedDate = selectedDate,
                onDateSelected = { date: LocalDate ->
                    onDateSelected(date)
                },
                events = events,
                registeredEvents = registeredEvents
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            UpcomingEvents(selectedDate = selectedDate, events = events, registeredEvents = registeredEvents)
        }
    }
}

@Composable
fun CalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    events: List<models.Event>,
    registeredEvents: MutableList<models.Event>
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row {
            repeat(days.size) {
                val item = days[it]
                DayItem(item, modifier = Modifier.weight(1f))
            }
        }
        Header(
            yearMonth = yearMonth,
            onPreviousMonthButtonClicked = onPreviousMonthButtonClicked,
            onNextMonthButtonClicked = onNextMonthButtonClicked
        )
        Content(
            dates = dates,
            selectedDate = selectedDate,
            onDateSelected = { date: LocalDate ->
                onDateSelected(date)
            },
            events = events,
            registeredEvents = registeredEvents
        )
    }
}

@Composable
fun Header(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
    Row {
        IconButton(onClick = {
            onPreviousMonthButtonClicked.invoke(yearMonth.minusMonths(1))
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back"
            )
        }
        Text(
            text = yearMonth.getDisplayName(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 25.sp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(onClick = {
            onNextMonthButtonClicked.invoke(yearMonth.plusMonths(1))
        }) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun DayItem(day: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)
        )
    }
}

@Composable
fun Content(
    dates: List<CalendarUiState.Date>,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    events: List<models.Event>,
    registeredEvents: MutableList<models.Event>
) {
    Column {
        var index = 0
        repeat(6) {
            if (index >= dates.size) return@repeat
            Row {
                repeat(7) {
                    val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
                    ContentItem(
                        date = item,
                        modifier = Modifier.weight(1f),
                        selectedDate = selectedDate,
                        onDateSelected = { date: LocalDate ->
                            onDateSelected(date)
                        },
                        events = events,
                        registeredEvents = registeredEvents
                    )
                    index++
                }
            }
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiState.Date,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    events: List<models.Event>,
    registeredEvents: MutableList<models.Event>
) {
    var hasEvent = false

    if(registeredEvents.any {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val parsedDate = LocalDate.parse(it.date, formatter)
            (parsedDate == date.fullDate)
    }) hasEvent = true

    Box(
        modifier = modifier.height(60.dp)
            .background(
                color = if (selectedDate.isEqual(date.fullDate)) {
                    Color(0xFF25a9ef)
                }
                else if (hasEvent) {
                    Color(0xFFdbf1fc)
                } else {
                    Color.Transparent
                }
            )
            .clickable {
                onDateSelected(date.fullDate)
            }


    ) {
        Text(
            text = date.dayOfMonth,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 20.sp,
            color = if (selectedDate.isEqual(date.fullDate)) Color.White else Color.Black,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp)

        )
    }
}

@Composable
fun UpcomingEvents(selectedDate: LocalDate, events: List<models.Event>, registeredEvents: MutableList<models.Event>) {
    val tabs = listOf("Local Events", "Volunteering")
    val selectedTabIndex = remember { mutableStateOf(0) }
    val roundedShape = MaterialTheme.shapes.medium

    val eventsToShow = if (selectedTabIndex.value == 0) {
        registeredEvents.filter {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val parsedDate = LocalDate.parse(it.date, formatter)
            parsedDate == selectedDate && !(it.isVolunteerEvent)
        }
    } else {
        registeredEvents.filter {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val parsedDate = LocalDate.parse(it.date, formatter)
            parsedDate == selectedDate && it.isVolunteerEvent
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface, shape = roundedShape)
    ) {

        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                    color = Color.Blue,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = { selectedTabIndex.value = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex.value == index) Color.White else Color.Black,
                            fontWeight = if (selectedTabIndex.value == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.background(
                        color = if (selectedTabIndex.value == index) Color(0xFF1d2856) else Color.LightGray
                    )
                )
            }
        }



        Spacer(modifier = Modifier.height(0.dp))



        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut()
        ) {

            // Display events based on selected tab
            Column {
                //val eventsToShow = if (selectedTabIndex.value == 0) localEvents else volunteeringEvents

                eventsToShow.forEach { event ->
                    //val event = eventsToShow[index]
                    EventItem(event)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

    }
}

@Composable
fun EventItem(event: models.Event) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(text = event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.description, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${event.date}", fontSize = 14.sp)
        }
    }
}

@Composable
fun Popup(date: LocalDate, onDismiss: () -> Unit) { //Add event as parameter to display actual data
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Event Details")
        },
        text = {
            Column {
                Text(text = "Host: Host")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Time:  Time")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Description: Description")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
