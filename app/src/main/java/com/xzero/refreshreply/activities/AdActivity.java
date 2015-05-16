package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.fragments.ImageDisplayFragment;
import com.xzero.refreshreply.fragments.MessageFragment;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.Ad;

import butterknife.ButterKnife;
import butterknife.InjectView;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;

//public class AdBrowserActivity extends FragmentActivity implements AdListListener{
public class AdActivity extends Activity implements AdListListener{

    @InjectView(R.id.adsViewPager)
    ViewPager mPager;

    @InjectView(R.id.slidingTabs)
    PagerSlidingTabStrip mTabs;

    private ListMapPagerAdapter mListMapPagerAdapter;

    private Boolean isFromPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adbrowser);

        ButterKnife.inject(this);

        FragmentManager fragmentManager = getFragmentManager();

        //mListMapPagerAdapter = new ListMapPagerAdapter(getSupportFragmentManager());
        mListMapPagerAdapter = new ListMapPagerAdapter(fragmentManager);
        mPager.setAdapter(mListMapPagerAdapter);

        mTabs.setViewPager(mPager);
        if (getIntent().getExtras() !=null) {
            isFromPush = getIntent().getExtras().getBoolean("isFromPush");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListRefreshederested() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFromPush) {
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

    private MessageFragment getMessageFragment() {
        return mListMapPagerAdapter.messageFragment;
    }


    public static class ListMapPagerAdapter extends FragmentPagerAdapter {

        //protected AdListFragment mAdListFragment;
        //protected AdStaggeredGridFragment adStaggeredGridFragment;
        protected ImageDisplayFragment imageDisplayFragment;
        protected MessageFragment messageFragment;

        public ListMapPagerAdapter(FragmentManager fm) {
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
