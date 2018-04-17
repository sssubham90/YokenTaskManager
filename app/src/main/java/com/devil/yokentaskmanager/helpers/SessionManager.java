package com.devil.yokentaskmanager.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.MailTo;

import com.devil.yokentaskmanager.activities.LoginActivity;
import com.devil.yokentaskmanager.activities.MainActivity;

public class SessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    private static final String PREF_NAME = "YokenPref";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADMIN_RIGHTS = "admin_rights";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
     
    public void createLoginSession(String name, String email, String adminRights){
        editor = pref.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ADMIN_RIGHTS, adminRights);
        editor.apply();
        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(i);
    }

    public void logoutUser(){
        editor = pref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _context.startActivity(i);
    }

    public String getEmail(){
        return pref.getString(KEY_EMAIL,null);
    }

    public String getName(){
        return pref.getString(KEY_NAME,null);
    }

    public String getAdminRights(){
        return pref.getString(KEY_ADMIN_RIGHTS,null);
    }
}