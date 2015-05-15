package com.xzero.refreshreply.listeners;


import com.xzero.refreshreply.models.Ad;

public interface AdListListener {
    public void onListRefreshederested();
    public void onAdListRowSelected(Ad ad);
    //public void onShouldInvalidatePagers();
}
