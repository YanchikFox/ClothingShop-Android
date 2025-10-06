package com.shop.app

import android.app.Application
import com.shop.app.di.AppContainer
import com.shop.app.di.DefaultAppContainer

class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}