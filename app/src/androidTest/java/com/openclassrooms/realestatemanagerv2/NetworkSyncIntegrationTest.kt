package com.openclassrooms.realestatemanagerv2

import androidx.activity.viewModels
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.ui.MainActivity
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyUiState
import com.openclassrooms.realestatemanagerv2.utils.DatabaseStatusTracker
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NetworkSyncIntegrationTest {

    @get:Rule(order = 0)
    var hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>()

    private lateinit var viewModel: PropertySharedViewModel

    @Inject
    lateinit var repository: PropertyRepository // Injecter le vrai repo


    @Inject
    lateinit var databaseStatusTracker: DatabaseStatusTracker


    @Before
    fun init() {
        hiltRule.inject()
        NetworkHelper.reset()
        runBlocking {
            repository.insertProperty(
                Property(
                    id = "test_id",
                    address = "123 rue de la paix Paris",
                    latitude = null,
                    longitude = null,
                    type = "Apartment",
                    price = 260000.0,
                    area = 70.0,
                    numberOfRooms = 3,
                    description = "Nice flat",
                    status = PropertyStatus.Available,
                    entryDate = 1_700_000_000L,
                    saleDate = null,
                    agent = Agent(
                        id = "1",
                        name = "testAgent",
                        phoneNumber = "0000",
                        email = "test@oc.com"
                        ),
                    media = emptyList(),
                    nearbyPointsOfInterest = emptyList(),
                )
            )
        }
        databaseStatusTracker.notifyPrepopulated()
        viewModel = composeTestRule.activity.viewModels<PropertySharedViewModel>().value
    }

    @Test
    fun whenNetworkIsBack_thenUIUpdatesAndMissingLocationsAreSynced() {

        val job = viewModel.viewModelScope.launch {
            viewModel.uiState.collect { }
        }

        try {

            composeTestRule.waitUntil(timeoutMillis = 20000) {
                viewModel.uiState.value is PropertyUiState.Success
            }

            NetworkHelper.testNetworkFlow.value = NetworkStatus.Unavailable

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                val state =
                    viewModel.uiState.value as? PropertyUiState.Success
                state?.networkStatus == NetworkStatus.Unavailable
            }

            composeTestRule.onNodeWithContentDescription("Offline Icon").assertIsDisplayed()

            NetworkHelper.testNetworkFlow.value = NetworkStatus.Available

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                val state =
                    viewModel.uiState.value as? PropertyUiState.Success
                state?.networkStatus == NetworkStatus.Available
            }

            composeTestRule.waitUntil(timeoutMillis = 20000) {
                var isUpdated = false
                runBlocking {
                    val property = repository.getPropertyById("test_id")
                    if (property.latitude != null) isUpdated = true
                }
                isUpdated
            }

        } finally {
            job.cancel()
        }
    }
}
