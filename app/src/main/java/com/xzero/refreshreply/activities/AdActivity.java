package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.fragments.ImageDisplayFragment;
import com.xzero.refreshreply.fragments.MessageFragment;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.Ad;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdActivity extends Activity implements AdListListener{

    @InjectView(R.id.adsViewPager)
    ViewPager mPager;

    @InjectView(R.id.slidingTabs)
    PagerSlidingTabStrip mTabs;

    private ListAdPagerAdapter mListAdPagerAdapter;

    private Boolean isFromPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adbrowser);

        ButterKnife.inject(this);
        FragmentManager fragmentManager = getFragmentManager();

        //mListAdPagerAdapter = new ListMapPagerAdapter(getSupportFragmentManager());
        mListAdPagerAdapter = new ListAdPagerAdapter(fragmentManager);
        mPager.setAdapter(mListAdPagerAdapter);

        mTabs.setViewPager(mPager);
        if (getIntent().getExtras() !=null) {
            isFromPush = getIntent().getExtras().getBoolean("isFromPush");
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
//        if (getMessageFragment().mMapPagerAdapter != null) {
//            getMessageFragment().mMapPagerAdapter.notifyDataSetChanged();
//        }
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
        if (isFromPush !=null && isFromPush) {
            switchToMessageView();
        }
    }


    @Override
    public void onAdListRowSelected(Ad ad) {
        switchToMessageView();

        getMessageFragment().setCurrentInterestedAd(ad);
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
}
