package dev.icerock.gitviewer.presentation.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = titleText,
                        style = GVTypography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = dialogText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        style = GVTypography.bodyLarge
                    )

                    TextButton(
                        onClick = { onShowDialogChange(false) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun InformationDialogPreview() {
    GVTheme {
        InformationDialog(
            showDialog = true,
            onShowDialogChange = {},
            titleText = "Title",
            dialogText = "Dialog content"
        )
    }
}