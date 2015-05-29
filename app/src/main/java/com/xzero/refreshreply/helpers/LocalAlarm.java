package com.xzero.refreshreply.helpers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xzero.refreshreply.notification.MyCustomReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kemo on 5/27/15.
 */
public class LocalAlarm {

    public boolean isAlarmSet = false;

    private void setAlarm(String message, Activity context) {
        try {
            DateFormat inputDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
            Date strToDate = inputDateFormat
                    .parse(message);

            Intent intent = new Intent(context, MyCustomReceiver.class);
            intent.setAction(MyCustomReceiver.ACTION_ALARM_RECEIVER);

            PendingIntent pintent = PendingIntent.getBroadcast(context, 1002, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarm = (AlarmManager) (context.getSystemService((Context.ALARM_SERVICE)));

            alarm.set(AlarmManager.RTC_WAKEUP, strToDate.getTime(), pintent);

            boolean isWorking = (PendingIntent.getBroadcast(context, 1002, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
            Log.d("alarm", "alarm " + (isWorking ? "" : "not") + " working...");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }
}
