package hu.ait.traveldiary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Map : BottomBarScreen(
        route = "map",
        title = "Map",
        icon = Icons.Default.Place
    )

    object WritePost : BottomBarScreen(
        route = "writepost",
        title = "WritePost",
        icon = Icons.Default.AddCircle
    )
}