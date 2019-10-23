package com.devil.yokentaskmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewTaskActivity extends AppCompatActivity {
    String taskTitle,taskDescription,taskType;
    private String key;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        sessionManager = new SessionManager(this);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        ((RadioButton)findViewById(R.id.open)).setChecked(true);
        findViewById(R.id.addTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskTitle = ((TextView)findViewById(R.id.taskTitle)).getText().toString();
                taskDescription = ((TextView)findViewById(R.id.taskDescription)).getText().toString();
                if(((RadioButton)findViewById(R.id.open)).isChecked())
                    taskType = "O";
                else if(((RadioButton)findViewById(R.id.close)).isChecked())
                    taskType = "C";
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("tasks");
                key = myRef.push().getKey();
                myRef.child(key).child("title").setValue(taskTitle);
                myRef.child(key).child("type").setValue(taskType);
                if(((CheckBox)findViewById(R.id.checkbox)).isChecked())
                    myRef.child(key).child("admin").setValue("Anonymous");
                else
                    myRef.child(key).child("admin").setValue(sessionManager.getEmail());
                myRef.child(key).child("description").setValue(taskDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewTaskActivity.this,"Task created.",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });
    }
}
