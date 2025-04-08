package com.example.datalift.screens.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.datalift.model.Muser
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.theme.DataliftTheme

@Composable
fun FriendsScreen(
    friendsViewModel: FriendsViewModel = hiltViewModel(),
    navUp: () -> Unit,
){
    val uiState by friendsViewModel.searchFriendsUiState.collectAsStateWithLifecycle()
    val searchQuery by friendsViewModel.searchQuery.collectAsStateWithLifecycle()

    FriendsScreen(
        navUp = navUp,
        uiState = uiState,
        searchQuery = searchQuery,
        onChangeQuery = friendsViewModel::onSearchQueryChange,
        currentFollowingUser = friendsViewModel::currentlyFollowingUser
    )
}

@Composable
internal fun FriendsScreen(
    navUp: () -> Unit = {},
    uiState: FriendsUiState = FriendsUiState.Loading,
    searchQuery: String = "",
    onChangeQuery: (String) -> Unit = {},
    currentFollowingUser: (Muser) -> Boolean = {_ -> false},
    modifier: Modifier = Modifier
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
                        currentlyFollowingUser = currentFollowingUser
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
        modifier = modifier
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
){
    LazyColumn {
        items(users){ user ->
            DisplayUser(
                name = user.name,
                username = user.uname,
                isFollowing = currentlyFollowingUser(user)
            )
        }
    }
}

@Composable
fun DisplayUser(
    name: String,
    username: String,
    isFollowing: Boolean,
){
    Row {
        Column {
            Text(name)
            Text(username)
        }
        Button(onClick = {}) {
            Text(
                text = if (isFollowing) "Following" else "Follow"
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