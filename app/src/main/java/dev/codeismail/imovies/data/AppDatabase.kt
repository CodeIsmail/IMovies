package dev.codeismail.imovies.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.codeismail.imovies.data.dao.PopularMoviesDao
import dev.codeismail.imovies.data.dao.RemoteKeysDao
import dev.codeismail.imovies.data.models.Movie
import dev.codeismail.imovies.data.repositories.RemoteKeys

@Database(version = 1, entities = [Movie::class, RemoteKeys::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun movieDao(): PopularMoviesDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private const val DB_NAME = "movies.db"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}
