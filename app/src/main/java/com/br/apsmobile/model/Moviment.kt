package com.br.apsmobile.model

import java.util.*

data class Moviment(
    var id: String = "",
    var date: Date? = null,
    var description: String = "",
    var value: Double = 0.0,
    var type: String = ""
)