package com.mohit.customplacesautocompletewidget.widget

interface AutoCompleteClickInterface {
    fun onClearSearchText()
    fun onItemClicked(placeData: PlaceData)
}