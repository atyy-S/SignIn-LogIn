package com.example.login_out.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_out.databinding.ItemContainerReceivedMessageBinding;
import com.example.login_out.databinding.ItemContainerSendMessageBinding;
import com.example.login_out.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Bitmap receiverProfileImage;
    private final List<ChatMessage> chatMessages;//all chat messages exchanged between the user and the reciever
    private final String sendId; //contains the senders identification
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public ChatAdapter(Bitmap bitmap, List<ChatMessage> chatMessages,String sendId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage=receiverProfileImage;
        this.sendId=sendId;
    }

    /**
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return  new SentMessageViewHolder(ItemContainerSendMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }else {
            return  new RecieverMessageViewHolder(ItemContainerReceivedMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    /**
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }else {
            ((RecieverMessageViewHolder)holder)
                    .setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    /**
     *
     * @return number of messages in the chat
     */
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    /**
     * @param position position to query
     * @return if message is being sent or received
     */
    public int getItemViewType(int position){
        if(chatMessages.get(position).senderId.equals(sendId)){
            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
private final ItemContainerSendMessageBinding binding;

        public SentMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding) {
            super(itemContainerSendMessageBinding.getRoot());
            binding =itemContainerSendMessageBinding;
        }
        void setData (ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dataTime);

        }
    }
    static class RecieverMessageViewHolder extends RecyclerView.ViewHolder{
        /**connects the reciever message binding xml of this java file
         */
        private final ItemContainerReceivedMessageBinding binding;

        /**
         * @param itemContainerReceivedMessageBinding container of received messages
         */
        public RecieverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
       /**
         * @param chatMessage Message that was received
         * @param receiverProfileImage profile image of Member getting messages
         */
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dataTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }}
}
