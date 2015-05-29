package com.xzero.refreshreply.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.adapters.ImageResultAdapter;
import com.xzero.refreshreply.helpers.NetworkUtil;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.listeners.EndlessScrollListener;
import com.xzero.refreshreply.models.Ad;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class ImageDisplayFragment extends Fragment implements OnRefreshListener {

    @InjectView(R.id.gvResult)
    GridView gvResult;
    @InjectView(R.id.pbLoading)
    ProgressBar pbLoading;

    @InjectView(R.id.ptrlAds)
    PullToRefreshLayout ptrlAds;

    public AdListListener mListener;

    private ArrayList<Ad> ads;
    ImageResultAdapter aImageResultAdapter;
    private final int REQUEST_CODE = 20;

    public int categoryId = 1;

    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            Log.i("INFO", "Loading more items");
        }
    };

    public static ImageDisplayFragment newInstance() {
        return new ImageDisplayFragment();
    }

    @Override
    public void onResume() {
        Log.d("DBG", "List fragment onResume");
        super.onResume();
        //fetchAndShowData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DBG", "List fragment onPause");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ads = new ArrayList<Ad>();
        aImageResultAdapter = new ImageResultAdapter(this.getActivity(), ads);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_imagedisplay, container, false);
        ButterKnife.inject(this, view);

        ActionBarPullToRefresh.from(getActivity())
                // Here we mark just the ListView and it's Empty View as pullable
                .theseChildrenArePullable(R.id.gvResult, android.R.id.empty)
                .listener(this)
                .setup(ptrlAds);


        aImageResultAdapter = new ImageResultAdapter((Activity)mListener, ads);

        fetchAndShowData();

        gvResult.setAdapter(aImageResultAdapter);
        gvResult.setOnScrollListener(endlessScrollListener);

        gvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ad ad = aImageResultAdapter.getItem(position);
                mListener.onAdListRowSelected(ad);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AdListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

//    private void fetchAdsInBackground(final ParseQuery<ParseObject> query) {
//
//        pbLoading.setVisibility(ProgressBar.INVISIBLE);
//        Log.d("debug", "Using ads fetched from remote");
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//        public void done(final List<ParseObject> ads, ParseException e) {
//
//            if (e == null) {
//                mListener.onListRefreshederested();
//
//                Log.d("info", "Fetching ads from local DB. Found " + ads.size());
//
//                if (ads.size() == 0) {
//                    fetchAdsFromRemote(ParseQuery.getQuery("Ad"));
//                } else {
//                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
//                    Log.d("debug", "Using ads fetched from local DB.");
//                    addAdsToAdapter(ads);
//                }
//            } else {
//                Log.d("error", "Exception while fetching ads: " + e);
//                pbLoading.setVisibility(ProgressBar.INVISIBLE);
//            }
//        }
//    });
//    }

//    private void unpinAndRepin(final List<ParseObject> ads) {
//
//        Log.d("debug", "Unpinning previously saved objects");
//        ParseObject.unpinAllInBackground(AdPersister.ALL_ADS, ads,
//                new DeleteCallback() {
//                    public void done(ParseException e) {
//                        if (e != null) {
//                            // There was some error.
//                            return;
//                        } else {
//                            Log.d("info", ads.size() + " previous cached ads deleted.");
//                        }
//                    }
//                }
//        );
//        // Add the latest results for this query to the cache.
//        Log.d("debug", "Pinning newly fetched ads " + ads.size());
//        ParseObject.pinAllInBackground(AdPersister.ALL_ADS, ads, new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d("debug", "Pinned newly fetched ads " + ads.size());
//                } else {
//                    Log.d("debug", "Couldn't pin ads: " + e.toString());
//                }
//            }
//        });
//    }

    private void fetchAdsFromRemote(ParseQuery<ParseObject> query) {

        Log.d("debug", "Fetching ads from remote DB.");


        query.whereEqualTo("categoryId", String.valueOf(categoryId));


        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> ads, ParseException e) {

                pbLoading.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    Log.d("info", "Fetching ads from remote DB. Found " + ads.size());
                    addAdsToAdapter(ads);
                    mListener.onListRefreshederested();

                } else {
                    Log.d("error", "Exception while fetching remote ads: " + e);
                }
            }
        });
    }

    private void addAdsToAdapter(List<ParseObject> ads) {

        List<Ad> adList = new ArrayList<Ad>();

        for (int i = 0; i < ads.size(); i++) {
            Ad ad = (Ad)ads.get(i);
            if (ad.isOnSale()) {
                adList.add(ad);
            }
        }
        aImageResultAdapter.addAll(adList);
        aImageResultAdapter.notifyDataSetChanged();

        mListener.onListRefreshederested();

    }

    private void prepareForDataFetch() {

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        if (aImageResultAdapter !=null) {
            aImageResultAdapter.clear();
        }
    }

    private void fetchAndShowData() {
        prepareForDataFetch();
        fetchAdsFromRemote(ParseQuery.getQuery("Ad"));
    }

    // Pull to refresh fetches data from the remote server.
    @Override
    public void onRefreshStarted(View view) {

        // Cannot refresh if network is not available.
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity().getBaseContext(),
                    "Could not refresh. Network not available.",
                    Toast.LENGTH_LONG).show();
            ptrlAds.setRefreshComplete();
            return;
        }

        fetchAndShowRemoteData();
        ptrlAds.setRefreshComplete();
    }

    // Fetch data remotely based on the selected sort option
    private void fetchAndShowRemoteData() {
        prepareForDataFetch();
        fetchAdsFromRemote(ParseQuery.getQuery("Ad"));
    }
}
