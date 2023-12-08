package hu.ait.traveldiary.data

data class Post(
    var uid: String = "",
    var cityName: String = "",
    var title: String = "",
    var body: String = "",
    var imgUrl: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

data class PostWithId(
    val postId: String,
    val post: Post
)