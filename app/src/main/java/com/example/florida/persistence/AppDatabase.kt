package com.example.florida.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.florida.persistence.entity.BudgetEntity
import com.example.florida.persistence.entity.BudgetItemEntity
import com.example.florida.persistence.entity.ClientEntity
import com.example.florida.persistence.entity.ReceiptEntity
import com.example.florida.persistence.entity.ReceiptItemEntity
import com.example.florida.persistence.entity.UserEntity
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.DashboardDao
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
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun budgetDao(): BudgetDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun dashboardDao(): DashboardDao

    companion object {
        const val DATABASE_NAME = "app_database.db"
    }
}
