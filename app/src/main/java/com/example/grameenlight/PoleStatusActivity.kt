package com.example.grameenlight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class PoleStatusActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PoleStatusScreen()
            }
        }
    }
}

data class PoleData(
    val poleId: String = "",
    val phone: String = "",
    val problem: String = "",
    val status: String = ""
)

@Composable
fun PoleStatusScreen() {

    val db = FirebaseFirestore.getInstance()

    var poleList by remember {
        mutableStateOf(listOf<PoleData>())
    }

    // 🔥 FIRESTORE REALTIME UPDATE
    LaunchedEffect(Unit) {

        db.collection("reports")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Log.e("FIREBASE", error.message.toString())
                    return@addSnapshotListener
                }

                if (value != null) {

                    val list = value.documents.map {

                        Log.d("DATA", it.data.toString())

                        val poleId =
                            it.getString("poleId") ?: "No Pole ID"

                        val phone =
                            it.getString("phone") ?: "No Phone"

                        val problem =
                            it.getString("problem") ?: "No Problem"

                        val status =
                            it.getString("status") ?: "Pending"

                        PoleData(
                            poleId = poleId,
                            phone = phone,
                            problem = problem,
                            status = status
                        )
                    }

                    poleList = list
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1C))
            .padding(16.dp)
    ) {

        // 🔝 HEADER
        Text(
            text = "Pole Status Dashboard",
            color = Color(0xFF00FF9D),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📋 POLE LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            items(poleList) { pole ->

                val statusColor =
                    if (pole.status == "Resolved")
                        Color(0xFF00FF9D)
                    else
                        Color.Red

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF111B2E)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        // 🪧 POLE ID
                        Text(
                            text = "Pole ID: ${pole.poleId}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 📞 PHONE
                        Text(
                            text = "📞 Phone: ${pole.phone}",
                            color = Color.LightGray,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ⚠ PROBLEM
                        Text(
                            text = "⚠ Problem: ${pole.problem}",
                            color = Color.LightGray,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ✅ STATUS BUTTON
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {

                            Card(
                                shape = RoundedCornerShape(50.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = statusColor
                                )
                            ) {

                                Text(
                                    text =
                                        if (pole.status == "Resolved")
                                            "🟢 Solved"
                                        else
                                            "🔴 Not Solved",

                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),

                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}