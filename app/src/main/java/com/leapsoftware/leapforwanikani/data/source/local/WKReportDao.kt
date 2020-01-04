package com.leapsoftware.leapforwanikani.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

@Dao
interface WKReportDao {
    @Query("SELECT * FROM summary")
    suspend fun getSummary(): WKReport.Summary?

    @Query("SELECT COUNT(*) FROM summary")
    suspend fun countSummary(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: WKReport.Summary)



    @Query("SELECT * FROM user")
    suspend fun getUser(): WKReport.User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: WKReport.User)
}