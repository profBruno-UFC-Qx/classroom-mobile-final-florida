package com.example.florida.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.florida.persistence.Entity.BudgetEntity
import com.example.florida.persistence.Entity.BudgetItemEntity
import com.example.florida.persistence.Entity.ClientEntity
import com.example.florida.persistence.Entity.ReceiptEntity
import com.example.florida.persistence.Entity.ReceiptItemEntity
import com.example.florida.persistence.Entity.UserEntity
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.dao.UserDao

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        BudgetEntity::class,
        BudgetItemEntity::class,
        ReceiptEntity::class,
        ReceiptItemEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun budgetDao(): BudgetDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        const val DATABASE_NAME = "app_database.db"
    }
}
