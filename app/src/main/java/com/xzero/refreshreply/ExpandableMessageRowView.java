package com.xzero.refreshreply;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Outline;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.Message;
import com.xzero.refreshreply.notification.MyCustomReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


//import android.view.animation.Animation;

public class ExpandableMessageRowView extends RelativeLayout {

    public static final int TARGET_DETAILS_HEIGHT = 600;
    public static final int ANIMATE_IN_DURATION_MILLIS = 300;
    public static final int ANIMATE_OUT_DURATION_MILLIS = 500;
    public static final int CIRCULAR_REVEAL_DURATION_NAVIGATE = 500;

    public Ad currentInterestedAd;
    //public PumpRowDelegate rowDelegate;

    private MapFragment mapFragment;

    MapView mapView;
    GoogleMap googleMap;

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

    public static final double MAP_DISPLAY_DELTA = 0.03;

    public ExpandableMessageRowView(final Context context, AttributeSet attrs) {
        super(context, attrs);
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

        //mapFragment = new MapFragment();

//        mapView = (MapView) findViewById(R.id.mapView);
//
//        mapView.onResume();
//        try {
//            MapsInitializer.initialize(context.getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        googleMap = mapView.getMap();
//        // latitude and longitude
//        double latitude = 17.385044;
//        double longitude = 78.486671;
//
//        // create marker
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("Hello Maps");
//
//        // Changing marker icon
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        // adding marker
//        googleMap.addMarker(marker);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));


        //Log.d("map", String.valueOf(map));
//        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls

        //MapsInitializer.initialize(context);


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

    GoogleMap getMap() {
        if (mapFragment == null) {
            return null;
        }
        return mapFragment.getMap();
    }

    private void setupClaimPumpButton() {
        fabStarPump = findViewById(R.id.fabStarPump);
        fabStarPump.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                beginAnimationToRevealNavigationOverviewAndHidePager();
                beginAnimationToRevealUnstarFAB();
                //mPump.setIsClaimedByATechnician(true);
                //rowDelegate.onPumpClaimClicked(mPump);
            }
        });
    }

    private void resetMapUI() {
        GoogleMap map = getMap();
        if (map != null) {
            getMap().getUiSettings().setZoomControlsEnabled(false);
            getMap().clear();
            centerMapOnAd();
        } else {
            Log.d("map", "map is null");
        }
    }

    private void setupUnclaimButton() {
        fabUnstarPump = findViewById(R.id.fabStarredIndicator);
        fabUnstarPump.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                beginAnimationToRevealStarPumpFAB();
                beginAnimationToUnrevealNavigationOverlayView();
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

    void centerMapOnAd () {
        if (currentInterestedAd == null) {
            return;
        }
        double lat = currentInterestedAd.getLocation().getLatitude();
        double longitude = currentInterestedAd.getLocation().getLongitude();
        LatLng positionTopLeft = new LatLng(lat - MAP_DISPLAY_DELTA, longitude - MAP_DISPLAY_DELTA);
        LatLng fartherAwayPosition = new LatLng(lat + MAP_DISPLAY_DELTA, longitude + MAP_DISPLAY_DELTA);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(positionTopLeft);
        builder.include(fartherAwayPosition);
        LatLngBounds bounds = builder.build();
        if (getMap() != null) {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
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

        mapView = (MapView) findViewById(R.id.mapView);

        //mapView.onResume();
        try {
            MapsInitializer.initialize(context.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }



    public void clearTextViews() {
        viewHolder.tvLocation.setText("");
        viewHolder.tvPumpDistance.setText("");
    }

    static class ViewHolder {
        TextView tvLocation;
        TextView tvPumpDistance;
        ImageView ivStatusIndicator;

    }
}
