package com.wofh.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.wofh.entity.Action
import com.wofh.entity.Workout

data class UserAction(
    @Embedded
    val detailAction: Action,

    @Relation(
        parentColumn = "workout_id",
        entity = Workout::class,
        entityColumn = "id"
    )
    val workout: Workout
)