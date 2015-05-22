//package com.xzero.refreshreply.adapters;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.support.v13.app.FragmentPagerAdapter;
//
//import com.xzero.refreshreply.fragments.FirstFragment;
//
///**
// * Created by kemo on 5/19/15.
// */
//public class MyPagerAdapter extends FragmentPagerAdapter {
//    private static int NUM_ITEMS = 3;
//
//    public MyPagerAdapter(FragmentManager fragmentManager) {
//        super(fragmentManager);
//    }
//
//    // Returns total number of pages
//    @Override
//    public int getCount() {
//        return NUM_ITEMS;
//    }
//
//    // Returns the fragment to display for that page
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0: // Fragment # 0 - This will show FirstFragment
//                return FirstFragment.newInstance(0, "Page # 1");
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                return FirstFragment.newInstance(1, "Page # 2");
//            default:
//                return null;
//        }
//    }
//
//    // Returns the page title for the top indicator
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return "Page " + position;
//    }
//
//
//}
