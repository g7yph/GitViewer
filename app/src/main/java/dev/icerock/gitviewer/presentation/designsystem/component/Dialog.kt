package dev.icerock.gitviewer.presentation.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography

@Composable
internal fun InformationDialog(
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    titleText: String,
    dialogText: String
) {
    if (showDialog) {
        Dialog(onDismissRequest = { onShowDialogChange(false) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = titleText,
                    style = GVTypography.titleMedium
                )

                Text(
                    text = dialogText,
                    modifier = Modifier.fillMaxWidth(),
                    style = GVTypography.bodyLarge
                )

                TextButton(onClick = { onShowDialogChange(false) }) {
                    Text(text = "OK")
                }
            }
        }
    }
}