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
    fun dashboard_rendersMainRooms() {
        composeRule.setContent {
            DashboardScreen(
                lights = mapOf(
                    "sala" to true,
                    "cocina" to false,
                    "dormitorio" to true,
                    "baño" to false,
                    "jardin" to true,
                    "garage" to false
                ),
                isLoading = false,
                onToggleRoom = { _, _ -> },
                onToggleAll = {},
                onRoomClick = {}
            )
        }

        composeRule.onNodeWithText("Sala").assertExists()
        composeRule.onNodeWithText("Cocina").assertExists()
        composeRule.onNodeWithText("Dormitorio").assertExists()
    }
}
