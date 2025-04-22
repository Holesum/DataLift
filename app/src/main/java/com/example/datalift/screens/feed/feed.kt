package com.example.datalift.screens.feed

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.Mexercise
import com.example.datalift.model.Mpost
import com.example.datalift.model.testPost
import com.example.datalift.model.testPostList
import com.example.datalift.ui.DevicePreviews
import com.google.firebase.Timestamp
import java.util.Locale

@Composable
fun PostScreen(
    post: Mpost?,
    modifier: Modifier = Modifier,
    navUp: () -> Unit = {},
    navigateToProfile: (String) -> Unit = {},
) {
    Column {
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = post?.title ?: "Post",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(
            modifier = modifier.padding(top = 8.dp),
            thickness = 1.dp
        )
        if (post != null) {
            Card(modifier = Modifier.fillMaxWidth()){
                Row(modifier = Modifier.padding(top= 8.dp)) {
                    IconButton(onClick = {
                        if(post.poster != null){
                            navigateToProfile(post.poster.uid)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                        )
                    }
                    Column {
                        Text(
                            text = post.poster?.uname.toString(),
                            fontWeight = FontWeight.Bold
                        )
                        TimeDisplay(post.date)
                    }
                }
                PostDescription(
                    text = post.body,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null
                        )
                    }
                    Text(
                        text = post.getLikesCount().toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                }
            }
            Card(
                modifier = Modifier.padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (post.workout != null){
                        post.workout.exercises.forEach() { exercise ->
                            PostExerciseDisplay(
                                exercise = exercise,
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            )
                        }
                    }
                }
            }
        } else {
            Text("Post is unavailable")
        }
    }
}

@Composable
fun PostExerciseDisplay(
    exercise: Mexercise,
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier) {
        Text(exercise.name)
        Column(modifier = Modifier.padding(start = 12.dp)){
            exercise.sets.forEach { set ->
                Text(text = "${set.rep} reps at ${set.weight} lbs")
            }
        }
    }
}

@Composable
fun PostDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
    )
}

@Composable
fun PostCard(
    navigateToPost: (String, String) -> Unit,
    navigateToProfile: (String) -> Unit,
    post: Mpost,
    modifier: Modifier = Modifier,
){
    Card(
        onClick = { navigateToPost(post.docID, post.poster?.uid!!) },
        modifier = modifier
    ){
        Column {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                IconButton(
                    onClick = {
                        if(post.poster != null){
                            navigateToProfile(post.poster.uid)
                        }

                }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                }
                Column {
                    Text(post.poster?.uname.toString())
                    TimeDisplay(post.date)
                }
            }
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(start = 8.dp,top = 8.dp)
            )
            PostDescription(
                text = post.body,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                }
                Text(
                    text = post.getLikesCount().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
            }
        }


    }
}

//@Composable
//fun dateFormatted(uploadDate: Timestamp): String = DateTimeFormatter
//    .ofLocalizedDate(FormatStyle.MEDIUM)
//    .withLocale(Locale.getDefault())
//    .format()


@Composable
fun dateFormatted(uploadDate: Timestamp): String =
    SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm",Locale.getDefault())
    .format(uploadDate.toDate())

@Composable
fun TimeDisplay(timestamp: Timestamp){
    val formatDate = dateFormatted(timestamp)
    Text(text = formatDate)
}

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel = hiltViewModel(),
    navigateToPost: (String, String) -> Unit,
    navigateToProfile: (String) -> Unit,
){
    feedViewModel.getPosts()
    val posts = feedViewModel.posts.collectAsStateWithLifecycle().value
//    val tPosts = testPostList()
    FeedScreen(
        posts = posts,
        navigateToPost = navigateToPost,
        navigateToProfile = navigateToProfile
    )
}

@Composable
internal fun FeedScreen(
    posts: List<Mpost>,
    navigateToPost: (String, String) -> Unit,
    navigateToProfile: (String) -> Unit = {},
){
//    Text("Feed Screen placeholder")
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts){ post ->
            PostCard(
                navigateToPost = navigateToPost,
                post = post,
                navigateToProfile = navigateToProfile,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@DevicePreviews
@Composable
fun FeedPreview(){
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
//            val tPost = testPost()
//            PostCard(post = tPost)
            val testPosts = testPostList()
            FeedScreen(
                posts = testPosts,
                navigateToPost = {_,_ ->}
            )
        }
    }
}

@DevicePreviews
@Composable
fun PostPreview(){
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val post = testPost()
            PostScreen(post = post)
        }
    }
}