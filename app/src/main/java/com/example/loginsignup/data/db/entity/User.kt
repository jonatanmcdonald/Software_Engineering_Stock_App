package com.example.loginsignup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class represents a user in the database.
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int, // The unique ID of the user.
    val firstName: String, // The first name of the user.
    val lastName: String, // The last name of the user.
    val email: String, // The email of the user.
    val password: String, // The password of the user.
    val securityQuestion: String = "What is the name of your home town?", // The security question for the user.
    val securityAnswer: String // The security answer for the user.
)
