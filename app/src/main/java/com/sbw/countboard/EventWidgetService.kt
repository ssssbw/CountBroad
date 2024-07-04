package com.sbw.countboard

/**
@description TODO
@author sbw
@create 2024-06-27 1:57
@version 1.0
 */
import android.content.Intent
import android.widget.RemoteViewsService

class EventWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return EventRemoteViewsFactory(this.applicationContext, intent)
    }
}
