package com.mohit.customplacesautocompletewidget.widget

data class PlaceData(
    var placeId: String,
    var placeName: String,
    var description: String?,
    val latitude: Double,
    val longitude: Double
)