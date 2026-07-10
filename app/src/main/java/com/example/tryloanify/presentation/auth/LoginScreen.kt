package com.example.tryloanify.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tryloanify.ui.theme.Appcolors

@Composable
fun LoginScreen(navController: NavController){
//
//    var mobile by remember{
//
//        mutableStateOf("")
//
//    }

    Column(

        modifier= Modifier

            .fillMaxSize()

            .padding(20.dp)

    ){

        Spacer(modifier=Modifier.height(80.dp))

        Text(

            "Welcome to",

            fontSize=24.sp,

            fontWeight= FontWeight.Bold

        )
        Text(

            "TryLoanify",

            fontSize=28.sp,

            fontWeight= FontWeight.Bold,
            color = Appcolors.Primary,


        )


        Spacer(modifier=Modifier.height(30.dp))
//
//        OutlinedTextField(
//
//            value=mobile,
//
//            onValueChange={
//
//                mobile=it
//
//            },
//
//            label={
//
//                Text("Mobile Number")
//
//            }
//
//        )

        Spacer(modifier=Modifier.height(20.dp))


        Button(

            onClick={

//                navController.navigate(Screen.OTP.route)

            }

        ){

            Text("Continue")

        }

    }

}