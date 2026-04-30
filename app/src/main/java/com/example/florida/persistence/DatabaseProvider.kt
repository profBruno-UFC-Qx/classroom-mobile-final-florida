package com.example.florida.persistence

import android.content.Context
import androidx.room.Room
import com.example.florida.persistence.migration.AppMigrations
import com.example.florida.persistence.reposity.BudgetRepository
import com.example.florida.persistence.reposity.ClientRepository
import com.example.florida.persistence.reposity.ReceiptRepository
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
                .addMigrations(AppMigrations.MIGRATION_1_2)
                .build()
            database = instance
            instance
        }
    }

    fun getUserRepository(context: Context): UserRepository {
        val db = getDatabase(context)
        return UserRepository(db.userDao())
    }

    fun getClientRepository(context: Context): ClientRepository {
        val db = getDatabase(context)
        return ClientRepository(db.clientDao())
    }

    fun getBudgetRepository(context: Context): BudgetRepository {
        val db = getDatabase(context)
        return BudgetRepository(db.budgetDao())
    }

    fun getReceiptRepository(context: Context): ReceiptRepository {
        val db = getDatabase(context)
        return ReceiptRepository(db.receiptDao())
    }
}
