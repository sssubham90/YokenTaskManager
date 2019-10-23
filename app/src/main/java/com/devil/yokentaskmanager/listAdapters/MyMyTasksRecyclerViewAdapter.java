package com.devil.yokentaskmanager.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.interfaces.OnUploadDOCFragmentInteractionListener;
import com.devil.yokentaskmanager.models.Task;

import java.util.List;

public class MyMyTasksRecyclerViewAdapter extends RecyclerView.Adapter<MyMyTasksRecyclerViewAdapter.ViewHolder> {

    private final List<Task> tasks;
    private final OnUploadDOCFragmentInteractionListener mListener;

    public MyMyTasksRecyclerViewAdapter(List<Task> tasks, OnUploadDOCFragmentInteractionListener listener) {
        this.tasks = tasks;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mytasks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.task = tasks.get(position);
        holder.mIdView.setText(tasks.get(position).getID());
        holder.mTitleView.setText(tasks.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onUploadDOCFragmentInteraction(holder.task);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mTitleView;
        public Task task;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.taskID);
            mTitleView = view.findViewById(R.id.taskTitle);
        }

        @Override
        public String toString() {
            return super.toString() + "'ID: " + mIdView.getText() + "Task: " + mTitleView.getText() + "'";
        }
    }
}
