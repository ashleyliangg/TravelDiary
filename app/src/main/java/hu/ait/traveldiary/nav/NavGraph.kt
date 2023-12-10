package hu.ait.traveldiary.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.traveldiary.MainScreen
import hu.ait.traveldiary.ui.screen.add.AddEntryScreen
import hu.ait.traveldiary.ui.screen.feed.FeedScreen
import hu.ait.traveldiary.ui.screen.login.LoginScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MainScreen.route)
                }
            )
        }
        composable(Screen.MainScreen.route){
            MainScreen()
        }
//        composable(Screen.Feed.route) {
//            FeedScreen(
//                onNavigateToAddPost = {
//                    navController.navigate(Screen.WritePost.route)
//                }
//            )
//        }
//        composable(Screen.WritePost.route) {
//            AddEntryScreen()
//        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
//    object Feed : Screen("feed")
//    object WritePost : Screen("writepost")

    object MainScreen : Screen("mainscreen")
}
