package com.example.login_out.activitiew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.login_out.adapters.userAdapter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.login_out.R;
import com.example.login_out.databinding.ActivityUserBinding;
import com.example.login_out.listeners.UserListener;
import com.example.login_out.models.User;
import com.example.login_out.utilities.Constant;
import com.example.login_out.utilities.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity implements UserListener {
    private ActivityUserBinding binding; //binding xml to this java file
    private com.example.login_out.utilities.PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager= new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListener();
        getUsers();
    }
    //set listener to button
    private void setListener(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    //get data of user from firebase database
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constant.KEY_COLLECTION_USER).get()
                .addOnCompleteListener(this::onComplete);
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading (Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);

        }
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void onComplete(Task<QuerySnapshot> task) {
        loading(false);
        String currentUserId = preferenceManager.getString(Constant.KEY_USER_ID);
        if (task.isSuccessful() && task.getResult() != null) {
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                    continue;
                }
                User user = new User();
                user.Firstname = queryDocumentSnapshot.getString(Constant.KEY_FIRSTNAME);
                user.lastname = queryDocumentSnapshot.getString(Constant.KEY_LASTNAME);
                user.email = queryDocumentSnapshot.getString(Constant.KEY_EMAIL);
                user.image = queryDocumentSnapshot.getString(Constant.KEY_IMAGE);
                user.token = queryDocumentSnapshot.getString(Constant.KEY_FCM_TOKEN);
                user.id = queryDocumentSnapshot.getId();
                users.add(user);
            }
            if (users.size() > 0) {
                userAdapter userAdapter = new userAdapter(users,this);
                binding.usersRecyclerview.setAdapter(userAdapter);
                binding.usersRecyclerview.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }
        } else {
            showErrorMessage();
        }

    }

    @Override
    public void onUserClicked(User user) {
       Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
       intent.putExtra(Constant.KEY_USER, user);
       startActivity(intent);
       finish();
    }
}