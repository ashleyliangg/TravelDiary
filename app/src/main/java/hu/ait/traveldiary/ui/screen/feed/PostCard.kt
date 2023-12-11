package hu.ait.traveldiary.ui.screen.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import hu.ait.traveldiary.R
import hu.ait.traveldiary.data.Post

@Composable
fun PostCard(
    post: Post,
    onRemoveItem: () -> Unit = {},
    currentUserId: String = "",
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier
            .padding(5.dp)
            .fillMaxSize(),
    ) {
        var showSheet by remember { mutableStateOf(false) }
        if (showSheet) {
            BottomSheet (postBody = post.body,
                postStartDate = post.startDate,
                onDismiss = {
                showSheet = false
            },
                onRemove = {onRemoveItem()})
        }


        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(3.dp)) {
                Text(
                    text = post.cityName,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.padding(3.dp))
//            if (post.imgUrl != "") {
            AsyncImage(
                model = post.imgUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        showSheet = true
                    },
                contentDescription = "Post Image"

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(postBody: String, postStartDate: String, onDismiss: () -> Unit, onRemove: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        val clauseComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                R.raw.clause
            )
        )
        val clauseProgress by animateLottieCompositionAsState(
            clauseComposition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true
        )
        LottieAnimation(
            composition = clauseComposition,
            progress = clauseProgress,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = postBody,
                    fontSize = 17.sp
                )
                Text(
                    text = postStartDate,
                    fontWeight = FontWeight.Light
                )
            }
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable {
                    onRemove()
                    onDismiss()
                },
                tint = Color.Red
            )
        }
        Spacer(modifier = Modifier.padding(30.dp))
    }
}

