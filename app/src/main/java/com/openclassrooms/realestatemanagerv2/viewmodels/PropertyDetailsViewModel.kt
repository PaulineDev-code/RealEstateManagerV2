package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Il est important de noter que vous devez utiliser @AndroidEntryPoint dans les fragments qui
//utilisent les viewmodels pour que Hilt puisse injecter vos dépendances correctement.
@HiltViewModel
class PropertyDetailsViewModel @Inject constructor
    (private val getPropertyByIdUseCase: GetPropertyByIdUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<PropertyDetailsUiState>(PropertyDetailsUiState.Success(null))


    val uiState: StateFlow<PropertyDetailsUiState> = _uiState

    fun getPropertyById(id: String) {
        val currentState = _uiState.value
        if (currentState is PropertyDetailsUiState.Success && currentState.property?.id == id) {
            return
        }
        viewModelScope.launch {
            _uiState.value = PropertyDetailsUiState.Loading

            try {
                val property = getPropertyByIdUseCase(id)
                Log.d("DetailsViewModel", "Collected property: $property")
                _uiState.value = PropertyDetailsUiState.Success(
                    property = property,
                    selectedPhotoIndex = 0,
                    isPhotoViewerShown = false
                )
            } catch (exception: Exception) {
                Log.e("DetailsViewModel", "Error collecting property by id", exception)
                _uiState.value = PropertyDetailsUiState.Error(exception)
            }
        }
    }

    fun updateSelectedPhotoIndex(newPhotoIndex: Int) {
        (_uiState.value as? PropertyDetailsUiState.Success)?.let { currentState ->
            _uiState.value = currentState.copy(selectedPhotoIndex = newPhotoIndex)
        }
    }

    fun updatePhotoViewerShown(newIsPhotoViewerShown: Boolean) {
        (_uiState.value as? PropertyDetailsUiState.Success)?.let { currentState ->
            _uiState.value = currentState.copy(isPhotoViewerShown = newIsPhotoViewerShown)
        }
    }

    sealed class PropertyDetailsUiState {
        object Loading: PropertyDetailsUiState()
        data class Success(
            val property: Property?,
            val selectedPhotoIndex: Int = 0,
            val isPhotoViewerShown: Boolean = false
        ): PropertyDetailsUiState()
        data class Error(val exception: Exception): PropertyDetailsUiState()
    }

}