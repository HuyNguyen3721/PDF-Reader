package com.ezstudio.pdfreaderver4

import android.app.Application
import com.ezstudio.pdfreaderver4.di.appModule
import com.ezteam.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzApplication
import office.file.ui.MyLibApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PdfApplication : MyLibApplication() {

    override fun onCreate() {
        super.onCreate()
        EzApplication()
        PreferencesUtils.init(this)
        setupKoin()
    }


    private fun setupKoin() {
        startKoin {
            androidContext(this@PdfApplication)
            modules(
                appModule
            )
        }
    }
}