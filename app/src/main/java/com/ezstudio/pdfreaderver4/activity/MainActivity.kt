package com.ezstudio.pdfreaderver4.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.databinding.ActivityMainBinding
import com.ezstudio.pdfreaderver4.fragment.FragmentAllDocument
import com.ezstudio.pdfreaderver4.utils.FileUtils
import com.ezstudio.pdfreaderver4.utils.KeyFile
import com.ezstudio.pdfreaderver4.viewmodel.FileViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.adapter.BasePagerAdapter
import com.ezteam.baseproject.dialog.rate.DialogRating
import com.ezteam.baseproject.dialog.rate.DialogRatingState
import com.ezteam.baseproject.utils.KeyboardUtils
import com.ezteam.baseproject.utils.ViewUtils
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val fileViewModel by inject<FileViewModel>()
    private val db by inject<AppDatabase>()
    private var isSortAZ = false
    private var isSortDate = false
    private var isSortSize = false
    var typeTab = KeyFile.PDF
    var typeNavigation = KeyFile.DOCUMENT

    // fragment
    private lateinit var fragmentAll: FragmentAllDocument
    private lateinit var fragmentPDF: FragmentAllDocument
    private lateinit var fragmentWORD: FragmentAllDocument
    private lateinit var fragmentEXCEL: FragmentAllDocument
    private lateinit var fragmentPPT: FragmentAllDocument
    override fun initView() {
        // pdf home
        tabHome()
//        fileViewModel.getAllFileFromDevice(this)
        // create fragment
        fragmentAll = FragmentAllDocument(KeyFile.ALL)
        fragmentPDF = FragmentAllDocument(KeyFile.PDF)
        fragmentWORD = FragmentAllDocument(KeyFile.WORD)
        fragmentEXCEL = FragmentAllDocument(KeyFile.EXCEL)
        fragmentPPT = FragmentAllDocument(KeyFile.PPT)
        // tap layout\
        supportFragmentManager.let {
            val adapter = BasePagerAdapter(it, 0).apply {
                addFragment(fragmentAll, getString(R.string.ALL))
                addFragment(fragmentPDF, getString(R.string.PDF))
                addFragment(fragmentWORD, getString(R.string.WORD))
                addFragment(fragmentEXCEL, getString(R.string.EXCEL))
                addFragment(fragmentPPT, getString(R.string.PPT))
            }
            binding.viewPager.offscreenPageLimit = 5
            binding.viewPager.adapter = adapter
            binding.viewPager.setCurrentItem(1, true)
            binding.tab.setupWithViewPager(binding.viewPager)
            binding.tab.getTabAt(0)?.icon?.setColorFilter(
                Color.parseColor("#FF9500"),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun initData() {
    }

    @SuppressLint("WrongConstant")
    override fun initListener() {
        // drawer
        binding.menu.setOnClickListener {
            binding.layoutDrawer.openDrawer(Gravity.START)
        }
        // navigation bar
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_documents -> {
                    item.setIcon(R.drawable.ic_document_selected)
                    binding.navigation.menu.getItem(1).setIcon(R.drawable.ic_recent)
                    binding.navigation.menu.getItem(2).setIcon(R.drawable.ic_bookmark)
                    //candleSearch
                    candleSearch()
                    //load data
                    fileViewModel.listAllFileModelLiveData.value =
                        fileViewModel.listLoadedAllFileModelLiveData.value
                    fileViewModel.listPDFFileModelLiveData.value =
                        fileViewModel.listLoadedPDFFileModelLiveData.value
                    fileViewModel.listWordFileModelLiveData.value =
                        fileViewModel.listLoadedWordFileModelLiveData.value
                    fileViewModel.listExcelFileModelLiveData.value =
                        fileViewModel.listLoadedExcelFileModelLiveData.value
                    fileViewModel.listPPTFileModelLiveData.value =
                        fileViewModel.listLoadedPPTFileModelLiveData.value

                    //
                    typeNavigation = KeyFile.DOCUMENT
                }
                R.id.navigation_recent -> {
                    item.setIcon(R.drawable.ic_recent_selected)
                    binding.navigation.menu.getItem(0).setIcon(R.drawable.ic_document)
                    binding.navigation.menu.getItem(2).setIcon(R.drawable.ic_bookmark)
                    //candleSearch
                    candleSearch()
                    //load data
                    fileViewModel.getAllFileRecent(db, KeyFile.ALL)
                    fileViewModel.getAllFileRecent(db, KeyFile.PDF)
                    fileViewModel.getAllFileRecent(db, KeyFile.WORD)
                    fileViewModel.getAllFileRecent(db, KeyFile.EXCEL)
                    fileViewModel.getAllFileRecent(db, KeyFile.PPT)
                    //
                    typeNavigation = KeyFile.RECENT
                }
                R.id.navigation_bookmarks -> {
                    item.setIcon(R.drawable.ic_bookmark_selected)
                    binding.navigation.menu.getItem(0).setIcon(R.drawable.ic_document)
                    binding.navigation.menu.getItem(1).setIcon(R.drawable.ic_recent)
                    //candleSearch
                    candleSearch()
                    //load data
                    fileViewModel.getFileFavorite(db, KeyFile.ALL)
                    fileViewModel.getFileFavorite(db, KeyFile.PDF)
                    fileViewModel.getFileFavorite(db, KeyFile.WORD)
                    fileViewModel.getFileFavorite(db, KeyFile.EXCEL)
                    fileViewModel.getFileFavorite(db, KeyFile.PPT)
                    //
                    typeNavigation = KeyFile.BOOKMARK
                }
            }
            true
        }
        //tab layout
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { tab ->
                    when (tab.text) {
                        getString(R.string.ALL) -> {
                            //candleSearch
                            candleSearch()
                            setBackgroundStatusBar(Color.parseColor("#FFFFFF"), true)
                            setColorLayoutTitle(
                                Color.parseColor("#FFFFFF"),
                                Color.parseColor("#F6F6F6")
                            )
                            setTabTextColors(true)
                            setIconTint(true)
                            //
                            typeTab = KeyFile.ALL
                        }
                        getString(R.string.PDF) -> {
                            //candleSearch
                            candleSearch()

                            setBackgroundStatusBar(Color.parseColor("#BF1925"))
                            setColorLayoutTitle(
                                Color.parseColor("#BF1925"),
                                Color.parseColor("#B81824")
                            )
                            setTabTextColors()
                            setIconTint()
                            typeTab = KeyFile.PDF
                        }
                        getString(R.string.WORD) -> {
                            //candleSearch
                            candleSearch()
                            setBackgroundStatusBar(Color.parseColor("#2F80ED"))
                            setColorLayoutTitle(
                                Color.parseColor("#2F80ED"),
                                Color.parseColor("#2E7BE4")
                            )
                            setTabTextColors()
                            setIconTint()
                            typeTab = KeyFile.WORD
                        }
                        getString(R.string.EXCEL) -> {
                            //candleSearch
                            candleSearch()
                            setBackgroundStatusBar(Color.parseColor("#27AE60"))
                            setColorLayoutTitle(
                                Color.parseColor("#27AE60"),
                                Color.parseColor("#26A85D")
                            )
                            setTabTextColors()
                            setIconTint()
                            typeTab = KeyFile.EXCEL
                        }
                        getString(R.string.PPT) -> {
                            //candleSearch
                            candleSearch()
                            setBackgroundStatusBar(Color.parseColor("#F47809"))
                            setColorLayoutTitle(
                                Color.parseColor("#F47809"),
                                Color.parseColor("#EA740B")
                            )
                            setTabTextColors()
                            setIconTint()
                            typeTab = KeyFile.PPT
                        }
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // filter
        popupMenu()

        // search
        binding.layoutSearch.setOnClickListener {
            if (!aVoidDoubleClick()) {
//                listSearchFile.clear()
//                listSearchFile.addAll(listAllFile)
//                checkSearchFileEmpty(listSearchFile)
                ViewUtils.hideView(true, binding.layoutTitleSearch, 350)
                ViewUtils.showView(true, binding.layoutSearching, 350)
                binding.search.requestFocus()
                binding.search.setText("")
                Handler().postDelayed({
                    KeyboardUtils.showSoftKeyboard(this)
                }, 300)
            }
        }
        // candle search
        binding.icCandle.setOnClickListener {
            candleSearch()
        }
        //
        // searching
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                fileViewModel.txtSearchLiveData.value = s.toString()
            }
        })
        // listener drawer
        //home
        binding.layoutHome.setOnClickListener {
            binding.layoutDrawer.closeDrawer(Gravity.START)
            Handler().postDelayed({
                //            tabHome()
                binding.viewPager.setCurrentItem(1, true)
                binding.navigation.menu.getItem(0).isChecked = true
                binding.navigation.menu.getItem(0).setIcon(R.drawable.ic_document_selected)
                binding.navigation.menu.getItem(1).setIcon(R.drawable.ic_recent)
                binding.navigation.menu.getItem(2).setIcon(R.drawable.ic_bookmark)
                //candleSearch
                candleSearch()
                //load data
                fileViewModel.getAllFileFromDevice(this, db)
                fileViewModel.getAllFilePDF(this, db)
                fileViewModel.getAllFileWord(this, db)
                fileViewModel.getAllFileSheet(this, db)
                fileViewModel.getAllFileSlide(this, db)
                //
                typeNavigation = KeyFile.DOCUMENT
            }, 300)
        }
        // share
        binding.layoutShare.setOnClickListener {
            binding.layoutDrawer.closeDrawer(Gravity.START)
            shareAppUrl(this, packageName)
        }
        // rate us
        binding.layoutRateUs.setOnClickListener {
            binding.layoutDrawer.closeDrawer(Gravity.START)
            rateUs()
        }

        //more app
        binding.layoutMoreApp.setOnClickListener {
            binding.layoutDrawer.closeDrawer(Gravity.START)
            openMoreApp()
        }
    }

    override fun viewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    private fun shareAppUrl(context: Context, packageUrl: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out the App at: https://play.google.com/store/apps/details?id=$packageUrl"
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    private fun openMoreApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("https://play.google.com/store/apps/developer?id=EZ+MOBI+CO.,+LTD")
        startActivity(intent)
    }

    private fun rateUs() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data =
            Uri.parse("market://details?id=${packageName}")
        startActivity(intent)
    }

    private fun popupMenu() {
        val popMenu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PopupMenu(this, binding.icFilter, Gravity.NO_GRAVITY, 0, R.style.Widget_App_PopupMenu)
        } else {
            PopupMenu(this, binding.icFilter)
        }
//        val popMenu = PopupMenu(this, binding.icFilter)
        popMenu.apply {
            inflate(R.menu.menu_filter)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sort_name -> {
                        if (!isSortAZ) {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_NAME_A_Z
                        } else {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_NAME_Z_A
                        }
                        isSortAZ = !isSortAZ
                    }
                    R.id.sort_date -> {
                        if (!isSortDate) {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_DATE_1_9
                        } else {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_DATE_9_1
                        }
                        isSortDate = !isSortDate
                    }
                    R.id.sort_size -> {
                        if (!isSortSize) {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_SIZE_1_9
                        } else {
                            fileViewModel.typeFilterLiveData.value = FileUtils.SORT_SIZE_9_1
                        }
                        isSortSize = !isSortSize
                    }
                }
                true
            }

//            val itemSetAs: Menu = popMenu.menu
//            val headerTitle1 = SpannableString(itemSetAs.findItem(R.id.sort_name).title)
//            val headerTitle2 = SpannableString(itemSetAs.findItem(R.id.sort_date).title)
//            val headerTitle3 = SpannableString(itemSetAs.findItem(R.id.sort_size).title)
//            // Change the color:
//            headerTitle1.setSpan(ForegroundColorSpan(Color.BLACK), 0, headerTitle1.length, 0)
//            headerTitle2.setSpan(ForegroundColorSpan(Color.BLACK), 0, headerTitle2.length, 0)
//            headerTitle3.setSpan(ForegroundColorSpan(Color.BLACK), 0, headerTitle3.length, 0)
//            //set title new color
//            itemSetAs.getItem(0).title = headerTitle1
//            itemSetAs.getItem(1).title = headerTitle2
//            itemSetAs.getItem(2).title = headerTitle3
            //
        }
        //
        binding.icFilter.setOnClickListener {
            try {
                val pop = PopupMenu::class.java.getDeclaredField("mPopup")
                pop.isAccessible = true
                val menu = pop.get(popMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popMenu.show()
            }

        }

    }


//
//    private fun replaceFragment(
//        fragment: BaseFragment<*>,
//        tag: String, isAdd: Boolean = false
//    ) {
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.apply {
//            setCustomAnimations(
//                R.anim.pull_in_right,
//                R.anim.push_out_left,
//                R.anim.pull_in_left,
//                R.anim.push_out_right
//            )
//            if (isAdd) {
//                add(R.id.fragment_navigation, fragment, tag)
//            } else {
//                replace(R.id.fragment_navigation, fragment, tag)
//            }
//            addToBackStack(tag)
//            commit()
//        }
//    }

    private fun setBackgroundStatusBar(color: Int, isAll: Boolean = false) {
        window.statusBarColor = color
        if (Build.VERSION.SDK_INT >= 23) {
            if (isAll) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    private fun setColorLayoutTitle(color: Int, colorbgEdit: Int) {
        binding.layoutTitle.setBackgroundColor(color)
        binding.tab.setBackgroundColor(color)
        changeColorShapeBg(colorbgEdit)
    }

    private fun setTabTextColors(isAll: Boolean = false) {
        if (isAll) {
            val color = Color.parseColor("#949494")
            val colorSelect = Color.parseColor("#262626")
            binding.tab.setTabTextColors(color, colorSelect)
        } else {
            val color = Color.parseColor("#ADECECEC")
            val colorSelect = Color.parseColor("#FFFFFF")
            binding.tab.setTabTextColors(color, colorSelect)
        }
    }

    private fun setIconTint(isAll: Boolean = false) {
        if (isAll) {
            val color = Color.parseColor("#262626")
            val colorIndicator = Color.parseColor("#BF1925")
            val colorSearch = Color.parseColor("#33262626")
            val colorTxtHide = Color.parseColor("#949494")
            binding.menu.setColorFilter(color)
            binding.icSearch.setColorFilter(color)
            binding.txtSearch.setTextColor(colorSearch)
            binding.txtPdf.setTextColor(colorSearch)
            binding.icFilter.setColorFilter(color)
            binding.tab.setSelectedTabIndicatorColor(colorIndicator)
            binding.icCandle.setColorFilter(color)
            binding.search.setTextColor(color)
            binding.search.setHintTextColor(colorTxtHide)
            binding.search.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
        } else {
            val color = Color.parseColor("#FFFFFF")
            val colorTxtHide = Color.parseColor("#80FFFFFF")
            binding.menu.setColorFilter(color)
            binding.icSearch.setColorFilter(color)
            binding.txtSearch.setTextColor(colorTxtHide)
            binding.txtPdf.setTextColor(colorTxtHide)
            binding.icFilter.setColorFilter(color)
            binding.tab.setSelectedTabIndicatorColor(color)
            binding.icCandle.setColorFilter(color)
            binding.search.setTextColor(color)
            binding.search.setHintTextColor(colorTxtHide)
            binding.search.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_search_white,
                0
            )
        }
    }

    private fun candleSearch() {
        if (binding.layoutSearching.isVisible) {
            ViewUtils.hideView(true, binding.layoutSearching, 350)
            ViewUtils.showView(true, binding.layoutTitleSearch, 350)
            Handler().postDelayed({
                KeyboardUtils.hideSoftKeyboard(this)
            }, 300)
            fileViewModel.txtSearchLiveData.value = ""
        }
    }

    private fun changeColorShapeBg(color: Int) {
        val bgShape = binding.search.background as GradientDrawable
        bgShape.mutate()
        bgShape.setColor(color)
    }

    private fun tabHome() {
        setBackgroundStatusBar(Color.parseColor("#BF1925"))
        setColorLayoutTitle(
            Color.parseColor("#BF1925"),
            Color.parseColor("#B81824")
        )
        setTabTextColors()
        setIconTint()
    }

    override fun onBackPressed() {
        showAppRating(true) {
            finishAffinity()
        }
    }

    private fun showAppRating(isHardShow: Boolean, complete: (Boolean) -> Unit) {
        DialogRating.ExtendBuilder(this)
            .setHardShow(isHardShow)
            .setListener { status ->
                when (status) {
                    DialogRatingState.RATE_BAD -> {
                        toast(resources.getString(R.string.thank_for_rate))
                        complete(false)
                    }
                    DialogRatingState.RATE_GOOD -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("market://details?id=$packageName")
                        startActivity(intent)
                        complete(true)
                    }
                    DialogRatingState.COUNT_TIME -> complete(false)
                }
            }
            .build()
            .show()
    }

}