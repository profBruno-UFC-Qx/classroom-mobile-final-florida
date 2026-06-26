package com.example.florida.di

import android.content.Context
import androidx.room.Room
import com.example.florida.persistence.AppDatabase
import com.example.florida.persistence.dao.BudgetDao
import com.example.florida.persistence.dao.ClientDao
import com.example.florida.persistence.dao.DashboardDao
import com.example.florida.persistence.dao.ReceiptDao
import com.example.florida.persistence.dao.UserDao
import com.example.florida.persistence.migration.AppMigrations
import com.example.florida.persistence.repository.BudgetRepository
import com.example.florida.persistence.repository.ClientRepository
import com.example.florida.persistence.repository.DashboardRepository
import com.example.florida.persistence.repository.ReceiptRepository
import com.example.florida.network.FloridaRemoteRepository
import com.example.florida.persistence.repository.SyncRepository
import com.example.florida.persistence.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(
                AppMigrations.MIGRATION_1_2,
                AppMigrations.MIGRATION_2_3,
                AppMigrations.MIGRATION_3_4,
                AppMigrations.MIGRATION_4_5,
                AppMigrations.MIGRATION_5_6
            )
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideClientDao(database: AppDatabase): ClientDao = database.clientDao()

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideReceiptDao(database: AppDatabase): ReceiptDao = database.receiptDao()

    @Provides
    fun provideDashboardDao(database: AppDatabase): DashboardDao = database.dashboardDao()

    @Provides
    fun provideUserRepository(
        userDao: UserDao,
        syncRepository: SyncRepository,
    ): UserRepository {
        return UserRepository(userDao, syncRepository)
    }

    @Provides
    fun provideClientRepository(
        clientDao: ClientDao,
        remoteRepository: FloridaRemoteRepository,
        syncRepository: SyncRepository,
    ): ClientRepository {
        return ClientRepository(clientDao, remoteRepository, syncRepository)
    }

    @Provides
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        remoteRepository: FloridaRemoteRepository,
        syncRepository: SyncRepository,
    ): BudgetRepository {
        return BudgetRepository(budgetDao, remoteRepository, syncRepository)
    }

    @Provides
    fun provideReceiptRepository(
        receiptDao: ReceiptDao,
        remoteRepository: FloridaRemoteRepository,
        syncRepository: SyncRepository,
    ): ReceiptRepository {
        return ReceiptRepository(receiptDao, remoteRepository, syncRepository)
    }

    @Provides
    fun provideSyncRepository(
        database: AppDatabase,
        userDao: UserDao,
        clientDao: ClientDao,
        budgetDao: BudgetDao,
        receiptDao: ReceiptDao,
        remoteRepository: FloridaRemoteRepository,
    ): SyncRepository {
        return SyncRepository(
            database = database,
            userDao = userDao,
            clientDao = clientDao,
            budgetDao = budgetDao,
            receiptDao = receiptDao,
            remoteRepository = remoteRepository
        )
    }

    @Provides
    fun provideDashboardRepository(
        clientDao: ClientDao,
        budgetDao: BudgetDao,
        receiptDao: ReceiptDao,
        dashboardDao: DashboardDao,
    ): DashboardRepository {
        return DashboardRepository(
            clientDao = clientDao,
            budgetDao = budgetDao,
            receiptDao = receiptDao,
            dashboardDao = dashboardDao
        )
    }
}
