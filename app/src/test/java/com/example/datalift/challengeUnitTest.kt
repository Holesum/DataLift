import com.example.datalift.MainCoroutineRule
import com.example.datalift.data.repository.ChallengeRepository
import com.example.datalift.model.ChallengeProgress
import com.example.datalift.model.GoalType
import com.example.datalift.model.Mchallenge
import com.example.datalift.model.Mgoal
import com.example.datalift.model.Muser
import com.example.datalift.model.userRepo
import com.example.datalift.screens.challenges.ChallengesUiState
import com.example.datalift.screens.challenges.ChallengesViewModel
import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.invoke
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateChallengeTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ChallengesViewModel
    private val challengeRepository: ChallengeRepository = mockk(relaxed = true)
    private val userRepo: userRepo = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = ChallengesViewModel(challengeRepository, userRepo)
    }

    @Test
    fun `createChallenge emits success when repository returns a challenge`() = coroutineRule.scope.runTest {
        val testUid = "user123"
        viewModel.uid.value = testUid

        val goal = Mgoal(type = GoalType.COMPLETE_X_WORKOUTS, targetValue = 5)
        val participants = listOf(Muser(uid = testUid))
        val startDate = com.google.firebase.Timestamp.now()
        val endDate = com.google.firebase.Timestamp.now()

        val testChallenge = Mchallenge(
            challengeId = "abc123",
            creatorUid = testUid,
            title = "Test Challenge",
            description = "This is a test",
            startDate = Timestamp.now(),
            endDate = Timestamp.now(),
            goal = Mgoal(type = GoalType.COMPLETE_X_WORKOUTS, targetValue = 5),
            participants = listOf(Muser(uid = testUid)),
            progress = mapOf(testUid to ChallengeProgress())
        )

        coEvery {
            challengeRepository.createChallenge(eq(testUid), any(), captureLambda())
        } answers {
            lambda<(Mchallenge?) -> Unit>().invoke(testChallenge)
        }

        viewModel.createChallenge("Test Challenge", "This is a test", goal, startDate.toDate(), endDate.toDate(), participants)

        val result = viewModel.uiState.value
        assertTrue(result is ChallengesUiState.CreationSuccess)
        assertEquals("abc123", (result as ChallengesUiState.CreationSuccess).challenge.challengeId)
    }
}
