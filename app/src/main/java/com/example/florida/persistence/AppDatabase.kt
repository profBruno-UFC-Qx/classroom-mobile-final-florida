package com.example.florida.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.florida.persistence.Entity.UserEntity
import com.example.florida.persistence.dao.UserDao

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "app_database.db"
    }
}
