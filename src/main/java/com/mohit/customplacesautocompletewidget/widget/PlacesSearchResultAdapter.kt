package com.mohit.customplacesautocompletewidget.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohit.customplacesautocompletewidget.R

class PlacesSearchResultAdapter(
    private val placesList: ArrayList<PlaceData>,
    private val onPlaceItemClickListener: OnPlaceItemClickListener
): RecyclerView.Adapter<PlacesSearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val placeName: TextView = itemView.findViewById(R.id.place_name)
        val placeDescription: TextView = itemView.findViewById(R.id.place_description)
        val lPlaces: LinearLayout = itemView.findViewById(R.id.l_places)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_places_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeName.text = placesList[position].placeName
        holder.placeDescription.text = placesList[position].description
        holder.lPlaces.setOnClickListener {
            onPlaceItemClickListener.onPLaceItemClicked(placesList[position])
        }
    }

    override fun getItemCount(): Int {
        return placesList.size
    }
}