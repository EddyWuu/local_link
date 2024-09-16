package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adventureaid.R
import models.Event
import screens.tools.CustomTopAppBar
import viewmodels.HomeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import models.User
import viewmodels.UserViewModel

@Composable
fun SearchScreen(viewModel: HomeViewModel, userViewModel: UserViewModel) {
    var selectedSearchEvent by remember { mutableStateOf<Event?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showFilterDialog = remember { mutableStateOf(false)}
    val user by userViewModel.loggedInUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.Text(
                            text = "Search",
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
            val events by viewModel.searchScreenEvents.collectAsState()

            var text by remember { mutableStateOf("") }

            var countryFilter = remember { mutableStateOf("")}
            var cityFilter = remember { mutableStateOf("")}
            var dateFilter = remember { mutableStateOf("")}
            var categoryFilter = remember { mutableStateOf("")}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFdbf1fc), Color(0xFF87CEEB))))) {

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textStyle = TextStyle(fontSize = 25.sp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.loadSearchAPIEvents(
                                search = text,
                                country = countryFilter.value,
                                city = cityFilter.value,
                                date = dateFilter.value,
                                category = categoryFilter.value)
                        }
                    )
                )

                Button(
                    onClick = {
                        showFilterDialog.value = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    androidx.compose.material.Text(text = "Filters", color = Color.White)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = if (events.isEmpty()) Arrangement.Center else Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(events.isEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(120.dp)
                        )
                    } else {
                        androidx.compose.material.Text(
                            text = "Results",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(events) { event ->
                                EventItem(event) {
                                    selectedSearchEvent = event
                                    showDialog = true
                                }
                            }
                        }
                    }
                }
            }

            // Show dialog if an event is selected
            if (showDialog && selectedSearchEvent != null) {
                user?.let { RecommendedEventDialog(event = selectedSearchEvent!!, onDismiss = { showDialog = false }, viewModel = viewModel, user = it) }
            }

            if (showFilterDialog.value) {
                filterDialog(onDismiss = { showFilterDialog.value = false },
                    countryFilter = countryFilter,
                    cityFilter = cityFilter,
                    dateFilter = dateFilter,
                    categoryFilter = categoryFilter,
                    showFilterDialog = showFilterDialog
                )
            }

        }
    )
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Gray),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(onClick = onClick)
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    androidx.compose.material.Text(
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
                    androidx.compose.material.Text(
                        text = event.date,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    androidx.compose.material.Text(
                        text = event.time,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    androidx.compose.material.Text(
                        text = if (event.isVolunteerEvent) "Volunteer Event" else "Community Event",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp
                        ),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    androidx.compose.material.Text(
                        text = event.description,
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
                        painter = painterResource(id = R.drawable.goose),
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
 * Dialog for when you select an event
 */
@Composable
fun EventDialog(event: Event, onDismiss: () -> Unit) {
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
                androidx.compose.material.Text(
                    text = event.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Date: ${event.date}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Time: ${event.time}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Location: ${event.address}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    androidx.compose.material.Text("Close")
                }
            }
        }
    }
}

@Composable
fun RecommendedEventDialog(event: Event, onDismiss: () -> Unit, viewModel: HomeViewModel, user: User) {
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
                androidx.compose.material.Text(
                    text = event.title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Date: ${event.date}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Time: ${event.time}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "Location: ${event.address}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.material.Text(
                    text = "About: ${event.description}",
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    androidx.compose.material.Text(text = "Add Event", color=Color.White)
                }
            }
        }
    }
}

/**
 * Dialog for filters
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun filterDialog(onDismiss: () -> Unit, countryFilter: MutableState<String>,
                 cityFilter: MutableState<String>, dateFilter: MutableState<String>,
                 categoryFilter: MutableState<String>, showFilterDialog: MutableState<Boolean>) {
    Dialog(onDismissRequest = onDismiss) {
        val categories = listOf("", "academic", "school-holidays", "public-holidays", "observances",
                "politics", "conferences", "expos", "concerts", "festivals", "performing-arts",
                "sports", "community", "daylight-savings", "airport-delays", "severe-weather",
                "disasters", "health-warnings")
        var expanded by remember { mutableStateOf(false) }
        val selectedCategory = remember { mutableStateOf(categoryFilter.value) }

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
                /**
                 * This dropdown is based on the example here:
                 * https://www.composables.com/material3/exposeddropdownmenubox
                 */
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.value,
                        onValueChange = { },
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop down arrow")
                            }
                        },
                        textStyle = TextStyle(fontSize = 25.sp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedCategory.value = category
                                    categoryFilter.value = category
                                    expanded = false
                                },
                                text = { Text(text = category) }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = countryFilter.value,
                    onValueChange = { countryFilter.value = it },
                    label = { Text("Country") },
                    textStyle = TextStyle(fontSize = 25.sp),
                )
                OutlinedTextField(
                    value = cityFilter.value,
                    onValueChange = { cityFilter.value = it },
                    label = { Text("City") },
                    textStyle = TextStyle(fontSize = 25.sp),
                )
                OutlinedTextField(
                    value = dateFilter.value,
                    onValueChange = { dateFilter.value = it },
                    label = { Text("Date") },
                    textStyle = TextStyle(fontSize = 25.sp),
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            countryFilter.value = ""
                            cityFilter.value = ""
                            dateFilter.value = ""
                            categoryFilter.value = ""
                            showFilterDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        androidx.compose.material.Text(text = "Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            showFilterDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1d2856)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        androidx.compose.material.Text(text = "OK", color = Color.White)
                    }

                }




            }
        }
    }
}