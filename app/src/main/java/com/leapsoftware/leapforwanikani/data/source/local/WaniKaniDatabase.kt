package com.leapsoftware.leapforwanikani.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.leapsoftware.leapforwanikani.data.typeconverters.*
import com.leapsoftware.leapforwanikani.data.source.remote.api.WKReport

/* Stores WKResource and WKReport types. */
@Database(
    entities = [WKReport.WKResource.Assignment::class, WKReport.Summary::class, WKReport.User::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    DateTypeConverter::class, ListStringTypeConverter::class,
    ListLessonTypeConverter::class, ListIntTypeConverter::class,
    ListReviewRefTypeConverter::class
)
abstract class WaniKaniDatabase : RoomDatabase() {
    abstract fun wkResourceDao(): WKResourceDao
    abstract fun wkReportDao() : WKReportDao

    companion object {
        const val WANIKANI_DATABASE_NAME = "wanikani_database"

        // For Singleton instantiation
        @Volatile
        private var instance: WaniKaniDatabase? = null

        fun getInstance(context: Context): WaniKaniDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WaniKaniDatabase {
            return Room.databaseBuilder(context, WaniKaniDatabase::class.java,
                WANIKANI_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        //val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        //WorkManager.getInstance().enqueue(request)
                    }
                }).build()
        }
    }
}