package hu.ait.traveldiary.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.traveldiary.MainScreen
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
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object MainScreen : Screen("mainscreen")
}
