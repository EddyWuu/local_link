package com.example.adventureaid

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import screens.MyActivitiesScreen
import screens.ProfileScreen
import screens.SearchScreen
import viewmodels.HomeViewModel
import viewmodels.ProfileViewModel
import viewmodels.UserViewModel
import androidx.navigation.NavController

@Composable
fun BottomNavGraph(navController: NavHostController, mainNavController: NavController, userViewModel: UserViewModel) {
    val sharedViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    sharedViewModel.loadAPIEvents()

    val loggedInUser = userViewModel.loggedInUser.collectAsState()

    // Load user preferences if user is logged in
    LaunchedEffect(loggedInUser.value?.profileID) {
        loggedInUser.value?.profileID?.let { profileID ->
            userViewModel.fetchUserPreferences(profileID)
        }
    }

    loggedInUser.value?.let { sharedViewModel.loadRegisteredEvents(profileID = it.profileID) }

    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Home.route
    ) {

        composable(route = BottomNavScreen.Home.route) {
            HomeScreen(viewModel = sharedViewModel, userViewModel = userViewModel)
        }
        composable(route = BottomNavScreen.MyActivities.route) {
            MyActivitiesScreen(viewModel = sharedViewModel)
        }
        composable(route = BottomNavScreen.Search.route) {
            SearchScreen(viewModel = sharedViewModel, userViewModel = userViewModel)
        }
        composable(route = BottomNavScreen.Profile.route) {
            ProfileScreen(profileViewModel = profileViewModel, homeViewModel = sharedViewModel, userViewModel = userViewModel, mainNavController = mainNavController)
        }
    }
}