package com.devil.yokentaskmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devil.yokentaskmanager.R;
import com.devil.yokentaskmanager.fragments.AllTasksFragment;
import com.devil.yokentaskmanager.fragments.MyTasksFragment;
import com.devil.yokentaskmanager.fragments.UserListFragment;
import com.devil.yokentaskmanager.helpers.SessionManager;
import com.devil.yokentaskmanager.interfaces.OnTaskListFragmentInteractionListener;
import com.devil.yokentaskmanager.interfaces.OnUploadDOCFragmentInteractionListener;
import com.devil.yokentaskmanager.interfaces.OnUserListFragmentInteractionListener;
import com.devil.yokentaskmanager.models.Task;
import com.devil.yokentaskmanager.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnTaskListFragmentInteractionListener,OnUserListFragmentInteractionListener,OnUploadDOCFragmentInteractionListener {

    private static final String TAG = "Devil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SessionManager sessionManager = new SessionManager(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ((TextView)findViewById(R.id.user_name)).setText(sessionManager.getName());
        findViewById(R.id.user_name).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change Password:");
                final EditText oldPass = new EditText(MainActivity.this);
                final EditText newPass = new EditText(MainActivity.this);
                final EditText confirmPass = new EditText(MainActivity.this);
                oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                oldPass.setHint("Old Password");
                newPass.setHint("New Password");
                confirmPass.setHint("Confirm Password");
                LinearLayout ll=new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(oldPass);
                ll.addView(newPass);
                ll.addView(confirmPass);
                alertDialog.setView(ll);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if(newPass.getText().toString().equals(confirmPass.getText().toString())){
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(),oldPass.getText().toString());
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "Password updated");
                                                            Toast.makeText(MainActivity.this,"Password updated.",Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        } else {
                                                            Log.d(TAG, "Error password not updated");
                                                            Toast.makeText(MainActivity.this,"Error password not updated.",Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "Error auth failed");
                                                Toast.makeText(MainActivity.this,"Error auth failed.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Password Mismatch...",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = alertDialog.create();
                alert11.show();
                return false;
            }
        });
        TabLayout tabLayout= findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logoutUser();
            }
        });
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        if(sessionManager.getAdminRights().equals("Y")) {
            findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,NewTaskActivity.class));
                }
            });
        }
        else
            findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
    }

    @Override
    public void onTaskListFragmentInteraction(Task task) {
        startActivity(new Intent(this,TaskDetailActivity.class).putExtra("id",task.getID()));
    }

    @Override
    public void onUserListFragmentInteraction(User user) {
        startActivity(new Intent(this,UserDetailActivity.class).putExtra("email",user.getEmail()));
    }

    @Override
    public void onUploadDOCFragmentInteraction(Task task) {
        startActivity(new Intent(this,UploadDOCActivity.class).putExtra("id",task.getID()));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new AllTasksFragment();
                case 1:
                    return new MyTasksFragment();
                case 2:
                    return new UserListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ALL TASKS";
                case 1:
                    return "MY TASKS";
                case 2:
                    return "USERS";
            }
            return null;
        }
    }
}
