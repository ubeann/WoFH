package com.wofh.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wofh.entity.Workout

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg workout: Workout)

    @Query("SELECT * from `workout`")
    fun getAllWorkout() : List<Workout>
}