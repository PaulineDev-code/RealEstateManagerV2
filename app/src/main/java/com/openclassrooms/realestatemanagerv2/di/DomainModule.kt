package com.openclassrooms.realestatemanagerv2.di

import com.openclassrooms.realestatemanagerv2.domain.usecases.AddPropertyUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.AddPropertyUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.CreateAgentUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.CreateAgentUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCaseImpl
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindAddPropertyUseCase(
        impl: AddPropertyUseCaseImpl
    ): AddPropertyUseCase

    @Binds
    abstract fun bindCreateAgentUseCase(
        impl: CreateAgentUseCaseImpl
    ): CreateAgentUseCase

    @Binds
    abstract fun bindGetAllAgentsUseCase(
        impl: GetAllAgentsUseCaseImpl
    ): GetAllAgentsUseCase

    @Binds
    abstract fun bindGetAllPropertiesUseCase(
        impl: GetAllPropertiesUseCaseImpl
    ): GetAllPropertiesUseCase

    @Binds
    abstract fun bindLocationUseCase(
        impl: GetLocationUseCaseImpl
    ): GetLocationUseCase

    @Binds
    abstract fun bindGetPropertyByIdUseCase(
        impl: GetPropertyByIdUseCaseImpl
    ): GetPropertyByIdUseCase

    @Binds
    abstract fun bindGetPropertyTypesUseCase(
        impl: GetPropertyTypesUseCaseImpl
    ): GetPropertyTypesUseCase

    @Binds
    abstract fun bindObserveNetworkStatusUseCase(
        impl: ObserveNetworkStatusUseCaseImpl
    ): ObserveNetworkStatusUseCase

    @Binds
    abstract fun bindSearchPropertiesUseCase(
        impl: SearchPropertiesUseCaseImpl
    ): SearchPropertiesUseCase

    @Binds
    abstract fun bindUpdateMissingLocationUseCase(
        impl: UpdateMissingLocationUseCaseImpl
    ): UpdateMissingLocationUseCase

    @Binds
    abstract fun bindUpdatePropertyUseCase(
        impl: UpdatePropertyUseCaseImpl
    ): UpdatePropertyUseCase

}