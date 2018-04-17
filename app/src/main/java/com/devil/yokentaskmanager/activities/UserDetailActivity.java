package com.devil.yokentaskmanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.ListDividerItem;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.devil.yokentaskmanager.listAdapters.MyMyTasksRecyclerViewAdapter;
import com.devil.yokentaskmanager.models.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDetailActivity extends AppCompatActivity {

    private List<Task> tasks;
    private String adminRights;
    private DatabaseReference mRef;
    private MyMyTasksRecyclerViewAdapter adapter;
    private SessionManager sessionManager;
    private Button admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        tasks = new ArrayList<>();
        sessionManager = new SessionManager(this);
        setContentView(R.layout.activity_user_detail);
        admin = findViewById(R.id.admin);
        admin.setVisibility(View.GONE);
        mRef = FirebaseDatabase.getInstance().getReference("users/"+ getIntent().getStringExtra("email"));
        Log.d("abcdef",mRef.getKey());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.userID)).setText(dataSnapshot.getKey());
                ((TextView)findViewById(R.id.userName)).setText(dataSnapshot.child("ID").getValue().toString());
                adminRights = dataSnapshot.child("adminRights").getValue().toString();
                if(sessionManager.getAdminRights().equals("Y")){
                    admin.setVisibility(View.VISIBLE);
                    if(adminRights.equals("N")){
                        admin.setText(R.string.makeAdmin);
                        admin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef.child("adminRights").setValue("Y");
                            }
                        });
                    }
                    else if(adminRights.equals("Y")){
                        admin.setText(R.string.removeAdmin);
                        admin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mRef.child("adminRights").setValue("N");
                            }
                        });
                    }
                }
                adapter = new MyMyTasksRecyclerViewAdapter(tasks,null);
                RecyclerView recyclerView = findViewById(R.id.tasks);
                recyclerView.setLayoutManager(new LinearLayoutManager(UserDetailActivity.this));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new ListDividerItem(UserDetailActivity.this, LinearLayoutManager.VERTICAL, R.drawable.listdivider));
                recyclerView.setAdapter(adapter);
                tasks.clear();
                for(DataSnapshot childsnapshot:dataSnapshot.child("tasks").getChildren()){
                    tasks.add(new Task(childsnapshot.getKey(),childsnapshot.getValue().toString()));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase","Unable to fetch Data");
            }
        });
    }
}
