package com.vlahov.fluencytask.utils

import com.vlahov.domain.model.Message
import java.time.LocalDateTime
import java.util.UUID

val dummyMessages = listOf(
    Message(
        id = UUID.randomUUID().toString(),
        body = "Hello there!",
        dateTime = LocalDateTime.now(),
        isMy = true
    ),
    Message(
        id = UUID.randomUUID().toString(),
        body = "General Kenobi!",
        dateTime = LocalDateTime.now(),
        isMy = false
    ),
)