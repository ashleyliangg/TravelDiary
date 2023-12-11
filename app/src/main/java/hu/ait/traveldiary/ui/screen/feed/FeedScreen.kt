package hu.ait.traveldiary.ui.screen.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.traveldiary.R

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
                title = { Text(stringResource(R.string.wander_snap)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
            )
        },
    ) { it ->
        Column(modifier = Modifier
            .padding(it)
        ) {
            if (postListState.value == FeedScreenUIState.Init) {
                Text(text = stringResource(R.string.init))
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