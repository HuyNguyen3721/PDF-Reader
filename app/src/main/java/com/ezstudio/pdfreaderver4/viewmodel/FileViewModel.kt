package com.ezstudio.pdfreaderver4.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.model.FileModel
import com.ezstudio.pdfreaderver4.utils.FileUtils
import com.ezstudio.pdfreaderver4.utils.KeyFile
import com.ezteam.baseproject.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FileViewModel(application: Application) : BaseViewModel(application) {
    var listAllFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listPDFFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listWordFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listExcelFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listPPTFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()

    //data loaded
    var listLoadedAllFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listLoadedPDFFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()
    var listLoadedWordFileModelLiveData: MutableLiveData<MutableList<FileModel>?> =
        MutableLiveData()
    var listLoadedExcelFileModelLiveData: MutableLiveData<MutableList<FileModel>?> =
        MutableLiveData()
    var listLoadedPPTFileModelLiveData: MutableLiveData<MutableList<FileModel>?> = MutableLiveData()

    var txtSearchLiveData: MutableLiveData<String?> = MutableLiveData()

    // filter
    var typeFilterLiveData: MutableLiveData<String?> = MutableLiveData()

    fun getAllFileFromDevice(context: Context, db: AppDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentsUri = MediaStore.Files.getContentUri("external")
            val docsProjection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            queryFilesFromDevice(
                documentsUri,
                docsProjection,
                FileUtils.getQuery(FileUtils.ALL),
                context, KeyFile.ALL, db
            )
        }
    }

    fun getAllFileRecent(db: AppDatabase, typeFile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listAll = (db.serverDao().allFile as MutableList<FileModel?>).toMutableList()
            for (i in listAll) {
                if (i == null) {
                    listAll.remove(i)
                }
            }
            //
            listAll.sortByDescending { it?.timeRecent }
            //
            val listRecent = mutableListOf<FileModel>()
            for (file in listAll) {
                file?.let {
                    if (File(file.path).exists() && it.timeRecent != 0L) {
                        when (typeFile) {
                            KeyFile.ALL -> {
                                listRecent.add(it)
                            }
                            KeyFile.PDF -> {
                                if (it.image == R.drawable.ic_pdf) {
                                    listRecent.add(it)
                                }
                            }
                            KeyFile.WORD -> {
                                if (it.image == R.drawable.ic_doc) {
                                    listRecent.add(it)
                                }
                            }
                            KeyFile.EXCEL -> {
                                if (it.image == R.drawable.ic_xls) {
                                    listRecent.add(it)
                                }
                            }
                            KeyFile.PPT -> {
                                if (it.image == R.drawable.ic_ppt) {
                                    listRecent.add(it)
                                }
                            }
                        }
                    }
                }
            }
            viewModelScope.launch(Dispatchers.Main) {
                when (typeFile) {
                    KeyFile.ALL -> {
                        listAllFileModelLiveData.value = listRecent
                    }
                    KeyFile.PDF -> {
                        listPDFFileModelLiveData.value = listRecent
                    }
                    KeyFile.WORD -> {
                        listWordFileModelLiveData.value = listRecent
                    }
                    KeyFile.EXCEL -> {
                        listExcelFileModelLiveData.value = listRecent
                    }
                    KeyFile.PPT -> {
                        listPPTFileModelLiveData.value = listRecent
                    }
                }
            }
        }
    }

    fun getFileFavorite(db: AppDatabase, typeFile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listFavorite = mutableListOf<FileModel>()
            val listAll = db.serverDao().allFile
            for (item in listAll) {
                item?.let {
                    if (it.isFavorite) {
                        when (typeFile) {
                            KeyFile.ALL -> {
                                listFavorite.add(it)
                            }
                            KeyFile.PDF -> {
                                if (it.image == R.drawable.ic_pdf) {
                                    listFavorite.add(it)
                                }
                            }
                            KeyFile.WORD -> {
                                if (it.image == R.drawable.ic_doc) {
                                    listFavorite.add(it)
                                }
                            }
                            KeyFile.EXCEL -> {
                                if (it.image == R.drawable.ic_xls) {
                                    listFavorite.add(it)
                                }
                            }
                            KeyFile.PPT -> {
                                if (it.image == R.drawable.ic_ppt) {
                                    listFavorite.add(it)
                                }
                            }
                        }
                    }
                }
            }
            viewModelScope.launch(Dispatchers.Main) {
                when (typeFile) {
                    KeyFile.ALL -> {
                        listAllFileModelLiveData.value = listFavorite
                    }
                    KeyFile.PDF -> {
                        listPDFFileModelLiveData.value = listFavorite
                    }
                    KeyFile.WORD -> {
                        listWordFileModelLiveData.value = listFavorite
                    }
                    KeyFile.EXCEL -> {
                        listExcelFileModelLiveData.value = listFavorite
                    }
                    KeyFile.PPT -> {
                        listPPTFileModelLiveData.value = listFavorite
                    }
                }
            }
        }
    }


    fun getAllFilePDF(context: Context, db: AppDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentsUri = MediaStore.Files.getContentUri("external")
            val docsProjection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            queryFilesFromDevice(
                documentsUri,
                docsProjection,
                FileUtils.getQuery(FileUtils.PDF),
                context, KeyFile.PDF, db
            )
        }

    }

    fun getAllFileSlide(context: Context, db: AppDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentsUri = MediaStore.Files.getContentUri("external")
            val docsProjection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            queryFilesFromDevice(
                documentsUri,
                docsProjection,
                FileUtils.getQuery(FileUtils.POWERPOINT),
                context, KeyFile.PPT, db
            )
        }

    }

    fun getAllFileWord(context: Context, db: AppDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentsUri = MediaStore.Files.getContentUri("external")
            val docsProjection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            queryFilesFromDevice(
                documentsUri,
                docsProjection,
                FileUtils.getQuery(FileUtils.WORD),
                context, KeyFile.WORD, db
            )
        }

    }

    fun getAllFileSheet(context: Context, db: AppDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val documentsUri = MediaStore.Files.getContentUri("external")
            val docsProjection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            queryFilesFromDevice(
                documentsUri,
                docsProjection,
                FileUtils.getQuery(FileUtils.EXCEL),
                context, KeyFile.EXCEL, db
            )
        }
    }


    private fun queryFilesFromDevice(
        uri: Uri?,
        projection: Array<String>?,
        selection: String?,
        context: Context,
        typeFile: String, db: AppDatabase
    ) {
        val arrFile: ArrayList<FileModel> = ArrayList<FileModel>()
        val c = context.contentResolver.query(
            uri!!, projection,
            selection,
            null,
            null
        )
        if (c != null) {
            while (c.moveToNext()) {
                val fileInfo = FileModel()
                fileInfo.path = c.getString(0)
                val name = fileInfo.path.substring(fileInfo.path.lastIndexOf("/") + 1)
                val indexDot = name.lastIndexOf(".")
                fileInfo.name = name.substring(0, indexDot)
                fileInfo.size = c.getString(1)
                val file = File(fileInfo.path)
                fileInfo.date = file.lastModified()
                fileInfo.fileType = FileUtils.getFileType(name)
                fileInfo.image = FileUtils.getIconResId(fileInfo.fileType)
                if (file.isFile) {
                    arrFile.add(fileInfo)
//                    viewModelScope.launch(Dispatchers.Main) {
//                        fileModelLiveData.value = fileInfo
//                    }
                }
            }
            c.close()
            //
            arrFile.forEach { f ->
                f.isFavorite = FileUtils.isFavorite(f, db)
            }
            viewModelScope.launch(Dispatchers.Main) {
                when (typeFile) {
                    KeyFile.ALL -> {

                        listAllFileModelLiveData.value = arrFile
                        listLoadedAllFileModelLiveData.value = arrFile
                    }
                    KeyFile.PDF -> {
                        listPDFFileModelLiveData.value = arrFile
                        listLoadedPDFFileModelLiveData.value = arrFile
                    }
                    KeyFile.WORD -> {
                        listWordFileModelLiveData.value = arrFile
                        listLoadedWordFileModelLiveData.value = arrFile
                    }
                    KeyFile.EXCEL -> {
                        listExcelFileModelLiveData.value = arrFile
                        listLoadedExcelFileModelLiveData.value = arrFile
                    }
                    KeyFile.PPT -> {
                        listPPTFileModelLiveData.value = arrFile
                        listLoadedPPTFileModelLiveData.value = arrFile
                    }
                }
            }
        }

    }


}