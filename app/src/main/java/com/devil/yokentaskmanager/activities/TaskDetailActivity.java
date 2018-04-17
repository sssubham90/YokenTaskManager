package com.devil.yokentaskmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.ListDividerItem;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.devil.yokentaskmanager.listAdapters.MyUserListRecyclerViewAdapter;
import com.devil.yokentaskmanager.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private List<User> users;
    private SessionManager sessionManager;
    String taskID,taskTitle,type;
    private boolean res;
    private Button join;
    private DatabaseReference mRef;
    private MyUserListRecyclerViewAdapter adapter;
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        users = new ArrayList<>();
        join = findViewById(R.id.join);
        Button add = findViewById(R.id.addUser);
        Button remove = findViewById(R.id.removeUser);
        mRef = FirebaseDatabase.getInstance().getReference("tasks/"+ getIntent().getStringExtra("id"));
        RecyclerView recyclerView = findViewById(R.id.users);
        adapter = new MyUserListRecyclerViewAdapter(users, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskDetailActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ListDividerItem(TaskDetailActivity.this, LinearLayoutManager.VERTICAL, R.drawable.listdivider));
        recyclerView.setAdapter(adapter);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ((TextView)findViewById(R.id.taskID)).setText(taskID = dataSnapshot.getKey());
                ((TextView)findViewById(R.id.taskTitle)).setText(taskTitle = dataSnapshot.child("title").getValue().toString());
                ((TextView)findViewById(R.id.taskDescription)).setText(dataSnapshot.child("description").getValue().toString());
                ((TextView)findViewById(R.id.taskAdmin)).setText(dataSnapshot.child("admin").getValue().toString());
                type = dataSnapshot.child("type").getValue().toString();
                if(type.equals("O")){
                    join.setVisibility(View.VISIBLE);
                    join.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mRef.child("users").child(sessionManager.getEmail()).setValue(sessionManager.getName());
                            FirebaseDatabase.getInstance().getReference("users/"+sessionManager.getEmail()+"/tasks").child(taskID).setValue(taskTitle);
                        }
                    });
                }
                else
                    join.setVisibility(View.GONE);
                users.clear();
                for(DataSnapshot childsnapshot:dataSnapshot.child("users").getChildren()){
                    users.add(new User(childsnapshot.getKey(),childsnapshot.getValue().toString()));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase","Unable to fetch Data");
            }
        });

        if(sessionManager.getAdminRights().equals("Y")){
            add.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater li = LayoutInflater.from(TaskDetailActivity.this);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            ID = userInput.getText().toString();
                                            res = false;
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot childsSnapshot : dataSnapshot.getChildren()){
                                                        if(childsSnapshot.getKey().equals(ID)){
                                                            FirebaseDatabase.getInstance().getReference("users/"+ID+"/tasks").child(taskID).setValue(taskTitle);
                                                            mRef.child("users").child(sessionManager.getEmail()).setValue(sessionManager.getName());
                                                            res = true;
                                                            break;
                                                        }
                                                    }
                                                    if(!res)
                                                        Toast.makeText(TaskDetailActivity.this,"User ID doesn't exist",Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater li = LayoutInflater.from(TaskDetailActivity.this);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskDetailActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            ID = userInput.getText().toString();
                                            res = false;
                                            FirebaseDatabase.getInstance().getReference("tasks/"+taskID+"/users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot childsSnapshot : dataSnapshot.getChildren()){
                                                        if(childsSnapshot.getKey().equals(ID)){
                                                            FirebaseDatabase.getInstance().getReference("tasks/"+taskID+"/users").child(ID).setValue(null);
                                                            FirebaseDatabase.getInstance().getReference("users/"+ID+"/tasks").child(taskID).setValue(null);
                                                            res = true;
                                                            break;
                                                        }
                                                    }
                                                    if(!res)
                                                        Toast.makeText(TaskDetailActivity.this,"User ID doesn't exist",Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
        else{
            add.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        }

        if(sessionManager.getAdminRights().equals("Y")){
            Button doc = findViewById(R.id.docs);
            doc.setVisibility(View.VISIBLE);
            doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(TaskDetailActivity.this,DocumentListActivity.class).putExtra("id",taskID));
                }
            });
        }
    }
}
