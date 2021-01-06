package net.arwix.spaceweather.library.xray.domain

import android.content.Context
import androidx.annotation.Keep
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.xray.data.XRayRepository

@Suppress("unused")
@Keep
abstract class BaseNotificationXRayFlareWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    protected abstract val alertChecker: NotificationXRayFlareChecker
    protected abstract val repository: XRayRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val r = repository.update(true)
            if (r is UpdateCheckerData.UpdateResult.Success<*>) {
                alertChecker.check(repository.getFlareFlow().first())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}