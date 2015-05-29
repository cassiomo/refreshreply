package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.fragments.ImageDisplayFragment;
import com.xzero.refreshreply.fragments.MessageFragment;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.Ad;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdActivity extends Activity implements AdListListener{

    @InjectView(R.id.adsViewPager)
    ViewPager mPager;

    @InjectView(R.id.slidingTabs)
    PagerSlidingTabStrip mTabs;

    private ListAdPagerAdapter mListAdPagerAdapter;

    private Boolean isFromPush;
    private String mAdId;
    private String mMessage;
    private String alarmTime;

    public static boolean isAlarmSet  = false;

    private Ad mAd;

    public int PLACE_PICKER_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        ButterKnife.inject(this);
        FragmentManager fragmentManager = getFragmentManager();

        mListAdPagerAdapter = new ListAdPagerAdapter(fragmentManager);
        mPager.setAdapter(mListAdPagerAdapter);

        mTabs.setViewPager(mPager);
        Intent receviedIntent = getIntent();
        if (receviedIntent.getExtras() !=null) {
            mAdId = receviedIntent.getExtras().getString("adId");
            mMessage = receviedIntent.getExtras().getString("body");
            isFromPush = receviedIntent.getExtras().getBoolean("isFromPush");
            alarmTime = receviedIntent.getExtras().getString("alarm");

            if (alarmTime !=null) {

                new AlertDialog.Builder(this)
                        .setTitle("MeetUp Confirmation")
                        .setMessage("Did you meet up?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // sale completed
                                getImageDisplayFragment().categoryId = 2;
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // sale not completed
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private MessageFragment getMessageFragment() {
        return mListAdPagerAdapter.messageFragment;
    }

    private ImageDisplayFragment getImageDisplayFragment() {
        return mListAdPagerAdapter.imageDisplayFragment;
    }

    @Override
    public void onListRefreshederested() {
        Log.d("DBG", "onListRefreshederested");
        if (getMessageFragment() != null && getMessageFragment().mAdResultAdapter != null) {
            Ad indexAd = getMessageFragment().mAdResultAdapter.getAdAtIndex(0);
            getMessageFragment().setCurrentlyDisplayedAd(indexAd);
        }
        else {
            Log.e("DBG", "Calling onListRefreshed at the wrong time.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdId !=null) {
            ParseQuery query = ParseQuery.getQuery("Ad");
            query.whereEqualTo("objectId", mAdId);
            query.findInBackground(new FindCallback<ParseObject>() {

                public void done(final List<ParseObject> ads, ParseException e) {

                    //pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    if (e == null) {
                        //Log.d("info", "Fetching ads from remote DB. Found " + ads.size());
                        switchToMessageViewAndSetValue((Ad)ads.get(0), true);

                    } else {
                        Log.d("error", "Exception while fetching remote ads: " + e);
                    }
                }
            });
        }
    }


    @Override
    public void onAdListRowSelected(Ad ad) {
        switchToMessageViewAndSetValue(ad, false);
    }

    public void switchToMessageViewAndSetValue(Ad ad, boolean isFromResume) {
        getMessageFragment().setCurrentInterestedAd(ad, mMessage);
        if (!isFromResume) {
            getMessageFragment().saveMessageInBackground(ad);
        }

        switchToMessageView();
    }

    public void switchToMessageView() {
        mPager.setCurrentItem(1, true);
    }

    public static class ListAdPagerAdapter extends FragmentPagerAdapter {

        //protected AdListFragment mAdListFragment;
        //protected AdStaggeredGridFragment adStaggeredGridFragment;
        protected ImageDisplayFragment imageDisplayFragment;
        protected MessageFragment messageFragment;

        public ListAdPagerAdapter(FragmentManager fm) {
            super(fm);
            messageFragment = MessageFragment.newInstance();
            imageDisplayFragment = ImageDisplayFragment.newInstance();
            //adStaggeredGridFragment = AdStaggeredGridFragment.newInstance();
            //mAdListFragment = mAdListFragment.newInstance();

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //return mAdListFragment;
                    //return adStaggeredGridFragment;
                    return imageDisplayFragment;
                case 1:
                    return messageFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "AD";
                case 1:
                    return "MESSAGE";
            }
            return super.getPageTitle(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {

            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            String toastMsg = String.format("Place: %s", place.getName());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            getMessageFragment().setMessagText(name + " - " + address + " -" + Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
