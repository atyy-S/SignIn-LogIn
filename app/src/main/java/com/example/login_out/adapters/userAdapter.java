package com.example.login_out.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_out.databinding.ItemContainerUserBinding;
import com.example.login_out.listeners.UserListener;
import com.example.login_out.models.User;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.userViewHolder> {
/*
keep track of the users
 */
    private final List <User> users ;
    private final UserListener userListener;

    public userAdapter(List<User> users,UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    /**
     * create a the viewholder, where new View will be added after it is bound to
     *      *               an adapter position
     */
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new userViewHolder(itemContainerUserBinding);
    }
/*
onBindViewHolder : will be updated to represent the contents of the item at the given position in the data set
 */
    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    /**
     * @return the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }
//hold user data
    class userViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;

        public userViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super (itemContainerUserBinding.getRoot());
            binding=itemContainerUserBinding;
        }
        void setUserData(User user){ // set user's data
            binding.textName.setText(user.Firstname+ " " +user.lastname);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }
    //encode user pf image and return decode user's image pf
    private Bitmap getUserImage(String encodedImage){
        byte [] bytes= Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
