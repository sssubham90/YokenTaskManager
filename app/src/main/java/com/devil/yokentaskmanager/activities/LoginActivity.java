package com.devil.yokentaskmanager.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private String email,password;
    private FirebaseAuth mAuth;
    private String TAG = "YOKEN";
    private ProgressDialog dialog;
    private SessionManager sessionManager;

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
            startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading.. Please wait.");
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ((EditText)findViewById(R.id.email)).getText().toString();
                password = ((EditText)findViewById(R.id.password)).getText().toString();
                if(email.isEmpty()) Toast.makeText(LoginActivity.this,"Invalid Email", Toast.LENGTH_SHORT).show();
                else if(password.isEmpty()) Toast.makeText(LoginActivity.this,"Invalid Email", Toast.LENGTH_SHORT).show();
                else {
                    dialog.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                dialog.dismiss();
                                Log.d(TAG, "signInWithEmail:success");
                                sessionManager = new SessionManager(LoginActivity.this);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/"+email.substring(0,email.indexOf('@')).replace(".",""));
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        sessionManager.createLoginSession(dataSnapshot.child("ID").getValue().toString(),dataSnapshot.getKey(),dataSnapshot.child("adminRights").getValue().toString());
                                        Toast.makeText(LoginActivity.this,"Welcome, "+sessionManager.getName(),Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        dialog.dismiss();
                                        Log.w(TAG, "signInWithEmail:failure, Could not found records.");
                                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                dialog.dismiss();
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
