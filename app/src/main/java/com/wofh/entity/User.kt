package com.wofh.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "user")
data class User (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "password")
    var password: String? = null,

    @ColumnInfo(name = "weight")
    var weight: Double?,

    @ColumnInfo(name = "height")
    var height: Double?,

    @ColumnInfo(name = "goal")
    var goal: Double?,

    @ColumnInfo(name = "created_at")
    var createdAt: OffsetDateTime
) : Parcelable