package com.ezstudio.pdfreaderver4.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.dialog.DialogDeleteFile
import com.ezstudio.pdfreaderver4.dialog.DialogInfoFile
import com.ezstudio.pdfreaderver4.dialog.DialogPasswordFile
import com.ezstudio.pdfreaderver4.dialog.DialogRenameFile
import com.ezstudio.pdfreaderver4.R
import com.ezstudio.pdfreaderver4.databinding.LayoutDialogDeleteFileBinding
import com.ezstudio.pdfreaderver4.databinding.LayoutDialogInfoFileBinding
import com.ezstudio.pdfreaderver4.databinding.LayoutDialogPasswordFileBinding
import com.ezstudio.pdfreaderver4.databinding.LayoutDialogRenameFileBinding
import com.ezstudio.pdfreaderver4.model.FileModel
import com.ezstudio.pdfreaderver4.viewmodel.MainViewModel
import com.ezteam.baseproject.EzListener
import com.ezteam.baseproject.listener.EzItemListener
import com.ezteam.baseproject.utils.KeyboardUtils
import office.file.ui.OpenFileActivity
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    const val SORT_DATE_1_9 = "SORT_DATE_1_9"
    const val SORT_DATE_9_1 = "SORT_DATE_9_1"
    const val SORT_SIZE_1_9 = "SORT_SIZE_1_9"
    const val SORT_SIZE_9_1 = "SORT_SIZE_9_1"
    const val SORT_NAME_A_Z = "SORT_NAME_A_Z"
    const val SORT_NAME_Z_A = "SORT_NAME_Z_A"

    const val KEY_FILE_NAME = "KEY_FILE_NAME"
    const val SEARCH_MAIN = "SEARCH_MAIN"
    const val INTENT_FILED_FILE_PATH = "INTENT_FILED_FILE_PATH"
    const val INTENT_FILED_FILE_URI = "INTENT_FILED_FILE_URI"
    const val EXCEL = "excel"
    const val WORD = "word"
    const val PDF = "pdf"
    const val POWERPOINT = "powerpoint"
    const val TEXT = "text"
    const val HTML = "html"
    const val ALL = "all"
    const val XLSX = "xlsx"
    const val DOCX = "docx"
    const val TXT = "txt"
    const val PPTX = "pptx"
    const val RAR = "rar"
    val fileExtension = arrayOf(
        ".csv",
        ".xlsx",
        ".xls",
        ".xlt",
        ".xlsm",
        ".xltm",
        ".xltx",
        ".doc",
        ".docx",
        ".dot",
        ".dotx",
        ".dotm",
        ".txt",
        ".pot",
        ".pptm",
        ".potx",
        ".potm",
        ".ppt",
        ".pptx",
        ".pdf"
    )
    val excelFileExtension = arrayOf(".csv", ".xlsx", ".xls", ".xlt", ".xlsm", ".xltm", ".xltx")
    val wordlFileExtension = arrayOf(".doc", ".docx", ".dot", ".dotx", ".dotm")
    val txtFileExtension = arrayOf(".txt")
    val powerpointFileExtension = arrayOf(".pot", ".pptm", ".potx", ".potm", ".ppt", ".pptx")
    val pdfFileExtension = arrayOf(".pdf")
    private var dialogQuestion: DialogDeleteFile? = null
    private var dialogRename: DialogRenameFile? = null
    private var dialogPassword: DialogPasswordFile? = null
    private var dialogInfo: DialogInfoFile? = null

    //
    @JvmStatic
    fun getFileType(fileName: String?): String {
        if (TextUtils.isEmpty(fileName)) return ""
        val endFile = "." + FilenameUtils.getExtension(fileName)
        return if (Arrays.asList(*excelFileExtension)
                .contains(endFile)
        ) EXCEL else if (Arrays.asList(
                *wordlFileExtension
            ).contains(endFile)
        ) WORD else if (Arrays.asList(
                *txtFileExtension
            ).contains(endFile)
        ) TXT else if (Arrays.asList(
                *powerpointFileExtension
            ).contains(endFile)
        ) POWERPOINT else if (Arrays.asList(
                *pdfFileExtension
            )
                .contains(endFile)
        ) PDF else ""
    }

    @JvmStatic
    fun getIconResId(fileType: String?): Int {
        return when (fileType) {
            PDF -> R.drawable.ic_pdf
            EXCEL -> R.drawable.ic_xls
            XLSX -> R.drawable.ic_xls
            WORD -> R.drawable.ic_doc
            DOCX -> R.drawable.ic_doc
            POWERPOINT -> R.drawable.ic_ppt
            PPTX -> R.drawable.ic_ppt
            else -> R.drawable.ic_doc
        }
    }

    fun getResourceWithDocumentType(url: String?): Int {
        if (TextUtils.isEmpty(url)) return 0
        val fileType = getFileType(url)
        if (fileType == PDF) return R.drawable.ic_pdf
        if (fileType == EXCEL) return R.drawable.ic_xls
        if (fileType == XLSX) return R.drawable.ic_xls
        if (fileType == WORD) return R.drawable.ic_doc
        if (fileType == DOCX) return R.drawable.ic_doc
        return if (fileType == POWERPOINT || fileType == PPTX) R.drawable.ic_ppt else R.drawable.ic_doc
    }


    fun getQuery(type: String): String {
        return when (type) {
            EXCEL -> "_data LIKE '%.csv' OR " +
                    " _data LIKE '%.xlsx' OR " +
                    " _data LIKE '%.xls' OR " +
                    " _data LIKE '%.xlt' OR " +
                    " _data LIKE '%.xlsm' OR " +
                    " _data LIKE '%.xltm' OR " +
                    "_data LIKE '%.xltx'"
            WORD -> "_data LIKE '%.doc' OR " +
                    " _data LIKE '%.docx' OR " +
                    " _data LIKE '%.dot' OR " +
                    " _data LIKE '%.dotx' OR " +
                    "_data LIKE '%.dotm'"
            TXT -> "_data LIKE '%.txt' "
            POWERPOINT -> "_data LIKE '%.pot' OR " +
                    " _data LIKE '%.pptm' OR " +
                    " _data LIKE '%.potx' OR " +
                    " _data LIKE '%.potm' OR " +
                    " _data LIKE '%.ppt' OR " +
                    "_data LIKE '%.pptx'"
            PDF -> "_data LIKE '%.pdf' "
            else -> "_data LIKE '%.csv' OR " +
                    " _data LIKE '%.xlsx' OR " +
                    " _data LIKE '%.xls' OR " +
                    " _data LIKE '%.xlt' OR " +
                    " _data LIKE '%.xlsm' OR " +
                    " _data LIKE '%.xltm' OR " +
                    "_data LIKE '%.xltx'  OR " +
                    "_data LIKE '%.pot' OR " +
                    " _data LIKE '%.pptm' OR " +
                    " _data LIKE '%.potx' OR " +
                    " _data LIKE '%.potm' OR " +
                    " _data LIKE '%.ppt' OR " +
                    "_data LIKE '%.pptx' OR " +
                    "_data LIKE '%.doc' OR " +
                    " _data LIKE '%.docx' OR " +
                    " _data LIKE '%.dot' OR " +
                    " _data LIKE '%.dotx' OR " +
                    " _data LIKE '%.pdf' OR " +
                    " _data LIKE '%.txt' OR " +
                    "_data LIKE '%.dotm'"
        }
    }

    fun getAllFileList(context: Context?): ArrayList<FileModel> {
        if (context == null) {
            return ArrayList<FileModel>()
        }
        val ducumentsUri = MediaStore.Files.getContentUri("external")
        val docsProjection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE
        )
        return queryFilesFromDevice(ducumentsUri, docsProjection, getQuery(ALL), context)
    }

    fun queryFilesFromDevice(
        uri: Uri?,
        projection: Array<String>?,
        selection: String?,
        context: Context
    ): ArrayList<FileModel> {
        val arrFile: ArrayList<FileModel> = ArrayList<FileModel>()
        val c = context.contentResolver.query(
            uri!!, projection,
            selection,
            null,
            null
        )
        if (c != null) {
            while (c.moveToNext()) {
                Log.e("getFile", "queryFilesFromDevice: " + c.getString(0))
                val fileInfo = FileModel()
                fileInfo.path = c.getString(0)
                val name = fileInfo.path.substring(fileInfo.path.lastIndexOf("/") + 1)
                fileInfo.name = name.substring(0, name.length - 4)
                fileInfo.size = c.getString(1)
                val file = File(fileInfo.path)
                fileInfo.date = file.lastModified()
                if (file.isFile) {
                    arrFile.add(fileInfo)
                }
            }
            c.close()
        }
        return arrFile
    }

    private val arrFolderSystem = arrayOf(
        Environment.getExternalStorageDirectory().path,
        Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOWNLOADS,
        Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_DOCUMENTS
    )

    fun scanAllFile(context: Context, Path: String) {
        Log.e("scanAllFile1", "scanAllFile: $Path")
        try {
            val file = File(Path)
            val files = file.listFiles()
            for (f in files) {
                val endFile = "." + FilenameUtils.getExtension(f.name)
                if (f.isFile && Arrays.asList(*fileExtension).contains(endFile)) {
                    Log.e("scanAllFile2", "scanAllFile: " + f.path)
                    scanFile(context, f.path, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkFolderSystem(context: Context) {
        val allFiles = File(Environment.getExternalStorageDirectory().path)
        scanAllFile(context, allFiles.path)
        if (allFiles.listFiles() != null) {
            for (file in allFiles.listFiles()) {
                if (file.isDirectory) {
                    Log.d("FileScan", file.path)
                    scanAllFile(context, file.path)
                }
            }
        }
        /*for (String path : arrFolderSystem) {
            scanAllFile(context, path);
        }*/
    }

    fun searchFileInFolder(fileName: String): File? {
        for (path in arrFolderSystem) {
            Log.e("XXX", "searchFileInFolder: $path")
            try {
                val file = File(path)
                val files = file.listFiles()
                for (f in files) {
                    if (f.isFile) {
                        if (getFileName(f.path) == fileName) {
                            return f
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun searchPathInFolder(fileName: String): String {
        for (path in arrFolderSystem) {
            Log.e("XXX", "searchFileInFolder: $path")
            try {
                val file = File(path)
                val files = file.listFiles()
                for (f in files) {
                    if (f.isFile) {
                        if (getFileName(f.path) == fileName) {
                            return "file://" + f.path
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun getFileName(path: String?): String {
        if (path == null) {
            return "not found"
        }
        val index = path.lastIndexOf('/')
        return if (index == -1) {
            "not found"
        } else {
            path.substring(path.lastIndexOf('/'), path.length)
        }
    }

    fun getFileExt(path: String): String {
        return path.substring(path.lastIndexOf('.'), path.length)
    }

    fun rename(
        context: Context,
        file: File,
        newName: String,
        listener: EzItemListener<RenameStatus?>,
        pathNewListener: EzItemListener<String?>
    ) {
        val name = file.path.substring(file.path.lastIndexOf("/") + 1)
        val endFile = FilenameUtils.getExtension(file.name)
        val toFile = File(file.path.replace(name, "$newName.$endFile"))
        if (toFile.exists()) {
            listener.onListener(RenameStatus.EXISTS)
        } else {
            val success = file.renameTo(toFile)
            if (success) {
                scanFile(context, file.path, object : EzListener {
                    override fun onListener() {
                        scanFile(context, toFile.path, object : EzListener {
                            override fun onListener() {
                                pathNewListener.onListener(toFile.path)
                                listener.onListener(RenameStatus.SUCCESS)
                            }
                        })
                    }
                })
            } else {
                listener.onListener(RenameStatus.FAIL)
            }
        }
    }

    fun scanFile(context: Context, pathFile: String, listener: EzListener?) {
        val mediaScanIntent = Intent(
            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
        )
        val contentUri = Uri.fromFile(File(pathFile))
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
        MediaScannerConnection.scanFile(
            context,
            arrayOf(pathFile),
            null
        ) { path: String?, uri: Uri? -> listener?.onListener() }
    }

    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                // Eat it
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getFileExtension(filePath: String): String {
        var extension = ""
        try {
            extension = filePath.substring(filePath.lastIndexOf("."))
        } catch (exception: Exception) {
            Log.e("Err", exception.toString() + "")
        }
        return extension
    }

    fun getFileName(context: Context, uri: Uri): String? {
        try {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        } catch (e: Exception) {
        }
        return ""
    }

    fun floatForm(d: Double): String {
        return DecimalFormat("#.##").format(d)
    }

    fun bytesToHuman(size: Long): String {
        val Kb = (1 * 1024).toLong()
        val Mb = Kb * 1024
        val Gb = Mb * 1024
        val Tb = Gb * 1024
        val Pb = Tb * 1024
        val Eb = Pb * 1024
        if (size < Kb) return floatForm(size.toDouble()) + " byte"
        if (size in Kb until Mb) return floatForm(size.toDouble() / Kb) + " Kb"
        if (size in Mb until Gb) return floatForm(size.toDouble() / Mb) + " Mb"
        if (size in Gb until Tb) return floatForm(size.toDouble() / Gb) + " Gb"
        if (size in Tb until Pb) return floatForm(size.toDouble() / Tb) + " Tb"
        if (size in Pb until Eb) return floatForm(size.toDouble() / Pb) + " Pb"
        return if (size >= Eb) floatForm(size.toDouble() / Eb) + " Eb" else "???"
    }

    fun isFileImageVideo(url: String): Boolean {
        if (TextUtils.isEmpty(url)) return false
        return (url.contains(".jpg") || url.contains(".png") || url.contains(".mp4")
                || url.contains(".avi") || url.contains(".flv") || url.contains("wmv")
                || url.contains(".mov"))
    }

    enum class RenameStatus {
        SUCCESS, FAIL, EXISTS
    }

    // delete file
    fun showDeleteFile(
        fileModel: FileModel?,
        content: String? = null,
        context: Context,
        done: ((Boolean) -> (Unit))? = null
    ) {
        val bindDialog =
            LayoutDialogDeleteFileBinding.inflate(LayoutInflater.from(context))
        dialogQuestion = DialogDeleteFile(context, bindDialog, R.style.dialog)
        dialogQuestion?.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content.let { c -> it.binding.txtContent.text = c }
        }

        dialogQuestion?.listenerYes = {
            DialogLoadingUtils.showDialogHiding(context, true)
            fileModel?.path?.let {
                FileManagerUtils.deleteFile(context, it) { success ->
                    if (success) {
                        // xoa thanh cong thi do something
                        done?.invoke(true)
                    } else {
                        done?.invoke(false)
                    }
                }
            }
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.show()
    }

    //
    fun reNameFIle(
        activity: Activity,
        fileModel: FileModel?,
        listener: (String, String) -> Unit?
    ) {
        val bindDialog = LayoutDialogRenameFileBinding.inflate(LayoutInflater.from(activity))
        fileModel?.let {
            bindDialog.edtInput.setText(it.name.toString())
            bindDialog.edtInput.selectAll()
            KeyboardUtils.showSoftKeyboard(activity)
            dialogRename = DialogRenameFile(activity, bindDialog, R.style.StyleDialog)
            dialogRename?.setCancelable(true)
            dialogRename?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialogRename?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogRename?.show()
            dialogRename?.listenerYes = {
                val from = File(it.path)
                rename(
                    activity,
                    from,
                    bindDialog.edtInput.text.toString(),
                    { status ->
                        when (status) {
                            RenameStatus.EXISTS -> {
                                toats(activity, activity.resources.getString(R.string.file_exit))
                            }
                            RenameStatus.FAIL -> {
                                toats(activity, activity.resources.getString(R.string.rename_fail))
                            }
                            else -> {
                            }
                        }
                    }) { pathNew ->
                    pathNew?.let {
                        listener(bindDialog.edtInput.text.toString(), pathNew)
                    }
                }
                dialogRename?.dismiss()
            }
            dialogRename?.listenerNo = {
                dialogRename?.dismiss()
            }
            dialogRename?.setOnDismissListener {
                KeyboardUtils.hideSoftKeyboardToggleSoft(activity)
            }
        }
    }

    private fun toats(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }
    // info

    @SuppressLint("SimpleDateFormat")
    fun infoFile(context: Context, fileModel: FileModel?) {
        fileModel?.let {
            val binding = LayoutDialogInfoFileBinding.inflate(LayoutInflater.from(context))
            dialogInfo = DialogInfoFile(context, binding)
            dialogInfo?.let { d ->
                d.setCancelable(true)
                d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                binding.apply {
                    txtName.text = it.name
                    txtSize.text = it.sizeString
                    txtDate.text = SimpleDateFormat("dd/MM/yyyy").format(Date(it.date))
                    txtPath.text = it.path
                }
            }
            dialogInfo?.show()
        }
    }

    //open
    fun openFileDetail(
        activity: FragmentActivity,
        fileModel: FileModel,
        mainViewModel: MainViewModel,
        db: AppDatabase
    ) {
//        if (mainViewModel.isPassword(fileModel, db)) {
//            password(activity, fileModel, false) {
//                if (it == mainViewModel.getPassword(fileModel, db)) {
//                    open(activity, fileModel, mainViewModel, db)
//                } else {
//                    Toast.makeText(
//                        activity,
//                        activity.getString(R.string.password_incorrect),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        } else {
            open(activity, fileModel, mainViewModel, db)
//        }
    }

    private fun open(
        context: Context,
        fileModel: FileModel,
        mainViewModel: MainViewModel,
        db: AppDatabase
    ) {
        mainViewModel.updateFileRecent(fileModel, db)
        /*val intent = Intent()
        if (PDF == fileModel.fileType) {
            intent.setClass(context, PdfViewerActivity::class.java)
        } else {
            intent.setClass(context, AppActivity::class.java)
        }
        intent.putExtra(MainConstant.INTENT_FILED_FILE_PATH, fileModel)
        context.startActivity(intent)*/

        val intent = Intent(context, OpenFileActivity::class.java)
        intent.action = "android.intent.action.VIEW"
        intent.data = Uri.fromFile(File(fileModel.path))
        intent.putExtra("STARTED_FROM_EXPLORER", true)
        intent.putExtra("START_PAGE", 0)
        intent.putExtra("ALLOW_EDIT", true)

        context.startActivity(intent)
    }

    // share
    fun shareFile(context: Context, fileModel: FileModel?) {
        fileModel?.let {
            if (it.file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".files.provider",
                    it.file
                )
                val share = Intent()
                share.action = Intent.ACTION_SEND
                share.type = "application/pdf"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(share)
            }
        }
    }

    // favorite
    fun favoriteFile(
        fileModel: FileModel?,
        mainViewModel: MainViewModel,
        db: AppDatabase,
        done: ((Boolean) -> Unit)?
    ) {
        fileModel?.let {
            mainViewModel.updateFavorite(fileModel, db) {
                if (it) {
                    done?.invoke(true)
                }
            }
        }
    }

    fun isFavorite(fileModel: FileModel, db: AppDatabase): Boolean {
        val fileDb = db.serverDao().getFileById(fileModel.path) ?: return false
        return fileDb.isFavorite
    }

    // recent
    fun recentFile(
        fileModel: FileModel?,
        mainViewModel: MainViewModel,
        db: AppDatabase,
        done: ((Boolean) -> Unit)?
    ) {
        fileModel?.let {
            mainViewModel.updateFileRecent(fileModel, db, 0)
            done?.invoke(true)
        }
    }

    // password

    fun password(
        activity: FragmentActivity,
        fileModel: FileModel?,
        isCreate: Boolean,
        done: ((String) -> Unit)?
    ) {
        val bindDialog = LayoutDialogPasswordFileBinding.inflate(LayoutInflater.from(activity))
        if (isCreate) {
            bindDialog.tvTitle.text = activity.getString(R.string.create_password)
            bindDialog.llInputAgain.visibility = View.VISIBLE
        } else {
            bindDialog.tvTitle.text = activity.getString(R.string.password)
            bindDialog.llInputAgain.visibility = View.GONE
        }
        fileModel?.let {
            KeyboardUtils.showSoftKeyboard(activity)
            dialogPassword = DialogPasswordFile(activity, bindDialog, R.style.StyleDialog)
            dialogPassword?.apply {
                setCancelable(true)
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                show()
            }
            dialogPassword?.listenerYes = {
                if (isCreate) {
                    if (bindDialog.edtPassword.text.toString() == bindDialog.edtPasswordAgain.text.toString()) {
                        done?.invoke(bindDialog.edtPassword.text.toString())
                        dialogPassword?.dismiss()
                    } else {
                        val color = android.graphics.Color.parseColor("#FC573B")
                        bindDialog.llInputText.boxStrokeColor = color
                        bindDialog.llInputAgain.boxStrokeColor = color
                        bindDialog.llInputText.hintTextColor =
                            getColorStateList(activity, R.color.color_BF1925)
                        bindDialog.llInputAgain.hintTextColor =
                            getColorStateList(activity, R.color.color_BF1925)
                        Toast.makeText(
                            activity,
                            activity.getString(R.string.check_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    done?.invoke(bindDialog.edtPassword.text.toString())
                    dialogPassword?.dismiss()
                }
            }
            dialogPassword?.listenerNo = {
                dialogPassword?.dismiss()
            }
            dialogPassword?.setOnDismissListener {
                KeyboardUtils.hideSoftKeyboardToggleSoft(activity)
            }
        }
    }
}