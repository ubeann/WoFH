package com.wofh.repository

import android.app.Application
import com.wofh.database.AppDatabase
import com.wofh.database.dao.UserDao
import com.wofh.entity.User
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun getUserByEmail(email: String): User = executorService.submit(Callable { mUserDao.getUserByEmail(email) }).get()

    fun isEmailRegistered(email: String): Boolean = executorService.submit(Callable { mUserDao.isEmailRegistered(email) }).get()

    fun insert(user: User) {
        executorService.execute { mUserDao.insert(user) }
    }

    fun update(user: User) {
        executorService.execute { mUserDao.update(user) }
    }
}