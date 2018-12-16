package com.example.cesar.agrosmart.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.cesar.agrosmart.models.LoginRespuesta;

public class sessionPrefs {
    private static final String prefs_name = "SESSION_USUARIO";
    private static final String nombre = "nombre";
    private static final String apellido = "apellido";
    private static final String nivel = "nivel";
    private static final String jwt = "jwt";

    private final SharedPreferences mPrefs;

    private boolean mIsLoggedIn = false;

    private static sessionPrefs INSTANCE;

    public static sessionPrefs get(Context context){
        if (INSTANCE == null){
            INSTANCE = new sessionPrefs(context);
        }
        return INSTANCE;
    }

    private sessionPrefs(Context context){
        mPrefs = context.getApplicationContext().getSharedPreferences(prefs_name,Context.MODE_PRIVATE);
        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(jwt,null));
    }

    public boolean isLoggedIn(){
        return mIsLoggedIn;
    }

    public void saveInfo(LoginRespuesta loginRespuesta){
        if (loginRespuesta != null ){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(nombre, loginRespuesta.getNombre());
            editor.putString(apellido, loginRespuesta.getApellido());
            editor.putString(nivel, loginRespuesta.getNivel());
            editor.putString(jwt, loginRespuesta.getJwt());

            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void logOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(nombre, null);
        editor.putString(apellido, null);
        editor.putString(nivel, null);
        editor.putString(jwt, null);
        editor.apply();
    }
}
