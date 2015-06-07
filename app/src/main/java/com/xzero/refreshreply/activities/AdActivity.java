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
import com.astuetz.PagerSlidingTabStrip.IconTabProvider;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xzero.refreshreply.Constant;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.fragments.ImageDisplayFragment;
import com.xzero.refreshreply.fragments.MessageFragment;
import com.xzero.refreshreply.fragments.ProfileFragment;
import com.xzero.refreshreply.fragments.SearchFragment;
import com.xzero.refreshreply.fragments.SettingFragment;
import com.xzero.refreshreply.helpers.KeyBoardUtil;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.Ad;

import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdActivity extends Activity implements AdListListener{

    @InjectView(R.id.adsViewPager)
    ViewPager mPager;

    @InjectView(R.id.slidingTabs)
    PagerSlidingTabStrip mTabs;

//    @InjectView(R.id.ibProfile)
//    ImageView ibProfile;

    private ListAdPagerAdapter mListAdPagerAdapter;

    private String mAdId;
    private String mMessageId;
    private String alarmTime;

    public static boolean isAlarmSet  = false;

    private Ad mAd;

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
            mMessageId = receviedIntent.getExtras().getString("messageId");
            alarmTime = receviedIntent.getExtras().getString("alarm");

            if (alarmTime !=null) {
                wakeupAlarm();
            }
        }

        KeyBoardUtil.hideKeyboard(this);

//        final ParseUser currentUser = ParseUser.getCurrentUser();
//        String profileUrl = ChatListAdapter.getProfilePic(currentUser.getUsername());
//
//        Picasso.with(getApplicationContext()).load(profileUrl)
//                .error(R.drawable.taptapchat)
//                .transform(new RoundTransform())
//                .into(ibProfile);

    }

    private void wakeupAlarm() {

        final ParseUser currentUser = ParseUser.getCurrentUser();
        String otherPerson;
        //hardcode username
        if (currentUser.getUsername().equals("june")) {
            otherPerson = "john";
        } else {
            otherPerson = "june";
        }

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.taptapchat)
                .setTitle(" Meetup with New Car")
                .setMessage("Did you meet with " + otherPerson + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // sale completed
                        if (currentUser.getUsername().equals("june")) {
                            getImageDisplayFragment().categoryId = 2;
                            //getImageDisplayFragment().buyerPrice = "50";
                            getImageDisplayFragment().fetchAndShowData();
                            wakupThankyou();
                        } else {
                            wakeUpCoupon();
                        }
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

    private void wakupThankyou() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.taptapchat)
                .setTitle("Thank you for shopping!")
                .setMessage("Have a nice day!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switchToAdView();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void wakeUpCoupon() {

        String uniqueString = UUID.randomUUID().toString();

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.taptapchat)
                .setTitle("Thank you - Post Ad Coupon")
                .setMessage(uniqueString)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // sale completed

                            }
                        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                        if (ads.size() > 0) {
                            switchToMessageViewAndSetValue((Ad) ads.get(0), true);
                        }

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
        getMessageFragment().setCurrentInterestedAd(ad);
        if (!isFromResume) {
            getMessageFragment().saveMessageInBackground(ad);
        }

        switchToMessageView();
    }

    public void switchToMessageView() {
        mPager.setCurrentItem(1, true);
    }

    public void switchToAdView() {
        mPager.setCurrentItem(0, true);
    }

    public static class ListAdPagerAdapter extends FragmentPagerAdapter implements IconTabProvider  {

        protected ImageDisplayFragment imageDisplayFragment;
        protected MessageFragment messageFragment;
        protected ProfileFragment profileFragment;
        protected SearchFragment searchFragment;
        protected SettingFragment settingFragment;
        private int tabIcons[] = {R.drawable.home,
                R.drawable.tranchat,
                R.drawable.profile,
                R.drawable.darksearch,
                R.drawable.post};


        public ListAdPagerAdapter(FragmentManager fm) {
            super(fm);
            imageDisplayFragment = ImageDisplayFragment.newInstance();
            messageFragment = MessageFragment.newInstance();
            profileFragment = ProfileFragment.newInstance();
            searchFragment = SearchFragment.newInstance();
            settingFragment = SettingFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return imageDisplayFragment;
                case 1:
                    return messageFragment;
                case 2:
                    return profileFragment;
                case 3:
                    return searchFragment;
                case 4:
                    return settingFragment;
            }
            return null;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Home";
//                case 1:
//                    return "Conversation";
//                case 2:
//                    return "Profile";
//                case 3:
//                    return "Search";
//                case 4:
//                    return "Setting";
//            }
//            return super.getPageTitle(position);
//        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == Constant.PLACE_PICKER_REQUEST  && data !=null) {

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
