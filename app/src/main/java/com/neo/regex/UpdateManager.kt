package com.neo.regex

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.neo.regex.utils.firebaseEnvironment

class UpdateManager(
    private val updateListener: UpdateListener
) : ValueEventListener {

    private val versionCode = BuildConfig.VERSION_CODE

    private val fbUpdateManager =
        firebaseEnvironment.child("update_manager")

    init {
        fbUpdateManager.addValueEventListener(this)
    }

    override fun onDataChange(snapshot: DataSnapshot) {

        runCatching {
            if (snapshot.exists()) {
                val downloadLink = snapshot.child("download_link").value as String
                val lastVersionCode = snapshot.child("last_version_code").value as Long
                val lastVersionName = snapshot.child("last_version_name").value as String

                if (versionCode == lastVersionCode.toInt()) {
                    updateListener.updated()
                } else {
                    updateListener.hasUpdate(
                        lastVersionCode.toInt(),
                        lastVersionName,
                        downloadLink
                    )
                }
            }
        }.onFailure {
            Firebase.crashlytics.recordException(it)
        }
    }

    override fun onCancelled(error: DatabaseError) = Unit

    interface UpdateListener {
        fun updated()
        fun hasUpdate(lastVersionCode: Int, lastVersionName: String, downloadLink: String)
    }
}
