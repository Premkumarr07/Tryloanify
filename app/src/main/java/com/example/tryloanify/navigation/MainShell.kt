package com.example.tryloanify.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tryloanify.presentation.application.ApplicationFormScreen
import com.example.tryloanify.presentation.home.HomeScreen
import com.example.tryloanify.presentation.loans.LoansScreen
import com.example.tryloanify.presentation.profile.ProfileScreen

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
    BottomNavItem(Screen.ApplicationForm, "Apply", Icons.Default.RequestQuote),
    BottomNavItem(Screen.Loans, "Loans", Icons.Default.AccountBalance),
    BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person),
)

@Composable
fun MainShell(rootNavController: androidx.navigation.NavController) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.screen.route,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) { HomeScreen(rootNavController, navController) }
            composable(Screen.ApplicationForm.route) { ApplicationFormScreen(rootNavController) }
            composable(Screen.Loans.route) { LoansScreen(rootNavController) }
            composable(Screen.Profile.route) { ProfileScreen(rootNavController) }
        }
    }
}
