package com.example.grameenlight

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grameenlight.ui.theme.GrameenLightTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            GrameenLightTheme {

                ProfileScreen(this)
            }
        }
    }
}

@Composable
fun ProfileScreen(activity: ProfileActivity) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val user =
        auth.currentUser

    var totalReports by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->

                totalReports = result.size()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0F1C),
                        Color(0xFF111B2E)
                    )
                )
            ),

        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),

            shape = RoundedCornerShape(24.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(alpha = 0.08f)
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "User Profile",
                    color = Color(0xFF00FF9D),
                    fontSize = 30.sp
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Logged In Email",
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = user?.email ?: "No User",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Total Complaints",
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = totalReports.toString(),
                    color = Color(0xFF3B82F6),
                    fontSize = 34.sp
                )

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

                Button(
                    onClick = {

                        auth.signOut()

                        activity.startActivity(
                            Intent(
                                activity,
                                MainActivity::class.java
                            )
                        )

                        activity.finish()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    shape = RoundedCornerShape(16.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {

                    Text(
                        text = "Logout",
                        color = Color.White
                    )
                }
            }
        }
    }
}