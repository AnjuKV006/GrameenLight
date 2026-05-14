package com.example.grameenlight

data class Report(

    val complaintId: String = "",

    val village: String = "",

    val poleNo: String = "",

    val issue: String = "",

    val status: String = "Pending",

    val imageUri: String = ""
)