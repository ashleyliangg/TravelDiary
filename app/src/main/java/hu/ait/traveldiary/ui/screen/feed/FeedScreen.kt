package hu.ait.traveldiary.ui.screen.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel = viewModel(),
    onNavigateToAddPost: () -> Unit
) {
    val postListState = feedViewModel.postsList().collectAsState(
        initial = FeedScreenUIState.Init)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wander Snap") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Info")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigateToAddPost()
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                )
            }
        }
    ) { it ->
        Column(modifier = Modifier.padding(it)) {
            if (postListState.value == FeedScreenUIState.Init) {
                Text(text = "Init...")
            } else if (postListState.value is FeedScreenUIState.Success) {
                LazyColumn {
                    items((postListState.value as FeedScreenUIState.Success).postList) {
                        PostCard(post = it.post,
                            onRemoveItem = {
                                feedViewModel.deletePost(it.postId)
                            },
                            currentUserId = feedViewModel.currentUserId)
                    }
                }
            }
        }
    }
}