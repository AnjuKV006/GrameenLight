package com.example.grameenlight

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationClient:
            com.google.android.gms.location.FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setContent {
            MaterialTheme {
                ReportScreen()
            }
        }
    }

    @Composable
    fun ReportScreen() {

        var issue by remember { mutableStateOf("") }
        var poleNo by remember { mutableStateOf("") }
        var village by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var imageUri by remember { mutableStateOf<Uri?>(null) }

        // IMAGE PICKER
        val imagePicker =
            rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                imageUri = uri
            }

        // LOCATION PERMISSION
        val permissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->

                if (isGranted) {

                    submitReport(
                        issue,
                        poleNo,
                        village,
                        phone,
                        imageUri
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0F1C))
                .padding(20.dp)
        ) {

            Text(
                text = "Report Electrical Issue",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF00FF9D)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ISSUE
            OutlinedTextField(
                value = issue,
                onValueChange = { issue = it },
                label = { Text("Problem Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF9D),
                    focusedLabelColor = Color(0xFF00FF9D),
                    cursorColor = Color(0xFF00FF9D)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // POLE ID
            OutlinedTextField(
                value = poleNo,
                onValueChange = { poleNo = it },
                label = { Text("Pole ID (Example: P001)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF9D),
                    focusedLabelColor = Color(0xFF00FF9D),
                    cursorColor = Color(0xFF00FF9D)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PHONE
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF9D),
                    focusedLabelColor = Color(0xFF00FF9D),
                    cursorColor = Color(0xFF00FF9D)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // VILLAGE
            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text("Village Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF9D),
                    focusedLabelColor = Color(0xFF00FF9D),
                    cursorColor = Color(0xFF00FF9D)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PICK IMAGE
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF9D),
                    contentColor = Color.Black
                )
            ) {
                Text("Upload Pole Image")
            }

            // IMAGE PREVIEW
            imageUri?.let {

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // SUBMIT
            Button(
                onClick = {

                    if (
                        issue.isBlank() ||
                        poleNo.isBlank() ||
                        village.isBlank() ||
                        phone.isBlank()
                    ) {

                        Toast.makeText(
                            this@ReportActivity,
                            "Fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val permission =
                        Manifest.permission.ACCESS_FINE_LOCATION

                    if (
                        ContextCompat.checkSelfPermission(
                            this@ReportActivity,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        submitReport(
                            issue,
                            poleNo,
                            village,
                            phone,
                            imageUri
                        )

                    } else {

                        permissionLauncher.launch(permission)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00FF9D),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text("Submit Complaint")
            }
        }
    }

    // FINAL SUBMIT FUNCTION
    private fun submitReport(
        issue: String,
        poleNo: String,
        village: String,
        phone: String,
        imageUri: Uri?
    ) {

        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                val time = SimpleDateFormat(
                    "dd/MM/yyyy hh:mm a",
                    Locale.getDefault()
                ).format(Date())

                val report = hashMapOf(

                    // OLD
                    "issue" to issue,
                    "poleNo" to poleNo,
                    "village" to village,

                    // NEW FOR POLE STATUS
                    "poleId" to poleNo,
                    "phone" to phone,
                    "problem" to issue,

                    // STATUS
                    "status" to "Pending",

                    // EXTRA
                    "time" to time,
                    "lat" to (location?.latitude ?: 0.0),
                    "lng" to (location?.longitude ?: 0.0),
                    "imageUri" to (imageUri?.toString() ?: "")
                )

                db.collection("reports")
                    .add(report)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Report Submitted Successfully ✅",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Submission Failed ❌",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }
}