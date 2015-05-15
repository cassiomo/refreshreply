package com.xzero.refreshreply.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.parse.ParseGeoPoint;
import com.xzero.refreshreply.listeners.AdListListener;

public interface AbstractListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView,
                        ParseGeoPoint location, AdListListener listener,
                        Context context);

    enum RowType {
        LIST_ITEM, HEADER_ITEM
    }
}
