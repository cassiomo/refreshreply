package com.xzero.refreshreply.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
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
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignInActivity extends ActionBarActivity {
    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.etUsername)
    EditText etUsername;
    @InjectView(R.id.pbLoading)
    ProgressBar pbLoading;

    private String username;
    private String password;

    private boolean isFromPush;

    private Ad ad;

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

        // hack
        isFromPush = false;
        String adId = getIntent().getStringExtra("adId");

        // coming from the message application
        if (adId == null) {
            // coming from the push open
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                isFromPush = true;
                ad = (Ad) extras.getSerializable("ad");
                if (ad != null) {
                    adId = ad.getObjectId();
                }
            }
        } else {
            isFromPush = true;
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
            intent.putExtra("isFromPush", isFromPush);
            startActivity(intent);
        } else {
            Log.d("debug", "No user logged in!");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));

    }

//    @Override
//    protected void onActivityResult(int requestCode,
//                                    int resultCode, Intent data) {
//
//        if (requestCode == PLACE_PICKER_REQUEST
//                && resultCode == Activity.RESULT_OK) {
//
//            final Place place = PlacePicker.getPlace(data, this);
//            final CharSequence name = place.getName();
//            final CharSequence address = place.getAddress();
//            String attributions = PlacePicker.getAttributions(data);
//            if (attributions == null) {
//                attributions = "";
//            }
//
//            mName.setText(name);
//            mAddress.setText(address);
//            mAttributions.setText(Html.fromHtml(attributions));
//
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_signin, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
