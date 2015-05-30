package com.xzero.refreshreply.fragments;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xzero.refreshreply.ExpandableMessageRowView;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.activities.AdActivity;
import com.xzero.refreshreply.adapters.ChatListAdapter;
import com.xzero.refreshreply.adapters.ImageResultAdapter;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;
import com.xzero.refreshreply.notification.MyCustomReceiver;
import com.xzero.refreshreply.queries.MessageQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageFragment extends Fragment {

    @InjectView(R.id.lvChat)
    ListView lvChat;
    @InjectView(R.id.vpChatHint)
    ViewPager vpChatHint;

    private static final String TAG = MessageFragment.class.getName();
    private static String sUserId;

    public static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 20;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();

    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    private Ad currentInterestedAd;
    public ExpandableMessageRowView adRow;

    public PagerAdapter mMapPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (currentInterestedAd == null) {
                currentInterestedAd = new Ad();
                currentInterestedAd.setCurrentStatus("Sale");
                currentInterestedAd.setPrice("$10");
                currentInterestedAd.setAddress("123123 Blacow Rd Fremont, CA");
                currentInterestedAd.setDescription("car for sell ");
                currentInterestedAd.setLocation(new ParseGeoPoint(37.50593151, -121.94696251));
                currentInterestedAd.setOwnerId("fINhuLpnZw");
                currentInterestedAd.setTitle("New car");
                currentInterestedAd.setPhotoUrl("http://thewowstyle.com/wp-content/uploads/2015/04/car-03.jpg");
            }

            adRow = new ExpandableMessageRowView(getActivity(), null);
            adRow.updateSubviews(currentInterestedAd, getActivity());
            container.addView(adRow);

            adRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adRow.onRowClick();
                    }
                });

            adRow.setTag(position);
            return adRow;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ExpandableMessageRowView prv = (ExpandableMessageRowView) object;
            container.removeView(prv);
        }

    };
    public ImageResultAdapter mAdResultAdapter;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DBG", "List fragment onCreate");

        super.onCreate(savedInstanceState);
        startWithCurrentUser();

       handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view);
        startWithCurrentUser();
        setupMessagePosting();

        vpChatHint.setAdapter(getViewPagerAdapter());

        vpChatHint.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                onAdPagerSwitchedPages(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
    }

    private void onAdPagerSwitchedPages(int i) {
        Ad ad = mAdResultAdapter.getAdAtIndex(i);
        currentInterestedAd = ad;
    }

    PagerAdapter getViewPagerAdapter() {
        return mMapPagerAdapter;
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
                    blinkChatIndicator(messages);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void blinkChatIndicator(List<Message> messages) {
        if (messages.size() > 0 ) {
            String senderId = messages.get(messages.size() - 1).getUserId();
            Log.d("message userId", senderId);
            Log.d("current userId", sUserId);
            if (!sUserId.equals(senderId)) {
                Log.d("FromYou", "FromYou");
                setConditionAndBlink(messages.get(messages.size() - 1).getBody());
            } else {
                Log.d("FromMe", "FromMe");
            }
        }
    }

    private void setupMessagePosting() {
        mMessages = new ArrayList<Message>();
        mAdapter = new ChatListAdapter(this.getActivity(), sUserId, mMessages);
        lvChat.setAdapter(mAdapter);
    }

    private void saveMessageInBackground(final String body, final Ad ad) {
        MessageQuery.saveMessageInBackground(body, ad);
    }


    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
    }

    public void saveMessageInBackground(Ad ad) {
        String body = "Interested: " + ad.getTitle();
        saveMessageInBackground(body, ad);
    }

    public void setCurrentInterestedAd(Ad ad) {
        currentInterestedAd = ad;
        if (adRow !=null) {
            adRow.updateSubviews(currentInterestedAd, getActivity());
        }
    }

    private void setConditionAndBlink(String message) {
        if (message !=null) {
            if (message.contains("where")) {
                adRow.mCondition = 0;
            } else if (message.contains("when")) {
                adRow.mCondition = 1;
            } else if (message.contains("anything")) {
                adRow.mCondition = 2;
            } else if (message.contains("2015")) {
                if (!AdActivity.isAlarmSet) {
                    setAlarm(message);
                    AdActivity.isAlarmSet = true;
                }
            }
        }
        // blink
    }

    private void setAlarm(String message) {
        try {
            DateFormat inputDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
            Date strToDate = inputDateFormat
                    .parse(message);

            Intent intent = new Intent(getActivity(), MyCustomReceiver.class);
            intent.setAction(MyCustomReceiver.ACTION_ALARM_RECEIVER);
            intent.putExtra("adId", currentInterestedAd.getObjectId());
            if (MessageQuery.mMessageId !=null) {
                intent.putExtra("messageId", MessageQuery.mMessageId);
            }

            PendingIntent pintent = PendingIntent.getBroadcast(getActivity(), 1002, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarm = (AlarmManager) (getActivity().getSystemService((Context.ALARM_SERVICE)));

            alarm.set(AlarmManager.RTC_WAKEUP, strToDate.getTime(), pintent);

            boolean isWorking = (PendingIntent.getBroadcast(getActivity(), 1002, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
            Log.d(TAG, "alarm " + (isWorking ? "" : "not") + " working...");
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        Log.d("DBG", "Message fragment resuming.");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DBG", "Message fragment onPause");
    }

    public void setCurrentlyDisplayedAd(Ad ad) {
        if (vpChatHint !=null) {
            vpChatHint.setCurrentItem(0, true);
        }
    }

    public void setMessagText(String message) {
        adRow.etMessage.setText(message);
    }

}
