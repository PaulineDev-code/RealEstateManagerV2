package com.openclassrooms.realestatemanagerv2.ui.models

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media

sealed interface AddPropertyUiState {
    data class Success(val propertyId: String): AddPropertyUiState
    data class Error(val exception: Exception, val previousEditingState: Editing? = null) : AddPropertyUiState
    data class Editing(

        val description: String = "",
        val type: String = "",
        val price: String = "",
        val area: String = "",
        val numberOfRooms: String = "",
        val photoUri: String = "",
        val photoDescription: String = "",
        val mediaList: List<Media> = emptyList(),
        val videoUri: String? = null,
        val address: String = "",
        val nearbyPoint: String = "",
        val nearbyPointList: List<String> = emptyList(),
        val entryDate: String = "",
        val saleDate: String = "",
        val agent: Agent? = null,
        val agentList: List<Agent> = emptyList()

    ) : AddPropertyUiState
}
