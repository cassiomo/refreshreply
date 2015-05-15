package com.xzero.refreshreply.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.fragments.AdStaggeredGridFragment;
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
public class AdBrowserActivity extends Activity implements AdListListener{

    @InjectView(R.id.adsViewPager)
    ViewPager mPager;

    @InjectView(R.id.slidingTabs)
    PagerSlidingTabStrip mTabs;

    private ListMapPagerAdapter mListMapPagerAdapter;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListRefreshederested() {

    }

    @Override
    public void onAdListRowSelected(Ad ad) {

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_adbrowser, menu);
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

    public static class ListMapPagerAdapter extends FragmentPagerAdapter {

        //protected AdListFragment mAdListFragment;
        protected AdStaggeredGridFragment adStaggeredGridFragment;
        protected MessageFragment messageFragment;

        public ListMapPagerAdapter(FragmentManager fm) {
            super(fm);
            messageFragment = MessageFragment.newInstance();
            adStaggeredGridFragment = AdStaggeredGridFragment.newInstance();
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
                    return adStaggeredGridFragment;
                case 1:
                    return messageFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LIST";
                case 1:
                    return "MESSAGE";
            }
            return super.getPageTitle(position);
        }
    }
}
