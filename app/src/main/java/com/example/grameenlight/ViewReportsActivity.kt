package com.example.grameenlight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.grameenlight.ui.theme.GrameenLightTheme
import com.google.firebase.firestore.FirebaseFirestore

class ViewReportsActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            GrameenLightTheme {

                var reports by remember {
                    mutableStateOf(listOf<Report>())
                }

                /* REALTIME REPORTS */
                LaunchedEffect(Unit) {

                    db.collection("reports")
                        .addSnapshotListener { result, error ->

                            if (error != null) {
                                return@addSnapshotListener
                            }

                            val reportList =
                                mutableListOf<Report>()

                            result?.documents?.forEach { document ->

                                val report = Report(

                                    complaintId =
                                        document.getString("complaintId")
                                            ?: "",

                                    village =
                                        document.getString("village")
                                            ?: "",

                                    poleNo =
                                        document.getString("poleNo")
                                            ?: "",

                                    issue =
                                        document.getString("issue")
                                            ?: "",

                                    status =
                                        document.getString("status")
                                            ?: "Pending",

                                    imageUri =
                                        document.getString("imageUri")
                                            ?: ""
                                )

                                reportList.add(report)
                            }

                            reports = reportList
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
                        )
                        .padding(16.dp)
                ) {

                    LazyColumn {

                        item {

                            Text(
                                text = "Complaint Tracker",
                                color = Color(0xFF00FF9D),
                                fontSize = 30.sp
                            )

                            Spacer(
                                modifier = Modifier.height(20.dp)
                            )
                        }

                        items(reports) { report ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),

                                shape = RoundedCornerShape(22.dp),

                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        Color.White.copy(alpha = 0.08f)
                                )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .padding(20.dp)
                                ) {

                                    Text(
                                        text = "Village: ${report.village}",
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )

                                    Spacer(
                                        modifier = Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = "Pole Number: ${report.poleNo}",
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier = Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = "Issue: ${report.issue}",
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier = Modifier.height(10.dp)
                                    )

                                    Text(
                                        text = "Status: ${report.status}",
                                        color = if (
                                            report.status == "Resolved"
                                        )
                                            Color.Green
                                        else
                                            Color.Yellow
                                    )

                                    Spacer(
                                        modifier = Modifier.height(16.dp)
                                    )

                                    /* IMAGE DISPLAY */
                                    if (report.imageUri.isNotEmpty()) {

                                        AsyncImage(
                                            model = report.imageUri,
                                            contentDescription = null,

                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                        )

                                        Spacer(
                                            modifier = Modifier.height(16.dp)
                                        )
                                    }

                                    Text(
                                        text = "Complaint ID",
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = report.complaintId,
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )

                                    Spacer(
                                        modifier = Modifier.height(20.dp)
                                    )

                                    Button(
                                        onClick = {

                                            db.collection("reports")
                                                .document(report.complaintId)
                                                .update(
                                                    "status",
                                                    "Resolved"
                                                )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),

                                        shape = RoundedCornerShape(16.dp),

                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                                Color(0xFF00FF9D)
                                        )
                                    ) {

                                        Text(
                                            text = "Mark as Resolved",
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}