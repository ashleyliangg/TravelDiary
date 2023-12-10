package hu.ait.traveldiary

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hu.ait.traveldiary.nav.Screen
import hu.ait.traveldiary.ui.screen.add.AddEntryScreen
import hu.ait.traveldiary.ui.screen.feed.FeedScreen
import hu.ait.traveldiary.ui.screen.map.MapScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            FeedScreen(
                onNavigateToAddPost = {
                    navController.navigate(BottomBarScreen.WritePost.route)
                }
            )
        }

        composable(BottomBarScreen.WritePost.route) {
            AddEntryScreen()
        }

        composable(route = BottomBarScreen.Map.route) {
            MapScreen()
        }
    }
}

//sealed class Screen(val route: String) {
////    object Feed : Screen("feed")
//    object WritePost : Screen("writepost")
//}


