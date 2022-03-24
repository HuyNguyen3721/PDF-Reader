package com.ezstudio.pdfreaderver4.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.databinding.ActivityPrivacyPolocyBinding
import com.ezstudio.pdfreaderver4.utils.KeyFile.POPLICY
import com.ezstudio.pdfreaderver4.viewmodel.FileViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.launchActivity
import com.ezteam.baseproject.utils.PreferencesUtils
import org.koin.android.ext.android.inject

class PrivacyPolicy : BaseActivity<ActivityPrivacyPolocyBinding>() {
    private val fileViewModel by inject<FileViewModel>()
    private val db by inject<AppDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferencesUtils.getBoolean(POPLICY, false) && isAcceptManagerStorage()) {
            launchActivity<SplashActivity> { }
        } else {
            super.onCreate(savedInstanceState)
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initListener() {
        binding.agree.setOnClickListener {
            requestPermission()
        }
    }

    override fun viewBinding(): ActivityPrivacyPolocyBinding {
        return ActivityPrivacyPolocyBinding.inflate(LayoutInflater.from(this))
    }

    private fun requestPermission() {
        requestPermissionStorage {
            if (it) {
//                loadData()
                //
                launchActivity<SplashActivity> { }
                //
                PreferencesUtils.putBoolean(POPLICY, true)
            } else {
                Toast.makeText(this, getString(R.string.allow_permission), Toast.LENGTH_SHORT)
                    .show()
                finishAffinity()
            }
        }
    }

    private fun loadData() {
        fileViewModel.getAllFileFromDevice(this, db)
        fileViewModel.getAllFilePDF(this, db)
        fileViewModel.getAllFileWord(this, db)
        fileViewModel.getAllFileSheet(this, db)
        fileViewModel.getAllFileSlide(this, db)
    }
}