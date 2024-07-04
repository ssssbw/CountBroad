package com.sbw.countboard

/**
@description TODO
@author sbw
@create 2024-06-27 1:57
@version 1.0
 */
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.text.SimpleDateFormat
import java.util.*

class EventRemoteViewsFactory(private val context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    private val events = mutableListOf<Event>()

    override fun onCreate() {
        // 初始化数据
        loadEvents()
    }

    override fun onDataSetChanged() {
        // 刷新数据
        loadEvents()
    }

    override fun onDestroy() {
        events.clear()
    }

    override fun getCount(): Int {
        return events.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val event = events[position]
        val views = RemoteViews(context.packageName, R.layout.item_widget_event)
        views.setTextViewText(R.id.titleWidgetTv, event.title)
        views.setTextViewText(R.id.doneTimeWidgetTv, event.doneTime)
        views.setTextViewText(R.id.leftTimeWidgetTv, event.leftTime)

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun loadEvents() {
        events.clear()

        val prefs = context.getSharedPreferences("events", Context.MODE_PRIVATE)
        val eventSet = prefs.getStringSet("event_set", setOf()) ?: setOf()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Calendar.getInstance().time

        events.addAll(eventSet.map {
            val parts = it.split(",")
            val title = parts[0]
            val doneTime = parts[1]

            val doneDate = sdf.parse(doneTime)
            val leftTime = if (doneDate != null) {
                val diffInMillis = doneDate.time - currentDate.time
                val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                "$diffInDays days left"
            } else {
                "Invalid date"
            }

            Event(title, doneTime)
        })
    }
}
