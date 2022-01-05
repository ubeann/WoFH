package com.wofh.repository

import android.app.Application
import com.wofh.database.AppDatabase
import com.wofh.database.dao.WorkoutDao
import com.wofh.entity.Workout
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WorkoutRepository(application: Application) {
    private val mWorkoutDao: WorkoutDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        mWorkoutDao = db.workoutDao()
    }

    fun getAllWorkout() : List<Workout> = executorService.submit(Callable { mWorkoutDao.getAllWorkout() }).get()
}