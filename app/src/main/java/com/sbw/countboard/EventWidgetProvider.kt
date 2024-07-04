package com.sbw.countboard

/**
@description TODO
@author sbw
@create 2024-06-27 2:12
@version 1.0
 */
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class EventWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, EventWidgetService::class.java)
            views.setRemoteAdapter(R.id.eventsLv, intent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}