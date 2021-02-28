package com.example.whatistodaysfood;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

class LoadText extends AsyncTask<String, Void, Elements> {
    private RemoteViews views;
    private AppWidgetManager manager;
    private int[] ids;

    public LoadText(RemoteViews views, AppWidgetManager manager, int[] ids) {
        this.views = views;
        this.manager = manager;
        this.ids = ids;
    }

    @Override
    protected Elements doInBackground(String... params) {
        try {
            Document document = Jsoup.connect("https://dorm.knu.ac.kr/_new_ver/newlife/05.php?get_mode=4").get();
            Elements elements = document.select("td.txt_right>p");

            return elements;
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Elements result) {
        String breakfast = result.get(0).text().replace(", ", "\n");
        String lunch = result.get(1).text().replace(", ", "\n");
        String dinner = result.get(2).text().replace(", ", "\n");

        views.setTextViewText(R.id.breakfast, breakfast);
        views.setTextViewText(R.id.lunch, lunch);
        views.setTextViewText(R.id.dinner, dinner);

        manager.updateAppWidget(ids, views);
    }
}