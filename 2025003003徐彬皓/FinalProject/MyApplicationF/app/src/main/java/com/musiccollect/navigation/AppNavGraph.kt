package com.musiccollect.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musiccollect.ui.components.BottomNavBar
import com.musiccollect.ui.screens.DetailScreen
import com.musiccollect.ui.screens.FavoriteScreen
import com.musiccollect.ui.screens.HomeScreen
import com.musiccollect.ui.screens.RecentlyPlayedScreen
import com.musiccollect.ui.screens.SearchScreen
import com.musiccollect.ui.screens.SettingsScreen
import androidx.compose.material3.MaterialTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Playlist : Screen("playlist")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object Detail : Screen("detail/{musicId}") {
        fun createRoute(musicId: String) = "detail/$musicId"
    }
    object Settings : Screen("settings")
    object RecentlyPlayed : Screen("recently_played")
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Playlist.route,
        Screen.Search.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMusicClick = { musicId ->
                        navController.navigate(Screen.Detail.createRoute(musicId))
                    },
                    onFavoriteClick = {
                        navController.navigate(Screen.Playlist.route)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onRecentlyPlayedClick = {
                        navController.navigate(Screen.RecentlyPlayed.route)
                    }
                )
            }

            composable(Screen.Playlist.route) {
                FavoriteScreen(
                    onBackClick = { navController.popBackStack() },
                    onMusicClick = { musicId ->
                        navController.navigate(Screen.Detail.createRoute(musicId))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onMusicClick = { musicId ->
                        navController.navigate(Screen.Detail.createRoute(musicId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val musicId = backStackEntry.arguments?.getString("musicId") ?: ""
                DetailScreen(
                    musicId = musicId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.RecentlyPlayed.route) {
                RecentlyPlayedScreen(
                    onBackClick = { navController.popBackStack() },
                    onMusicClick = { musicId ->
                        navController.navigate(Screen.Detail.createRoute(musicId))
                    }
                )
            }
        }
    }
}
