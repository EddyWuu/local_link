import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodels.HomeViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import java.util.Locale
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel

// Model imports
import models.Event
import models.Unavailability
import models.User
import viewmodels.ProfileViewModel
import viewmodels.UserViewModel
import java.text.SimpleDateFormat

// JSON PARSING BEGINS

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val registeredEvents by viewModel.registeredEvents.collectAsState()
    val user by userViewModel.loggedInUser.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedRecommendedEvent by remember { mutableStateOf<Event?>(null) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }
    val userPreferences by userViewModel.userPreferences.collectAsState()

    val cleanPreferences = remember(userPreferences) {
        userPreferences?.split(",")?.map { it.trim().lowercase(Locale.getDefault()) } ?: emptyList()
    }

    val filteredEvents = filterEvents(events, cleanPreferences)

    val unavailabilities2 by profileViewModel.unavailabilities2.collectAsState()

    LaunchedEffect(user?.profileID) {
        user?.profileID?.let { profileID ->
            profileViewModel.getUnavailability2(profileID)
        }
    }
    //unavailabilities2.forEach{ ... } refer to profile page to see how this is done

    val filteredEvents2 = filterUnavailability(events, unavailabilities2)
    val filteredEvents3 = filteredEvents2.intersect(filteredEvents).toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Home",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFdbf1fc), Color(0xFF87CEEB))
                        )
                    )
                    .padding(paddingValues),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                ) {
                    if(registeredEvents.size > 0) {
                        Column {
                            Text(
                                text = "Upcoming Events",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(registeredEvents) { event ->
                                    UpcomingEventItem(
                                        event = event,
                                        onClick = {
                                            selectedEvent = event
                                            isDialogVisible = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else{
                        Column(
                            modifier = Modifier.fillMaxSize().fillMaxWidth()
                        ) {

                            Text(
                                text = "No upcoming events found",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(2.dp, Color.Gray),
                                elevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                backgroundColor = Color.Transparent
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFF1e81b0), Color(0xFF76b5c5))
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .fillMaxSize()
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Try adding some recommended events to your calendar",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 22.sp
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }

                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Recommended Events",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            .align(Alignment.Start)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEvents3) { event ->
                            EventItem(event) {
                                selectedRecommendedEvent = event
                                showDialog = true
                            }
                        }
                    }
                }
            }

            // Show dialog if an event is selected
            if (showDialog && selectedRecommendedEvent != null) {
                user?.let {
                    RecommendedEventDialog(event = selectedRecommendedEvent!!, viewModel=viewModel, user = it) {
                        showDialog = false
                    }
                }
            }
            else if (isDialogVisible && selectedEvent != null) {
                user?.let {
                    EventDialog(
                        event = selectedEvent!!,
                        onDismiss = { isDialogVisible = false },
                        viewModel=viewModel,
                        user = it
                    )
                }
            }
        }
    )
}

@Composable
fun filterUnavailability(events: List<Event>, unavailabilities: List<Unavailability>): List<Event> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var filteredOutCount by remember { mutableStateOf(0) }


    val filteredEvents = events.filter { event ->
        val eventDate = dateFormat.parse(event.date)
        val eventStartHour = event.time.split(":")[0].toInt()
        unavailabilities.none { unavailability ->
            val unavailDate = dateFormat.parse(unavailability.date)
            val unavailStart = unavailability.startTime.toInt()
            if (eventDate == unavailDate) {
                eventStartHour >= unavailStart
            } else {
                false
            }
        }
    }
    filteredOutCount = events.size - filteredEvents.size
    Log.d("HomePage", "Events filtered out: $filteredOutCount")
    return filteredEvents
}



@Composable
fun filterEvents(events: List<Event>, preferences: List<String>): List<Event> {
    return if (preferences.isNotEmpty()) {
        events.filter { event ->
            // Assuming event.labels is a List<String>
            val eventLabels = event.labels.map { it.lowercase(Locale.getDefault()) }
            preferences.any { preference ->
                eventLabels.any { label ->
                    label.contains(preference.lowercase(Locale.getDefault()))
                }
            }
        }
    } else {
        events
    }
}
/**
 * This dialog appears when the user selects a recommended event
 * The usage of a dialog was inspired by the official documentation
 * on dialogs: https://developer.android.com/develop/ui/compose/components/dialog
 * Also Surface: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#Surface(androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.foundation.BorderStroke,androidx.compose.ui.unit.Dp,kotlin.Function0)
 */
@Composable
fun RecommendedEventDialog(viewModel: HomeViewModel, event: Event, user: User, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Date: ${event.date}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Time: ${event.time}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Location: ${event.address}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "About: ${if (event.description.length > 150) {
                        "${event.description.take(150)}..."
                    } else {
                        event.description
                    }}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        viewModel.registerEvent(event = event, user = user)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Add Event", color=Color.White)
                }
            }
        }
    }
}

/**
 * This dialog pops up when a user selects an upcoming event
 * Same inspirations as previous dialog function
 */
@Composable
fun EventDialog(viewModel: HomeViewModel, event: Event, user: User, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Date: ${event.date}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Time: ${event.time}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Location: ${event.address}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                    ) {
                        Text("Close", color=Color.White)
                    }
                    Button(
                        onClick = {
                            viewModel.removeEvent(event = event, user = user)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                    ) {
                        Text("Remove Event", color=Color.White)
                    }
                }

            }
        }
    }
}

/**
 * Front end code for each event item in the feed.
 * Each event is represented by a box in this section
 * that contains all of its relevant information.
 */
@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Gray),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(onClick = onClick),
        backgroundColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFd3dfff), Color(0xFFeff2f9))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = event.title,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.date,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = event.time,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (event.isVolunteerEvent) "Volunteer Event" else "Community Event",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (event.description.length > 150) {
                            "${event.description.take(150)}..."
                        } else {
                            event.description
                        },
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (event.imageResId != 0) {
                    Image(
                        painter = painterResource(id = event.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

/**
 * Front end code for each upcoming event item
 * Shows basic information about upcoming events
 */
@Composable
fun UpcomingEventItem(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Gray),
        elevation = 8.dp,
        modifier = Modifier
            .width(200.dp)
            .height(150.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        backgroundColor = Color.Transparent // Set to transparent to allow gradient to show
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1e81b0), Color(0xFF76b5c5))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = event.title, // Update to use title if needed
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (event.description.length > 150) {
                        "${event.description.take(150)}..."
                    } else {
                        event.description
                    },
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (event.imageResId != 0) {
                    Image(
                        painter = painterResource(id = event.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}
