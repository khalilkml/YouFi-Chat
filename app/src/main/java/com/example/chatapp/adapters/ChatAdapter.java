package com.example.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerReceivedImageBinding;
import com.example.chatapp.databinding.ItemContainerRecievedMessageBinding;
import com.example.chatapp.databinding.ItemContainerSentImageBinding;
import com.example.chatapp.databinding.ItemContainerSentMessageBinding;
import com.example.chatapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    //receiver means the other user
    private Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT_TEXT = 1;
    public static final int VIEW_TYPE_RECEIVED_TEXT = 2;
    public static final int VIEW_TYPE_SENT_IMAGE = 3;
    public static final int VIEW_TYPE_RECEIVED_IMAGE = 4;
    public void setReceiverProfileImage(Bitmap bitmap) {
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SENT_TEXT:
                return new SentTextMessageViewHolder(
                        ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_RECEIVED_TEXT:
                return new ReceivedTextMessageViewHolder(
                        ItemContainerRecievedMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_SENT_IMAGE:
                return new SentImageMessageViewHolder(
                        ItemContainerSentImageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_RECEIVED_IMAGE:
                return new ReceivedImageMessageViewHolder(
                        ItemContainerReceivedImageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            default:
                throw new IllegalArgumentException("Invalid viewType");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENT_TEXT:
                ((SentTextMessageViewHolder) holder).setTextData(message);
                break;
            case VIEW_TYPE_RECEIVED_TEXT:
                ((ReceivedTextMessageViewHolder) holder).setTextData(message, receiverProfileImage);
                break;
            case VIEW_TYPE_SENT_IMAGE:
                ((SentImageMessageViewHolder) holder).setImageData(message);
                break;
            case VIEW_TYPE_RECEIVED_IMAGE:
                ((ReceivedImageMessageViewHolder) holder).setImageData(message, receiverProfileImage);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.ismessage) {
            if (message.senderId.equals(senderId)) {
                return VIEW_TYPE_SENT_TEXT;
            } else {
                return VIEW_TYPE_RECEIVED_TEXT;
            }
        } else {
            // For image messages
            if (message.senderId.equals(senderId)) {
                return VIEW_TYPE_SENT_IMAGE;
            } else {
                return VIEW_TYPE_RECEIVED_IMAGE;
            }
        }

    }

    static class SentTextMessageViewHolder extends  RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;
        SentTextMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setTextData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
    static class SentImageMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentImageBinding binding;
        SentImageMessageViewHolder(ItemContainerSentImageBinding itemContainerSentImageBinding) {
            super(itemContainerSentImageBinding.getRoot());
            binding = itemContainerSentImageBinding;
        }
        void setImageData(ChatMessage chatMessage){
            binding.imageMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.message));
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceivedTextMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerRecievedMessageBinding binding;

        ReceivedTextMessageViewHolder(ItemContainerRecievedMessageBinding itemContainerRecievedMessageBinding) {
            super(itemContainerRecievedMessageBinding.getRoot());
            binding = itemContainerRecievedMessageBinding;
        }

        void setTextData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }
    }

    static class ReceivedImageMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedImageBinding binding;
        ReceivedImageMessageViewHolder(ItemContainerReceivedImageBinding itemContainerReceivedImageBinding) {
            super(itemContainerReceivedImageBinding.getRoot());
            binding = itemContainerReceivedImageBinding;
        }

        void setImageData(ChatMessage chatMessage,Bitmap receiverProfileImage){
            binding.imageMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.message));
            binding.textDateTime.setText(chatMessage.dateTime);
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }

    }

    static Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        }else {
            return null;
        }
    }
}
