package com.example.tryloanify.navigation

sealed class Screen(val route:String){
    data object Splash:Screen("splash")
    data object Login:Screen("login")




}