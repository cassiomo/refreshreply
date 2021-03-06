package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.helpers.NetworkUtil;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import butterknife.ButterKnife;
import butterknife.InjectView;

//public class SignInActivity extends ActionBarActivity {
public class SignInActivity extends Activity  {
    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.etUsername)
    EditText etUsername;
    @InjectView(R.id.pbLoading)
    ProgressBar pbLoading;

    private String username;
    private String password;

    private String alarmTime;

    private String mAdId;
    private String mMessageId;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //getActionBar().hide();
        ButterKnife.inject(this);

        Intent receviedIntent = getIntent();
        String adId = receviedIntent.getStringExtra("adId");
        Bundle extras = receviedIntent.getExtras();
        if (extras != null){
            mAdId = extras.getString("adId");
            mMessageId = extras.getString("messageId");
            alarmTime = extras.getString("alarm");
        }

        final VideoView mVideoView = (VideoView) findViewById(R.id.vvMovieBackground);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.reply));
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.setMediaController(null);

        //Video Loop
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start(); //need to make transition seamless.
            }
        });

        mVideoView.start();
    }

    @Override
    public void onResume() {

        super.onResume();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d("debug", "Current user is " + currentUser.toString());
            Intent intent = new Intent(getApplicationContext(), AdActivity.class);
            intent.putExtra("adId", mAdId);
            intent.putExtra("messageId", mMessageId);
            intent.putExtra("alarm", alarmTime);
            startActivity(intent);
        } else {
            Log.d("debug", "No user logged in!");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));

    }

    public void onSignIn(View v) {

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (isInvalidInput()) {
            return;
        }

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        // If no network is available, Toast the user and do nothing.
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this,
                    "Not connected to network. This app needs Internet connection to start.",
                    Toast.LENGTH_LONG).show();
            pbLoading.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    Log.d("Info", "Current Parse User: " + ParseUser.getCurrentUser().toString());

                    // select theme
                    startActivity(new Intent(getApplicationContext(), AdActivity.class));
                } else {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    if (e != null) {
                        Log.d("info", "Parse login failed: " + e.toString());
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            Toast.makeText(getApplicationContext(),
                                    "The username and password did not match.",
                                    Toast.LENGTH_SHORT).show();
                            etPassword.selectAll();
                            etPassword.requestFocus();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Could not login. Please try again later.",
                                    Toast.LENGTH_LONG).show();
                            Log.d("SignInActivity", "error logging into Parse: " + e.toString());
                        }
                    }
                } // else failed login
            } //done
        });
    }

//    public void selectTheme(ParseUser parseUser, Activity activity) {
//        if (parseUser.getUsername().equals("june")) {
//            ParseApplication.currentPosition = 1;
//        } else {
//            ParseApplication.currentPosition = 0;
//        }
//        Utils.changeToTheme(activity, ParseApplication.currentPosition);
//    }

    private boolean isInvalidInput() {

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

}
