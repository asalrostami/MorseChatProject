package com.example.asal.morsechatproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asal.morsechatproject.Model.Brain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Brain brain;
    private Toolbar mToolbar;

    private EditText mEditTextEmail,mEditTextPassword;
    private Button mButtonLogin;
    private ProgressDialog mProgressDialog;


    private static final String TAG = "ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);

        mEditTextEmail = (EditText)findViewById(R.id.etEmail_login);
        mEditTextPassword = (EditText)findViewById(R.id.etPassword_login);
        mButtonLogin = (Button)findViewById(R.id.btnSignin_login);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = mEditTextEmail.getText().toString();
                String password = mEditTextPassword.getText().toString();
                
                LoginUserAccount(email,password);
            }
        });
    }

    private void LoginUserAccount(String email, String password) {
        if(TextUtils.isEmpty(email))
        {
            brain.showToast("Please Enter Email",LoginActivity.this);
        }
        else if(TextUtils.isEmpty(password))
        {
            brain.showToast("Please Enter Password",LoginActivity.this);
        }
        else
        {
            mProgressDialog.setTitle("Login Account");
            mProgressDialog.setMessage("Please Wait,while we are verifying your credentials ... ");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {

                        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else
                    {

                        Toast.makeText(LoginActivity.this,"Wrong Email and Password",Toast.LENGTH_SHORT).show();
                       // brain.showToast("Wrong Email and Password",LoginActivity.this);
                        Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                    }
                    mProgressDialog.dismiss();

                }
            });
        }

    }
}
