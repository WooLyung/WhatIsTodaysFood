package com.example.whatistodaysfood;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class LoadText extends AsyncTask<String, Void, Elements> {
    private RemoteViews views;
    private AppWidgetManager manager;
    private int[] ids;
    private Context context;

    private String breakfast = "";
    private String lunch = "";
    private String dinner = "";

    private class NoticeThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 29; i++) {
                try {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dayFormat = new SimpleDateFormat("MM:dd");
                    String formatedDay = dayFormat.format(date);
                    String[] day = formatedDay.split(":");

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String formatedTime = timeFormat.format(date);

                    String menu = "null";
                    String korTime = "";

                    if (formatedTime.compareTo("06:30") == 0) {
                        menu = "breakfast";
                        korTime = "아침";
                    }
                    else if (formatedTime.compareTo("10:30") == 0) {
                        menu = "lunch";
                        korTime = "점심";
                    }
                    else if (formatedTime.compareTo("00:33") == 0) {
                        menu = "dinner";
                        korTime = "저녁";
                    }

                    if (menu.compareTo("null") != 0) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "def");
                        builder.setSmallIcon(R.drawable.symbol)
                                .setContentTitle("(" + Integer.parseInt(day[0]) + "월 " + Integer.parseInt(day[1]) + "일) 누리관 " + korTime + " 메뉴")
                                .setAutoCancel(true)
                                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        if (menu.compareTo("breakfast") == 0) builder.setStyle(new NotificationCompat.BigTextStyle().bigText(breakfast));
                        else if (menu.compareTo("lunch") == 0) builder.setStyle(new NotificationCompat.BigTextStyle().bigText(lunch));
                        else if (menu.compareTo("dinner") == 0) builder.setStyle(new NotificationCompat.BigTextStyle().bigText(dinner));

                        Notification notification = builder.build();
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationManager.createNotificationChannel(new NotificationChannel("def", "def", NotificationManager.IMPORTANCE_DEFAULT));
                        }
                        notificationManager.notify(1, notification);
                    }
                    Thread.sleep(60000);
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

    public LoadText(RemoteViews views, AppWidgetManager manager, int[] ids, Context context) {
        this.views = views;
        this.manager = manager;
        this.ids = ids;
        this.context = context;
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
        breakfast = result.get(0).text();
        lunch = result.get(1).text();
        dinner = result.get(2).text();

        views.setTextViewText(R.id.breakfast, breakfast.replace(",", "\n"));
        views.setTextViewText(R.id.lunch, lunch.replace(",", "\n"));
        views.setTextViewText(R.id.dinner, dinner.replace(",", "\n"));

        manager.updateAppWidget(ids, views);

        new NoticeThread().start();
    }
}