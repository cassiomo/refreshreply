package com.xzero.refreshreply;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parse.ParseGeoPoint;
import com.xzero.refreshreply.helpers.AddressUtil;
import com.xzero.refreshreply.helpers.DateTimeUtil;
import com.xzero.refreshreply.models.Ad;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

public class AdRowView extends RelativeLayout {

    public ViewHolder viewHolder;

    private DecimalFormat df = new DecimalFormat("#.#");

    public Ad mAd;

    private ImageView mGraphWord;
    private Context mContext;
    private TextView mAdFlowLabel;

    public AdRowView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        View.inflate(context, R.layout.row_ad, this);
        viewHolder = new ViewHolder();
        populateViewHolder();
    }

    private void populateViewHolder() {
        viewHolder.tvLastUpdated = (TextView)findViewById(R.id.tvAdLastUpdated);
        viewHolder.tvLocation = (TextView)findViewById(R.id.tvAdLocation);
        viewHolder.tvAdDistance = (TextView)findViewById(R.id.tvAdDistance);
        viewHolder.ivStatusIndicator = (ImageView)findViewById(R.id.ivAdStatusIndicator);
        mGraphWord = (ImageView)findViewById(R.id.ivGraphWord);
        mAdFlowLabel = (TextView)findViewById(R.id.tvAdFlowLabelNonExpanding);
    }

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = mContext.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

    public void updateSubviews(ParseGeoPoint currentUserLocation) {

        //String mostOfTheTimeCorrectRelativeTime = DateTimeUtil.getRelativeTimeofTweet(mAd.getUpdatedAt().toString());
        Date now = new Date();
        String mostOfTheTimeCorrectRelativeTime = DateTimeUtil.getRelativeTimeofTweet(now.toString());

        if (mostOfTheTimeCorrectRelativeTime.equalsIgnoreCase("yesterday")) {
            viewHolder.tvLastUpdated.setText(mostOfTheTimeCorrectRelativeTime);
        }
        else {
            viewHolder.tvLastUpdated.setText(mostOfTheTimeCorrectRelativeTime);
        }

//        viewHolder.ivStatusIndicator.setImageResource(mAd.getDrawableBasedOnStatus());

        final Double distanceFromOrigin =
                currentUserLocation.distanceInKilometersTo(mAd.getLocation());
        viewHolder.tvAdDistance.setText(
                String.format("%s km", df.format(distanceFromOrigin.doubleValue())));

        setupLocationLabel(mAd);

//        if (mAd.isClaimedByATechnician()) {
//            viewHolder.ivStatusIndicator.setImageResource(R.drawable.ic_list_starred);
//        }

        String fname = String.format("listviewSparkline%d.png", Math.abs(mAd.getHash()) % 9);
        mGraphWord.setImageBitmap(getBitmapFromAsset(fname));

        mAdFlowLabel.setText(generateRandomAdFlowString());
    }

    public static String generateRandomAdFlowString() {
        return String.format("%d.0L/hr", 9 + Math.abs(new Random().nextInt() % 20));
    }
    private void setupLocationLabel(Ad ad) {
        try {
            viewHolder.tvLocation.setText(AddressUtil.stripCountryFromAddress(ad.getAddress()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTextViews() {
        viewHolder.tvLocation.setText("");
        viewHolder.tvLastUpdated.setText("");
        viewHolder.tvAdDistance.setText("");
    }

    static class ViewHolder {
        TextView tvLastUpdated;
        TextView tvLocation;
        TextView tvAdDistance;
        ImageView ivStatusIndicator;
    }
}
