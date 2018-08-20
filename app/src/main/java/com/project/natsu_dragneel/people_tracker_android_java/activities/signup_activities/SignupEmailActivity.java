package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities.SigninEmailActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;

public class SignupEmailActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText signup_email_edittext;
    Button signup_email_next_button;
    ProgressDialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        signup_email_edittext = (EditText)findViewById(R.id.signup_email_edittext);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        signup_email_next_button = (Button)findViewById(R.id.signup_email_next_button);
        signup_email_next_button.setEnabled(false);
        signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
        toolbar.setTitle("Email Address");
        setSupportActionBar(toolbar);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        signup_email_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(signup_email_edittext.getText().toString().matches(emailPattern) && s.length() > 0)
                {
                    signup_email_next_button.setEnabled(true);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    signup_email_next_button.setEnabled(false);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void email_exist_check(View v)
    {
        dialog.setMessage("Please wait");
        dialog.show();

        auth.fetchProvidersForEmail(signup_email_edittext.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        dialog.dismiss();
                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(!check)
                        {
                            Intent myIntent = new Intent(SignupEmailActivity.this,SignupPasswordActivity.class);
                            myIntent.putExtra("email",signup_email_edittext.getText().toString());
                            startActivity(myIntent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"You already have an account. Please signin.",Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SignupEmailActivity.this,SigninEmailActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }
}