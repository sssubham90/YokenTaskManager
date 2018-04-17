package com.devil.yokentaskmanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.ListDividerItem;
import com.devil.yokentaskmanager.interfaces.OnTaskListFragmentInteractionListener;
import com.devil.yokentaskmanager.listAdapters.MyAllTasksRecyclerViewAdapter;
import com.devil.yokentaskmanager.models.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private OnTaskListFragmentInteractionListener mListener;
    private List<Task> tasks;
    private MyAllTasksRecyclerViewAdapter mAdapter;

    public AllTasksFragment() {
        tasks = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alltasks_list, container, false);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("tasks");
        mAdapter = new MyAllTasksRecyclerViewAdapter(tasks, mListener);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new ListDividerItem(context, LinearLayoutManager.VERTICAL, R.drawable.listdivider));
            recyclerView.setAdapter(mAdapter);
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    tasks.add(new Task(childSnapshot.getKey(),childSnapshot.child("title").getValue().toString()));
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("sm", "Failed to read value.", error.toException());
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskListFragmentInteractionListener) {
            mListener = (OnTaskListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
