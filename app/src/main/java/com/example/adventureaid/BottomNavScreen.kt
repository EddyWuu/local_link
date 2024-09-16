package com.example.adventureaid

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val textStyle: TextStyle
) {
    object  Home: BottomNavScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
        textStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
    )
    object  Profile: BottomNavScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person,
        textStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
    )
    object  MyActivities: BottomNavScreen(
        route = "myactivities",
        title = "My Activities",
        icon = Icons.Default.DateRange,
        textStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
    )
    object  Search: BottomNavScreen(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search,
        textStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
    )
}