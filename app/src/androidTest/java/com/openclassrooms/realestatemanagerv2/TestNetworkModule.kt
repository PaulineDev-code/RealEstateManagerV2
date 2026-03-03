package com.openclassrooms.realestatemanagerv2

import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitor
import com.openclassrooms.realestatemanagerv2.di.RepositoryModule
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.repositories.AgentRepositoryImpl
import com.openclassrooms.realestatemanagerv2.repositories.LocationRepositoryImpl
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

object NetworkHelper {
    val testNetworkFlow: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Unavailable)

    fun reset() {
        testNetworkFlow.value = NetworkStatus.Unavailable
    }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeRepositoryModule {

    @Binds
    abstract fun bindAgentRepository(impl: AgentRepositoryImpl): AgentRepository

    @Binds
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    abstract fun bindPropertyRepository(impl: PropertyRepositoryImpl): PropertyRepository

    @Binds
    abstract fun bindNetworkMonitor(impl: FakeNetworkMonitor): NetworkMonitor
}

@Singleton
class FakeNetworkMonitor @Inject constructor() : NetworkMonitor {
    override val networkStatus: Flow<NetworkStatus> = NetworkHelper.testNetworkFlow
}