package com.wofh.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wofh.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * from user WHERE email = :email ORDER BY id ASC LIMIT 1")
    fun getUserByEmailLive(email: String): LiveData<User>

    @Query("SELECT * from user WHERE email = :email ORDER BY id ASC LIMIT 1")
    fun getUserByEmail(email: String): User

    @Query("SELECT EXISTS (SELECT * from user WHERE email = :email)")
    fun isEmailRegistered(email: String): Boolean
}