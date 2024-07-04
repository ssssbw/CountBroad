package com.sbw.countboard

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
@description TODO
@author sbw
@create 2024-06-27 1:11
@version 1.0
 */

class UpdateEventsWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // 获取SharedPreferences
        val prefs = applicationContext.getSharedPreferences("events", Context.MODE_PRIVATE)
        val eventSet = prefs.getStringSet("event_set", setOf()) ?: setOf()

        // 创建SimpleDateFormat实例
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 获取当前日期
        val currentDate = Calendar.getInstance().time

        // 更新事件列表
        val updatedEvents = eventSet.map {
            val parts = it.split(",")
            val title = parts[0]
            val doneTime = parts[1]

//            val doneDate = sdf.parse(doneTime)
//            val leftTime = if (doneDate != null) {
//                val diffInMillis = doneDate.time - currentDate.time
//                val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
//                "$diffInDays days left"
//            } else {
//                "Invalid date"
//            }

            Event(title, doneTime)
        }

        // 保存更新后的事件列表
        val editor = prefs.edit()
        val updatedEventSet = updatedEvents.map { "${it.title},${it.doneTime},${it.leftTime}" }.toSet()
        editor.putStringSet("event_set", updatedEventSet)
        editor.apply()

        return Result.success()
    }
}
