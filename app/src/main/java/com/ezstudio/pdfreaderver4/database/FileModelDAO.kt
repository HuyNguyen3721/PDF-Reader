package com.ezstudio.pdfreaderver4.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ezstudio.pdfreaderver4.model.FileModel
import io.reactivex.Maybe

@Dao
interface FileModelDAO {
    @Query(
        """SELECT * FROM file WHERE CASE WHEN :isAll = 0 THEN file_type =:typeDocument ELSE file_type IS NOT NULL END 
                AND name LIKE :textSearch  
                ORDER BY  
                CASE WHEN :typeSort = 5 THEN name END ASC, 
                CASE WHEN :typeSort = 6 THEN date END ASC,
                CASE WHEN :typeSort = 7 THEN size END ASC,
                CASE WHEN :typeSort = 8 THEN name END DESC,
                CASE WHEN :typeSort = 9 THEN date END DESC,
                CASE WHEN :typeSort = 10 THEN size END DESC
                 """
    )
    fun getLiveDataFileType(
        isAll: Boolean?,
        typeDocument: String?,
        textSearch: String?,
        typeSort: Int
    ): LiveData<List<FileModel?>?>?

    @Query("SELECT * FROM file WHERE path =:url ")
    fun checkFileExist(url: String?): Maybe<FileModel?>?

    @get:Query("SELECT * FROM file WHERE time_recent <> 0 ORDER BY time_recent DESC")
    val liveDataFileRecent: LiveData<List<FileModel?>?>

    @get:Query("SELECT * FROM file WHERE is_favorite <> 0 ORDER BY name DESC")
    val liveDataFileFavorite: LiveData<List<FileModel?>?>

    @get:Query("SELECT * FROM file ORDER BY name ASC")
    val allFile: List<FileModel?>

    @Query("SELECT * FROM file WHERE path =:path")
    fun getFileById(path: String?): FileModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(data: List<FileModel?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fileModel: FileModel?)

    @Query("DELETE FROM file")
    fun deleteAll()

    @Query("DELETE FROM file WHERE  path =:path")
    fun deleteById(path: String?)

    @Delete
    fun delete(fileInfo: FileModel?)

    @Update
    fun update(fileInfo: FileModel?)
}