package com.example.flus.ponto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Implementation of App Widget functionality.
 */
public class PontoWidget extends AppWidgetProvider {

    private static final String TAG = "FLUSSS";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        String dateString = new SimpleDateFormat("H:mm").format(new Date());
//
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ponto_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.appwidget_id_label, sp.getString("LastPonto", "-"));

        NotificationChannel serviceChannel = new NotificationChannel("PONTOSERVICE", "Servico do ponto", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

//        Intent intentUpdate = new Intent(context, PontoWidget.class);
//        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] idArray = new int[]{appWidgetId};
//        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
//        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
//                context, appWidgetId, intentUpdate,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.button2, pendingUpdate);


/*
        Intent pontoServiceIntent = new Intent(context, MyReceiver.class);
        pontoServiceIntent.setAction(PontoIntentService.ACTION_MARCAR);
        pontoServiceIntent.putExtra("appWidgetId", appWidgetId);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, pontoServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button2, pendingUpdate);
*/

        Intent pontoServiceIntent = new Intent(context, PontoIntentService.class);
        pontoServiceIntent.setAction(PontoIntentService.ACTION_MARCAR);
        pontoServiceIntent.putExtra("appWidgetId", appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getForegroundService(context, appWidgetId, pontoServiceIntent, FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button2, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

