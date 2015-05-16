package com.xzero.refreshreply.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.adapters.ChatListAdapter;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

//import android.support.v4.app.Fragment;

public class MessageFragment extends Fragment  {

    @InjectView(R.id.etMessage)
    EditText etMessage;
    @InjectView(R.id.btSend)
    Button btSend;
    @InjectView(R.id.lvChat)
    ListView lvChat;

    private static final String TAG = MessageFragment.class.getName();
    private static String sUserId;

    public static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 1000;

    //private EditText etMessage;
    //private Button btSend;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();

    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    private Ad currentInterestedAd;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DBG", "List fragment onCreate");

        super.onCreate(savedInstanceState);
        startWithCurrentUser();
       // Run the runnable object defined every 100ms
       handler.postDelayed(runnable, 100);
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view);
        startWithCurrentUser();
        setupMessagePosting();
        return view;
    }

    private void refreshMessages() {
        receiveMessage();
    }

    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        // Execute query for messages asynchronously
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    lvChat.invalidate();
                    if (messages.size() > 0 ) {
                        String senderId = messages.get(messages.size() - 1).getUserId();
                        Log.d("message userId", senderId);
                        Log.d("current userId", sUserId);
                        if (!sUserId.equals(senderId)) {
                            Log.d("FromYou", "FromYou");
                            if (messages.get(messages.size() - 1).getBody().contains("when")) {
                                Log.d("Found when", "Found when");
                                // pop up the map and log the time to calendar
                            }
                        } else {
                            Log.d("FromMe", "FromMe");
                        }
                    }

                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void setupMessagePosting() {
        mMessages = new ArrayList<Message>();
        mAdapter = new ChatListAdapter(this.getActivity(), sUserId, mMessages);
        lvChat.setAdapter(mAdapter);
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                saveMessageInBackground(body, null);

//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        });
    }

    private void saveMessageInBackground(final String body, final Ad ad) {
        Message message = new Message();
        message.setUserId(sUserId);
        message.setBody(body);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                receiveMessage();

                if (ad !=null) {
                    // send Push notification
                    sendPush(body, ad);
                }
            }
        });
        etMessage.setText("");
    }

    private void sendPush(String body, Ad ad) {

        JSONObject obj;
        try {
            obj = new JSONObject();
            obj.put("alert", "New Sale");
            obj.put("action", MyCustomReceiver.intentAction);
            obj.put("adId", ad.getObjectId());

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

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        //setupMessagePosting();
    }

    public void setCurrentInterestedAd(Ad ad) {
        String body = "Interested " + ad.getDescription() + " When?";
        saveMessageInBackground(body, ad);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        Log.d("DBG", "Message fragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DBG", "Message fragment onPause");
    }
}
