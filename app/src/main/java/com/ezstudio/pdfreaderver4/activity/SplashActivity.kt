package com.ezstudio.pdfreaderver4.activity

import android.os.Handler
import android.view.LayoutInflater
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.databinding.ActivitySplashBinding
import com.ezstudio.pdfreaderver4.viewmodel.FileViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.launchActivity
import com.google.android.gms.ads.ez.AdFactoryListener
import com.google.android.gms.ads.ez.LogUtils
import com.google.android.gms.ads.ez.admob.AdmobOpenAdUtils
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val fileViewModel by inject<FileViewModel>()
    private val db by inject<AppDatabase>()

    override fun initView() {
        loadAds()
    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    //
    private fun loadAds() {
        AdmobOpenAdUtils.getInstance(this).setAdListener(object : AdFactoryListener() {
            override fun onError() {
                LogUtils.logString(SplashActivity::class.java, "onError")
                openMain()
            }

            override fun onLoaded() {
                LogUtils.logString(SplashActivity::class.java, "onLoaded")
                // show ads ngay khi loaded
                AdmobOpenAdUtils.getInstance(this@SplashActivity).showAdIfAvailable(false)
            }

            override fun onDisplay() {
                super.onDisplay()
                LogUtils.logString(SplashActivity::class.java, "onDisplay")
            }

            override fun onDisplayFaild() {
                super.onDisplayFaild()
                LogUtils.logString(SplashActivity::class.java, "onDisplayFaild")
                openMain()
            }

            override fun onClosed() {
                super.onClosed()
                // tam thoi bo viec load lai ads thi dismis
                LogUtils.logString(SplashActivity::class.java, "onClosed")
                openMain()
            }
        }).loadAd()
    }

    private fun openMain() {
        Handler().postDelayed({
            //load data
            fileViewModel.getAllFileFromDevice(this, db)
            fileViewModel.getAllFilePDF(this, db)
            fileViewModel.getAllFileWord(this, db)
            fileViewModel.getAllFileSheet(this, db)
            fileViewModel.getAllFileSlide(this, db)
            launchActivity<MainActivity> { }
            finish()
        }, 200)
    }

//    private fun checkPermission(isPermission: (Boolean) -> Unit) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                isPermission.invoke(true)
//            } else {
//                isPermission.invoke(false)
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                isPermission.invoke(true)
//            } else {
//                isPermission.invoke(false)
//            }
//        } else {
//            isPermission.invoke(true)
//        }
//    }
}