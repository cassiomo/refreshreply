package com.xzero.refreshreply;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.xzero.refreshreply.helpers.GPSTracker;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.notification.LocalAlarmManager;
import com.xzero.refreshreply.queries.MessageQuery;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;

public class ExpandableMessageRowView extends RelativeLayout implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public Ad currentInterestedAd;

    public static final int TARGET_DETAILS_HEIGHT = 600;
    public static final int ANIMATE_IN_DURATION_MILLIS = 300;
    public static final int ANIMATE_OUT_DURATION_MILLIS = 500;
    public static final int CIRCULAR_REVEAL_DURATION_NAVIGATE = 500;


    private MapFragment mapFragment;

    private Context mContext;

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    View fabStarAd;
    View mNavigationOverlayViewToBeRevealed;

    TextView mtvLocationLabel;
    TextView mtvPriceLabel;
    TextView mtvRemember1;
    TextView mtvRemember2;
    ImageView mIvAd1;
    ImageView mIvAd2;

    private ViewGroup detailsContainer;

    private ViewGroup detailsChat;
    private Button btSend;
    public EditText etMessage;
    private Button btSuggestedPrice;

    private ViewHolder viewHolder;

    private static final String TAG = "ExpandableRowView";

    public static final double MAP_DISPLAY_DELTA = 0.03;

    public int mCondition = 0;

    public ExpandableMessageRowView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.expandable_row_ad, this);

        detailsChat = (ViewGroup) findViewById(R.id.llSend);

        btSend = (Button) findViewById(R.id.btSend);
        etMessage = (EditText) findViewById(R.id.etMessage);

        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();

                saveMessageInBackground(body, null);

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
            }
        });


        detailsContainer = (ViewGroup) findViewById(R.id.vgDetailsContainer);
        detailsContainer.setVisibility(View.GONE);
        mtvLocationLabel = (TextView) findViewById(R.id.tvLocationDescription);
        mNavigationOverlayViewToBeRevealed = findViewById(R.id.viewToBeRevealed);
        mtvPriceLabel = (TextView) findViewById(R.id.tvAdPrice);
        mtvRemember1 = (TextView) findViewById(R.id.tvRemember1);
        mtvRemember2 = (TextView) findViewById(R.id.tvRemember2);
        mIvAd1 = (ImageView) findViewById(R.id.ivAd1);
        mIvAd2 = (ImageView) findViewById(R.id.ivAd2);

        btSuggestedPrice = (Button) findViewById(R.id.btSuggested);
        btSuggestedPrice.setVisibility(INVISIBLE);

        viewHolder = new ViewHolder();
        mContext = context;

        populateViewHolder();
        setupChatHintButton();
    }

    public void loadMap(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        // Now that map has loaded, let's get our location!
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        connectClient();
    }

    protected void connectClient() {
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("RowView", "Google Play services is available.");
            return true;
        } else {
            Log.d("RowView", "Google Play services is not available.");
            return false;
        }
    }

    Calendar dateAndTime = Calendar.getInstance();

    DateFormat fmtDateAndTime= DateFormat.getDateTimeInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    private void updateLabel() {

        // buyer
         etMessage.setText(fmtDateAndTime.format(dateAndTime.getTime()));

        LocalAlarmManager.setLocalAlarm(mContext, currentInterestedAd,
                dateAndTime.getTimeInMillis(), Constant.SENDER_REQUEST_CODE);
    }

    private void setupChatHintButton() {
        fabStarAd = findViewById(R.id.ibChatHint);

        fabStarAd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                performOnClick();

            }
        });
    }

    private void performOnClick() {
        switch (mCondition) {
            // 0 : PlacePicker (where)
            case 0:
                double lat = 37.3770091;
                double longitude = 37.3770091;
                LatLng position;

                GPSTracker tracker = new GPSTracker(mContext);
                if (tracker.canGetLocation() == false) {
                    tracker.showSettingsAlert();
                } else {
                    lat = tracker.getLatitude();
                    longitude = tracker.getLongitude();
                }

                LatLng positionTopLeft = new LatLng(lat - MAP_DISPLAY_DELTA, longitude - MAP_DISPLAY_DELTA);
                LatLng fartherAwayPosition = new LatLng(lat + MAP_DISPLAY_DELTA, longitude + MAP_DISPLAY_DELTA);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(positionTopLeft);
                builder.include(fartherAwayPosition);
                LatLngBounds bounds = builder.build();

                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(bounds);
                Intent intent = null;
                try {
                    intent = intentBuilder.build(mContext);

                    ((Activity)mContext).startActivityForResult(intent, Constant.PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            // 1 : TimePicker (when)
            case 1:
                new TimePickerDialog(mContext,
                        t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true).show();
                break;
            // 2: AdPicker (what)
            case 2:

                break;
            // 3: PricePicker (how much)
            case 3:
                btSuggestedPrice.setVisibility(VISIBLE);
                Random random = new Random();
                int Low = 10;
                int High = 100;
                int suggested = random.nextInt(High-Low) + Low;
                btSuggestedPrice.setText("$" + suggested);
                break;
            // 4: set the local notifcation time
            case 4:

            default:
                btSuggestedPrice.setVisibility(INVISIBLE);
 //                Intent i = new Intent(mContext, MapActivity.class);
//                mContext.startActivity(i);

//                if (getMap() != null) {
//
//                    MarkerOptions options = new MarkerOptions();
//                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.me));
//
//                    position = new LatLng(lat, longitude);
//                    options.position(position);
//                    options.title(ME_TITLE);
//                    getMap().addMarker(options);
//
//                    getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
//                }
                break;
        }
    }

    public void onRowClick() {
        toggleExpandedState();
    }

    public void toggleExpandedState() {
        boolean expanded = detailsContainer.getVisibility() == View.VISIBLE;
        if (expanded) {
            //beginAnimationToRevealStarPumpFAB();
            DropDownAnim anim = new DropDownAnim(detailsContainer, TARGET_DETAILS_HEIGHT, false);
            anim.setDuration(ANIMATE_OUT_DURATION_MILLIS);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    detailsContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            detailsContainer.startAnimation(anim);
            switch (mCondition) {
                case 0:
                    mtvRemember1.setText("1600 Amphitheatre Pkwy, Mountain View, CA 94043");
                    mtvRemember2.setText("2211 N 1st St, San Jose, CA 95131");
                    break;
                case 1:
                    mtvRemember1.setText("Tomorrow 12:30pm");
                    mtvRemember2.setText("Today 5:00pm");
                    break;
                case 2:

                    String sUserName = ParseUser.getCurrentUser().getUsername();
                    if (sUserName.equals("june")) {
                        mtvRemember1.setText("New Car $10");
                        mtvRemember2.setText("New Car mug $10");
                        Picasso.with(getContext()).load(Constant.newCarUrl).into(mIvAd1);
                        Picasso.with(getContext()).load(Constant.newMugUrl).into(mIvAd2);
                    } else {
                        mtvRemember1.setText("Old Car $20");
                        mtvRemember2.setText("Old Car mug $20");
                        Picasso.with(getContext()).load(Constant.oldCarUrl).into(mIvAd1);
                        Picasso.with(getContext()).load(Constant.oldMugUrl).into(mIvAd2);
                    }

                    break;
                default:
                    // nothing.
            }
        } else {
            DropDownAnim anim = new DropDownAnim(detailsContainer, TARGET_DETAILS_HEIGHT, true);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    detailsContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            anim.setDuration(ANIMATE_IN_DURATION_MILLIS);
            detailsContainer.startAnimation(anim);
        }
    }

    private void saveMessageInBackground(final String body, final Ad ad) {

        MessageQuery.saveMessageInBackground(body, ad);
        etMessage.setText("");
    }

    private void populateViewHolder() {
        viewHolder.tvAdLocation = (TextView)findViewById(R.id.tvAdTitle);
        viewHolder.tvAdDistance = (TextView)findViewById(R.id.tvAdDistance);
        viewHolder.ivAdImage = (ImageView)findViewById(R.id.ivAdImage);
    }

    public void updateSubviews(Ad theAd, Context context) {

        currentInterestedAd = theAd;
        viewHolder.ivAdImage.setImageResource(0);
        Picasso.with(getContext()).load(currentInterestedAd.getPhotoUrl()).into(viewHolder.ivAdImage);
        viewHolder.tvAdLocation.setText(Html.fromHtml(currentInterestedAd.getTitle()));
        mtvPriceLabel.setText(currentInterestedAd.getPrice());

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static class ViewHolder {
        TextView tvAdLocation;
        TextView tvAdDistance;
        ImageView ivAdImage;

    }
}
