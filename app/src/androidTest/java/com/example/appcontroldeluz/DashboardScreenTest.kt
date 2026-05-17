package com.example.appcontroldeluz

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.appcontroldeluz.ui.screens.DashboardScreen
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun dashboard_rendersLedLocations() {
        composeRule.setContent {
            DashboardScreen(
                ledStates = (1..8).associateWith { it == 1 },
                ledLabels = mapOf(
                    1 to "Casa",
                    2 to "Patio frontal",
                    3 to "Patio trasero"
                ),
                isLoading = false,
                isSendingCommand = false,
                onToggleLed = { _, _ -> },
                onToggleAll = {},
                onRenameLed = { _, _ -> }
            )
        }

        composeRule.onNodeWithText("Casa").assertExists()
        composeRule.onNodeWithText("Patio frontal").assertExists()
        composeRule.onNodeWithText("Patio trasero").assertExists()
    }
}
