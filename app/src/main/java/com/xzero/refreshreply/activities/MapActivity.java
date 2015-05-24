package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.helpers.GPSTracker;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by kemo on 5/21/15.
 */
public class MapActivity extends Activity implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    @InjectView(R.id.btMapOk)
    Button btMapOk;
    @InjectView(R.id.btMapCancel)
    Button btMapCancel;
    @InjectView(R.id.tvMapAddress)
    TextView tvMapAddress;

    MapFragment mapFragment;

    private ParseGeoPoint currentUserLocation;
    public static final double MAP_DISPLAY_DELTA = 0.03;
    public static final String ME_TITLE = "ME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_detail_map);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_frame);
        Log.d("DBG", String.format(this.getClass().toString(), "onCreate"));
        ButterKnife.inject(this);

        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
        //centerMapOnUser();
        if (mapFragment !=null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    getMap().clear();
                    getMap().getUiSettings().setCompassEnabled(true);
                    centerMapOnUser();
                }
            });
        }
    }

    @Override
    public void onResume() {
        Log.d("DBG", "Map resuming.");
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetMapUIAndCardView();
            }
        }, 500);
    }

    void centerMapOnUser() {

        double lat = 37.3770091;
        double longitude = 37.3770091;
        LatLng position;

        GPSTracker tracker = new GPSTracker(this);
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
        if (getMap() != null) {

            MarkerOptions options = new MarkerOptions();
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.me));

            position = new LatLng(lat, longitude);
            options.position(position);
            options.title(ME_TITLE);
            getMap().addMarker(options);

            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }
    }

    public void resetMapUIAndCardView() {
        resetMapUI();
    }

    private void resetMapUI() {
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().clear();
        addUsersToMap();
        centerMapOnUser();
    }

    private void addUsersToMap() {
        GoogleMap map = getMap();
        double lat;
        double longitude;
        LatLng position;
        if (map == null) {
            return;
        }

        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);

        centerMapOnUser();
    }

    GoogleMap getMap() {
        if (mapFragment == null) {
            return null;
        }
        return mapFragment.getMap();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle() != null && marker.getTitle().equals(ME_TITLE)) {
            marker.showInfoWindow();
            Log.w("ME", "Clicked on map marker for ME");
        }
        return true; // false defaults to showing infoWindow set for every marker
    }
}
