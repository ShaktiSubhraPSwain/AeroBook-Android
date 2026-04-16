package com.app.aerobook.presentation.screen1_map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.domain.model.LocationDetail
import com.app.aerobook.ui.theme.AeroGray
import com.app.aerobook.ui.theme.AeroYellow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToNickname: (String) -> Unit,
    onNavigateToBooking: (BookingResult) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val target = cameraPositionState.position.target
            viewModel.onCameraIdle(target.latitude, target.longitude)
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.fetchUserCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event is NavigationEvent.MoveCameraTo) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(event.lat, event.lng), 15f
                    )
                )
            } else if (event is NavigationEvent.BookingDetailsScreen) {
                onNavigateToBooking(event.booking)
            }
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // 1. Map Layer
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false) // Clean look
            ) {
                uiState.locationA?.let { loc ->
                    Marker(state = MarkerState(position = LatLng(loc.latitude, loc.longitude)))
                }
                uiState.locationB?.let { loc ->
                    Marker(state = MarkerState(position = LatLng(loc.latitude, loc.longitude)))
                }
            }

            // 2. Static Center Pin (Shadow overlay)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .offset(y = (-20).dp),
                tint = Color.Black
            )

            // 3. AQI Display (Top Right)
            uiState.currentMarkerLocation?.aqi?.let { aqi ->
                Text(
                    text = "aqi $aqi",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. Bottom Selection Panel (Matching Screenshot)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: A and B bars
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocationSelectionBar(
                        label = "A",
                        locationName = uiState.locationA?.displayName,
                        onClick = {
                            uiState.locationA?.let {
                                onNavigateToNickname(it.id)
                            }
                        }
                    )
                    LocationSelectionBar(
                        label = "B",
                        locationName = uiState.locationB?.displayName,
                        onClick = {
                            uiState.locationB?.let {
                                onNavigateToNickname(it.id)
                            }
                        }
                    )
                }

                // Right Side: The Yellow "V" Action Button
                androidx.compose.material3.Button(
                    onClick = {
                        if (uiState.selectionStep == SelectionStep.BOOK) {
                            viewModel.performBooking()
                        } else viewModel.onConfirmLocation()
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(75.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.selectionStep != SelectionStep.BOOKING) AeroYellow else AeroGray) ,
                    enabled = uiState.selectionStep != SelectionStep.BOOKING
                ) {
                    Text(
                        text = when (uiState.selectionStep) {
                            SelectionStep.SET_A -> "Set A"
                            SelectionStep.SET_B -> "Set B"
                            SelectionStep.BOOKING -> "Booking"
                            else -> "Book"
                        },
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            // Loading Indicator overlay
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun LocationSelectionBar(label: String, locationName: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = locationName ?: "location name",
            style = MaterialTheme.typography.bodyLarge,
            color = if (locationName == null) Color.Gray else Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}