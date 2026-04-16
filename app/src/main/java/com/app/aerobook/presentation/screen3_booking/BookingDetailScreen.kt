package com.app.aerobook.presentation.screen3_booking

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.aerobook.domain.model.BookingResult

@Composable
fun BookingDetailScreen(
    onBookingSuccess: () -> Unit,
    bookingResult: BookingResult
) {
    val aeroYellow = Color(0xFFFFCC4D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // --- Location A ---
        LocationDetailItem(
            label = "A",
            name = bookingResult.a.address,
            aqi = bookingResult.a.aqi,
            nickname = bookingResult.a.nickname
        )

        Divider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = Color.LightGray.copy(alpha = 0.3f)
        )

        // --- Location B ---
        LocationDetailItem(
            label = "B",
            name = bookingResult.b.address,
            aqi = bookingResult.b.aqi,
            nickname = bookingResult.b.nickname
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- Price Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "price",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${bookingResult.price}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // --- Action Button ---
        Button(
            onClick = onBookingSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = aeroYellow)
        ) {
            Text(
                text = "V",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun LocationDetailItem(
    label: String,
    name: String,
    aqi: Int,
    nickname: String?
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // AQI Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("aqi", color = Color.Gray)
                Text("$aqi", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Nickname Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("nickname", color = Color.Gray)
                Text(nickname ?: "-", fontWeight = FontWeight.Bold)
            }
        }
    }
}