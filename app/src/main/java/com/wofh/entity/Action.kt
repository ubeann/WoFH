package com.wofh.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(
    tableName = "action",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = CASCADE
        )
    ]
)
data class Action (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "workout_id")
    var workoutId: Int,

    @ColumnInfo(name = "workout_type")
    var workoutType: String,

    @ColumnInfo(name = "date_time")
    var dateTime: OffsetDateTime,

    @ColumnInfo(name = "is_cleared")
    var isCleared: Boolean
) : Parcelable