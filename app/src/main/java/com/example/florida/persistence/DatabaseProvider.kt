package com.example.florida.persistence

import android.content.Context
import androidx.room.Room
import com.example.florida.persistence.reposity.UserRepository

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // Para desenvolvimento
                .build()
            database = instance
            instance
        }
    }

    fun getUserRepository(context: Context): UserRepository {
        val db = getDatabase(context)
        return UserRepository(db.userDao())
    }
}
