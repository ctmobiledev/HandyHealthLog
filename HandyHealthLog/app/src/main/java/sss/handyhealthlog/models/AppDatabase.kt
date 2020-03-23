package sss.handyhealthlog.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Template: https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#6

// Annotates class to be a Room Database with a table (entity) of the Word class

@Database(entities = arrayOf(HealthLogModel::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun healthLogDao(): HealthLogDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->

                scope.launch {
                    var healthLogDao = database.healthLogDao()

                    // Delete all content here.  TEST ONLY
                    //healthLogDao.deleteAllEvents()
                    //println(">>> ALL DATA DELETED")

                    /***
                    // Add sample words.
                    var word = Word("Hello")
                    wordDao.insert(word)
                    word = Word("World!")
                    wordDao.insert(word)

                    // TODO: Add your own words!
                    word = Word("TODO!")
                    wordDao.insert(word)
                    ***/

                }

            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .allowMainThreadQueries()                           // This is a "cheat" - supposed to run on async task
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}