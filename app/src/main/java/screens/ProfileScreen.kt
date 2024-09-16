package screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.adventureaid.R
import viewmodels.HomeViewModel
import viewmodels.ProfileViewModel
import viewmodels.UserViewModel
import models.Unavailability
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavController



@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    mainNavController: NavController
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var boxes by remember { mutableStateOf(listOf<String>()) }
    var displaySettingsMenu by remember { mutableStateOf(false) }
    val user by userViewModel.loggedInUser.collectAsState()
    val userPreferences by userViewModel.userPreferences.collectAsState()

    val profilepic = "www.example.com" // Pull from Database later


    var displaysettingsmenu by remember { mutableStateOf(false) }
    var popupState by remember { mutableStateOf(false) }
    val interests = listOf(
        "academic",
        "academic-session",
        "agriculture",
        "air-quality",
        "airport",
        "american-football",
        "animal",
        "architecture",
        "attraction",
        "australian-football",
        "auto-racing",
        "automotive",
        "autumn-holiday",
        "badminton",
        "bars-open",
        "baseball",
        "basketball",
        "bicycle",
        "boxing",
        "business",
        "campus",
        "career",
        "chemical",
        "christmas-holiday",
        "civil",
        "clothing",
        "club",
        "coastal-event",
        "cold-wave",
        "comedy",
        "comic",
        "community",
        "concert",
        "conference",
        "construction",
        "corporate",
        "course",
        "craft",
        "debates",
        "delay",
        "design",
        "digital",
        "drought",
        "easter-holiday",
        "education",
        "election",
        "entertainment",
        "entertainment-closed",
        "entertainment-open",
        "environment",
        "environment-pollution",
        "esports",
        "estimated",
        "exam",
        "expo",
        "f1",
        "family",
        "fashion",
        "festival",
        "food",
        "football",
        "fundraiser",
        "furniture",
        "gaming",
        "golf",
        "graduation",
        "gymnastics",
        "hazmat",
        "health",
        "health-warning",
        "hockey",
        "holiday",
        "holiday-christian",
        "holiday-hebrew",
        "holiday-hindu",
        "holiday-local",
        "holiday-local-common",
        "holiday-muslim",
        "holiday-national",
        "holiday-observed",
        "holiday-orthodox",
        "holiday-religious",
        "horse-racing",
        "horticulture",
        "hostage-crisis",
        "household",
        "hurricane",
        "hybrid-session",
        "ice-hockey",
        "in-person-session",
        "industrial",
        "indycar",
        "instrument",
        "jewelry",
        "landslide",
        "local-market",
        "lockdown",
        "lpga",
        "marathon",
        "medical",
        "mineral",
        "minor-league",
        "mlb",
        "mls",
        "mma",
        "monster-truck",
        "motocross",
        "motogp",
        "movie",
        "music",
        "nascar",
        "natural",
        "nba",
        "nba-gleague",
        "ncaa",
        "nfl",
        "nhl",
        "nursing",
        "observance",
        "observance-local",
        "observance-season",
        "observance-united-nations",
        "observance-worldwide",
        "office",
        "olympic",
        "online-session",
        "outdoor",
        "packaging",
        "paper",
        "parade",
        "parliament",
        "performing-arts",
        "personal-care-closed",
        "personal-care-open",
        "pet",
        "pga",
        "plastic",
        "politics",
        "president",
        "print",
        "product",
        "rain",
        "rallies",
        "real-estate",
        "recreation-closed",
        "recreation-open",
        "referendum",
        "religion",
        "research-development",
        "restaurant-closed",
        "restaurant-open",
        "retail-closed",
        "retail-open",
        "rodeo",
        "rugby",
        "running",
        "sales",
        "sand",
        "school",
        "science",
        "seminar",
        "skating",
        "snow",
        "soccer",
        "social",
        "space",
        "sport",
        "spring-holiday",
        "storm",
        "storm-surge",
        "summer-holiday",
        "table-tennis",
        "technology",
        "tennis",
        "terror",
        "thanksgiving-holiday",
        "thunderstorm",
        "tool",
        "tornado",
        "tourism",
        "training",
        "transportation",
        "travel",
        "triathlon",
        "tropical-storm",
        "tsunami",
        "typhoon",
        "vehicle-accident",
        "volcano",
        "volleyball",
        "weather",
        "weather-warning",
        "wedding",
        "wildfire",
        "wind",
        "winter-holiday",
        "wnba",
        "worship-closed",
        "worship-open",
        "wrestling",
        "wwe",
        "youth-sport"
    )
    val eventsAttended = 0
    val volunteerHours = 0

    LaunchedEffect(user) {
        user?.preferences?.let { prefs ->
            boxes = prefs.split(",").filter { it.isNotEmpty() }
        }
    }
    LaunchedEffect(userPreferences) {
        userPreferences?.let { prefs ->
            boxes = prefs.split(",").filter { it.isNotEmpty() }
        }
    }

    val profilePics = listOf(
        R.drawable.profile1,
        R.drawable.profile2,
        R.drawable.profile3,
        R.drawable.profile4,
        R.drawable.profile5,
        R.drawable.profile6
    )
    val currentPic by profileViewModel.currentPic.collectAsState()
    var showPicOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "    Profile",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { displaySettingsMenu = !displaySettingsMenu }) {
                        Icon(
                            imageVector = if (!displaySettingsMenu) Icons.Filled.Menu else Icons.Filled.Close,
                            contentDescription = "Settings",
                            tint = Color.White
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
                    .background(Color.White)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFdbf1fc), Color(0xFF87CEEB))
                        )
                    )
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .clickable(onClick = {
                            //add onclick here
                            showPicOptions = true
                        })
                )  {
                    AsyncImage( //https://coil-kt.github.io/coil/compose/
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentPic)
                            //.crossfade(true)
                            .build(),
                        placeholder = painterResource(android.R.drawable.ic_menu_camera),
                        contentDescription = "Profile pic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                }

                if (showPicOptions) {
                    ChoosePfp(
                        profilePics = profilePics,
                        onDismiss = { showPicOptions = false },
                        onSelect = { profilePic ->
                            profileViewModel.updateProfilePic(profilePic)
                            showPicOptions = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                if(displaySettingsMenu){
                    SettingsMenu(userViewModel = userViewModel, navController = mainNavController)
                } else {
                    Text(
                        text = "${user?.firstName} ${user?.lastName}",
                        color = Color.Black,
                        fontSize = 36.sp
                    )
                    Text(
                        text = "${user?.email}",
                        color = Color.Black,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Events Attended: ",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = eventsAttended.toString(),
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Volunteer Hours: ",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = volunteerHours.toString(),
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            popupState = true
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))) {
                        Text(text = "Update Unavailability",color = Color.White)

                    }
                    if (popupState) {
                        Availability(profileViewModel, onDismiss = { popupState = false }, userViewModel)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(vertical = 8.dp, horizontal = 24.dp)
                            .background(Color(0xFF181D63), RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFF3455EB), RoundedCornerShape(8.dp)) // Adjust thickness as needed
                            .padding(8.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2F3185)) // Set the color to purple
                                    .padding(horizontal = 8.dp) // Set the horizontal padding
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .background(Color(0xFF2F3185)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Preferences",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp)
                            ) {
                                items(boxes.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth(0.5f)
                                            .height(40.dp)
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .border(3.dp, Color.White, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 8.dp, end = 0.dp)
                                        ) {
                                            Text(
                                                text = boxes[index],
                                                color = Color(0xFF5F6066),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            IconButton(onClick = {
                                                val removedItem = boxes[index]
                                                boxes = boxes.toMutableList().apply { removeAt(index) }
                                                Log.d("ProfileScreen", "Removed preference: $removedItem")
                                                val updatedPreferences = boxes.joinToString(",")
                                                userViewModel.updateUserPreferences(updatedPreferences)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color(0xFF9A9DAB) // Set the color to blue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { showOptionsDialog = true }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))) {
                        Text("Add Preference", color = Color.White)
                    }
                }
            }

            if (showOptionsDialog) {
                Dialog(onDismissRequest = { showOptionsDialog = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Select Interest")
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            ) {
                                items(interests.size) { index ->
                                    Button(
                                        onClick = {
                                            selectedOption = interests[index]
                                            Log.d("ProfileScreen", "Selected option: $selectedOption")
                                            if (!boxes.contains(selectedOption)) {
                                                boxes = boxes + selectedOption
                                                Log.d("ProfileScreen", "Added preference: $selectedOption")
                                                val updatedPreferences = boxes.joinToString(",")
                                                userViewModel.updateUserPreferences(updatedPreferences)
                                            }
                                            selectedOption = ""
                                            showOptionsDialog = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(interests[index])
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ChoosePfp(
    profilePics: List<Int>,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Choose a Profile Picture") },
        text = {
            LazyVerticalGrid( //docs https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/package-summary#LazyVerticalGrid(androidx.compose.foundation.lazy.grid.GridCells,androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.grid.LazyGridState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)
                columns = GridCells.Adaptive(minSize = 77.dp), //try to fit max images horizontally
                modifier = Modifier
                    .height(250.dp)
                    .padding(12.dp)
            ) {
                items(profilePics.size) { index ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(77.dp)
                            .padding(12.dp)
                            .background(Color.Black, shape = CircleShape)
                            .clickable {
                                onSelect(profilePics[index])
                            }
                    ) {
                        Image(
                            painter = painterResource(id = profilePics[index]),
                            contentDescription = "Profile picture choice",
                            modifier = Modifier
                                .size(77.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }, buttons = {Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) { Button(
            onClick = { onDismiss() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))
        ) {
            Text("Exit", color = Color.White)
        }}}



    )
}


@Composable
fun SettingsMenu(userViewModel: UserViewModel, navController: NavController) {
    val isLoggedOut by userViewModel.isLoggedOut.collectAsState()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
            userViewModel.resetLogoutState()
        }
    }

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC3E9E7), Color(0xFF60c78e))
                )
            )
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("support@locallink.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Support Needed")
                }
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))
        ) {
            Text(
                text = "Contact Us", color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        var displayTOS by remember { mutableStateOf(false) }
        Button(
            onClick = {
                displayTOS = true
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))

        ) {
            Text(
                text = "Terms of Service",color = Color.White
            )
        }
        if (displayTOS) {
            AlertDialog(
                onDismissRequest = { displayTOS = false },
                title = { Text(text = "Terms of Service") },
                text = {
                    Box(
                        modifier = Modifier
                            .height(435.dp)
                            .padding(all = 8.dp)
                            .fillMaxWidth()



                    ) {
                        Text(
                            text = """                                            
                            By using LocalLink, you agree to be bound by these Terms of Service.
                            
                            1. Acceptance of Terms
                            By using LocalLink, you acknowledge that you have read, understood, and agree to be bound by these Terms, as well as our Privacy Policy.
                            2. Description of Service
                            LocalLink provides a platform for users to find volunteering and meetup events.
                            4. Community Guidelines
                            Treat each other with respect. Violence, Harassment, bullying, and discrimination will not be tolerated.
                            5. Limitation of Liability
                            LocalLink is not responsible for any harm or damages resulting from your use of the App or from attending events posted on the App. Events are organized and attended at your own risk.
                            """.trimIndent()
                        )
                    }
                },
                buttons = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { displayTOS = false },colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))) {
                            Text("Close", color = Color.White)
                        }
                    }
                }
            )
        }

        Button(
            onClick = {
                userViewModel.logout()
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))

        ) {
            Text(
                text = "Sign Out", color = Color.White
            )
        }
    }
}

@Composable
fun Availability(profileViewModel: ProfileViewModel, onDismiss: () -> Unit, userViewModel: UserViewModel) {
    val year = 2024
    val user by userViewModel.loggedInUser.collectAsState()
    val months = listOf(
        "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
        "11", "12"
    )
    val days = listOf(
        "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
    )
    val hours = listOf(
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
        "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20", "21", "22", "23"
    )
    var selectedMonth by remember { mutableStateOf(months.first()) }
    var selectedDay by remember { mutableStateOf(days.first()) }
    var selectedStartTime by remember { mutableStateOf(hours.first()) }
    var selectedEndTime by remember { mutableStateOf(hours.first()) }
    var monthPop by remember { mutableStateOf(false) }
    var dayPop by remember { mutableStateOf(false) }
    var startTimePop by remember { mutableStateOf(false) }
    var endTimePop by remember { mutableStateOf(false) }
    val unavailabilities = profileViewModel.unavailabilities
    LaunchedEffect(user) {
        user?.profileID?.let {
            profileViewModel.getUnavailability(it)
        }
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.background,
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        onDismiss()
                    },modifier = Modifier.align(Alignment.End), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))

                ) {
                    Text(
                        text = "Exit", color = Color.White
                    )
                }
                Text(text = "Current Unavailability", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                 //ADD  here

                unavailabilities.forEach { unavailability ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${unavailability.date}: ${unavailability.startTime}h - ${unavailability.endTime}h",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                // delete functionality
                                user?.profileID?.let { profileID ->
                                    profileViewModel.deleteUnavailability(
                                        profileID,
                                        unavailability.date,
                                        unavailability.startTime,
                                        unavailability.endTime
                                    )
                                }
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))

                        ) {
                            Text(
                                text = "Del.",
                                color = Color.White
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Select Date and Time", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // month work here, also added rows for alighning boxes and text
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Month:       ",
                            modifier = Modifier.padding(end = 18.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)
                        Box {
                            Text(
                                text = selectedMonth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { monthPop = true }
                                    .background(Color(0xFF1d2856), shape = RectangleShape)
                                    .padding(9.dp),
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,

                                )
                            DropdownMenu( // https://medium.com/@german220291/building-a-custom-exposed-dropdown-menu-with-jetpack-compose-d65232535bf2
                                expanded = monthPop,
                                onDismissRequest = { monthPop = false }
                            ) {
                                months.forEach { month ->
                                    DropdownMenuItem(text = { Text(text = month) },
                                        onClick = {
                                            selectedMonth = month
                                            monthPop = false
                                        })
                                }
                            }
                        }
                    }

                }



                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Day:            ",
                            modifier = Modifier.padding(end = 18.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box {
                            Text(
                                text = selectedDay,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dayPop = true }
                                    .background(Color(0xFF1d2856), shape = RectangleShape)
                                    .padding(9.dp),
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            DropdownMenu(
                                expanded = dayPop,
                                onDismissRequest = { dayPop = false }
                            ) {
                                days.forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(text = day) },
                                        onClick = {
                                            selectedDay = day
                                            dayPop = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Start Time:",
                            modifier = Modifier.padding(end = 18.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box {
                            Text(
                                text = "$selectedStartTime h",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { startTimePop = true }
                                    .background(Color(0xFF1d2856), shape = RectangleShape)
                                    .padding(9.dp),
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            DropdownMenu(
                                expanded = startTimePop,
                                onDismissRequest = { startTimePop = false }
                            ) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text(text = "$hour h") },
                                        onClick = {
                                            selectedStartTime = hour
                                            startTimePop = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "End Time:  ",
                            modifier = Modifier.padding(end = 18.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box {
                            Text(
                                text = "$selectedEndTime h",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { endTimePop = true }
                                    .background(Color(0xFF1d2856), shape = RectangleShape)
                                    .padding(9.dp),
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            DropdownMenu(
                                expanded = endTimePop,
                                onDismissRequest = { endTimePop = false }
                            ) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text(text = "$hour h") },
                                        onClick = {
                                            selectedEndTime = hour
                                            endTimePop = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedMonth.isNotEmpty() && selectedDay.isNotEmpty() && selectedStartTime.isNotEmpty() && selectedEndTime.isNotEmpty()) {
                            val newUnavailability = user?.let {
                                Unavailability(
                                    profileID = it.profileID,
                                    date = "$year-${selectedMonth.padStart(2, '0')}-${selectedDay.padStart(2, '0')}",
                                    startTime = selectedStartTime,
                                    endTime = selectedEndTime
                                )
                            }
                            if (newUnavailability != null) {
                                profileViewModel.addUnavailability(newUnavailability)
                            }
                            onDismiss()
                        }
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856))
                ) {
                    Text(text = "Submit Unavailability", color = Color.White)
                }
            }
        }
    }
}
