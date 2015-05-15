package com.xzero.refreshreply.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.parse.ParseGeoPoint;
import com.xzero.refreshreply.AdRowView;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.listeners.AdListListener;

public class AdListItem implements AbstractListItem {

    public Ad ad;


    public AdListItem(Ad ad) {
        this.ad = ad;
    }

    @Override
    public View getView(final LayoutInflater inflater, final View convertView, final ParseGeoPoint location,
                        final AdListListener listener, final Context context) {

        AdRowView adRowView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            adRowView = (AdRowView)inflater.inflate(R.layout.row_ad_list_item, null);
        }
        else {
            adRowView = (AdRowView)convertView;
        }
        adRowView.clearTextViews();

        adRowView.mAd = ad;
        //adRowView.updateSubviews(location);

        return adRowView;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }

}
