package com.wofh.database.dao

import androidx.room.*
import com.wofh.entity.Action
import com.wofh.entity.relation.UserAction
import java.time.OffsetDateTime

@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg action: Action)

    @Update
    fun update(action: Action)

    @Transaction
    @Query("SELECT * from `action` WHERE `user_id` = :userId AND DATETIME(`date_time`) >= DATETIME(:day) ORDER BY `workout_type` ASC, `is_cleared` DESC")
    fun getUserAction(userId: Int, day: OffsetDateTime) : List<UserAction>

    @Transaction
    @Query("SELECT * from `action` WHERE `user_id` = :userId AND `workout_type` = :filter AND DATETIME(`date_time`) >= DATETIME(:day) ORDER BY `workout_type` ASC, `is_cleared` DESC")
    fun getUserActionByFilter(userId: Int, filter: String, day: OffsetDateTime) : List<UserAction>
}