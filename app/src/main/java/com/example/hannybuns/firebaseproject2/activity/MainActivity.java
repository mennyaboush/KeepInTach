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
import com.example.hannybuns.firebaseproject2.R;
import com.example.hannybuns.firebaseproject2.service.MyService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String JOB_TAG = "my_job_tag";
    private FirebaseJobDispatcher jobDispatcher;
    private static MainActivity _instance = null;
    private Button buttonRegister;
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    KeepInTouchLogic keepInTouchLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keepInTouchLogic = KeepInTouchLogic.getInstance();
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        startJob();
        if (keepInTouchLogic.getMyfirebaseHelperRef().getUser() != null) {
            startActivity(new Intent(this, KeepInTouchActivity.class));
        }

        editTextEmail = findViewById(R.id.editLoginEmail);
        editTextPassword = findViewById(R.id.editLoginPassword);

        buttonRegister = findViewById(R.id.newUser);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        buttonRegister.setOnClickListener(this);
        buttonLogIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        if (view == buttonLogIn) {
            userLogIn();
        }

    }

    private void userLogIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty((email))) {
            Toast.makeText(this, "Please anter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty((password))) {
            Toast.makeText(this, "Please anter password", Toast.LENGTH_SHORT).show();
            return;
        }
        keepInTouchLogic.getMyfirebaseHelperRef().getFirebaseAuth().
                signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), KeepInTouchActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Login Faild, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void startJob() {
        Job job = jobDispatcher.newJobBuilder()
                .setService(MyService.class)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTag(JOB_TAG)
                .setTrigger(Trigger.executionWindow(10, 15))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK).build();
        jobDispatcher.mustSchedule(job);
        Toast.makeText(this, "Job successfully", Toast.LENGTH_SHORT).show();
    }

    public void stopJob(View view) {
        jobDispatcher.cancel(JOB_TAG);
        Toast.makeText(this, "Job cancelled..", Toast.LENGTH_SHORT).show();


    }
}


