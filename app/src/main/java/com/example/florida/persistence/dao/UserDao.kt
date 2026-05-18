package com.example.florida.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.florida.persistence.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_setup WHERE id = 1 LIMIT 1")
    fun getUserSetupFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM user_setup WHERE id = 1 LIMIT 1")
    suspend fun getUserSetup(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM user_setup WHERE id = 1")
    suspend fun deleteUser()

    @Query("SELECT EXISTS(SELECT 1 FROM user_setup LIMIT 1)")
    fun hasUser(): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM user_setup LIMIT 1)")
    suspend fun hasUserSuspend(): Boolean
}