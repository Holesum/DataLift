package com.example.datalift.screens.challenges

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.datalift.designsystem.theme.DataliftTheme
import com.example.datalift.model.ChallengeProgress
import com.example.datalift.model.GoalType
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.smallTestChallenge
import com.example.datalift.model.testChallenge
import com.example.datalift.navigation.getCurrentUserId
import com.example.datalift.screens.profile.LoadingIcon
import com.example.datalift.ui.DevicePreviews
import com.example.datalift.ui.components.DataliftIcons
import com.google.firebase.Timestamp
import java.time.Duration
import java.util.Locale
import kotlin.math.abs

const val LEADERBOARD_CARD_MAX_USERS = 5

@Composable
fun ChallengeCard(
    navigateToChallenge: (String) -> Unit,
    currentUser: String,
    challenge: Mchallenge,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { navigateToChallenge(challenge.challengeId) },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
           Column(
               modifier = Modifier
                   .padding(vertical = 8.dp)
                   .weight(1f)
           ) {
               ChallengesTitle(challenge.title)
               ChallengeTypeDescription(challenge.goal)
               Spacer(Modifier.padding(8.dp))
//               TimeDisplay(challenge.startDate)
//               TimeDisplay(challenge.endDate)
               DaysLeftDisplay(Timestamp.now(),challenge.endDate)
           }
            when(challenge.goal.type){
                GoalType.INCREASE_ORM_BY_VALUE,
                GoalType.COMPLETE_X_WORKOUTS,
                GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART,
                GoalType.COMPLETE_X_REPS_OF_EXERCISE
                     -> LeaderBoardDisplay(
                            goal = challenge.goal,
                            currentUser = currentUser,
                            participants = challenge.participants,
                            progress = challenge.progress,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.70f)
                        )
                GoalType.INCREASE_ORM_BY_PERCENTAGE -> TODO()
                GoalType.UNKNOWN -> TODO()
            }
        }
    }
}

@Composable
fun LeaderBoardDisplay(
    goal: Mgoal,
    currentUser: String,
    participants: List<Muser>,
    progress: Map<String, ChallengeProgress>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val sortedMap = progress.entries.sortedByDescending {
            it.value.currentValue + it.value.completionTimestamp.nanoseconds
        }
        val ind = sortedMap.indexOfFirst { it.key == currentUser }

        LeaderboardDisplay(
            variant = if(sortedMap.size <= LEADERBOARD_CARD_MAX_USERS || ind <= 5) 0 else 1,
            participants = participants,
            leaderboardList = sortedMap,
            currentUserIndex = ind,
            goal = goal
        )
        

        // Need a function that sorts the progress map by
        //  ChallengeProgress.currentValue
        // Then we decided the current user's location
        // on the leaderboard and display that leaderboard type
    }
}
@Composable
fun LeaderboardDisplay(
    variant: Int,
    goal: Mgoal,
    participants: List<Muser>,
    leaderboardList: List<Map.Entry<String, ChallengeProgress>>,
    currentUserIndex: Int,
) {
    if(variant == 0){
        for(i in 0 until minOf(LEADERBOARD_CARD_MAX_USERS,leaderboardList.size)){
            LeaderboardRow(
                rank = i+1,
                name = participants.first { it.uid == leaderboardList[i].key }.name,
//                targetValue = goal.targetValue,
                score = leaderboardList[i].value.currentValue,
                isCurrentUser = i == currentUserIndex,
            )
        }
        if (leaderboardList.size > LEADERBOARD_CARD_MAX_USERS){
            Text("...")
        }
    } else {
        for(i in 0 until LEADERBOARD_CARD_MAX_USERS-1){
            LeaderboardRow(
                rank = i+1,
                name = participants.first { it.uid == leaderboardList[i].key }.name,
                score = leaderboardList[i].value.currentValue,
//                targetValue = goal.targetValue,
                isCurrentUser = false
            )
        }
        Text("...")
        if (leaderboardList.size > LEADERBOARD_CARD_MAX_USERS){
            LeaderboardRow(
                rank = currentUserIndex+1,
                name = participants
                    .first { it.uid == leaderboardList[currentUserIndex].key }
                    .name,
                score = leaderboardList[currentUserIndex].value.currentValue,
//                targetValue = goal.targetValue,
                isCurrentUser = true
            )
        }
    }

}

/*
    1. Bob      12
    2. Sean     19     BOLDED if current user
    3. Anoop   100
    4. Dylan    20
    5. James    49
    ...

    1. Bob      12
    2. Sean     19
    3. Anoop   100
    4. Dylan    20
    ...
    6. James    49     BOLDED if current user

 */
@Composable
fun LeaderboardRow(
    rank: Int,
    name: String,
    score: Int,
//    targetValue: Int,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier) {
        val fontWeight = if(isCurrentUser) FontWeight.Bold else null

        Text(
            text = "$rank.",
            fontWeight = fontWeight
        )
        Text(
            text = name,
            fontWeight = fontWeight
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$score",
            fontWeight = fontWeight
        )
    }
}


@Composable
fun DaysLeftDisplay(currentTime: Timestamp, endTime: Timestamp){
    val daysRemaining = daysRemaining(currentTime,endTime)
    Text(
        text = if(daysRemaining != 1L) "$daysRemaining days left" else "Final Day"
    )
}

@Composable
fun TimeDisplay(timestamp: Timestamp){
    val formatDate = dateFormatted(timestamp)
    Text(text = formatDate)
}

@Composable
fun dateFormatted(uploadDate: Timestamp): String =
    SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        .format(uploadDate.toDate())

@Composable
fun daysRemaining(currentTime: Timestamp,endTime: Timestamp): Long {
    val instant1 = currentTime.toInstant()
    val instant2 = endTime.toInstant()

    val duration = Duration.between(instant1, instant2)
    return abs(duration.toDays()).plus(1)
}


@Composable
fun ChallengesTitle(
    title: String,
    modifier: Modifier = Modifier
){
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun ChallengeTypeDescription(
    goal: Mgoal,
    modifier: Modifier = Modifier
){
    val challengeDescriptionText = returnChallengeDescriptionText(
        goalTargetValue = goal.targetValue,
        goalTargetPercentage = goal.targetPercentage,
        bodyPart = goal.bodyPart,
        exerciseName = goal.exerciseName,
        goalType = goal.type
    )

    Text(
        text = challengeDescriptionText,
        modifier = modifier,
    )
}

fun returnChallengeDescriptionText(
    goalTargetValue: Int,
    goalTargetPercentage: Double? = null,
    bodyPart: String? = null,
    exerciseName: String? = null,
    goalType: GoalType
) =
    when(goalType){
        GoalType.INCREASE_ORM_BY_VALUE ->
            "Increase ORM by $goalTargetValue"
        GoalType.INCREASE_ORM_BY_PERCENTAGE ->
            "Increase ORM by ${goalTargetPercentage ?: 0}%"
        GoalType.COMPLETE_X_WORKOUTS ->
            "Complete $goalTargetValue workouts"
        GoalType.COMPLETE_X_WORKOUTS_OF_BODY_PART ->
            "Complete $goalTargetValue ${bodyPart ?: ""} workouts"
        GoalType.COMPLETE_X_REPS_OF_EXERCISE ->
            "Complete $goalTargetValue reps of ${exerciseName ?: ""}"
        GoalType.UNKNOWN -> ""
    }

@Composable
fun LeaderboardProgressBar(
    target: Int,
    currentProgress: Int,
    modifier: Modifier = Modifier,
){
    val progress = currentProgress.toFloat() / target.toFloat()

    LinearProgressIndicator(
        progress = { progress },
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.onSurfaceVariant,
        strokeCap = StrokeCap.Square,
        gapSize = 0.dp,
        modifier = modifier,
        drawStopIndicator = {
            drawStopIndicator(
                drawScope = this,
                stopSize = 0.dp,
                color = Color.White,
                strokeCap = StrokeCap.Butt
            )
        }
    )
}

@Composable
fun LeaderboardDetailRow(
    rank: Int,
    name: String,
    target: Int,
    currentProgress: Int,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
){
    val fontWeight = if(isCurrentUser) FontWeight.Bold else null

    Row(modifier = modifier) {
        Text(
            text = "$rank. $name",
            softWrap = false,
            fontWeight = fontWeight,
            overflow = TextOverflow.Ellipsis,
//            text = "123456789!@#$%^&",
            modifier = Modifier
                .padding(8.dp)
                .weight(0.75f)
        )
        LeaderboardProgressBar(
            target = target,
            currentProgress = currentProgress,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(0.70f)
        )
//        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$currentProgress/$target",
            fontWeight = fontWeight,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(8.dp)
                .weight(0.55f)
        )
    }
}

@Composable
fun DetailLeaderBoard(
    goal: Mgoal,
    currentUser: String,
    participants: List<Muser>,
    progress: Map<String, ChallengeProgress>,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val sortedMap = progress.entries.sortedByDescending {
            it.value.currentValue + it.value.completionTimestamp.nanoseconds
        }
        val ind = sortedMap.indexOfFirst { it.key == currentUser }

        DetailLeaderboard(
            variant = if(sortedMap.size <= 20 || ind <= 20) 0 else 1,
            participants = participants,
            leaderboardList = sortedMap,
            currentUserIndex = ind,
            goal = goal
        )


        // Need a function that sorts the progress map by
        //  ChallengeProgress.currentValue
        // Then we decided the current user's location
        // on the leaderboard and display that leaderboard type
    }
}


@Composable
fun DetailLeaderboard(
    variant: Int,
    goal: Mgoal,
    participants: List<Muser>,
    leaderboardList: List<Map.Entry<String, ChallengeProgress>>,
    currentUserIndex: Int,
){
    if(variant == 0){
        for(i in 0 until minOf(20,leaderboardList.size)){
            LeaderboardDetailRow(
                rank = i+1,
                name = participants.first { it.uid == leaderboardList[i].key }.name,
                target = goal.targetValue,
                currentProgress = leaderboardList[i].value.currentValue,
                isCurrentUser = i == currentUserIndex,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (leaderboardList.size > 20){
            Text("...")
        }
    } else {
        for(i in 0 until 19){
            LeaderboardDetailRow(
                rank = i+1,
                name = participants.first { it.uid == leaderboardList[i].key }.name,
                currentProgress = leaderboardList[i].value.currentValue,
                target = goal.targetValue,
                isCurrentUser = false
            )
        }
        Text("...")
        if (leaderboardList.size > 20){
            LeaderboardDetailRow(
                rank = currentUserIndex+1,
                name = participants
                    .first { it.uid == leaderboardList[currentUserIndex].key }
                    .name,
                currentProgress = leaderboardList[currentUserIndex].value.currentValue,
                target = goal.targetValue,
                isCurrentUser = true
            )
        }
    }
}

@Composable
fun ChallengesScreen(
    challengesViewModel: ChallengesViewModel = hiltViewModel(),
    navigateToChallenge: (String) -> Unit = { _ -> },
    navigateToChallengeCreation: () -> Unit,
){
    val uiState = challengesViewModel.uiState.collectAsStateWithLifecycle()

    ChallengesScreen(
        uiState = uiState.value,
        navigateToChallenge = navigateToChallenge,
        navigateToChallengeCreation = navigateToChallengeCreation
    )
}

@Composable
internal fun ChallengesScreen(
    uiState: ChallengesUiState,
    navigateToChallenge: (String) -> Unit = {_ -> },
    navigateToChallengeCreation: () -> Unit = {},
){
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                ChallengesUiState.Error -> item {
                    Text("Failed to load screen")
                }

                ChallengesUiState.Loading -> item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadingIcon()
                    }
                }

                is ChallengesUiState.Success -> items(uiState.challenges) { challenge ->
                    ChallengeCard(
                        navigateToChallenge = navigateToChallenge,
                        currentUser = getCurrentUserId(),
                        challenge = challenge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        Button (
            onClick = navigateToChallengeCreation,
            modifier = Modifier
                .padding(12.dp)
                .clip(CircleShape)
                .align(Alignment.BottomEnd)
                .size(64.dp)
        ) {
            Icon(
                imageVector = DataliftIcons.Add,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun ChallengeDetailScreen(
    challenge: Mchallenge?,
    currentUser: String,
    navigateUp: () -> Unit,
    error: Boolean,
){
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = navigateUp) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                text = challenge?.title ?: "Challenge",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 1.dp
        )
        if (error || challenge == null){
            Text("An error has occurred whilst trying to load this screen")
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                ChallengeDetailTitle(
                    title = challenge.title,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
                ChallengeDetailDescription(
                    text = challenge.description,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Card(
                    colors = CardColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(.85f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    DetailLeaderBoard(
                        goal = challenge.goal,
                        currentUser = currentUser,
                        participants = challenge.participants,
                        progress = challenge.progress,
                        modifier = Modifier.padding(top = 16.dp)
//                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeDetailTitle(
    title: String,
    modifier: Modifier = Modifier
){
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = modifier,
    )
}

@Composable
fun ChallengeDetailDescription(
    text: String,
    modifier: Modifier = Modifier
){
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview
@Composable
fun ChallengeCardPreview(){
    val testChallenge = testChallenge()
    ChallengeCard(
        navigateToChallenge =  {_ ->},
        challenge = testChallenge,
        currentUser = "999",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun ChallengeCardVariant2Preview(){
    val testChallenge = testChallenge()
    ChallengeCard(
        navigateToChallenge =  {_ ->},
        challenge = testChallenge,
        currentUser = "392",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun SmallChallengeCardPreview(){
    val testChallenge = smallTestChallenge()
    ChallengeCard(
        navigateToChallenge =  {_ ->},
        challenge = testChallenge,
        currentUser = "172",
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview
@Composable
fun LeaderboardDisplayPreview(){
    Surface{
        LeaderBoardDisplay(
            goal = Mgoal(
                targetValue = 185
            ),
            currentUser = "999",
            progress = mapOf(
                "999" to ChallengeProgress(
                    currentValue = 180,
                ),
                "132" to ChallengeProgress(
                    currentValue = 120
                ),
                "44" to ChallengeProgress(
                    currentValue = 150
                )
            ),
            participants = listOf(
                Muser(
                    uid = "999",
                    name = "Mr 999"
                ),
                Muser(
                    uid = "132",
                    name = "Mrs. 132"
                ),
                Muser(
                    uid = "44",
                    name = "Mc 44"
                )
            )
        )
    }
}

@Preview
@Composable
fun ChallengeDetailLeaderboardRowPreview(){
    DataliftTheme {
        Surface {
            LeaderboardDetailRow(
                rank = 1,
                name = "Test",
                target = 135,
                currentProgress = 120,
                isCurrentUser = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@DevicePreviews
@Composable
fun ChallengeFeedScreenPreview(){
    DataliftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val challengesList = listOf(smallTestChallenge(), testChallenge())

            ChallengesScreen(
                uiState = ChallengesUiState.Success(challengesList),
            )
        }
    }
}

@DevicePreviews
@Composable
fun ChallengeDetailScreenPreivew(){
    DataliftTheme(
        darkTheme = false,
        disableDynamicThemeing = true
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val challenge = testChallenge()
            ChallengeDetailScreen(
                challenge = challenge,
                currentUser = "999",
                navigateUp = {},
                error = false
            )
        }
    }
}