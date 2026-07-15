package com.example.tryloanify.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tryloanify.presentation.application.ApplicationFormScreen
import com.example.tryloanify.presentation.home.HomeScreen
import com.example.tryloanify.presentation.loans.LoansScreen
import com.example.tryloanify.presentation.profile.ProfileScreen
import com.example.tryloanify.ui.theme.Appcolors

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomNavItem(Screen.Home,            "Home",    Icons.Default.Home),
    BottomNavItem(Screen.ApplicationForm, "Apply",   Icons.Default.RequestQuote),
    BottomNavItem(Screen.Loans,           "Loans",   Icons.Default.AccountBalance),
    BottomNavItem(Screen.Profile,         "Profile", Icons.Default.Person),
)

@Composable
fun MainShell(rootNavController: androidx.navigation.NavController) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            Column {
                Divider(color = Appcolors.Border, thickness = 0.8.dp)
                NavigationBar(
                    containerColor = Appcolors.white,
                    tonalElevation = 0.dp,
                ) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon = {
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Appcolors.referBackground)
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector        = item.icon,
                                            contentDescription = item.label,
                                            tint               = Appcolors.PrimaryAppbar,
                                            modifier           = Modifier.size(22.dp),
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector        = item.icon,
                                        contentDescription = item.label,
                                        tint               = Appcolors.rockblue,
                                        modifier           = Modifier.size(22.dp),
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text       = item.label,
                                    fontSize   = 11.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color      = if (selected) Appcolors.PrimaryAppbar else Appcolors.rockblue,
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor      = Color.Transparent,
                                selectedIconColor   = Appcolors.PrimaryAppbar,
                                unselectedIconColor = Appcolors.rockblue,
                                selectedTextColor   = Appcolors.PrimaryAppbar,
                                unselectedTextColor = Appcolors.rockblue,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route)            { HomeScreen(rootNavController, navController) }
            composable(Screen.ApplicationForm.route) { ApplicationFormScreen(rootNavController) }
            composable(Screen.Loans.route)           { LoansScreen(rootNavController) }
            composable(Screen.Profile.route)         { ProfileScreen(rootNavController) }
        }
    }
}