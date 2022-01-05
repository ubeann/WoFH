package com.wofh.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wofh.R
import com.wofh.database.dao.*
import com.wofh.entity.*
import java.util.concurrent.Executors

@Database(
    version = 1,
    entities = [
        User::class,
        Workout::class,
        Action::class
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun actionDao(): ActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "database.db"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(prePopulate)
                        .build()
                }
            }
            return INSTANCE as AppDatabase
        }

        private val prePopulate = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    INSTANCE?.workoutDao()?.insert(*workouts)
                }
            }
        }

        private val workouts = arrayOf(
            Workout(
                id = 1,
                name = "Dumbbell Raise",
                description = "4×8 reps",
                type = "bulk",
                image = R.drawable.bulk_dumbbell
            ),
            Workout(
                id = 2,
                name = "Pull Up",
                description = "4x8 reps",
                type = "bulk",
                image = R.drawable.bulk_pull
            ),
            Workout(
                id = 3,
                name = "Leg Extension",
                description = "4x8 reps",
                type = "bulk",
                image = R.drawable.bulk_leg
            ),
            Workout(
                id = 4,
                name = "Bicep Curl",
                description = "4x10 reps",
                type = "cut",
                image = R.drawable.cut_bicep
            ),
            Workout(
                id = 5,
                name = "Barbell Squat",
                description = "4x12 reps",
                type = "cut",
                image = R.drawable.cut_barbell
            ),
            Workout(
                id = 6,
                name = "Burpees",
                description = "4x12 reps",
                type = "cut",
                image = R.drawable.cut_burpees
            ),
            Workout(
                id = 7,
                name = "Bodyweight Squat",
                description = "4×8 reps",
                type = "recomp",
                image = R.drawable.recomp_bodyweight
            ),
            Workout(
                id = 8,
                name = "Palm Plank",
                description = "4×8 reps",
                type = "recomp",
                image = R.drawable.recomp_palm
            ),
            Workout(
                id = 9,
                name = "Bodyweight Deadlift",
                description = "4×8 reps",
                type = "recomp",
                image = R.drawable.recomp_deadlift
            ),
            Workout(
                id = 10,
                name = "Sleep",
                description = "7-9 hours",
                type = "diet",
                image = R.drawable.diet_sleep
            ),
            Workout(
                id = 11,
                name = "Eat",
                description = "More fruit and veggies",
                type = "diet",
                image = R.drawable.diet_eat
            ),
            Workout(
                id = 12,
                name = "Running Training",
                description = "10min",
                type = "diet",
                image = R.drawable.diet_running
            )
        )
    }
}