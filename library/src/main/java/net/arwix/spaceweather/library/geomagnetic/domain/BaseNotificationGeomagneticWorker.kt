package net.arwix.spaceweather.library.geomagnetic.domain

import android.content.Context
import androidx.annotation.Keep
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import net.arwix.spaceweather.library.common.UpdateCheckerData
import net.arwix.spaceweather.library.geomagnetic.data.GeomagneticRepository

@Suppress("unused")
@Keep
abstract class BaseNotificationGeomagneticWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    protected abstract val alertChecker: NotificationGeomagneticChecker
    protected abstract val repository: GeomagneticRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val r = repository.update(true)
            if (r is UpdateCheckerData.UpdateResult.Success<*>) {
                alertChecker.check(repository.getFlow().first())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}