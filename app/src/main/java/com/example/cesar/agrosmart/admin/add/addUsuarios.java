package com.example.cesar.agrosmart.admin.add;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.add.addUsuariosBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class addUsuarios extends Fragment{

    public static final String TAG = "adduser";

    private String jwt;
    private Long jerarquia;
    private EditText mNombreView, mApellidoView, mEmailView, mPasswordView;
    private View mFormView, mProgressView;
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            jwt=getArguments().getString("jwt", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_add_usuarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNombreView=view.findViewById(R.id.nombre);
        mApellidoView=view.findViewById(R.id.apellido);
        mEmailView=view.findViewById(R.id.email);
        mPasswordView=view.findViewById(R.id.password);
        AppCompatSpinner mNivelUsuariosView = view.findViewById(R.id.nivelUsuarios);
        Button mGuardarView = view.findViewById(R.id.guardar);

        String[] niveles = {"Administrador", "Agronomo", "Cliente"};
        mNivelUsuariosView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,niveles));

        mNivelUsuariosView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jerarquia = parent.getItemIdAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGuardarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUsuario();
            }
        });

        mFormView=view.findViewById(R.id.form);
        mProgressView=view.findViewById(R.id.progress);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void guardarUsuario(){
        mNombreView.setError(null);
        mApellidoView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String nombre = mNombreView.getText().toString();
        String apellido = mApellidoView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean calcel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nombre)){
            mNombreView.setError(getString(R.string.error_field_required));
            focusView = mNombreView;
            calcel = true;
        }

        if (TextUtils.isEmpty(apellido)){
            mApellidoView.setError(getString(R.string.error_field_required));
            focusView = mApellidoView;
            calcel = true;
        }

        if (TextUtils.isEmpty(email)){
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            calcel = true;
        }else if (!isValidEmail(email)){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            calcel = true;
        }

        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            calcel = true;
        }

        if (calcel){
            focusView.requestFocus();
        }else {
            showProgress(true);
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.guardasUsuario(new addUsuariosBody(nombre,apellido,email,password, jerarquia.toString(),jwt));

            respuestaCall.enqueue(new Callback<Respuesta>() {
                @Override
                public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                    showProgress(false);
                    if (!response.isSuccessful()){
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if (response.errorBody().contentType().subtype().equals("json")){
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                            error = apiError.getMessage();
                            Log.e(TAG, error);
                        }else{
                            try {
                                Log.d(TAG, response.errorBody().toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        showMessage(error);
                        return;
                    }
                    Respuesta respuesta = response.body();
                    showMessage(respuesta.getMessage());
                }

                @Override
                public void onFailure(Call<Respuesta> call, Throwable t) {
                    showProgress(false);
                }
            });
        }

    }

    private void showMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void showProgress(final boolean show){
        int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

        mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    }

    private boolean isValidEmail(String email){
        return email.contains("@") && email.contains(".com");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
