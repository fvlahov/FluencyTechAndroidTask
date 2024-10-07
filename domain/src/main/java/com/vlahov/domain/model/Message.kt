package com.vlahov.domain.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val body: String,
    val dateTime: LocalDateTime,
    val isMy: Boolean,
)
