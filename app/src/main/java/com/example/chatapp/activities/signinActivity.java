package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.Utilities.Constants;
import com.example.chatapp.Utilities.PreferenceManager;
import com.example.chatapp.databinding.ActivitySigninBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class signinActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // binding=ActivitySigninBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        preferenceManager =new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.createNewAccount.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext() ,
                        SignupActivity.class)));
       binding.ButtonSignIn.setOnClickListener(view -> {
           if (isValidSingInDetail()){
               signIn();
           }
       });
    }
    private void signIn() {
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputemail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful() && task.getResult() != null
                   && task.getResult().getDocuments().size() >0){
                       DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                       preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                       preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                       preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                       preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                       Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                   }else {
                       loading(false);
                       showToast("Unable to Sign In");
                   }
                });
    }
    private void loading(boolean isLoading)
    {
        if (isLoading){
            binding.ButtonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.ButtonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSingInDetail(){
     if (binding.inputemail.getText().toString().trim().isEmpty()){
         showToast("Enter Email");
         return  false;
     }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputemail.getText().toString()).matches()){
         showToast("Enter Valid Email");
         return false;
     }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
         showToast("Enter Password");
         return false;
     }else {
         return true;
     }
    }


    /**private void addDataToFireStore() {
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> data=new HashMap<>();
        data.put("First_Name","Mansi");
        data.put("Last_Name","Vandra");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }**/
}