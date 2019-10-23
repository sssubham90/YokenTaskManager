package com.devil.yokentaskmanager.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.interfaces.DeleteRequest;
import com.devil.yokentaskmanager.interfaces.DownloadRequest;
import com.devil.yokentaskmanager.models.Document;

import java.util.List;

public class DocumentListRecyclerViewAdapter extends RecyclerView.Adapter<DocumentListRecyclerViewAdapter.ViewHolder> {

    private final List<Document> documents;
    private final DownloadRequest mListener;
    private final DeleteRequest mListener1;

    public DocumentListRecyclerViewAdapter(List<Document> documents, DownloadRequest listener, DeleteRequest listener1) {
        this.documents = documents;
        mListener = listener;
        mListener1 = listener1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.document = documents.get(position);
        holder.mName.setText(documents.get(position).getName());
        holder.mUserName.setText(documents.get(position).getUserName());
        holder.mUserID.setText(documents.get(position).getUserID());
        holder.mType.setText(type(documents.get(position)));
        holder.timeStamp.setText(documents.get(position).getTimeStamp());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.downloadRequest(holder.document);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener1) {
                    mListener1.deleteRequest(holder.document);
                }
            }
        });
    }

    private String type(Document document) {
        switch (document.getType())
        {
            case "application/pdf":
                return "PDF";
            case "application/msword":
                return "DOC";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return "DOCX";
            case "application/vnd.ms-powerpoint":
                return "PPT";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return "PPTX";
            case "application/vnd.ms-excel":
                return "XLS";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return "XLSX";
            case "text/plain":
                return "TXT";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mName;
        final TextView mUserID;
        final TextView mUserName;
        final TextView mType;
        final TextView timeStamp;
        final Button download,delete;
        Document document;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.name);
            mUserID = view.findViewById(R.id.userID);
            mUserName = view.findViewById(R.id.userName);
            mType = view.findViewById(R.id.type);
            download = view.findViewById(R.id.download);
            delete = view.findViewById(R.id.delete);
            timeStamp = view.findViewById(R.id.timeStamp);
        }

        @Override
        public String toString() {
            return super.toString() + "'Name: " + mName.getText() + "User: " + mUserName.getText() + "'";
        }
    }
}
