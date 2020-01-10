package com.leapsoftware.leapforwanikani.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

@Dao
interface WKResourceDao {
    // Assignments
    @Query("SELECT * FROM assignment")
    suspend fun getAllAssignments(): List<WKReport.WKResource.Assignment>

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 0")
    suspend fun countAssignmentsByInitiateSrsStage(): Int

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 1 OR srs_stage = 2 OR srs_stage = 3 OR srs_stage = 4")
    suspend fun countAssignmentsByApprenticeSrsStage(): Int

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 5 OR srs_stage = 6")
    suspend fun countAssignmentsByGuruSrsStage(): Int

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 7")
    suspend fun countAssignmentsByMasterSrsStage(): Int

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 8")
    suspend fun countAssignmentsByEnlightenedSrsStage(): Int

    @Query("SELECT COUNT(*) FROM assignment WHERE srs_stage = 9")
    suspend fun countAssignmentsByBurnedSrsStage(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<WKReport.WKResource.Assignment>)
}