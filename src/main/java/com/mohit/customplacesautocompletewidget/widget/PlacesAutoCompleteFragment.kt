package com.mohit.customplacesautocompletewidget.widget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohit.customplacesautocompletewidget.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class PlacesAutoCompleteFragment : Fragment(), OnPlaceItemClickListener {

    private lateinit var viewModel: PlacesAutoCompleteViewModel
    private lateinit var searchTextBox: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var placesListRecycler: RecyclerView
    private var searchText = ""

    private var maps_api_key: String? = null
    private lateinit var autoCompleteClickInterface: AutoCompleteClickInterface
    private val placesList: ArrayList<PlaceData> = ArrayList<PlaceData>()

    private var latitude = 0.0
    private var longitude = 0.0
    private var radius = 500000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[PlacesAutoCompleteViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_places_auto_complete, container, false)
        searchTextBox = view.findViewById(R.id.search_bar)
        clearSearchButton = view.findViewById(R.id.clear_text)
        placesListRecycler = view.findViewById(R.id.places_search_result)

        return view
    }

    fun initializeAutocompleteFragment(mapKey: String, autoCompleteClickInterface: AutoCompleteClickInterface){
        maps_api_key = mapKey
        this.autoCompleteClickInterface = autoCompleteClickInterface
    }

    fun setCurrentLocation(cLatitude: Double, cLongitude: Double){
        latitude = cLatitude
        longitude = cLongitude
    }

    fun setSearchRadius(searchRadius: Int){
        radius = searchRadius
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesListRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = PlacesSearchResultAdapter(placesList, this@PlacesAutoCompleteFragment)
        }

        searchTextBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchHandler.removeCallbacks(searchRunnable)
            }

            override fun afterTextChanged(s: Editable?) {
                if(maps_api_key.isNullOrEmpty()){
                    throw RuntimeException("Auto complete fragment not initialized, call initializeAutocompleteFragment(String, AutoCompleteClickInterface) in your onCreate")
                }

                if(latitude == 0.0 && longitude == 0.0){
                    throw RuntimeException("Location not set. Set the location by calling setCurrentLocation(latitude, longitude")
                }

                searchText = s.toString()
                if(searchText.isEmpty()){
                    clearSearchButton.visibility = View.GONE
                    placesListRecycler.visibility = View.GONE
                }else{
                    clearSearchButton.visibility = View.VISIBLE
                    placesListRecycler.visibility = View.VISIBLE
                }

                if(searchText.length > 2){
                    searchHandler.postDelayed(searchRunnable, 500)
                }
            }

        })

        clearSearchButton.setOnClickListener {
            searchTextBox.setText("")
            autoCompleteClickInterface.onClearSearchText()
        }
    }

    private val searchHandler = Handler(Looper.myLooper()!!)
    private val searchRunnable = object : Runnable{
        override fun run() {
            callPlacesAPI()
        }

    }

    private fun callPlacesAPI(){
        var connection: HttpURLConnection? = null
        val jsonResult = StringBuilder()

        CoroutineScope(IO).launch {
            try {
                val sb =
                    "https://maps.googleapis.com/maps/api/place/textsearch/json?" + "query=" + searchText +  //"&components=country:us" +
                            //"&location=${latitude}%2C${longitude}" +
                            "&key=" + maps_api_key

                Log.d( "callPlacesAPI: ", "URL=$sb")
                val url = URL(sb)
                connection = url.openConnection() as HttpURLConnection
                val inputStreamReader = InputStreamReader(connection!!.inputStream)
                var read: Int
                val buff = CharArray(1024)
                while (inputStreamReader.read(buff).also { read = it } != -1) {
                    jsonResult.append(buff, 0, read)
                }
                Log.d("JSon", jsonResult.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                Log.e( "callPlacesAPI: ", e.message?:"Exception")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e( "callPlacesAPI: ", e.message?:"Exception")
            } finally {
                connection?.disconnect()
            }

            try {
                val jsonObject = JSONObject(jsonResult.toString())
                val prediction = jsonObject.getJSONArray("results")
                placesList.clear()
                for (i in 0 until prediction.length()) {
                    val placeData = PlaceData(
                        prediction.getJSONObject(i).getString("place_id"),
                        prediction.getJSONObject(i).getString("name"),
                        prediction.getJSONObject(i).optString("formatted_address"),
                        prediction.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        prediction.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    )
                    placesList.add(placeData)
                }
            } catch (e: JSONException) {
                Log.e( "callPlacesAPI: ", e.message?:"Exception")
                e.printStackTrace()
            }

            withContext(Main){
                placesListRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onPLaceItemClicked(placeData: PlaceData) {
        searchTextBox.setText(placeData.placeName)
        searchTextBox.setSelection(searchTextBox.length())
        searchHandler.removeCallbacks(searchRunnable)
        placesList.clear()
        placesListRecycler.adapter?.notifyDataSetChanged()
        placesListRecycler.visibility = View.GONE
        autoCompleteClickInterface.onItemClicked(placeData)
    }

}