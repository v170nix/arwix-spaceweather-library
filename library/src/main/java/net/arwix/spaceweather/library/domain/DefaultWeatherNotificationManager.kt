@file:Suppress("unused")

package net.arwix.spaceweather.library.domain

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.app.NotificationManagerCompat
import net.arwix.extension.getTextRect
import net.arwix.notification.createNotificationChannels
import net.arwix.notification.notification
import net.arwix.spaceweather.library.R
import net.arwix.spaceweather.library.geomagnetic.data.KpIndexData
import net.arwix.spaceweather.library.radiation.data.ProtonData
import net.arwix.spaceweather.library.xray.data.XRayFlareEventData

class DefaultWeatherNotificationManager(
    private val applicationContext: Context,
    private val config: Config,
    private val activityClass: Class<out Activity>,
    private val alertColors: WeatherAlertColors
): WeatherNotification {

    data class Config(
        val channelId: String = "SpaceWeather.notification.activity.id",
        val geomagneticIdAction: IdAction,
        val radiationIdAction: IdAction,
        val flareIdAction: IdAction
    ) {
        data class IdAction(val id: Int, val action: String)
    }

    init {
        createNotificationChannels(applicationContext) {
            channel(
                config.channelId,
                applicationContext.getString(R.string.space_weather_notification_channel_solar_activity),
                importance = NotificationManagerCompat.IMPORTANCE_LOW
            ) {
                vibrationEnabled = false
            }
        }
    }

    override fun doGeomagneticNotify(kpIndexData: KpIndexData) {
        getManager()?.notify(config.geomagneticIdAction.id,
            notification(applicationContext, config.channelId, R.drawable.ic_geo) {

                val index = kpIndexData.getIntIndex().takeIf { it > 3 } ?: return
                val largeIcon = createNotificationIcon(
                    alertColors.getGeomagneticColor(index),
                    if (index > 4) "G${index - 4}" else "A"
                )

                contentTitle(applicationContext.getString(R.string.space_weather_notification_geomagnetic_header))
                subText(if (index > 4) "G${index - 4}" else "Kp4")
                contentText(applicationContext.resources.getStringArray(R.array.space_weather_notification_geomagnetic_info)[index - 4])
                whenTime(kpIndexData.time * 1000L)
                largeIcon(largeIcon)
                contentIntent(createIntent(config.geomagneticIdAction.action))
                autoCancel(true)
            }
        )
    }

    override fun doProtonNotify(protonData: ProtonData) {
        getManager()?.notify(config.radiationIdAction.id,
            notification(
                applicationContext,
                config.channelId,
                R.drawable.ic_solar_radiation
            ) {

                val index = protonData.getIntIndex()
                val largeIcon = createNotificationIcon(
                    alertColors.getRadiationColor(index),
                    "S$index"
                )

                contentTitle(applicationContext.getString(R.string.space_weather_notification_geomagnetic_header))
                subText("S$index")
                contentText(applicationContext.resources.getStringArray(R.array.space_weather_notification_radiation_info)[index])
                whenTime(protonData.time * 1000L)
                largeIcon(largeIcon)
                contentIntent(createIntent(config.radiationIdAction.action))
                autoCancel(true)
            }
        )
    }

    override fun doFlareNotify(flareData: XRayFlareEventData) {
        getManager()?.notify(config.flareIdAction.id,
            notification(
                applicationContext,
                config.channelId,
                R.drawable.ic_solar_flare
            ) {
                contentTitle(applicationContext.getString(R.string.space_weather_notification_solar_flare_header))

                val eventClass = flareData.maxClass.orEmpty().trim()
                val isMaxEvent = eventClass.isNotEmpty()

                if (isMaxEvent) {
                    val maxValue = eventClass.substring(1).toDoubleOrNull() ?: 0.0
                    val index = when (eventClass.substring(0, 1)) {
                        "M" -> if (maxValue < 5.0) 1 else 2
                        "X" -> if (maxValue < 10.0) 3 else if (maxValue < 20.0) 4 else 5
                        else -> 0
                    }
                    subText(eventClass)
                    contentText(
                        applicationContext.getString(
                            R.string.space_weather_notification_solar_flare_context,
                            eventClass
                        )
                    )
                    whenTime(flareData.maxTime ?: 0L)
                    largeIcon(
                        createNotificationIcon(
                            alertColors.getFlareColor(eventClass),
                            "R$index"
                        )
                    )
                } else {
                    contentText(applicationContext.getString(R.string.space_weather_notification_solar_flare_begin_event_context))
                    whenTime(flareData.beginTime)
                    largeIcon(
                        createNotificationIcon(
                            alertColors[WeatherAlertColors.TypeColor.Active],
                            " "
                        )
                    )
                }
                contentIntent(createIntent(config.flareIdAction.action))
                autoCancel(true)
            }
        )
    }

    private fun getManager() =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager


    private fun createIntent(keyAction: String) =
        PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, activityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                action = keyAction
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun createNotificationIcon(@ColorInt color: Int, text: String): Bitmap {
        val size = (24f * applicationContext.resources.displayMetrics.density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
        val arcPaint = Paint(paint)
        arcPaint.color = color
        arcPaint.alpha = (0.7 * 255).toInt()
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, arcPaint)
        arcPaint.alpha = 255
        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size / 2f - 2f * applicationContext.resources.displayMetrics.density,
            arcPaint
        )

        paint.textSize = bitmap.width / 2.5f
        paint.isFakeBoldText = true
        paint.color = Color.WHITE
        val bounds = paint.getTextRect(text)
        val widthArray = FloatArray(2)
        paint.getTextWidths(text.first().toString(), widthArray)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            text,
            bitmap.width / 2f,
            bitmap.height / 2f + bounds.height() / 2f,
            paint
        )
        return bitmap
    }

    private companion object {
//        private const val NOTIFICATION_CHANNEL_ACTIVITY_ID = "SpaceWeather.notification.activity.id"
//        const val NOTIFICATION_GEOMAGNETIC_ID = 484
//        const val NOTIFICATION_RADIATION_ID = 485
//        const val NOTIFICATION_RADIO_ID = 486
//        const val INTENT_GEOMAGNETIC_KEY_ACTION = "SpaceWeather.notification.intent.geomagnetic"
//        const val INTENT_RADIATION_KEY_ACTION = "SpaceWeather.notification.intent.radiation"
//        const val INTENT_X_RAY_KEY_ACTION = "SpaceWeather.notification.intent.x_ray.Notification"
    }

}