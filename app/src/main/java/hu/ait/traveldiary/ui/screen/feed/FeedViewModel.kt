package hu.ait.traveldiary.ui.screen.feed

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.traveldiary.data.Post
import hu.ait.traveldiary.data.PostWithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class FeedViewModel : ViewModel() {
    var currentUserId: String = Firebase.auth.currentUser!!.uid

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            "posts"
        ).document(postKey).delete()
    }

    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("posts")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        val postWithIdList = mutableListOf<PostWithId>()

                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                        }

                        FeedScreenUIState.Success(
                            postWithIdList
                        )
                    } else {
                        FeedScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

}


sealed interface FeedScreenUIState {
    object Init : FeedScreenUIState
    data class Success(val postList: List<PostWithId>) : FeedScreenUIState
    data class Error(val error: String?) : FeedScreenUIState
}