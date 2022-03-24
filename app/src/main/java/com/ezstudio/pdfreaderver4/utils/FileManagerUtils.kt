package com.ezstudio.pdfreaderver4.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import com.ezstudio.pdfreaderver4.R
import org.apache.commons.io.FilenameUtils
import java.io.*

object FileManagerUtils {
    fun saveFileStorage(
        context: Context,
        fileInput: String?,
        parentPath: String? = null,
        fileNameDisplay: String? = null
    ): Uri? {
        var parentFolder = parentPath
        parentPath ?: let {
            parentFolder = context.getString(R.string.app_name)
        }
        var filename = "$fileNameDisplay.pdf"
        fileNameDisplay ?: let {
            filename = FileUtils.getFileName(fileInput)
        }
        var outputStream: OutputStream? = null
        var fileOutputUri: Uri? = null
        val newFile = File(
            (parentPath?.let { parentFolder } ?: getFolderParent(parentFolder!!))
                    + File.separator
                    + filename
        )
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
            || context.applicationInfo.targetSdkVersion < 30
        ) {
            outputStream = FileOutputStream(newFile)
            val inputStream = FileInputStream(fileInput)
            val buffer = ByteArray(1024 * 4)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            inputStream.close()
            outputStream.flush()
            outputStream.close()
            fileOutputUri = Uri.fromFile(newFile)
            context.sendBroadcast(
                Intent(
                    "android.intent.action.MEDIA_SCANNER_SCAN_FILE",
                    fileOutputUri
                )
            )

        } else {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    getRealtivePath(context, newFile.path)
                )
                put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            }
            val resolver = context.contentResolver
            val inputStream = FileInputStream(fileInput)
            return try {
                val contentUri = MediaStore.Files.getContentUri("external")
                fileOutputUri = resolver.insert(contentUri, contentValues)
                val pfd: ParcelFileDescriptor?
                try {
                    fileOutputUri?.let { uri ->
                        pfd = resolver.openFileDescriptor(uri, "w")
                        pfd?.let {
                            val out = FileOutputStream(pfd.fileDescriptor)
                            val buf = ByteArray(4 * 1024)
                            var len: Int
                            while (inputStream.read(buf).also { len = it } > 0) {
                                out.write(buf, 0, len)
                            }
                            out.close()
                            inputStream.close()
                            pfd.close()
                        }
                        contentValues.apply {
                            clear()
                            put(MediaStore.Video.Media.IS_PENDING, 0)
                        }
                        resolver.update(uri, contentValues, null, null)
                        outputStream = resolver.openOutputStream(uri)
                        outputStream ?: let {
                            throw IOException("Failed to get output stream.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                fileOutputUri
            } catch (e: IOException) {
                // Don't leave an orphan entry in the MediaStore
                fileOutputUri?.let {
                    resolver.delete(it, null, null)
                }
                throw e
            } finally {
                outputStream?.close()
            }
        }
        return fileOutputUri
    }

    private fun getFolderParent(name: String = ""): String? {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + Environment.DIRECTORY_DOCUMENTS, name
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return ""
            }
        }
        return mediaStorageDir.absolutePath
    }

    fun checkFileExist(context: Context, fileName: String, folderName: String = ""): String? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val file = File(getFolderParent(folderName) + File.separator + fileName)
            return if (file.exists()) file.path else null
        } else {
            val projection = arrayOf(
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            val path = Environment.DIRECTORY_DOCUMENTS + File.separator + folderName
            val selection =
                MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? and " +
                        MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"
            val selectionargs = arrayOf("%$path%", "%$fileName%")
            val cursor = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionargs,
                null
            )

            if (cursor!!.count > 0) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH))
            }
        }
        return null
    }

    private fun getRealtivePath(context: Context, filePath: String?): String? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val projection = arrayOf(
                MediaStore.MediaColumns.RELATIVE_PATH,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            val path = FilenameUtils.getBaseName(File(filePath).parent)
            val selection =
                MediaStore.Files.FileColumns.RELATIVE_PATH + " like ? and " +
                        MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"
            val selectionargs = arrayOf("%$path%", "%${FilenameUtils.getBaseName(filePath)}%")
            val cursor = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionargs,
                null
            )
            var pathOut: String? = null
            if (cursor != null) {
                val isDataPresent = cursor.moveToFirst()
                if (isDataPresent) {
                    do {
                        pathOut =
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH))
                        Log.e(
                            "XXX",
                            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH))
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            return pathOut
        }
        return filePath
    }

    fun deleteFile(context: Context, filePath: String, listener: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            listener(File(filePath).delete())
            FileUtils.scanFile(context, filePath, null)
        } else {
            MediaScannerConnection.scanFile(
                context, arrayOf(filePath), arrayOf("application/pdf")
            ) { _, uri ->
                if (uri != null) {
                    if (context.contentResolver.delete(uri, null, null) != -1) {
                        listener(true)
                    } else {
                        listener(false)
                    }
                } else {
                    listener(false)
                }
            }
        }
    }
}