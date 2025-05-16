package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.utils.formatMillisToLocal
import com.openclassrooms.realestatemanagerv2.utils.toReadableString

@Composable
fun DetailsInformationsContent(property: Property) {

    Column(
        modifier = Modifier
            .padding(4.dp)
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.informations),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))

        IndividualDetailsContent(
            title = stringResource(id = R.string.area),
            icon = R.drawable.ic_area, data = property.area
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.type),
            icon = R.drawable.ic_type, data = property.type
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.price),
            icon = R.drawable.ic_money, data = property.price
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.number_of_rooms),
            icon = R.drawable.ic_room, data = property.numberOfRooms
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.location),
            icon = R.drawable.ic_location,
            data = property.address
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.status),
            icon = R.drawable.ic_refresh,
            data = property.status.toReadableString()
        )
        IndividualDetailsContent(
            title = stringResource(id = R.string.entry_date),
            icon = R.drawable.ic_entry_date,
            data = property.entryDate.formatMillisToLocal()
        )
        //TODO : saleDate
        IndividualDetailsContent(
            title = stringResource(id = R.string.agent),
            icon = R.drawable.ic_agent,
            data = property.agent.name
        )
    }
}

@Composable
fun <T> IndividualDetailsContent(title: String, icon: Int, data: T) {

    Row(Modifier.padding(start = 4.dp)) {

        Icon(painter = painterResource(id = icon), contentDescription = title)
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = title, fontSize = MaterialTheme.typography.titleSmall.fontSize,
                fontWeight = FontWeight.Bold
            )
            Text(data.toString(), fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}

@Preview(showSystemUi = false, showBackground = true, backgroundColor = -1)
@Composable
private fun MyInfoContentPreview() {
    DetailsInformationsContent(property =

    Property(
        "2",
        "Duplex",
        700000.0,
        47.0,
        2,
        "Charmant duplex au calme, situé au premier étage d'un immeuble ancien datant de 1614, avec vue sur cour." +
                "\n" +
                "Cet agréable 2 pièces, de 46,65 m2 loi Carrez saura vous séduire par ses prestations de qualité et sa hauteur sous plafond de 3,85 mètres.\n" +
                "\n" +
                "Au premier niveau, vous y trouverez une entrée, un vaste séjour de 20 m2 avec cheminée et deux grandes fenêtres en double vitrage, une cuisine américaine équipée et un WC séparé. Au second niveau, vous trouverez une charmante mezzanine et une chambre exposée SUD EST avec vue sur jardin et rangement.\n" +
                "\n" +
                "Situé dans un quartier prisé à DEUX PAS DES QUAIS DE SEINE et des MONUMENTS PARISIENS.\n",

        listOf(
            Photo(
                "https://v.seloger.com/s/cdn/x/visuels/2/1/a/v/21avf4qbks8pbv924mtgtmkurp740d7xma6y628nq.jpg",
                "Front door"
            ),
            Photo(
                "https://v.seloger.com/s/cdn/x/visuels/0/r/9/f/0r9fn8jnkwy5622utakwb36ml3fo94w91cgyhzw46.jpg",
                "LivingRoom side 1/2"
            ),
            Photo("https://v.seloger.com/s/cdn/x/visuels/1/9/v/b/19vb6ql2n8sv94gw32d1bf2vykmit3suwu5s6u79y.jpg",
                "Living room side 2/2"
            ),
            Photo(
                "https://v.seloger.com/s/cdn/x/visuels/1/1/k/o/11kon8apbn4g8j35o7ahweoqaqbn82ipv80flzeqe.jpg",
                "Bathroom"
            ),
            Photo("https://v.seloger.com/s/cdn/x/visuels/1/h/l/5/1hl5dsclzgqucacx41ca9sbsqx61oga1lmd8eulhy.jpg",
                "Bedroom"
            ),
            Photo("https://v.seloger.com/s/cdn/x/visuels/1/j/n/p/1jnp3nwdn4nuniwcunjt0vucfy9y3h7f1ij8f9is6.jpg",
                "Kitchen"
            ),
            Photo("https://v.seloger.com/s/cdn/x/visuels/1/4/t/p/14tpmuphdszj25f8xhjnd7z2ro312jp8b52h8uiqe.jpg",
                "First floor")

        ),
        "4 rue Poulletier, 75004 Paris, France",
        listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        PropertyStatus.Available,
        338210283777,
        null,
        Agent("1", "Will", "911", "willagent@brooklyn.com")
    )
    )


}