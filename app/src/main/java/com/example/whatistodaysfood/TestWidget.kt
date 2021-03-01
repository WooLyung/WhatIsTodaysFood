package com.example.whatistodaysfood

import com.example.whatistodaysfood.R
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TestWidget() : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        val views = RemoteViews(context!!.packageName, R.layout.widget)
        val load : LoadText = LoadText(views, appWidgetManager, appWidgetIds, context);
        load.execute()
    }
}