package com.pavel.openweather.di

import com.pavel.openweather.ui.WeatherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): WeatherFragment

}
