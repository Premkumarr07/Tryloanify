package com.example.tryloanify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tryloanify.presentation.auth.LoginScreen
import com.example.tryloanify.presentation.splash.SplashScreen

@Composable
fun AppNavigation(){

    val navController = rememberNavController()

    NavHost(

        navController,

        startDestination = Screen.Splash.route

    ){

        composable(Screen.Splash.route){

            SplashScreen(navController)

        }

        composable(Screen.Login.route){

            LoginScreen(navController)

        }
//
//        composable(Screen.OTP.route){
//
//            OTPScreen(navController)
//
//        }
//
//        composable(Screen.Profile.route){
//
//            ProfileSetupScreen(navController)
//
//        }
//
//        composable(Screen.Home.route){
//
//            HomeScreen()
//
//        }

    }

}