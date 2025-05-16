package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyListViewModel


@Composable
fun PropertyList(navController: NavController, modifier:Modifier, viewModel: PropertyListViewModel = hiltViewModel()) {


    /*val viewState by viewModel.uiState.collectAsState()
    when (viewState) {
        is PropertyListViewModel.PropertyUiState.Success ->
            LazyColumn(modifier = modifier.fillMaxSize()) {
            itemsIndexed(items = (viewState as PropertyListViewModel.PropertyUiState.Success).properties) { _, item ->
                PropertyListItem(property = item, onItemClick = {
                    navController.navigate("details_screen" + "/" + item.id)
                })
            }
        }

        is PropertyListViewModel.PropertyUiState.Error -> Log.e("UI ERROR", "PropertyUiState.Error")
    }*/
}


/*


class PropertyListPreviewParameterProvider : PreviewParameterProvider<List<Property>> {
    private val mediaList1s: List<Photo> = listOf(Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "belle maison"))
    private val mediaList2s: List<Media> = listOf(Photo("https://www.istockphoto.com/fr/photo/immeubles-dappartements-modernes-sur-une-journ%C3%A9e-ensoleill%C3%A9e-avec-un-ciel-bleu-gm1177797403-328940905?phrase=appartement", "bel appart"))
    private val nearByPointsList: List<String> = listOf("Ecole","Boulangerie")
    private val property1: Property = Property("1", "maison", 100000.00, 50.00, 2,
        "Une belle petite maison", mediaList1s,  "2 DownStreet NY",
        nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
        Agent("1", "Léo l'agent", "0678910111", "toto@gmail.com")
    )
    private val property2: Property = Property("2", "appart", 50000.00, 60.00, 1,
        "Un beau p'tit appart", mediaList2s, "2 DownStreet NY",
        nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
        Agent("1", "Léo l'agent", "0678910111", "toto@gmail.com")
    )
    private val propertyList: List<Property> = listOf(property1, property2)
    override val values = sequenceOf(
        propertyList
    )
}*/
