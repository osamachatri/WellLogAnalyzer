package com.oussama_chatri.feature.settings.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.WellLogTheme
import com.oussama_chatri.core.ui.components.LabeledTextField
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@Composable
fun DefaultPathPicker(
    label:    String,
    path:     String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = WellLogTheme.colors

    Row(
        modifier            = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LabeledTextField(
            label         = label,
            value         = path,
            onValueChange = onChange,
            placeholder   = "Not set — click Browse to choose",
            modifier      = Modifier.weight(1f)
        )
        Column {
            Spacer(Modifier.height(22.dp))
            OutlinedButton(
                onClick = {
                    SwingUtilities.invokeLater {
                        val chooser = JFileChooser().apply {
                            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                            dialogTitle       = "Select folder"
                            if (path.isNotBlank()) currentDirectory = java.io.File(path)
                        }
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            onChange(chooser.selectedFile.absolutePath)
                        }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = c.accent),
                border = androidx.compose.foundation.BorderStroke(1.dp, c.accent.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Browse", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}