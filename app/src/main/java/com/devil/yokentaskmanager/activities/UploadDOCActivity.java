package com.devil.yokentaskmanager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;

public class UploadDOCActivity extends AppCompatActivity {

    private static final int SELECT_DOC = 0;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String docPath;
    private String key;
    private String name;
    private StorageReference docRef;
    private Uri selectedUri_Doc;
    private ProgressDialog progressUploadDialog;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_doc);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        myRef = FirebaseDatabase.getInstance().getReference("tasks/"+getIntent().getStringExtra("id")+"/docs");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        progressUploadDialog = new ProgressDialog(this);
        progressUploadDialog.setMessage("Uploading DOCUMENT:");
        progressUploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressUploadDialog.setIndeterminate(false);

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDocument();
            }
        });

        findViewById(R.id.docSelector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] mimeTypes =
                        {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                } else {
                    StringBuilder mimeTypesStr = new StringBuilder();
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr.append(mimeType).append("|");
                    }
                    intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                }
                startActivityForResult(Intent.createChooser(intent, "Select the Document to upload."),SELECT_DOC);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_DOC) {
                selectedUri_Doc = data.getData();
                if (selectedUri_Doc != null){
                    docPath = selectedUri_Doc.toString();
                    ((TextView)findViewById(R.id.docPath)).setText(docPath);
                }
            }
        }
    }

    private void addDocument(){
        if (validate()){
            key = myRef.push().getKey();
            name = ((EditText)findViewById(R.id.title)).getText().toString().trim();
            docRef = mStorageRef.child("Documents/"+key+"/"+name);
            UploadTask uploadTask = docRef.putFile(selectedUri_Doc);
            progressUploadDialog.show();
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    myRef.child(key).removeValue();
                    mStorageRef.child("Documents").child(key).child(selectedUri_Doc.getLastPathSegment()).delete();
                    Toast.makeText(UploadDOCActivity.this, "Document Upload Failed", Toast.LENGTH_LONG).show();
                    progressUploadDialog.setProgress(0);
                    progressUploadDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    docRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            myRef.child(key).child("User ID").setValue(sessionManager.getEmail());
                            myRef.child(key).child("User Name").setValue(sessionManager.getName());
                            myRef.child(key).child("Name").setValue(name);
                            myRef.child(key).child("URL").setValue(uri.toString());
                            myRef.child(key).child("Type").setValue(getMimeType(selectedUri_Doc));
                            myRef.child(key).child("TimeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));
                            progressUploadDialog.dismiss();
                            Toast.makeText(UploadDOCActivity.this, "Document Uploaded Successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressUploadDialog.setProgress((int) progress);
                }
            });
        }
    }

    private boolean validate(){
        if(((EditText)findViewById(R.id.title)).getText().toString().isEmpty()){
            Toast.makeText(this, "You should enter a name for the document", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(docPath.isEmpty()){
            Toast.makeText(this, "Please choose a document to upload", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }

    public String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}

