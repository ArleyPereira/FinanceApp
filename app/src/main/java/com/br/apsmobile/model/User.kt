package com.br.apsmobile.model

import com.google.firebase.firestore.Exclude

data class User (
    val id: String,
    val name: String,
    val email: String,
    @get:Exclude
    val password: String,
    val dateRegister: Long
) {


}