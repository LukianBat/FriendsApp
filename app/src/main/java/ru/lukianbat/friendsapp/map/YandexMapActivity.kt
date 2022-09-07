package ru.lukianbat.friendsapp.map

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import ru.lukianbat.friendsapp.R
import com.yandex.maps.mobile.R as YandexMapR

class YandexMapActivity : AppCompatActivity() {

    companion object {
        const val CITY_PARAM = "CITY"
    }

    private var mapView: MapView? = null
    private var progressBar: ProgressBar? = null
    private val searchManagerSubmitCallback = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            response.collection.children.firstOrNull()?.let { searchResult ->
                val resultLocationPoint = searchResult.obj?.geometry?.get(0)?.point

                resultLocationPoint?.let {
                    mapView?.map?.mapObjects?.addPlacemark(
                        resultLocationPoint,
                        ImageProvider.fromResource(
                            this@YandexMapActivity,
                            YandexMapR.drawable.search_layer_pin_icon_default,
                        )
                    )
                    mapView?.map?.move(
                        CameraPosition(
                            resultLocationPoint,
                            3F,
                            0F,
                            0F,
                        )
                    )
                }
            }
        }

        override fun onSearchError(searchError: Error) {
            progressBar?.isVisible = false
            Toast.makeText(
                this@YandexMapActivity,
                R.string.message_unknown_error,
                Toast.LENGTH_LONG,
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yandex_map)

        val cityNameParam = intent.getStringExtra(CITY_PARAM)

        initMapKit()
        setupViews()
        if (cityNameParam != null) {
            startSearch(cityNameParam)
        }
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    private fun startSearch(cityName: String) {
        SearchFactory.getInstance()
            .createSearchManager(SearchManagerType.COMBINED)
            .submit(
                cityName,
                Geometry.fromPoint(Point(0.0, 0.0)),
                SearchOptions(
                    SearchType.GEO.value,
                    1,
                    null,
                    null,
                    false,
                    false,
                    null,
                ),
                searchManagerSubmitCallback
            )
    }

    private fun initMapKit() {
        MapKitFactory.initialize(this)
    }

    private fun setupViews() {
        mapView = findViewById(R.id.mapView)
        progressBar = findViewById(R.id.progressBar)
    }
}
