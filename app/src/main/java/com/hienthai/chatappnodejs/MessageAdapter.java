package com.hienthai.chatappnodejs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;
    private static final int TYPE_IMAGE_SENT = 3;
    private static final int TYPE_IMAGE_RECEIVED = 4;

    private List<JSONObject> messages = new ArrayList<>();
    private LayoutInflater inflater;

    public MessageAdapter (LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                return new SentMessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sent_message, parent, false));
            case TYPE_MESSAGE_RECEIVED:
                return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_mesage, parent, false));
            case TYPE_IMAGE_SENT:
                return new SentImageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_sent_image, parent, false));
            case TYPE_IMAGE_RECEIVED:
                return new ReceivedImageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_received_image, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message")) {

                    SentMessageHolder sentMessageHolder = (SentMessageHolder) holder;
                    sentMessageHolder.txtItemSentMessage.setText(message.getString("message"));
                } else {
                    SentImageHolder sentImageHolder = (SentImageHolder) holder;
                    Bitmap bitmap = getBitmapFromString(message.getString("image"));

                    sentImageHolder.imgItemSentImage.setImageBitmap(bitmap);

                }

            } else {
                if (message.has("message")) {
                    ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
                    receivedMessageHolder.txtItemNameReceivedMessage.setText(message.getString("name"));
                    receivedMessageHolder.txtItemReceivedMessage.setText(message.getString("message"));
                } else {
                    ReceivedImageHolder receivedImageHolder= (ReceivedImageHolder) holder;
                    receivedImageHolder.txtItemNameReceivedImage.setText(message.getString("name"));
                    Bitmap bitmap = getBitmapFromString(message.getString("image"));
                    receivedImageHolder.imgItemReceivedImage.setImageBitmap(bitmap);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void addItem(JSONObject jsonObject){
        messages.add(jsonObject);
        notifyDataSetChanged();
    }

    private Bitmap getBitmapFromString(String image) {

        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    @Override
    public int getItemViewType(int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message")) {
                    return TYPE_MESSAGE_SENT;
                } else
                    return TYPE_IMAGE_SENT;

            } else {
                if (message.has("message")) {
                    return TYPE_MESSAGE_RECEIVED;
                } else
                    return TYPE_IMAGE_RECEIVED;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView txtItemSentMessage;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            txtItemSentMessage = itemView.findViewById(R.id.txtItemSentMessage);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView txtItemNameReceivedMessage;
        TextView txtItemReceivedMessage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            txtItemNameReceivedMessage = itemView.findViewById(R.id.txtItemNameReceivedMessage);
            txtItemReceivedMessage = itemView.findViewById(R.id.txtItemReceivedMessage);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {

        ImageView imgItemSentImage;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);

            imgItemSentImage = itemView.findViewById(R.id.imgItemSentImage);
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {

        TextView txtItemNameReceivedImage;
        ImageView imgItemReceivedImage;

        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);

            txtItemNameReceivedImage = itemView.findViewById(R.id.txtItemNameReceivedImage);
            imgItemReceivedImage = itemView.findViewById(R.id.imgItemReceivedImage);
        }
    }


}
