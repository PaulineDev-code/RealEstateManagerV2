package com.openclassrooms.realestatemanagerv2

import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Règle JUnit pour remplacer le Dispatcher.Main par un TestDispatcher
 * dans les tests unitaires et d'intégration.
 */

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@ExperimentalCoroutinesApi
class UpdateMissingLocationTest {

    // Notre règle pour gérer les coroutines et le Dispatcher.Main
    @get:Rule
    val mainCoroutineRule: MainCoroutineRule = MainCoroutineRule()

    // --- Mocks pour nos dépendances ---
    // 'mock()' vient de la bibliothèque org.mockito.kotlin
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase = mock()
    private val searchPropertiesUseCase: SearchPropertiesUseCase = mock()
    private val updateMissingLocationUseCase: UpdateMissingLocationUseCase = mock()
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase = mock()

    // Le "faux" Flow que l'on va contrôler
    private lateinit var networkStatusFlow: MutableStateFlow<NetworkStatus>

    // La classe que nous testons
    private lateinit var viewModel: PropertySharedViewModel

    @Before
    fun setUp() {
        // 1. Préparer le "faux" Flow et le lier à notre mock de NetworkMonitor
        networkStatusFlow = MutableStateFlow(NetworkStatus.Available) // État de départ
        // 'whenever' est l'équivalent de 'every' de MockK.
        // On dit à Mockito : "Quand la propriété networkStatus est accédée, retourne notre flow"
        whenever(observeNetworkStatusUseCase()).thenReturn(networkStatusFlow)

        wheneverBlocking { getAllPropertiesUseCase() }.thenReturn(emptyList())

        // 2. Créer l'instance du ViewModel avec les mocks
        viewModel = PropertySharedViewModel(
            getAllPropertiesUseCase,
            searchPropertiesUseCase,
            updateMissingLocationUseCase,
            observeNetworkStatusUseCase
        )
    }

    @Test
    fun whenNetworkBecomesAvailable_updateMissingLocationUseCaseIsCalled(): TestResult = runTest {
        // ARRANGE: On simule une perte de réseau initiale lors de la création du ViewModel
        networkStatusFlow.value = NetworkStatus.Unavailable
        // On s'assure que toutes les coroutines de l'init du ViewModel sont terminées
        advanceUntilIdle()

        // ACT: On simule le retour du réseau
        networkStatusFlow.value = NetworkStatus.Available
        // On attend que la nouvelle valeur du Flow soit collectée et traitée
        advanceUntilIdle()

        // ASSERT: On vérifie que la UseCase a bien été appelée une fois.
        // 'verify' est utilisé pour vérifier les interactions.
        // On doit spécifier le nombre d'appels avec times(1).
        verify(updateMissingLocationUseCase, times(1)).invoke()
    }

    @Test
    fun whenNetworkIsUnavailable_updateMissingLocationUseCaseIsNotCalled(): TestResult = runTest {
        // ARRANGE: On simule une perte de réseau
        networkStatusFlow.value = NetworkStatus.Unavailable
        advanceUntilIdle()

        // ACT: Rien à faire, le changement d'état est l'action.

        // ASSERT: On vérifie que la UseCase n'a JAMAIS été appelée.
        // 'never()' est un raccourci pratique pour 'times(0)'.
        verify(updateMissingLocationUseCase, never()).invoke()
    }

}

