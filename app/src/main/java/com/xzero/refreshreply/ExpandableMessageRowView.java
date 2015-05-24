package com.xzero.refreshreply;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.xzero.refreshreply.helpers.GPSTracker;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


//import android.view.animation.Animation;

public class ExpandableMessageRowView extends RelativeLayout implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int TARGET_DETAILS_HEIGHT = 600;
    public static final int ANIMATE_IN_DURATION_MILLIS = 300;
    public static final int ANIMATE_OUT_DURATION_MILLIS = 500;
    public static final int CIRCULAR_REVEAL_DURATION_NAVIGATE = 500;

    public Ad currentInterestedAd;
    //public PumpRowDelegate rowDelegate;

    private MapFragment mapFragment;

    private Context mContext;

    public int PLACE_PICKER_REQUEST = 1;

   // MapView mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    View fabStarPump;
    View fabUnstarPump;
    View fabAddReport;
    View mNavigationOverlayViewToBeRevealed;

    TextView mClaimedLabel;
    TextView mPumpFlowLabel;

    private DecimalFormat df = new DecimalFormat("#.#");
    private ViewGroup detailsContainer;

    private ViewGroup detailsChat;
    private Button btSend;
    private EditText etMessage;

    private ViewHolder viewHolder;

    //private static final String TAG = "RowView";

    public static final double MAP_DISPLAY_DELTA = 0.03;

    public ExpandableMessageRowView(final Context context, AttributeSet attrs) {
        super(context, attrs);

//        try {
            View.inflate(context, R.layout.expandable_row_ad, this);

            detailsChat = (ViewGroup) findViewById(R.id.llSend);
            //detailsChat.setVisibility(View.GONE);

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
            mClaimedLabel = (TextView) findViewById(R.id.tvLocationDescription);
            mNavigationOverlayViewToBeRevealed = findViewById(R.id.viewToBeRevealed);
            mPumpFlowLabel = (TextView) findViewById(R.id.tvPumpFlowLabel);
            viewHolder = new ViewHolder();
            mContext = context;

            populateViewHolder();
            setupUnclaimButton();
            setupClaimPumpButton();
            fabAddReport = findViewById(R.id.fabAddReport);
            fabAddReport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddReport(context);
                }
            });

            int size = getResources().getDimensionPixelSize(R.dimen.fab_size);


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

    private void setupClaimPumpButton() {
        fabStarPump = findViewById(R.id.fabStarPump);
        fabStarPump.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent i = new Intent(mContext, MapActivity.class);
//                mContext.startActivity(i);

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

//                int PLACE_PICKER_REQUEST = 1;

                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(bounds);
                Intent intent = null;
                try {
                    intent = intentBuilder.build(mContext);

                    ((Activity)mContext).startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                //mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                //beginAnimationToRevealNavigationOverviewAndHidePager();
                //beginAnimationToRevealUnstarFAB();

                //mPump.setIsClaimedByATechnician(true);
                //rowDelegate.onPumpClaimClicked(mPump);
            }
        });
    }

    private void setupUnclaimButton() {
        fabUnstarPump = findViewById(R.id.fabStarredIndicator);
        fabUnstarPump.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //beginAnimationToRevealStarPumpFAB();
                //beginAnimationToUnrevealNavigationOverlayView();
                //mPump.setIsClaimedByATechnician(false);
            }
        });
    }

    private void beginAnimationToUnrevealNavigationOverlayView() {
        int xpos = 0;
        int ypos = 0;
        int beginWidth = 1000; /// Width of the FAB, hopefully
        Animator revealStartButton =  ViewAnimationUtils.createCircularReveal(mNavigationOverlayViewToBeRevealed, xpos, ypos, beginWidth, 0);
        Log.d("DBG", String.format("Unrevealing fabEnd from x:%d, y:%d, begin width:%d", xpos, ypos, beginWidth));
        revealStartButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mNavigationOverlayViewToBeRevealed.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealStartButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealStartButton.start();
    }


    private void beginAnimationToRevealUnstarFAB() {
        int xpos = 0;
        int ypos = 0;
        int finalRadius = 320; /// Width of the FAB, hopefully
        Animator revealUnstarAnimation = ViewAnimationUtils.createCircularReveal(fabUnstarPump, xpos, ypos, 0, finalRadius);
        Log.d("DBG", String.format("Revealing fabEnd from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        revealUnstarAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fabUnstarPump.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fabStarPump.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealUnstarAnimation.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealUnstarAnimation.start();
    }

    private void onAddReport(Context context) {
//        Intent i = new Intent(context, CreateReportActivity.class);
//        i.putExtra(CreateReportActivity.EXTRA_PUMP_OBJECT_ID, mPump.getObjectId());
//        ((Activity)context).startActivityForResult(i, CreateReportActivity.CREATE_REPORT_SUCCESSFUL_OR_NOT_REQUEST_CODE);

        toggleExpandedState();
    }

    public void onRowClick() {
        if (currentInterestedAd.isOnSale()) {
            toggleExpandedState();
        }
    }

    public void toggleExpandedState() {
        boolean expanded = detailsContainer.getVisibility() == View.VISIBLE;
        if (expanded) {

            //resetMapUI();
            beginAnimationToRevealStarPumpFAB();
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
        }
        else {
            beginAnimationToRevealAddReportFab();
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

    private void setOutlinesOnFabs(int size) {
        Outline outline = new Outline();
        outline.setOval(0, 0, size, size);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };
        fabStarPump.setOutlineProvider(viewOutlineProvider);
        fabUnstarPump.setOutlineProvider(viewOutlineProvider);
        fabAddReport.setOutlineProvider(viewOutlineProvider);
    }

    private void beginAnimationToRevealNavigationOverviewAndHidePager() {
        int xpos = this.getRight();
        int ypos = 0;
        int finalRadius = this.getWidth() * 2;
        Log.d("DBG", String.format("Revealing navigation overlay view from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        Animator reveal =  ViewAnimationUtils.createCircularReveal(
                mNavigationOverlayViewToBeRevealed,
                xpos,
                ypos,
                112,
                finalRadius);
        reveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mNavigationOverlayViewToBeRevealed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        reveal.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        reveal.start();
    }

    private void beginAnimationToRevealAddReportFab() {
        int xpos = 0;
        int ypos = 0;
        int finalRadius = 320; /// Width of the FAB, hopefully
        Animator revealAddReportButton = ViewAnimationUtils.createCircularReveal(fabAddReport, xpos, ypos, 0, finalRadius);
        Log.d("DBG", String.format("Revealing add report from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        revealAddReportButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fabAddReport.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fabUnstarPump.setVisibility(View.INVISIBLE);
                fabStarPump.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealAddReportButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealAddReportButton.start();
    }

    View getCurrentlyActiveButton() {
        View[] views = {fabUnstarPump, fabStarPump, fabAddReport};
        for (View v : views) {
            if (v.getVisibility() == View.VISIBLE) {
                return v;
            }
        }
        return null;
    }

    private void beginAnimationToRevealStarPumpFAB() {
        int xpos = 0;
        int ypos = 0;
        int finalRadius = 320; /// Width of the FAB, hopefully
        final View currentButton = getCurrentlyActiveButton();
        Animator revealStartButton =  ViewAnimationUtils.createCircularReveal(currentButton, xpos, ypos, finalRadius, 0);
        Log.d("DBG", String.format("Revealing star pump from x:%d, y:%d, width:%d", xpos, ypos, finalRadius));
        revealStartButton.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fabStarPump.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                currentButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        revealStartButton.setDuration(CIRCULAR_REVEAL_DURATION_NAVIGATE);
        revealStartButton.start();
    }

    private void saveMessageInBackground(final String body, final Ad ad) {
        Message message = new Message();
        String sUserId = ParseUser.getCurrentUser().getObjectId();
        message.setUserId(sUserId);
        message.setBody(body);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (ad != null) {
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


    private void populateViewHolder() {
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvPumpLocation);
        viewHolder.tvPumpDistance = (TextView)findViewById(R.id.tvPumpDistance);
        viewHolder.ivStatusIndicator = (ImageView)findViewById(R.id.ivPumpStatusIndicator);
    }

    public void updateSubviews(Ad theAd, Context context) {

        currentInterestedAd = theAd;
        viewHolder.ivStatusIndicator.setImageResource(0);
        Picasso.with(getContext()).load(currentInterestedAd.getPhotoUrl()).into(viewHolder.ivStatusIndicator);
        viewHolder.tvLocation.setText(Html.fromHtml(currentInterestedAd.getTitle()));
        mPumpFlowLabel.setText(currentInterestedAd.getPrice());

    }



    public void clearTextViews() {
        viewHolder.tvLocation.setText("");
        viewHolder.tvPumpDistance.setText("");
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
        TextView tvLocation;
        TextView tvPumpDistance;
        ImageView ivStatusIndicator;

    }
}