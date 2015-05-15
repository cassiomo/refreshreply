package com.xzero.refreshreply.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.listeners.AdListListener;
import com.xzero.refreshreply.models.AbstractListItem;
import com.xzero.refreshreply.models.Ad;
import com.xzero.refreshreply.models.AdListItem;

public class AdListAdapter extends ArrayAdapter<AbstractListItem> {

    private LayoutInflater mInflater;

    public AdListListener rowListener;

    private ParseGeoPoint currentUserLocation;

    public AdListAdapter(Context context) {

        super(context, R.layout.row_ad_list_item);

        currentUserLocation = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return AbstractListItem.RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final AbstractListItem ad = getItem(position);
        return ad.getView(mInflater, convertView, currentUserLocation, rowListener, getContext());
    }

    public int indexForAdIncludingHeaders(Ad ad) {
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof AdListItem) {
                AdListItem adListItem = (AdListItem)item;
                if (adListItem.ad.getObjectId().equals(ad.getObjectId())) {
                    return i;
                }
                return i;
            }
        }
        return -1;
    }

    public int getAdIndexBetweenZeroAndNumberOfAds(Ad ad) {
        int count = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof AdListItem) {
                AdListItem adListItem = (AdListItem)item;
                if (adListItem.ad.getObjectId().equals(ad.getObjectId())) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }


    public Ad getAdAtIndex(int index) {
        int currentIndex = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof AdListItem) {
                if (index == currentIndex) {
                    return ((AdListItem) item).ad;
                }
                currentIndex++;
            }
        }
        return null;
    }

    public int getTotalAdCount() {
        int totalNumberOfRealAds = 0;
        for (int i = 0; i < getCount(); i++) {
            AbstractListItem item = getItem(i);
            if (item instanceof AdListItem) {
                totalNumberOfRealAds++;
            }
        }
        return totalNumberOfRealAds;
    }
}
