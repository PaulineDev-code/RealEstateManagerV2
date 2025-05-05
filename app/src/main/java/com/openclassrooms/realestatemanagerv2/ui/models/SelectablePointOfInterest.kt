package com.openclassrooms.realestatemanagerv2.ui.models

import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import java.util.UUID
import kotlin.random.Random

data class SelectablePointOfInterest(
    var name :String,
    var isSelected :Boolean
) {
    companion object {
        fun fromPointOfInterest(poi: PointOfInterest): SelectablePointOfInterest? {
            return SelectablePointOfInterest(name = poi.name, isSelected = false)
        }
    }


}
