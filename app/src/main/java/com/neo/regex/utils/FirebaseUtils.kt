package com.neo.regex.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.neo.regex.Environment

val firebaseEnvironment: DatabaseReference by lazy {
    Firebase.database.getReference(Environment.FIREBASE)
}
