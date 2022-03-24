package com.ezstudio.pdfreaderver4.viewmodel

import android.app.Application
import com.ezstudio.pdfreaderver4.database.AppDatabase
import com.ezstudio.pdfreaderver4.model.FileModel
import com.ezstudio.pdfreaderver4.utils.FileUtils.isFavorite
import com.ezteam.baseproject.viewmodel.BaseViewModel

class MainViewModel(application: Application) : BaseViewModel(application) {

    fun checkFileExistDb(db: AppDatabase, path: String, fileModel: (FileModel?) -> Unit) {
        fileModel.invoke(db.serverDao().getFileById(path))
    }

    //
    fun updateFavorite(fileModel: FileModel, db: AppDatabase, done: ((Boolean) -> Unit)?) {
        //        viewModelScope.launch(Dispatchers.IO) {
        val fileDB = db.serverDao().getFileById(fileModel.path)
        //
        if (fileDB != null) {
            if (isFavorite(fileDB, db)) {
//            db.serverDao().deleteById(fileDB.path)
                fileDB.isFavorite = false
                db.serverDao().update(fileDB)
                done?.invoke(true)
            } else {
                fileDB.isFavorite = true
                db.serverDao().update(fileDB)
                done?.invoke(true)
            }
        } else {
            fileModel.isFavorite = true
            db.serverDao().insert(fileModel)
            done?.invoke(true)
        }
    }


    fun isPassword(fileModel: FileModel, db: AppDatabase): Boolean {
        val fileDb = db.serverDao().getFileById(fileModel.path) ?: return false
        return fileDb.password != null
    }

    fun getPassword(fileModel: FileModel, db: AppDatabase): String? {
        val fileDb = db.serverDao().getFileById(fileModel.path) ?: return null
        return fileDb.password
    }

    // recent
    fun updateFileRecent(fileModel: FileModel, db: AppDatabase, time: Long = 1) {
        val file = db.serverDao().getFileById(fileModel.path)
        val fileDb = file ?: fileModel
        fileDb.timeRecent = if (time != 0L) (System.currentTimeMillis()) else 0L
        if (file == null) {
            db.serverDao().insert(fileDb)
        } else {
            db.serverDao().update(fileDb)
        }
    }

    //password
    fun updatePassword(
        fileModel: FileModel,
        db: AppDatabase,
        password: String? = null,
        state: ((Int) -> Unit)?
    ) {
        val file = db.serverDao().getFileById(fileModel.path)
        val fileDb = file ?: fileModel
        if (file == null) {
            fileDb.password = password
            db.serverDao().insert(fileDb)
            state?.invoke(3)
        } else {
            if (fileDb.password == null) {
                fileDb.password = password
                db.serverDao().update(fileDb)
                state?.invoke(3)
            } else {
                if (password == fileDb.password) {
                    fileDb.password = null
                    db.serverDao().update(fileDb)
                    state?.invoke(2)
                } else {
                    state?.invoke(1)
                }
            }
        }
    }

}