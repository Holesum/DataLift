package com.example.datalift.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.ExerciseItem
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.navigation.getCurrentUserId
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.UserPreviewParameterProvider


sealed class GoalInputState {
    data class IncreaseORMByValue(val exerciseName: String, val targetValue: Int) : GoalInputState()
    data class IncreaseORMByPercentage(val bodyPart: String, val percentage: Double) : GoalInputState()
    data class CompleteXWorkouts(val count: Int) : GoalInputState()
    data class CompleteXWorkoutsOfBodyPart(val bodyPart: String, val count: Int) : GoalInputState()
    data class CompleteXRepsOfExercise(val exerciseName: String, val reps: Int) : GoalInputState()
    object None : GoalInputState()
}

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navUp: () -> Unit = {}
){
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val uid = getCurrentUserId()
    val goals by profileViewModel.goals.collectAsStateWithLifecycle()
    val isDialogVisible by profileViewModel.isDialogVisible.collectAsState()
    val exercises by profileViewModel.exercises.collectAsState()

    LaunchedEffect(uid) {
        profileViewModel.loadGoals(uid)
    }

    ProfileScreen(
        uiState = uiState,
        navUp = navUp,
        goals = goals,
        onAddGoalClicked = { profileViewModel.toggleDialogVisibility() },
        createGoal = { goal: Mgoal -> profileViewModel.createGoal(goal)
                     profileViewModel.hideDialog()},
        exercises = exercises,
        getQuery = { query: String -> profileViewModel.getExercises(query) },
        isDialogVisible = isDialogVisible,
        removeGoal = { goal: Mgoal -> profileViewModel.deleteGoal(goal) },
        isImperial = profileViewModel.getUnitSystem(),
        currUser = uid
    )
}

@Composable
internal fun ProfileScreen(
    uiState: ProfileUiState,
    navUp: () -> Unit = {},
    goals: List<Mgoal> = emptyList(),
    isDialogVisible: Boolean = false,
    onAddGoalClicked: () -> Unit = {},
    createGoal: (Mgoal) -> Unit = {},
    exercises: List<ExerciseItem> = emptyList(),
    getQuery: (String) -> Unit = {},
    removeGoal: (Mgoal) -> Unit = {},
    isImperial: Boolean = true,
    currUser: String = "",
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
                text = "Profile",
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
        when(uiState){
            ProfileUiState.Loading -> Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LoadingIcon()
            }
            ProfileUiState.Error -> Text("Failed to load screen")
            is ProfileUiState.Success -> {
                UserInformationDisplay(
                    name = uiState.user.name,
                    userName = uiState.user.uname,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Row {
                    FollowDisplay(
                        followingCount = uiState.user.following.size,
                        followerCount = uiState.user.followers.size,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                }
                Spacer(modifier = Modifier.height(24.dp))
                if(currUser == uiState.user.uid)
                    GoalSection(
                        goals = goals,
                        isDialogVisible = isDialogVisible,
                        onAddGoalClicked =  onAddGoalClicked,
                        createGoal = createGoal,
                        exercises = exercises,
                        getQuery = getQuery,
                        removeGoal = removeGoal,
                        isImperial = isImperial
                    )
            }
        }

    }
}



@Composable
fun LoadingIcon(
    modifier: Modifier = Modifier
){
    CircularProgressIndicator(
        modifier = modifier
            .size(128.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
fun UserInformationDisplay(
    name: String,
    userName: String,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Column(
            modifier.padding(start = 10.dp)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text("@$userName")
        }
    }
}

@Composable
fun FollowDisplay(
    followingCount: Int,
    followerCount: Int,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Following",
            )
            Text(
                text = followingCount.toString(),
                color = Color.Blue
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text("Followers")
            Text(
                text = followerCount.toString(),
                color = Color.Blue
            )
        }
    }
}




@DevicePreviews
@Composable
fun ProfileScreenLoadingPreview(){
    DataliftTheme {
        Surface() {
            ProfileScreen(
                uiState = ProfileUiState.Loading
            )
        }
    }
}

@DevicePreviews
@Composable
fun ProfileScreenSuccessPreview(
    @PreviewParameter(UserPreviewParameterProvider::class)
    user: Muser
){
    DataliftTheme {
        Surface() {
            ProfileScreen(
                uiState = ProfileUiState.Success(user)
            )
        }
    }
}
