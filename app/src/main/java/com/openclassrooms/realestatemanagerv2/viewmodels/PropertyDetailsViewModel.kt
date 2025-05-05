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
        viewModelScope.launch {
            try {
                getPropertyByIdUseCase(id).collect { property ->
                    Log.d("DetailsViewModel", "Collected property: $property")
                    _uiState.value = PropertyDetailsUiState.Success(property)
                }
            } catch (exception: Exception) {
                Log.e("DetailsViewModel", "Error collecting property by id", exception)
                _uiState.value = PropertyDetailsUiState.Error(exception)
            }
        }
    }

    sealed class PropertyDetailsUiState {
        data class Success(val property: Property?): PropertyDetailsUiState()
        data class Error(val exception: Exception): PropertyDetailsUiState()
    }

}