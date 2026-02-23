package com.openclassrooms.realestatemanagerv2.di

import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitor
import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitorImpl
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import com.openclassrooms.realestatemanagerv2.repositories.AgentRepositoryImpl
import com.openclassrooms.realestatemanagerv2.repositories.LocationRepositoryImpl
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAgentRepository(
        impl: AgentRepositoryImpl
    ): AgentRepository

    @Binds
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    abstract fun bindPropertyRepository(
        impl: PropertyRepositoryImpl
    ): PropertyRepository

    @Binds
    abstract fun bindNetworkMonitor(
        impl: NetworkMonitorImpl
    ): NetworkMonitor

}