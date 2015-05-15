package com.xzero.refreshreply.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.listeners.AdListListener;

public class HeaderListItem implements AbstractListItem {

    private String mTitle;

    public HeaderListItem(String theTitle) {
        mTitle = theTitle;
    }

    @Override
    public int getViewType() {
        return RowType.HEADER_ITEM.ordinal();

    }

    @Override
    public View getView(LayoutInflater inflater, View convertView,
                        ParseGeoPoint location, AdListListener listener, Context context) {
        View v = inflater.inflate(R.layout.ad_list_row_header, null);
        TextView tv = (TextView)v.findViewById(R.id.tvListRowHeader);
        tv.setText(mTitle);

        return v;
    }
}
