package com.devil.yokentaskmanager.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Devil";
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading.. Please wait.");
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final String email =((EditText)findViewById(R.id.email)).getText().toString();
                final String password =((EditText)findViewById(R.id.password)).getText().toString();
                final String name = ((EditText)findViewById(R.id.name)).getText().toString();
                if(email.isEmpty()||password.isEmpty())
                    Toast.makeText(RegisterActivity.this,"Invalid email and password.",Toast.LENGTH_SHORT);
                else{
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseDatabase.getInstance().getReference("users/"+email.substring(0,email.indexOf('@')).replace(".","")).child("ID").setValue(name);
                                FirebaseDatabase.getInstance().getReference("users/"+email.substring(0,email.indexOf('@')).replace(".","")).child("adminRights").setValue("N");
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Welcome, "+name,
                                        Toast.LENGTH_SHORT).show();
                                sessionManager.createLoginSession(name,email.substring(0,email.indexOf('@')).replace(".",""),"N");
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
