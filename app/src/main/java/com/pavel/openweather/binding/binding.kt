package com.pavel.openweather.binding

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.openweather.R
import com.pavel.openweather.apiObjects.WeatherX

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, uri: List<WeatherX>?) {
    view.loadImage(uri, getProgressDrawable(view.context))
}

@SuppressLint("NewApi")
fun getProgressDrawable(contex: Context): CircularProgressDrawable {

    return CircularProgressDrawable(contex).apply {
        strokeWidth = 6f
        centerRadius = 50f
        backgroundColor = contex.getColor(R.color.colorAccent)
        start()
    }
}

fun ImageView.loadImage(image: List<WeatherX>?, progressDrawable: CircularProgressDrawable) {
    var iconWeather: String = ""
    image?.forEach {
        iconWeather = it.icon
    }

    Glide.with(context)
        .load("http://openweathermap.org/img/wn/$iconWeather@2x.png")
        .into(this)
}

@BindingAdapter("android:loadDescritpion")
fun loadDescription(view: TextView, text: List<WeatherX>?) {
    view.setDesctiption(text)
}

fun TextView.setDesctiption(textString: List<WeatherX>?) {
    var text_final: String = ""
    textString?.forEach {
        text_final = it.description
    }
    text = text_final
}

@BindingAdapter("android:loadTemp")
fun loadTemp(view: TextView, temp: Double?) {
    view.setTemp(temp)
}

fun TextView.setTemp(temp: Double?) {
    val getText: String = temp.toString()
    if (getText == "0.0") {
        text = ""
    }else {
        text = "temp: $getText°C"
    }
}

@BindingAdapter("android:loadHumidity")
fun loadHumidity(view: TextView,humidity:Int){
    view.setHumidity(humidity)
}

fun TextView.setHumidity(temp: Int?) {
    val getText: String = temp.toString()
    if (getText == "0") {
        text = ""
    }else {
        text = "humidity: $getText"
    }
}

@BindingAdapter("android:loadPressure")
fun loadPressure(view: TextView,pressure:Int){
    view.setPressure(pressure)
}

fun TextView.setPressure(pressure:Int){
    val getText: String = pressure.toString()
    if (getText == "0"){
        text = ""
    }else {
        text = "pressure: $getText"
    }
}