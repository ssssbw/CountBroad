package com.sbw.countboard

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
@description TODO
@author sbw
@create 2024-06-26 23:28
@version 1.0
 */
data class Event(val title: String = "", val doneTime: String = ""){
    val leftTime: String
    get() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val doneDate = sdf.parse(doneTime)
        val currentDate = Calendar.getInstance().time
        return if (doneDate != null) {
            val diffInMillis = doneDate.time - currentDate.time
            val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
            "${diffInDays}天"
        } else {
            "日期非法"
        }
    }
}
