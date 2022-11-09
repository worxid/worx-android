package id.worx.device.client.welcome

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import id.worx.device.client.screen.welcome.CreateTeamScreen
import id.worx.device.client.screen.welcome.WelcomeScreen
import id.worx.device.client.theme.WorxTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class WelcomeScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var session: id.worx.device.client.data.database.Session

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun simpleTest() {
        composeTestRule.setContent {
            Text(text = "HEY")
        }
        composeTestRule.onNodeWithText("HEY").assertIsDisplayed()
    }

    @Test
    fun welcome_screen() {
        composeTestRule.setContent {
            WorxTheme {
                WelcomeScreen(onEvent = {}, session = session)
            }
        }
        composeTestRule.onNodeWithText("Create New Team").assertIsDisplayed()
        composeTestRule.onNodeWithText("Join Existing Team").assertIsDisplayed()
    }

    @Test
    fun createteam_screen() {
        composeTestRule.setContent {
            WorxTheme {
                CreateTeamScreen(session = session, {})
            }
        }
        composeTestRule.onNodeWithText("Create New Team").assertIsDisplayed()
    }

    @Test
    fun jointeam_screen() {

    }
}