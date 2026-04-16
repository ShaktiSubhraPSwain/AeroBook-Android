package com.app.aerobook.presentation.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.aerobook.domain.model.BookingResult
import com.app.aerobook.presentation.screen1_map.MapScreen
import com.app.aerobook.presentation.screen1_map.MapViewModel
import com.app.aerobook.presentation.screen2_nickname.NicknameScreen
import com.app.aerobook.presentation.screen3_booking.BookingDetailScreen
import com.app.aerobook.presentation.screen4_history.HistoryScreen
import com.google.gson.Gson

@Composable
fun NavGraph(
    navController: NavHostController,
    // Provide the ViewModel at the Graph level so it's shared across destinations
    mapViewModel: MapViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        // --- SCREEN 1: MAP ---
        composable(Screen.Map.route) {
            MapScreen(
                viewModel = mapViewModel,
                onNavigateToNickname = { address ->
                    navController.navigate(Screen.NickName.createRoute(address))
                },
                onNavigateToBooking = { bookingResult ->
                    val bookingJson = Gson().toJson(bookingResult)
                    val encodedJson = Uri.encode(bookingJson)
                    navController.navigate(Screen.BookingDetail.createRoute("$encodedJson"))
                }
            )
        }

        // --- SCREEN 2: NICKNAME ---
        composable(
            route = Screen.NickName.route,
            arguments = listOf(navArgument("locationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId")
            NicknameScreen(
                locationId = locationId,
                viewModel = mapViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- SCREEN 3: BOOKING DETAIL ---
        composable(Screen.BookingDetail.route,
            arguments = listOf(navArgument("bookingJson") { type = NavType.StringType })
        ) { backStackEntry ->

            val json = backStackEntry.arguments?.getString("bookingJson")
            if (json.isNullOrEmpty()) {
                // Fallback or Error UI
                return@composable
            }

            // 3. Parse with Gson
            val bookingResult = try {
                Gson().fromJson(json, BookingResult::class.java)
            } catch (e: Exception) {
                Log.e("NAV_ERROR", "Failed to parse JSON: $json")
                null
            }

            if (bookingResult == null) return@composable

            BookingDetailScreen(
                onBookingSuccess = {
                    navController.navigate(Screen.History.route) {
                        // Clear the flow so user can't "back" into a completed booking
                        popUpTo(Screen.Map.route) { inclusive = false }
                    }
                },
                bookingResult = bookingResult,
            )
        }

        // --- SCREEN 4: HISTORY ---
        composable(Screen.History.route) {
            HistoryScreen(onItemClick = {
                    // Optional Enhancement: Restore selection from history
                    navController.popBackStack(Screen.Map.route, false)
                }
            )
        }
    }
}