package office.file.ui.extension

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import office.file.ui.OpenFileActivity
import office.support.UiUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


fun Activity.openFile(path: String, flag: Int, allowEdit: Boolean = false, list: ArrayList<String> = arrayListOf(), copyString: String = "") {
    val fromFile = android.net.Uri.fromFile(File(path))
    val intent = Intent(this, OpenFileActivity::class.java)
    intent.action = "android.intent.action.VIEW"
    intent.data = fromFile
    intent.putExtra("STARTED_FROM_EXPLORER", true)
    intent.putExtra("START_PAGE", flag)
    intent.putExtra("ALLOW_EDIT", allowEdit)

    if (copyString != "") {
        intent.putExtra("content", copyString)
    }

    if (list.isNotEmpty()) {
        intent.putStringArrayListExtra("list_img", list)
    }

    this.startActivityForResult(intent, 1000)

}

fun Activity.setLocale(language: String) {
    val myLocale = Locale(language)
    val res = resources
    val dm = res.displayMetrics
    val conf = res.configuration
    conf.locale = myLocale
    res.updateConfiguration(conf, dm)
}

fun Context.getBaseImageFolder(): String {
    val path = this.getExternalFilesDir(null)?.path + File.separator + "images"
    val folder = File(path)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return folder.path
}

fun Context.getBaseDocumentFolder(): String {
    val path = this.getExternalFilesDir(null)?.path + File.separator + "documents"
    val folder = File(path)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return folder.path
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

fun Activity.isKeyboardOpened(): Boolean {
    val r = Rect()

    val activityRoot = getActivityRoot()
    val visibleThreshold = dip(100)

    activityRoot.getWindowVisibleDisplayFrame(r)

    val heightDiff = activityRoot.rootView.height - r.height()

    return heightDiff > visibleThreshold;
}

fun dip(value: Int): Int {
    return (value * Resources.getSystem().displayMetrics.density).toInt()
}

fun Activity.getActivityRoot(): View {
    return (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0);
}

val type = "type"
val type_scanner = "type_scanner"
val type_ocr = "type_ocr"
val type_ocr_text = "type_ocr_text"

val language = "LANGUAGE"
val en = "en"
val es = "es"
val hi = "hi"

val lastRateDate = "WAS_RATE"


val fileDOC = "document-blank.docx"
val filePPT = "presentation-blank.pptx"
val fileXLS = "spreadsheet-blank.xlsx"

val openFavourite = "open_favourite"
val openRecent = "open_recent"

val RELOAD_FAVOURITE = "reload_favourite"
val RELOAD_CREATE = "reload_create"
val RELOAD_DELETE = "reload_delete"
val KEY_NAME = "name"
val SHOW_RATE = "show_rate"