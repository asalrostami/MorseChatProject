package com.example.asal.morsechatproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.asal.morsechatproject.Model.Brain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "ERROR";

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private EditText mEditTextName, mEditTextEmail, mEditTextPassword;
    private Button mButtonRegister;
    private ProgressDialog progressDialog;
    Brain brain = new Brain();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextName = (EditText) findViewById(R.id.etName_register);
        mEditTextEmail = (EditText) findViewById(R.id.etEmail_register);
        mEditTextPassword = (EditText) findViewById(R.id.etPassword_register);

        mButtonRegister = (Button) findViewById(R.id.btnCreateAccount_register);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_register = mEditTextName.getText().toString();
                String email_register = mEditTextEmail.getText().toString();
                String password_register = mEditTextPassword.getText().toString();

                RegisterAccount(name_register, email_register, password_register);

            }


        });

    }

    private void RegisterAccount(String name_register, String email_register, String password_register) {
        if (TextUtils.isEmpty(name_register)) {
            brain.showToast("Please Enter Name!", RegisterActivity.this);
        } else if (TextUtils.isEmpty(email_register)) {
            brain.showToast("Please Enter Email!", RegisterActivity.this);
        } else if (TextUtils.isEmpty(password_register)) {
            brain.showToast("Please Enter Password!", RegisterActivity.this);
        } else {


            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please Wait ....");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email_register, password_register)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                            else
                            {
                                Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                brain.showToast("ERROR Occured.Try Again ...",RegisterActivity.this);
                            }
                            progressDialog.dismiss();
                        }


                    });


        }

    }
    //hide keyboard by clicking everywhere else on the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
