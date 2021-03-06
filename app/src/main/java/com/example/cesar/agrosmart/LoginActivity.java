package com.example.cesar.agrosmart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.loginBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.LoginRespuesta;
import com.example.cesar.agrosmart.session.sessionPrefs;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    public static final String TAG ="login";

    // UI references.
    private EditText mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_login);
        // Set up the login form.
        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        if (sessionPrefs.get(this).isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if (!isOnLine()){
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnLine()){
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.form);
        mProgressView = findViewById(R.id.progress);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_incorrect_password));

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ApiService service = retrofit.create(ApiService.class);
            Call<LoginRespuesta> loginRespuestaCall = service.obtenerJWT(new loginBody(email,password));

            loginRespuestaCall.enqueue(new Callback<LoginRespuesta>() {
                @Override
                public void onResponse(Call<LoginRespuesta> call, Response<LoginRespuesta> response) {
                    showProgress(false);
                    if(!response.isSuccessful()){
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("json")){
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                            error = apiError.getMessage();
                            Log.e(TAG,apiError.getMessage());
                        }else{
                            try{
                                Log.d(TAG,response.errorBody().string());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        showLoginError(error);
                        return;
                    }

                    sessionPrefs.get(LoginActivity.this).saveInfo(response.body());
                    showHomeScreen();
                }

                @Override
                public void onFailure(Call<LoginRespuesta> call, Throwable t) {
                    showProgress(false);
                }
            });
        }


    }

    private void showHomeScreen(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void showLoginError(String error){
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".com");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    private boolean isOnLine(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork !=null && activeNetwork.isConnected();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

