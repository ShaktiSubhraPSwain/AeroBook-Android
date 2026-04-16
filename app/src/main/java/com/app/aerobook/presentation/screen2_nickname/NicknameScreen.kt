package com.app.aerobook.presentation.screen2_nickname

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.aerobook.presentation.screen1_map.MapViewModel

@Composable
fun NicknameScreen(
    locationId: String?, // The address string used as the key
    viewModel: MapViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Logic to find which location we are currently editing
    val isLocationA = uiState.locationA?.id == locationId
    val targetLocation = if (isLocationA) uiState.locationA else uiState.locationB
    val labelPrefix = if (isLocationA) "A" else "B"

    // Local state for the text field
    var nickname by rememberSaveable {
        mutableStateOf(targetLocation?.nickname ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // --- 1. Header (Displaying A or B info) ---
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = labelPrefix,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = targetLocation?.displayName ?: "Unknown Location",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("aqi", color = Color.Gray)
                    Text("${targetLocation?.aqi ?: 0}", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- 2. Nickname Input (with 20-character limit) ---
        OutlinedTextField(
            value = nickname,
            onValueChange = {
                if (it.length <= 20) nickname = it
            },
            placeholder = { Text("nickname (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                Text(
                    text = "${nickname.length}/20",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                focusedBorderColor = Color(0xFFFFCC4D)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. Save Button ("V") ---
        Button(
            onClick = {
                locationId?.let { viewModel.updateNickname(it, nickname) }
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC4D))
        ) {
            Text("V", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}