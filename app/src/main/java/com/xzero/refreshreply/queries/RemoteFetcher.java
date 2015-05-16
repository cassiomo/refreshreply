//package com.xzero.refreshreply.queries;
//
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ProgressBar;
//
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.xzero.refreshreply.models.Ad;
//
//import java.util.List;
//
///**
// * Created by kemo on 5/15/15.
// */
//public class RemoteFetcher {
//
//    public static void fetchAdsInBackground(final ParseQuery<ParseObject> query,
//                                            final ArrayAdapter<Ad> arrayAdapter,
//                                            ProgressBar pbLoading) {
//
//        pbLoading.setVisibility(ProgressBar.INVISIBLE);
//        Log.d("debug", "Using ads fetched from local DB.");
//
//        //query.fromPin(AdsPersister.ALL_ads);
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            public void done(final List<ParseObject> ads, ParseException e) {
//
//                if (e == null) {
//                    mListener.onListRefreshederested();
//
//                    Log.d("info", "Fetching ads from local DB. Found " + ads.size());
//
//                    if (ads.size() == 0) {
//                        fetchAdsFromRemote(ParseQuery.getQuery("Ad"));
//                    } else {
//                        pbLoading.setVisibility(ProgressBar.INVISIBLE);
//                        Log.d("debug", "Using ads fetched from local DB.");
//                        addAdsToAdapter(ads);
//                    }
//                } else {
//                    Log.d("error", "Exception while fetching ads: " + e);
//                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
//                }
//            }
//        });
//    }
//}
