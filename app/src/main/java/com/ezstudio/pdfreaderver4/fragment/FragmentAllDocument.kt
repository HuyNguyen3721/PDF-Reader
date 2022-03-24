package com.ezstudio.pdfreaderver4.fragment

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.activity.MainActivity
import com.ezstudio.pdfreaderver4.adapter.FileAdapter
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.databinding.LayoutFragmentAllDocumentBinding
import com.ezstudio.pdfreaderver4.model.FileModel
import com.ezstudio.pdfreaderver4.utils.DialogLoadingUtils
import com.ezstudio.pdfreaderver4.utils.FileUtils
import com.ezstudio.pdfreaderver4.utils.FileUtils.favoriteFile
import com.ezstudio.pdfreaderver4.utils.FileUtils.infoFile
import com.ezstudio.pdfreaderver4.utils.FileUtils.reNameFIle
import com.ezstudio.pdfreaderver4.utils.FileUtils.recentFile
import com.ezstudio.pdfreaderver4.utils.FileUtils.shareFile
import com.ezstudio.pdfreaderver4.utils.FileUtils.showDeleteFile
import com.ezstudio.pdfreaderver4.utils.KeyFile
import com.ezstudio.pdfreaderver4.utils.RecycleViewUtils
import com.ezstudio.pdfreaderver4.viewmodel.FileViewModel
import com.ezstudio.pdfreaderver4.viewmodel.MainViewModel
import com.ezteam.baseproject.extensions.getFileSize
import com.ezteam.baseproject.fragment.BaseFragment
import com.google.android.gms.ads.ez.EzAdControl
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAllDocument(private val keyFile: String) :
    BaseFragment<LayoutFragmentAllDocumentBinding>(), SwipeRefreshLayout.OnRefreshListener {
    private var listAllFile: MutableList<FileModel> = mutableListOf()
    private var listSearchFile: MutableList<FileModel> = mutableListOf()
    private var fileAdapter: FileAdapter? = null
    private val db by inject<AppDatabase>()
    private val mainViewModel by inject<MainViewModel>()
    private val fileViewModel by inject<FileViewModel>()
    private var nativeAds: RelativeLayout? = null

    override fun initView() {

        fileAdapter = FileAdapter(requireContext(), listAllFile)
        binding.rclFile.adapter = fileAdapter
        //clear anim recycle
        RecycleViewUtils.clearAnimation(binding.rclFile)
        //
        binding.swipeRefreshLayout.setColorSchemeColors(
            when (keyFile) {
                KeyFile.ALL -> {
                    resources.getColor(R.color.color_262626)
                }
                KeyFile.PDF -> {
                    resources.getColor(R.color.color_DA0A19)
                }
                KeyFile.WORD -> {
                    resources.getColor(R.color.color_2F80ED)
                }
                KeyFile.EXCEL -> {
                    resources.getColor(R.color.color_27AE60)
                }
                KeyFile.PPT -> {
                    resources.getColor(R.color.color_F47809)
                }
                else -> {
                    resources.getColor(R.color.color_262626)
                }
            }
        )
        //load ads
        loadAds()
    }

    override fun initData() {
        //data
        when (keyFile) {
            KeyFile.ALL -> {
                fileViewModel.listAllFileModelLiveData.observe(this) {
                    setUpList(it)
                }
//                fileViewModel.getAllFileFromDevice(requireContext())
            }
            KeyFile.PDF -> {
                fileViewModel.listPDFFileModelLiveData.observe(this) {
                    setUpList(it)
                }
//                fileViewModel.getAllFilePDF(requireContext())
            }
            KeyFile.WORD -> {
                fileViewModel.listWordFileModelLiveData.observe(this) {
                    setUpList(it)
                }
//                fileViewModel.getAllFileWord(requireContext())
            }
            KeyFile.EXCEL -> {
                fileViewModel.listExcelFileModelLiveData.observe(this) {
                    setUpList(it)
                }
//                fileViewModel.getAllFileSheet(requireContext())
            }
            KeyFile.PPT -> {
                fileViewModel.listPPTFileModelLiveData.observe(this) {
                    setUpList(it)
                }
//                fileViewModel.getAllFileSlide(requireContext())
            }
        }
        // live Data search

        fileViewModel.txtSearchLiveData.observe(requireActivity()) {
            it?.let {
                if (keyFile == (requireActivity() as MainActivity).typeTab) {
                    listAllFile.clear()
                    if (it.isNotEmpty()) {
                        for (item in listSearchFile) {
                            item.name?.let { name ->
                                if (name.contains(it, true)) {
                                    listAllFile.add(item)
                                }
                            }
                        }
                        Log.d("Huy", "listAllFile size : ${listAllFile.size} ")
                        fileAdapter?.notifyDataSetChanged()
                        if (listAllFile.isEmpty()) {
                            binding.txtNoResult.visibility = View.VISIBLE
                        } else {
                            binding.txtNoResult.visibility = View.INVISIBLE
                        }
                    } else {
                        binding.txtNoResult.visibility = View.INVISIBLE
                        listAllFile.addAll(listSearchFile)
                        fileAdapter?.notifyDataSetChanged()
                        //
                        addAdsToList(nativeAds)
                    }
                }
            }
        }
    }

    private fun loadAds() {
        AdmobNativeAdView.getNativeAd(
            requireContext(),
            R.layout.native_admob_item_file,
            object : NativeAdListener() {
                override fun onError() {
                }

                override fun onLoaded(nativeAd: RelativeLayout?) {
                    addAdsToList(nativeAd)
                    nativeAds = nativeAd
                }

                override fun onClickAd() {
                }
            })
    }

    private fun addAdsToList(nativeAd: RelativeLayout?) {
        val fileModel = FileModel()
        fileModel.ads = nativeAd
        if (listAllFile.size >= 2) {
            listAllFile.add(2, fileModel)
            listSearchFile.add(2, fileModel)
            fileAdapter?.notifyItemInserted(2)
        }
    }

    private fun setUpList(list: MutableList<FileModel>?) {
        list?.let {
            binding.viewEmpty.visibility = View.INVISIBLE
            if (it.isNullOrEmpty()) {
                listAllFile.clear()
                listSearchFile.clear()
                listAllFile.addAll(it)
                listSearchFile.addAll(it)
                fileAdapter?.notifyDataSetChanged()
                binding.layoutEmptyFile.visibility = View.VISIBLE
            } else {
                binding.layoutEmptyFile.visibility = View.INVISIBLE
                it.let { list ->
                    listAllFile.clear()
                    listSearchFile.clear()
                    listAllFile.addAll(it)
                    listSearchFile.addAll(it)
                    fileAdapter?.notifyDataSetChanged()
                    sortList()
                    // load ads
                    nativeAds?.let {
                        addAdsToList(nativeAds)
                    }
                }
            }
        }
    }

    override fun initListener() {
        // fresh data
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        // adapter
        fileAdapter?.listenerClickItem = {
            if (it >= 0) {
                if (!aVoidDoubleClick()) {
                    FileUtils.openFileDetail(
                        requireActivity(),
                        listAllFile[it],
                        mainViewModel,
                        db
                    )
                }
            }
        }
        fileAdapter?.listenerClickFavorite = {
            // check file book mark
            if ((requireActivity() as MainActivity).typeNavigation == KeyFile.BOOKMARK) {
                listAllFile.removeAt(it)
                listSearchFile.removeAt(it)
                fileAdapter?.notifyItemRemoved(it)
                checkEmpty()
            } else {
                listAllFile[it].isFavorite =
                    !listAllFile[it].isFavorite
                fileAdapter?.notifyItemChanged(it)
            }
            // change db
            favoriteFile(listAllFile[it], mainViewModel, db) { b ->
            }
        }
        // book mark
        fileAdapter?.listenerAddBookmark = {

            listAllFile[it].isFavorite =
                !listAllFile[it].isFavorite
            fileAdapter?.notifyItemChanged(it)
            //change db
            favoriteFile(listAllFile[it], mainViewModel, db) { b ->
            }
        }
        fileAdapter?.listenerRemoveBookmark = {
            if ((requireActivity() as MainActivity).typeNavigation == KeyFile.BOOKMARK) {
                listAllFile.removeAt(it)
                listSearchFile.removeAt(it)
                fileAdapter?.notifyItemRemoved(it)
                checkEmpty()
            } else {
                listAllFile[it].isFavorite =
                    !listAllFile[it].isFavorite
                fileAdapter?.notifyItemChanged(it)
            }
            //change db
            favoriteFile(listAllFile[it], mainViewModel, db) { b ->
            }
        }
        // share
        fileAdapter?.listenerShare = {
            shareFile(requireContext(), listAllFile[it])
        }
        //rename
        fileAdapter?.listenerRename = {
            val data = listAllFile[it]
            val fileDb = db.serverDao().getFileById(data.path)
            reNameFIle(requireActivity(), data) { newName, newPath ->
                data.path = newPath
                data.name = newName
                //update file in db
                if (fileDb != null) {
                    fileDb.path = newPath
                    fileDb.name = newName
                    db.serverDao().update(fileDb)
                }
                // update rcl
                requireActivity().runOnUiThread {
                    fileAdapter?.notifyItemChanged(it)
                    //show ads
                    EzAdControl.getInstance(requireActivity()).showAds()
                }
            }
        }
        //delete
        fileAdapter?.listenerDelete = {
            showDeleteFile(
                listAllFile[it],
                resources.getString(R.string.do_you_want_to_delete_file),
                requireContext()
            ) { touch ->
                if (touch) {
                    requireActivity().runOnUiThread {
                        DialogLoadingUtils.showDialogHiding(requireContext(), false)
                        listAllFile.removeAt(it)
                        listSearchFile.removeAt(it)
                        fileAdapter?.notifyItemRemoved(it)
                        checkEmpty()
                        //show ads
                        EzAdControl.getInstance(requireActivity()).showAds()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        DialogLoadingUtils.showDialogHiding(requireContext(), false)
                        toast(getString(R.string.delete_failed))
                    }
                }
            }
        }
        //info
        fileAdapter?.listenerInfo = {
            infoFile(requireContext(), listAllFile[it])
        }
        // remove recent
        fileAdapter?.listenerRemoveRecent = {
            recentFile(listAllFile[it], mainViewModel, db) { b ->
                listAllFile.removeAt(it)
                listSearchFile.removeAt(it)
                fileAdapter?.notifyItemRemoved(it)
                checkEmpty()
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentAllDocumentBinding {
        return LayoutFragmentAllDocumentBinding.inflate(LayoutInflater.from(requireActivity()))
    }

    private fun checkEmpty() {
        if (listAllFile.isNullOrEmpty()) {
            binding.layoutEmptyFile.visibility = View.VISIBLE
        } else {
            binding.txtNoResult.visibility = View.INVISIBLE
            binding.layoutEmptyFile.visibility = View.INVISIBLE
        }
    }

    private fun sortList() {
        // sort
        fileViewModel.typeFilterLiveData.observe(requireActivity()) { type ->
            when (type) {
                FileUtils.SORT_NAME_A_Z -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortBy { it.name }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                FileUtils.SORT_NAME_Z_A -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortByDescending { it.name }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                FileUtils.SORT_DATE_1_9 -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortBy { it.date }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                FileUtils.SORT_DATE_9_1 -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortByDescending { it.date }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                FileUtils.SORT_SIZE_1_9 -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortBy { File(it.path).getFileSize() }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                FileUtils.SORT_SIZE_9_1 -> {
                    if (listAllFile.size > 2) {
                        listAllFile.sortByDescending { File(it.path).getFileSize() }
                        (binding.rclFile.adapter)?.notifyDataSetChanged()
                    }
                }
                else -> {
                    (binding.rclFile.adapter)?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onRefresh() {
        loadByKeyType()
        Handler().postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    private fun loadByKeyType() {
        val activity = requireActivity() as MainActivity
        when (val type = activity.typeTab) {
            KeyFile.ALL -> {
                loadDataByTabNavigation(type)
            }
            KeyFile.PDF -> {
                loadDataByTabNavigation(type)
            }
            KeyFile.WORD -> {
                loadDataByTabNavigation(type)
            }
            KeyFile.EXCEL -> {
                loadDataByTabNavigation(type)
            }
            KeyFile.PPT -> {
                loadDataByTabNavigation(type)
            }
        }
    }

    private fun loadDataByTabNavigation(type: String) {
        val activity = requireActivity() as MainActivity
        when (activity.typeNavigation) {
            KeyFile.DOCUMENT -> {
                fileViewModel.getAllFileFromDevice(requireContext(), db)
            }
            KeyFile.RECENT -> {
                fileViewModel.getAllFileRecent(db, type)
            }
            KeyFile.BOOKMARK -> {
                fileViewModel.getFileFavorite(db, type)
            }
        }
    }
}