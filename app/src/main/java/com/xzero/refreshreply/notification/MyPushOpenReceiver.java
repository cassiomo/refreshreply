package com.xzero.refreshreply.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.xzero.refreshreply.activities.SignInActivity;

/**
 * Created by kemo on 5/15/15.
 */
public class MyPushOpenReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        //String adId = intent.getStringExtra("adId");

        Intent i = new Intent(context, SignInActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
