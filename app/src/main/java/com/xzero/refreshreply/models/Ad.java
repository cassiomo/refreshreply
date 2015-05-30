package com.xzero.refreshreply.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.Serializable;


@ParseClassName("Ad")
public class Ad extends ParseObject implements Serializable {

    public static final String SOLD = "Sold";
    public static final String SALE = "Sale";

    public Ad() {

    }

    public void setCategoryId(String categoryId) {
        put("categoryId", categoryId);
    }

    public String getCategoryId() {
        return getString("categoryId");
    }

    public void setTitle(String title) {
        put("title", title) ;
    }

    public String getTitle() {
        return getString("title");
    }

    public void setPrice(String price) {
        put("price", price) ;
    }

    public String getPrice() {
        return getString("price");
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint point) {
        put("location", point);
    }

    public void setDescription(String description) {
        put("description", description) ;
    }

    public String getDescription() {
        return getString("description");
    }

    public void setOwnerId(String ownerId) {
        put("ownerId", ownerId);
    }

    public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

    public void setPhotoUrl(String photoUrl) {
        put("photoUrl", photoUrl);
    }

    public String getPhotoUrl() {
        return getString("photoUrl");
    }

    public String getOwnerId() {
        return getString("ownerId");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getAddress() {
        return getString("address");
    }

    public String getCurrentStatus() {
        return getString("currentStatus");
    }

    public void setCurrentStatus(String currentStatus) {
        put("currentStatus", currentStatus);
    }

    public String getName() {return getString("name"); }

    public String getOwnerName() {
        return getString("ownerName");
    }

    public void setOwnerName(String ownerName) {
        put("ownerName", ownerName);
    }

    public boolean isOnSale() {
        return getCurrentStatus().equalsIgnoreCase("Sale");
    }

    public int getHash() {
        if (getAddress() == null) {
            return 0;
        }
        return getAddress().hashCode();
    }
}
