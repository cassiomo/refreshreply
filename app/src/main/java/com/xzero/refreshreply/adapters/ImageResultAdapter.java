package com.xzero.refreshreply.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.helpers.RoundTransform;
import com.xzero.refreshreply.models.Ad;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ImageResultAdapter extends ArrayAdapter<Ad>  {


    private static final String TAG = "ImageResultAdapter";
//    private final Random mRandom;
//    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

//    private final ArrayList<Integer> mBackgroundColors;

    public ImageResultAdapter(Context context, List<Ad> images) {
        super(context, R.layout.item_image_result, images);
//        mRandom = new Random();
//        mBackgroundColors = new ArrayList<Integer>();
    }

    public static class ViewHolder {
        @InjectView(R.id.ivImage)
        ImageView imageView;
        @InjectView(R.id.tvAdInfo)
        TextView tvAdInfo;
        @InjectView(R.id.tvAdSeller)
        TextView tvAdSeller;
        @InjectView(R.id.tvAdTitlePrice)
        TextView tvAdTitlePrice;
        @InjectView(R.id.ivAdTitleOwnerImage)
        ImageView ivAdTitleOwnerImage;

        public ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Ad imageResult = getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //double positionHeight = getPositionRatio(position);

//        int backgroundIndex = position >= mBackgroundColors.size() ?
//                position % mBackgroundColors.size() : position;
//
//        convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

        viewHolder.imageView.setImageResource(0);
        //viewHolder.imageView.setHeightRatio(positionHeight);
        viewHolder.tvAdInfo.setText(Html.fromHtml(imageResult.getTitle()));
        viewHolder.tvAdTitlePrice.setText(Html.fromHtml(" $" + imageResult.getPrice()));
        viewHolder.tvAdSeller.setText(Html.fromHtml(imageResult.getOwnerName()));

        Picasso.with(getContext()).load(ChatListAdapter.getProfilePic(imageResult.getOwnerName()))
                .error(R.drawable.taptapchat)
                .transform(new RoundTransform())
                .into(viewHolder.ivAdTitleOwnerImage);
        Picasso.with(getContext()).load(imageResult.getPhotoUrl()).into(viewHolder.imageView);

        return convertView;
    }

//    private double getPositionRatio(final int position) {
//        double ratio = sPositionHeightRatios.get(position, 0.0);
//        // if not yet done generate and stash the columns height
//        // in our real world scenario this will be determined by
//        // some match based on the known height and width of the image
//        // and maybe a helpful way to get the column height!
//        if (ratio == 0) {
//            ratio = getRandomHeightRatio();
//            sPositionHeightRatios.append(position, ratio);
//            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
//        }
//        return ratio;
//    }
//
//    private double getRandomHeightRatio() {
//        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
//    }

    public Ad getAdAtIndex(int index) {
        Ad ad = getItem(index);
        return ad;
    }
}
