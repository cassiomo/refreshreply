package com.xzero.refreshreply.fragments;


import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.activities.ChatActivity;
import com.xzero.refreshreply.adapters.ChatListAdapter;
import com.xzero.refreshreply.models.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageFragment extends Fragment  {

    @InjectView(R.id.etMessage)
    EditText etMessage;
    @InjectView(R.id.btSend)
    Button btSend;
    @InjectView(R.id.lvChat)
    ListView lvChat;

    private static final String TAG = ChatActivity.class.getName();
    private static String sUserId;

    public static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    //private EditText etMessage;
    //private Button btSend;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();

    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

//    @Override
//    public void onResume() {
//        Log.d("DBG", "Message fragment onResume");
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d("DBG", "Message fragment onPause");
//    }

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
                // Use Message model to create new messages now
                Message message = new Message();
                message.setUserId(sUserId);
                message.setBody(body);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                etMessage.setText("");

//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        });
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        //setupMessagePosting();
    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }
}
