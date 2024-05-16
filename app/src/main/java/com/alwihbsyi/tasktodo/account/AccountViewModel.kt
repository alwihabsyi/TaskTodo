package com.alwihbsyi.tasktodo.account

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AccountViewModel(private val auth: FirebaseAuth): ViewModel() {

    fun getUserEmail(): String = auth.currentUser!!.email!!
    fun logOut() = auth.signOut()
}