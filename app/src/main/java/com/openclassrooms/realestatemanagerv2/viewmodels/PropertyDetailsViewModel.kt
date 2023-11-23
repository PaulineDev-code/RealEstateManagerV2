package com.openclassrooms.realestatemanagerv2.viewmodels

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//Il est important de noter que vous devez utiliser @AndroidEntryPoint dans les fragments qui
//utilisent les viewmodels pour que Hilt puisse injecter vos dépendances correctement.
@HiltViewModel
class PropertyDetailsViewModel @Inject constructor
    (private val propertyRepository : PropertyRepository) : ViewModel(){

}