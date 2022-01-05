package com.wofh.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.wofh.database.AppDatabase
import com.wofh.database.dao.ActionDao
import com.wofh.entity.Action
import com.wofh.entity.relation.UserAction
import java.time.OffsetDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ActionRepository(application: Application) {
    private val mActionDao: ActionDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        mActionDao = db.actionDao()
    }

    fun insert(vararg action: Action) {
        executorService.execute { mActionDao.insert(*action) }
    }

    fun update(action: Action) {
        executorService.execute { mActionDao.update(action) }
    }

    fun getUserAction(userId: Int, day: OffsetDateTime) : List<UserAction> = executorService.submit(Callable { mActionDao.getUserAction(userId, day) }).get()

    fun getUserActionByFilter(userId: Int, filter: String, day: OffsetDateTime) : List<UserAction> = executorService.submit(Callable { mActionDao.getUserActionByFilter(userId, filter, day) }).get()
}