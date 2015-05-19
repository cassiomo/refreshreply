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
import com.xzero.refreshreply.models.Ad;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ImageResultAdapter extends ArrayAdapter<Ad>  {

    public ImageResultAdapter(Context context, List<Ad> images) {
        super(context, R.layout.item_image_result, images);
    }

    public static class ViewHolder {
        @InjectView(R.id.ivImage)
        ImageView imageView;
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvPrice)
        TextView tvPrice;

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

//        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
//        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        viewHolder.imageView.setImageResource(0);

        viewHolder.tvTitle.setText(Html.fromHtml(imageResult.getTitle()));
        viewHolder.tvPrice.setText(Html.fromHtml(imageResult.getPrice()));
        Picasso.with(getContext()).load(imageResult.getPhotoUrl()).into(viewHolder.imageView);

        return convertView;
    }

    public Ad getAdAtIndex(int index) {
        Ad ad = getItem(index);
        return ad;
    }
}
