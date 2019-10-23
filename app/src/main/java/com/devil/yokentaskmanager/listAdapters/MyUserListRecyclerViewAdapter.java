package com.devil.yokentaskmanager.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.interfaces.OnUserListFragmentInteractionListener;
import com.devil.yokentaskmanager.models.User;

import java.util.List;

public class MyUserListRecyclerViewAdapter extends RecyclerView.Adapter<MyUserListRecyclerViewAdapter.ViewHolder> {

    private final List<User> users;
    private final OnUserListFragmentInteractionListener mListener;

    public MyUserListRecyclerViewAdapter(List<User> users, OnUserListFragmentInteractionListener listener) {
        this.users = users;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_userlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.user = users.get(position);
        holder.mUserID.setText(users.get(position).getEmail());
        holder.mUserName.setText(users.get(position).getID());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserListFragmentInteraction(holder.user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mUserID;
        final TextView mUserName;
        User user;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mUserID = view.findViewById(R.id.userID);
            mUserName = view.findViewById(R.id.userName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
