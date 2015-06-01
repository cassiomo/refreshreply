package com.xzero.refreshreply.queries;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kemo on 5/29/15.
 */
public class MessageQuery {

    public static String mMessageId;

    public static void saveMessageInBackground(final String body, final Ad ad) {
        final Message message = new Message();
        String sUserId = ParseUser.getCurrentUser().getObjectId();
        String sUserName = ParseUser.getCurrentUser().getUsername();
        message.setUserId(sUserId);
        message.setUserName(sUserName);
        message.setBody(body);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (ad != null) {
                    // send Push notification
                    sendPush(body, ad, message);
                    mMessageId = message.getObjectId();
                }
            }
        });
    }

    public static void sendPush(String body, Ad ad, Message message) {

        JSONObject obj;
        try {
            obj = new JSONObject();
            obj.put("alert", "New Sale");
            obj.put("action", MyCustomReceiver.intentAction);
            obj.put("adId", ad.getObjectId());
            obj.put("messageId", message.getObjectId());

            ParsePush push = new ParsePush();
            ParseQuery query = ParseInstallation.getQuery();

            // Push the notification to Android users
            query.whereEqualTo("deviceType", "android");
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}