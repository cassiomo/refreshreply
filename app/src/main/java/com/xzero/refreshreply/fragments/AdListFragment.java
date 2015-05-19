package com.xzero.refreshreply.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.adapters.AdListAdapter;
import com.xzero.refreshreply.helpers.NetworkUtil;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.AbstractListItem;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.AdListItem;
import com.xzero.refreshreply.models.HeaderListItem;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class AdListFragment extends Fragment implements OnRefreshListener {

    public AdListAdapter mAdArrayAdapter;
    private AlphaInAnimationAdapter alphaAdapter;

    private ListView lvAds;
    private ProgressBar pbLoading;
    private PullToRefreshLayout ptrlAds;


    private ParseUser currentUser;
    public AdListListener mListener;
    public AdListFragment() {}

    public static AdListFragment newInstance() {
        return new AdListFragment();
    }


    @Override
    public void onResume() {
        Log.d("DBG", "List fragment onResume");
        super.onResume();
        fetchAndShowData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DBG", "List fragment onPause");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DBG", "List fragment onCreate");

        super.onCreate(savedInstanceState);
        // There's an options menu for this fragment only
        setHasOptionsMenu(true);

        mAdArrayAdapter = new AdListAdapter((Activity)mListener);

        currentUser = ParseUser.getCurrentUser();

//        Log.d("DBG", String.format("Current user: %s %s", currentUser.getUsername(), currentUser.get("location").toString()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_ad_list, container, false);

        setupViews(v);
        mAdArrayAdapter.rowListener = (AdListListener) getActivity();

        ActionBarPullToRefresh.from(getActivity())
                // Here we mark just the ListView and it's Empty View as pullable
                .theseChildrenArePullable(R.id.lvAds, android.R.id.empty)
                .listener(this)
                .setup(ptrlAds);

        setupListRowListener();
        Log.d("DBG", "List fragment onCreateView");
        return v;
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

    /* Private methods */
    private void setupViews(View v) {

        ptrlAds = (PullToRefreshLayout) v.findViewById(R.id.ptrlAds);

        lvAds = (ListView) v.findViewById(R.id.lvAds);

        alphaAdapter = new AlphaInAnimationAdapter(mAdArrayAdapter);
        SwingRightInAnimationAdapter riaa = new SwingRightInAnimationAdapter(alphaAdapter);
        riaa .setAbsListView(lvAds);


        lvAds.setAdapter(riaa);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
    }

    private void setupListRowListener() {
        lvAds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbstractListItem pli = mAdArrayAdapter.getItem(position);
                if (pli instanceof AdListItem) {
                    AdListItem theAdListItem = (AdListItem) pli;
                    mListener.onAdListRowSelected(theAdListItem.ad);
                }
            }
        });
    }

    // This is done each time before we fetch data both locally or remotely
    private void prepareForDataFetch() {

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        mAdArrayAdapter.clear();
        //mListener.onShouldInvalidatePagers();
    }

    // Fetch data (from local or remote source) based on the selected sort option
    private void fetchAndShowData() {
        prepareForDataFetch();
        fetchAdsInBackground(ParseQuery.getQuery("Ad"));
        //fetchAdsInBackground();
    }

    // Fetch data remotely based on the selected sort option
    private void fetchAndShowRemoteData() {
        prepareForDataFetch();
        fetchAdsFromRemote(ParseQuery.getQuery("Ad"));

    }
    
    private void fetchAdsInBackground(final ParseQuery<ParseObject> query) {

        pbLoading.setVisibility(ProgressBar.INVISIBLE);
        Log.d("debug", "Using ads fetched from local DB.");

        //query.fromPin(AdsPersister.ALL_ads);

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> ads, ParseException e) {

                if (e == null) {
                    mListener.onListRefreshederested();

                    Log.d("info", "Fetching ads from local DB. Found " + ads.size());

                    if (ads.size() == 0) {
                        fetchAdsFromRemote(ParseQuery.getQuery("Ad"));
                    } else {
                        pbLoading.setVisibility(ProgressBar.INVISIBLE);
                        Log.d("debug", "Using ads fetched from local DB.");
                        addAdsToAdapter(ads);
                    }
                } else {
                    Log.d("error", "Exception while fetching ads: " + e);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    private void fetchAdsFromRemote(ParseQuery<ParseObject> query) {

        Log.d("debug", "Fetching ads from remote DB.");
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> ads, ParseException e) {

                pbLoading.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    Log.d("info", "Fetching ads from remote DB. Found " + ads.size());
                    addAdsToAdapter(ads);
                    mListener.onListRefreshederested();

                    // Unpin previously cached data and re-pin the newly fetched.
                    if (ads != null && !ads.isEmpty()) {
                        unpinAndRepin(ads);
                    }

                } else {
                    Log.d("error", "Exception while fetching remote ads: " + e);
                }
            }
        });
    }

    private void unpinAndRepin(final List<ParseObject> ads) {

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
        // Add the latest results for this query to the cache.
        Log.d("debug", "Pinning newly fetched ads " + ads.size());
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
    }

    private void addAdsToAdapter(List<ParseObject> ads) {


        List<AbstractListItem> sortedAds = new ArrayList<AbstractListItem>();
        sortedAds.add(new HeaderListItem("Item on sale"));

        for (int i = 0; i < ads.size(); i++) {
            Ad ad = (Ad)ads.get(i);
            if (ad.isOnSale()) {
                sortedAds.add(new AdListItem(ad));
            }
        }

        sortedAds.add(new HeaderListItem("Items on sale"));

        for (int i = 0; i < ads.size(); i++) {
            Ad ad = (Ad)ads.get(i);
            if (ad.getCurrentStatus().equals(Ad.SOLD) ) {
                sortedAds.add(new AdListItem(ad));
            }
        }

        for (int i = 0; i < ads.size(); i++) {
            Ad ad = (Ad)ads.get(i);
            if (ad.getCurrentStatus().equals(Ad.SALE)) {
                sortedAds.add(new AdListItem(ad));
            }
        }

        mAdArrayAdapter.addAll(sortedAds);
        mAdArrayAdapter.notifyDataSetChanged();

        mListener.onListRefreshederested();

    }


}
