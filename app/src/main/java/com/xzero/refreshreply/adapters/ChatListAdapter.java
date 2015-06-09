package com.xzero.refreshreply.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xzero.refreshreply.R;
import com.xzero.refreshreply.helpers.RoundTransform;
import com.xzero.refreshreply.models.Message;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by kemo on 9/30/14.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {
    private String mUserId;

    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.imageLeft = (ImageView)convertView.findViewById(R.id.ivProfileLeft);
            holder.imageRight = (ImageView)convertView.findViewById(R.id.ivProfileRight);
            holder.leftName = (TextView) convertView.findViewById(R.id.tvChatLeft);
            holder.rightName = (TextView) convertView.findViewById(R.id.tvChatRight);
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }
        final Message message = (Message)getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        final boolean isMe = message.getUserId().equals(mUserId);
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.rightName.setVisibility(View.VISIBLE);
            holder.rightName.setText(message.getUserName());
            holder.leftName.setVisibility(View.GONE);
            holder.leftName.setText("");
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.leftName.setVisibility(View.VISIBLE);
            holder.leftName.setText(message.getUserName());
            holder.rightName.setVisibility(View.GONE);
            holder.rightName.setText("");
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        if (message.getUserName().equals("june")) {
            holder.body.setTextColor(Color.parseColor("#ff69b4"));
        } else {
            holder.body.setTextColor(Color.parseColor("#21ace3"));
        }
        final ImageView profileView = isMe ? holder.imageRight : holder.imageLeft;
//        Picasso.with(getContext()).load(getProfileUrl(message.getUserId())).into(profileView);
        Picasso.with(getContext()).load(getProfilePic(message.getUserName()))
                .error(R.drawable.taptapchat)
                .transform(new RoundTransform())
                .into(profileView);
        holder.body.setText(message.getBody());
        return convertView;
    }

    // Create a gravatar image based on the hash value obtained from userId
    public static String getProfilePic(final String userName) {
        if (userName.equals("june")) {
            return "http://www.salon124.com/wp-content/uploads/2013/07/keira-knightley-hairstyles.jpg";
        } else if (userName.equals("john")) {
            return "http://s.plurielles.fr/mmdia/i/83/9/brad-pitt-aujourd-hui-10779839fbwvd.jpg";
        } else {
            return getProfileUrl(userName);
        }
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }

    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
        public TextView leftName;
        public TextView rightName;
    }

}