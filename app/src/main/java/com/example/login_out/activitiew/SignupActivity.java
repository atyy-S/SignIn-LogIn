package com.example.login_out.activitiew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_out.databinding.ActivitySignupBinding;
import com.example.login_out.utilities.Constant;
import com.example.login_out.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private String encodeImage;
    private PreferenceManager preferenceManager;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
    }

    /**setListener()
     *for UI element, lead to sign in page, check information from the sign-up page
     * help to get image from storage
     */
    private void setListener() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidateSignUpDetails()) {
                SignUp();
            }
        });
        binding.imageLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     *SignUp method manages the sign-up process in your Android app, leveraging Firebase Firestore
     * to store user information and handle sign-up logic
     */
    private void SignUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constant.KEY_FIRSTNAME, binding.inputFirstName.getText().toString());
        user.put(Constant.KEY_LASTNAME, binding.inputLastName.getText().toString());
        user.put(Constant.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constant.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constant.KEY_IMAGE, encodeImage);

        database.collection(Constant.KEY_COLLECTION_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constant.KEY_FIRSTNAME, binding.inputFirstName.getText().toString());
                    preferenceManager.putString(Constant.KEY_LASTNAME, binding.inputLastName.getText().toString());
                    preferenceManager.putString(Constant.KEY_IMAGE, encodeImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**@param bitmap
     * reducing image size, and transfer image data to text to then being store.
     * @return
     */
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     *pickImage allows users to select an image from their gallery, displays
     * it in the profile image view, and encodes it for further use, such as storing in a database
     */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

    /**
     *isValidateSignUpDetails() check if data user enter is valid or not
     * or they fill all the information or not
     * @return
     */
    private Boolean isValidateSignUpDetails() {
        if (encodeImage == null) {
            showToast("Please add a profile image");
            return false;
        } else if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your First Name");
            return false;
        } else if (binding.inputLastName.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Last Name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please Enter Valid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please Enter your Password");
            return false;
        } else if (binding.confirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your Password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.confirmPassword.getText().toString())) {
            showToast("Password & Confirm Password must be the same");
            return false;
        } else {
            return true;
        }
    }

    /**
     * To make sure user know the data is being check
     * @param isLoading
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
