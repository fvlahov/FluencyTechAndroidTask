package com.vlahov.fluencytask.presentation.chat.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vlahov.domain.model.Message
import com.vlahov.fluencytask.ui.theme.FluencyTaskTheme
import com.vlahov.fluencytask.ui.theme.LocalSpacing
import com.vlahov.fluencytask.utils.dummyMessages
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ChatMessage(
    message: Message,
    modifier: Modifier = Modifier,
) {
    val colors = CardDefaults.cardColors(
        containerColor = if (message.isMy)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surface
    )

    ElevatedCard(
        colors = colors,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = LocalSpacing.current,
                vertical = LocalSpacing.current / 2
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = message.body,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = message.dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                color = colors.contentColor.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    FluencyTaskTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalSpacing.current)
        ) {
            dummyMessages.forEach {
                ChatMessage(message = it)
            }
        }
    }
}