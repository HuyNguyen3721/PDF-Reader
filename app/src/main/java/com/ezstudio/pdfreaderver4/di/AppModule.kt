package com.ezstudio.pdfreaderver4.di

import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.viewmodel.FileViewModel
import com.ezstudio.pdfreaderver4.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single { FileViewModel(androidApplication()) }
    single { MainViewModel(androidApplication()) }
//    single { FileManagerViewModel(androidApplication()) }
}