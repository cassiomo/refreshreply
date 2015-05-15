package com.xzero.refreshreply;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.xzero.refreshreply.models.Ad;


public class AdPersister {

    public static final String ALL_ADS = "allAds";

    public static Ad getAdByObjectIdSyncly(String adObjectId) {
        final ParseQuery<Ad> query = ParseQuery.getQuery(Ad.class);
        query.fromPin(ALL_ADS);
        query.whereEqualTo("objectId", adObjectId);
        try {
            return ((Ad) query.getFirst());
        } catch (ParseException e) {
            Log.d("debug", "getAdByObjectIdSyncly failed to return ad with objectId: "
                    + adObjectId + " " + e.toString());
        }
        return null;
    }
}
