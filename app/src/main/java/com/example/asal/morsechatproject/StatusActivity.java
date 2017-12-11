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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = "ERROR";

    private Toolbar mToolbar;
    private Button mButtonStatus;
    private EditText mEditTextStatus;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProgressDialog = new ProgressDialog(this);
        mToolbar = (Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonStatus = (Button)findViewById(R.id.btn_ChangeStatus_Status);
        mEditTextStatus = (EditText)findViewById(R.id.etStatus_status);

        mButtonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String new_status = mEditTextStatus.getText().toString();
                ChangeProfileStatus(new_status);

            }
        });

    }

    private void ChangeProfileStatus(String new_status) {

        if(TextUtils.isEmpty(new_status))
        {
            Toast.makeText(StatusActivity.this,"Please Write Status",Toast.LENGTH_SHORT).show();

        }
        else
        {
            mProgressDialog.setTitle("Change Profile Status");
            mProgressDialog.setMessage("Please wait....");
            mProgressDialog.show();

            mDatabaseReference.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        mProgressDialog.dismiss();
                        Intent settingsIntent = new Intent(StatusActivity.this,SettingsActivity.class);
                        startActivity(settingsIntent);
                        Toast.makeText(StatusActivity.this,"Status Profile Updated Successfully ...",Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                        Toast.makeText(StatusActivity.this,"ERROR Occured.Try Again ...",Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }
}
