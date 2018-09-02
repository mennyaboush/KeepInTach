package com.example.hannybuns.firebaseproject2.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hannybuns.firebaseproject2.KeepInTouchLogic;
import com.example.hannybuns.firebaseproject2.Keys;
import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.objects.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private Button buttonRegister;
    private EditText editTextName, editTextEmail, editTextPassword, editPhone;
    String uesrToken;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegister = findViewById(R.id.buttonRegister);

        editTextName = findViewById(R.id.editRegisterName);
        editTextEmail = findViewById(R.id.editRegisterEmail);
        editTextPassword = findViewById(R.id.editRegisterPassword);
        editPhone = findViewById(R.id.editPhone);

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view ==buttonRegister)
            registerUser();

    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editPhone.getText().toString().trim();

        if(TextUtils.isEmpty((name))){
            Toast.makeText(this, "enter your name"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty((email))){
            Toast.makeText(this, "Please anter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this, "password have to be more then 6 char"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty((password))){
            Toast.makeText(this, "Please anter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty((phone))){
            Toast.makeText(this, "enter phone number, please try again"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.length()<6){
            Toast.makeText(this, "phone number is incorrect"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), KeepInTouchActivity.class));
                    saveUserInformation(name, email, phone);
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Registered Faild, please try again", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

     private void saveUserInformation(String name, String email, String phoneNumber) {
        FirebaseUser user = KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().getUser();
        UserInformation userInformation = new UserInformation(name, email, phoneNumber, user.getUid());
        KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().getDatabaseReference().getRef().
                child("my_users").child(user.getUid()).setValue(userInformation);
        KeepInTouchLogic.getInstance().getMyfirebaseHelperRef().addToFirebase(
                Keys.PHONE_NUMBERS+ "/"+ phoneNumber, user.getUid());

    }




}
