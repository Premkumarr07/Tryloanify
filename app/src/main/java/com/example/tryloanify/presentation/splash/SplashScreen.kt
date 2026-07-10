package com.example.tryloanify.presentation.splash

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tryloanify.R
import com.example.tryloanify.component.CapsuleLoader
import com.example.tryloanify.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    LaunchedEffect(Unit) {

        delay(4000)
        navController.navigate(Screen.Login.route){

            popUpTo(0)

        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.bgsplash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1.2f))

//            Image(
//                painter = painterResource(R.drawable.bgsplash),
//                contentDescription = "",
//                modifier = Modifier.size(210.dp)
//            )

            Spacer(modifier = Modifier.height(20.dp))

//            Text(
//                text = "TryLoanify",
//                fontSize = 42.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xff2447F6)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))

//            Text(
//                text = "Smart loans. Trusted always.",
//                color = Color(0xff5F6475),
//                fontSize = 18.sp
//            )



            Spacer(modifier = Modifier.height(20.dp))
            Button(

                onClick={

                navController.navigate(Screen.Login.route)

                }

            ){

                Text(
                    text = "Next",
                    fontSize = 18.sp,
                )

            }


            Spacer(modifier = Modifier.height(90.dp))

        }

    }
}