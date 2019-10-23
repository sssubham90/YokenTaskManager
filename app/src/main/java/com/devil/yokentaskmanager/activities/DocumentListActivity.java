package com.devil.yokentaskmanager.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.ListDividerItem;
import com.devil.yokentaskmanager.interfaces.DeleteRequest;
import com.devil.yokentaskmanager.interfaces.DownloadRequest;
import com.devil.yokentaskmanager.listAdapters.DocumentListRecyclerViewAdapter;
import com.devil.yokentaskmanager.models.Document;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class DocumentListActivity extends AppCompatActivity implements DownloadRequest, DeleteRequest {

    private List<Document> documents;
    private DocumentListRecyclerViewAdapter adapter;
    private ProgressDialog pDialog;
    private String fileName;
    private Toast toast;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        documents = new ArrayList<>();
        toast = Toast.makeText(this,"Download Successful",Toast.LENGTH_SHORT);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        RecyclerView recyclerView = findViewById(R.id.documents);
        adapter = new DocumentListRecyclerViewAdapter(documents,this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(DocumentListActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ListDividerItem(this, LinearLayoutManager.VERTICAL, R.drawable.listdivider));
        recyclerView.setAdapter(adapter);
        mRef = FirebaseDatabase.getInstance().getReference("tasks/" + getIntent().getStringExtra("id") + "/docs");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                documents.clear();
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    try{
                        documents.add(new Document(childSnapshot.child("Name").getValue().toString(),childSnapshot.child("User ID").getValue().toString(),childSnapshot.child("User Name").getValue().toString(),childSnapshot.child("URL").getValue().toString(),childSnapshot.child("Type").getValue().toString(),childSnapshot.child("TimeStamp").getValue().toString(),childSnapshot.getKey()));
                    }
                    catch (Exception e){
                        documents.clear();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DocumentListActivity.this,"Slow network connection",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void downloadRequest(Document document) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE")
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                        0);
            }
        }
        fileName = document.getName()+".";
        switch (document.getType())
        {
            case "application/pdf":
                fileName+="pdf";
                break;
            case "application/msword":
                fileName+="doc";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                fileName+="docx";
                break;
            case "application/vnd.ms-powerpoint":
                fileName+="ppt";
                break;
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                fileName+="pptx";
                break;
            case "application/vnd.ms-excel":
                fileName+="xls";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                fileName+="xlsx";
                break;
            case "text/plain":
                fileName+="txt";
                break;
        }
        new DownloadFileFromURL().execute(document.getDownloadURL());
    }

    @Override
    public void deleteRequest(Document document) {
        final String key = document.getKey();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Documents/"+key);
        storageRef.child(document.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRef.child(key).setValue(null);
            }
        });
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File folder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "YokenDOCS");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                OutputStream output = new FileOutputStream(folder+ File.separator + fileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(folder)));
            pDialog.dismiss();
            toast.show();
        }

    }
}
