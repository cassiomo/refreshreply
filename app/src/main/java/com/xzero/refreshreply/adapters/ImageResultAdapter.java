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

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ImageResultAdapter extends ArrayAdapter<Ad> implements OnRefreshListener {

    public ImageResultAdapter(Context context, List<Ad> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Ad imageResult = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        imageView.setImageResource(0);

        tvTitle.setText(Html.fromHtml(imageResult.getDescription()));
        Picasso.with(getContext()).load(imageResult.getPhotoUrl()).into(imageView);

        return convertView;
    }

    @Override
    public void onRefreshStarted(View view) {

    }
}
