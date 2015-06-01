package com.xzero.refreshreply.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.queries.MessageQuery;

import java.util.Calendar;

/**
 * Created by kemo on 5/29/15.
 */
public class LocalAlarmManager {

    private static final String TAG = "LocalAlarmManager";

    public static void setLocalAlarm(Context mContext, Ad currentInterestedAd, long timeInMillis, int requestCode) {

        Calendar dateAndTime = Calendar.getInstance();

        AlarmManager alarm = (AlarmManager)(((Activity)mContext).getSystemService((Context.ALARM_SERVICE)));

        Intent intent = new Intent(mContext, MyCustomReceiver.class);
        intent.setAction(MyCustomReceiver.ACTION_ALARM_RECEIVER);
        String adId = currentInterestedAd.getObjectId();
        intent.putExtra("adId", adId);
        if (MessageQuery.mMessageId !=null) {
            intent.putExtra("messageId", MessageQuery.mMessageId);
        }
        intent.putExtra("alarm", String.valueOf(timeInMillis));

        PendingIntent pintent = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarm.set(AlarmManager.RTC_WAKEUP, timeInMillis, pintent);

        boolean isWorking = (PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.d(TAG, "alarm " + (isWorking ? "" : "not") + " working...");
    }
}
