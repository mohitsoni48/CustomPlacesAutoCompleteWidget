# CustomPlacesAutoCompleteWidget

This is an alternative to google's AutoCompleteFragment because it has too many limitation related to UI and callbacks. You can customize/use this widget in your projects for place search.

<h1>Step 1</h1>
You need to set up google cloud account, set google maps billing and get google API key to use this library. See google map sdk: https://developers.google.com/maps/documentation/android-sdk/overview

<h1>Step 2</h1>
download and import the source code in your project

//will be live on maven soon

<h1>Step 3</h1>
place the fragment in your layout file like

//..
<pre>&lt;fragment
        android:id="@+id/autocomplete_fragment"
        android:name="com.mohit.customplacesautocompletewidget.widget.PlacesAutoCompleteFragment"
        android:layout_width="match_parent"
        android:layout_marginHorizontal="@dimen/_6adp"
        android:layout_marginTop="@dimen/_16adp"
        map:layout_constraintTop_toTopOf="parent"
        android:layout_height="wrap_content"/&gt;</pre>
//..//

<h1>Step 4</h1>
In you maps Activity Fragment, implement "AutoCompleteClickInterface", override callbacks and initialize maps fragment with your maps API key in your onViewCreated

<pre>
class MapsFragment : Fragment(), AutoCompleteClickInterface  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //.....
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as PlacesAutoCompleteFragment
        autocompleteFragment.initializeAutocompleteFragment(resources.getString(R.string.google_maps_key), this)
        autocompleteFragment.setCurrentLocation(latitude, longitude)
        .....///
    }
    override fun onClearSearchText() {
        //todo: clear ui 
    }
    override fun onItemClicked(placeData: PlaceData) {
        val selectedPlace = LatLng(placeData.latitude, placeData.longitude)
        //todo: use the selected place data
    }
}
</pre>
