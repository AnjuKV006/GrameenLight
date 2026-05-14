package com.example.grameenlight

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // DEFAULT LOCATION
    val defaultLocation = LatLng(12.9716, 77.5946)

    var userLocation by remember {
        mutableStateOf(defaultLocation)
    }

    // REPORT COUNTS
    var totalReports by remember { mutableStateOf(0) }
    var pendingReports by remember { mutableStateOf(0) }
    var resolvedReports by remember { mutableStateOf(0) }

    // FIRESTORE LISTENER
    LaunchedEffect(Unit) {

        db.collection("reports")
            .addSnapshotListener { value, _ ->

                if (value != null) {

                    totalReports = value.size()

                    pendingReports =
                        value.documents.count {
                            it.getString("status") == "Pending"
                        }

                    resolvedReports =
                        value.documents.count {
                            it.getString("status") == "Resolved"
                        }
                }
            }
    }

    // RESOLVED PERCENTAGE
    val resolvedPercent =
        if (totalReports > 0)
            (resolvedReports * 100) / totalReports
        else
            0

    // MAP CAMERA
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 14f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1C))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // LOGO
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.dashboard),
                    contentDescription = "Dashboard Logo",
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = "GrameenLight",
                        color = Color(0xFF00FF9D),
                        fontSize = 24.sp
                    )

                    Text(
                        text = "Smart Monitoring Dashboard",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STATS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatBox(
                    title = "Total",
                    value = totalReports.toString(),
                    color = Color.Cyan
                )

                StatBox(
                    title = "Pending",
                    value = pendingReports.toString(),
                    color = Color.Yellow
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatBox(
                    title = "Resolved",
                    value = resolvedReports.toString(),
                    color = Color.Green
                )

                StatBox(
                    title = "Completed %",
                    value = "$resolvedPercent%",
                    color = Color(0xFF00FF9D)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ACTION PANEL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF111B2E)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(context, ReportActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Report Issue")
                    }

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(context, ViewReportsActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Reports")
                    }

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(context, PoleStatusActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pole Map Status")
                    }

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(context, ProfileActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Profile")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // MAP SECTION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            ) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {

                    Marker(
                        state = MarkerState(position = userLocation),
                        title = "Your Location"
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color
) {

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111B2E)
        )
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = color,
                fontSize = 20.sp
            )
        }
    }
}