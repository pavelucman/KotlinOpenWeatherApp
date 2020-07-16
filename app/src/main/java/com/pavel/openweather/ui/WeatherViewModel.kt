package com.pavel.openweather.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pavel.openweather.api.WeatherApiService
import com.pavel.openweather.apiObjects.Weather
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherViewModel @Inject constructor(private val weatherApiService: WeatherApiService) : ViewModel() {

    val weatherLiveData by lazy { MutableLiveData<Weather>() }
    val loadError by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    private val disposable = CompositeDisposable()

    private val API_KEY = "d9db413b4aa011de300a258576be9988"

    fun showDataByLocation(location: String?) {
        loading.value = true
        getWeatherByLocation(location)
    }

    private fun getWeatherByLocation(location: String?) {
        disposable.add(
            weatherApiService.getWeatherCity(location!!, "metric", API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Weather>() {
                    override fun onSuccess(weather: Weather) {

                        Log.d("getData", weather.toString())
                        loadError.value = false
                        weatherLiveData.value = weather
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        loadError.value = true
                        weatherLiveData.value = null
                    }
                })
        )
    }

    fun showDataByGeoLocation(lat: String?, lon: String?) {
        loading.value = true
        showWeatherGeoLocation(lat, lon)
    }


    private fun showWeatherGeoLocation(lat: String?, lon: String?) {
        disposable.addAll(
            weatherApiService.getWeatherGeo(lat!!, lon!!, "metric", API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Weather>() {
                    override fun onSuccess(weather: Weather) {
                        Log.d("getData", weather.toString())
                        loadError.value = false
                        weatherLiveData.value = weather
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        loadError.value = true
                        weatherLiveData.value = null
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}