package com.openclassrooms.realestatemanagerv2.ui

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyListViewModel
import java.util.Date

@Preview
@Composable
fun PropertyList(@PreviewParameter(PropertyListPreviewParameterProvider::class)  viewModel: PropertyListViewModel = hiltViewModel()) {



    val viewState by viewModel.uiState.collectAsState()
    when(viewState) {
        is PropertyListViewModel.PropertyUiState.Success -> LazyColumn {
            itemsIndexed(items = (viewState as PropertyListViewModel.PropertyUiState.Success).properties) { _, item ->
                PropertyItem(property = item)
            }
        }
        is PropertyListViewModel.PropertyUiState.Error -> Log.e("UI ERROR", "PropertyUiState.Error")
        }
    }


class PropertyListPreviewParameterProvider : PreviewParameterProvider<List<Property>> {
    private val photoList1: List<Photo> = listOf(Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "belle maison"))
    private val photoList2: List<Photo> = listOf(Photo("https://www.istockphoto.com/fr/photo/immeubles-dappartements-modernes-sur-une-journ%C3%A9e-ensoleill%C3%A9e-avec-un-ciel-bleu-gm1177797403-328940905?phrase=appartement", "bel appart"))
    private val nearByPointsList: List<String> = listOf("Ecole","Boulangerie")
    private val property1: Property = Property(1, "maison", 100000.00, 50.00, 2,
        "Une belle petite maison", photoList1, null, "2 DownStreet NY",
        nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
        Agent(1, "Léo l'agent", "0678910111", "toto@gmail.com")
    )
    private val property2: Property = Property(2, "appart", 50000.00, 60.00, 1,
        "Un beau p'tit appart", photoList2, null, "2 DownStreet NY",
        nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
        Agent(1, "Léo l'agent", "0678910111", "toto@gmail.com")
    )
    private val propertyList: List<Property> = listOf(property1, property2)
    override val values = sequenceOf(
        propertyList
    )
}