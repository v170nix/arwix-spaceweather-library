package net.arwix.spaceweather.library.common

import android.content.SharedPreferences
import org.threeten.bp.Instant

class UpdateCheckerData(
    private val preferences: SharedPreferences,
    private val key: String
) {
    fun getLastSuccessTime(): Long? {
        val data = preferences.getLong(key, -1)
        if (data == -1L) return null
        return data
    }

    fun saveSuccessUpdateTime(time: Long) {
        preferences.edit().putLong(key, time).apply()
    }

    fun saveSuccessUpdateTime() {
        preferences.edit().putLong(key, Instant.now().epochSecond).apply()
    }

    fun isUpdateTime(delta: Long): Boolean {
        val successTime = getLastSuccessTime() ?: return true
        return successTime < (Instant.now().epochSecond - delta)
    }

    sealed class UpdateResult {
        object IsNotUpdateTime : UpdateResult()
        data class Success<T>(val values: T) : UpdateResult()
        data class Failure(val throwable: Throwable) : UpdateResult()
    }
}

