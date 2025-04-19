package com.example.datalift.screens.friends

//import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.DataliftTheme
import com.example.datalift.model.Muser
import com.example.datalift.ui.DevicePreviews

@Composable
fun FriendsScreen(
    friendsViewModel: FriendsViewModel = hiltViewModel(),
    navUp: () -> Unit,
    navigateToProfile: (String) -> Unit,
){
    val uiState by friendsViewModel.searchFriendsUiState.collectAsStateWithLifecycle()
    val searchQuery by friendsViewModel.searchQuery.collectAsStateWithLifecycle()
    friendsViewModel.getCurrUser()

    FriendsScreen(
        navUp = navUp,
        navigateToProfile = navigateToProfile,
        uiState = uiState,
        searchQuery = searchQuery,
        onChangeQuery = friendsViewModel::onSearchQueryChange,
        currentFollowingUser = friendsViewModel::currentlyFollowingUser,
        follow = friendsViewModel::followUser,
        unfollow = friendsViewModel::unfollowUser
    )
}

@Composable
internal fun FriendsScreen(
    modifier: Modifier = Modifier,
    navUp: () -> Unit = {},
    navigateToProfile: (String) -> Unit = {},
    uiState: FriendsUiState = FriendsUiState.Loading,
    searchQuery: String = "",
    onChangeQuery: (String) -> Unit = {},
    currentFollowingUser: (Muser) -> Boolean = {_ -> false},
    follow: (Muser) -> Unit = {},
    unfollow: (Muser) -> Unit = {}
){
    Column {
        Row(modifier = modifier.fillMaxWidth()) {
            IconButton(onClick = navUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = "Friends",
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
        FriendsSearchToolbar(
            searchQuery = searchQuery,
            onChangeQuery = onChangeQuery
        )
        when(uiState){
            FriendsUiState.LoadFailed,
            FriendsUiState.Loading,
            -> Unit

            FriendsUiState.SearchNotReady,
            FriendsUiState.EmptyQuery,
            -> Unit

            is FriendsUiState.Success -> {
                if(uiState.usersSearched.isEmpty()){
                    // Empty Search Body\
                    EmptyFriendsSearchBody()
                } else {
                    // Search Result Body
                    FriendsSearchBody(
                        users = uiState.usersSearched,
                        currentlyFollowingUser = currentFollowingUser,
                        navigateToProfile = navigateToProfile,
                        follow =  follow,
                        unfollow = unfollow
                    )
                }
            }
        }
    }
}

@Composable
fun FriendsSearchToolbar(
    searchQuery: String,
    onChangeQuery: (String) -> Unit,
){
    SearchTextField(
        query = searchQuery,
        onChangeQuery = onChangeQuery,
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun SearchTextField(
    query: String,
    onChangeQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    TextField(
        value = query,
        onValueChange = onChangeQuery,
        prefix = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        shape = RoundedCornerShape(32.dp),
        modifier = modifier,
    )
}

@Composable
fun EmptyFriendsSearchBody(

){
    Text("No Results")
}

@Composable
fun FriendsSearchBody(
    users: List<Muser> = emptyList(),
    currentlyFollowingUser: (Muser) -> Boolean,
    navigateToProfile: (String) -> Unit,
    follow: (Muser) -> Unit,
    unfollow: (Muser) -> Unit
){
    LazyColumn {
        items(users){ user ->
            DisplayUser(
                name = user.name,
                username = user.uname,
                isFollowing = currentlyFollowingUser(user),
                follow = follow,
                unfollow = unfollow,
                user = user,
                navigateToProfile = navigateToProfile
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun DisplayUser(
    name: String,
    username: String,
    isFollowing: Boolean,
    follow: (Muser) -> Unit,
    unfollow: (Muser) -> Unit,
    user: Muser,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier
){
//    Log.d("Firebase", "isFollowing: $isFollowing")
    var userFollowed by remember{mutableStateOf(isFollowing)}
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 8.dp),
    ) {
        IconButton(onClick = { navigateToProfile(user.uid) } ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
        }
        Text(
            text = "$name (@$username)",
            fontSize = 15.sp,
            modifier = Modifier.weight(1.0f)
        )
        if(userFollowed){
            Button(onClick = {
                unfollow(user)
                userFollowed = false
            }) {
                Text(
                    text = "Unfollow"
                )
            }
        }else {
            Button(onClick = {
                follow(user)
                userFollowed = true
            }) {
                Text(
                    text = "Follow"
                )
            }
        }
    }
}

@Preview
@Composable
fun DisplayUserPreview(){
   DataliftTheme {
       Surface() {
           DisplayUser(
               "Dylan",
               "DCSmith",
               isFollowing = true,
               follow = {},
               unfollow = {},
               user = Muser(),
               navigateToProfile = {},
               modifier = Modifier.fillMaxWidth()
           )
       }
   }
}

@DevicePreviews
@Composable
fun FriendsScreenPreview(){
    DataliftTheme {
        Surface() {
            FriendsScreen(
                uiState = FriendsUiState.Loading
            )
        }
    }
}