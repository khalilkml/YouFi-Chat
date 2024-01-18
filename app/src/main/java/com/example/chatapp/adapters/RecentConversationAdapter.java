package com.example.chatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Listeners.ConversionListener;
import com.example.chatapp.R;
import com.example.chatapp.databinding.ItemContainerRecentConversationBinding;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;

import java.util.List;


public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;
    private final Context context;
    public RecentConversationAdapter(Context context,List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;

    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ConversionViewHolder holder, int position) {
            ChatMessage chatMessage = chatMessages.get(position);
            Boolean isimage = false;
            isimage = chatMessage.ismessage;
            holder.setData(chatMessage, isimage);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversationBinding binding;

        ConversionViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }

        void setData(ChatMessage chatMessage, Boolean isimage) {
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversationImage));
            binding.textName.setText(chatMessage.conversationName);

            // Check if isimage is not null before accessing it
            if (isimage != null && !isimage) {
                // If isimage is true, display the text
                binding.textRecentMessage.setText(chatMessage.message);
                binding.textRecentMessage.setVisibility(View.VISIBLE);
                binding.Imagemessage.setVisibility(View.INVISIBLE);
            } else {
                // If isimage is false or null, display the image icon
                binding.textRecentMessage.setVisibility(View.INVISIBLE);
                binding.Imagemessage.setImageResource(R.drawable.baseline_image_24);
                binding.Imagemessage.setVisibility(View.VISIBLE);
            }

            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversationId;
                user.name = chatMessage.conversationName;
                user.image = chatMessage.conversationImage;
                conversionListener.onConversionClick(user);
            });
        }

    }
    private Bitmap getConversationImage(String encodedImage) {
        if (encodedImage != null && !encodedImage.isEmpty()) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            Toast.makeText(context, "Error in loading image", Toast.LENGTH_SHORT).show();
            return null; // For example, returning null here
        }
    }
}
