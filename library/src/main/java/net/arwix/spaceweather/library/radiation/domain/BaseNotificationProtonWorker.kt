package net.arwix.spaceweather.library.radiation.domain

import android.content.Context
import androidx.annotation.Keep
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.radiation.data.ProtonRepository

@Suppress("unused")
@Keep
abstract class BaseNotificationProtonWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    protected abstract val alertChecker: NotificationProtonAlertChecker
    protected abstract val repository: ProtonRepository

    override suspend fun doWork(): Result = coroutineScope {
        val r = repository.update(true)
        try {
            if (r is UpdateCheckerData.UpdateResult.Success<*>) {
                alertChecker.check(repository.getFlow().first())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}