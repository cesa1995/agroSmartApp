package com.example.cesar.agrosmart.admin.update;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.update.updateFincaBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateFinca extends Fragment {

    public static final String TAG = "updatefinca";

    private String jwt, id, nombre, telefono, direccion;
    private EditText mNombreView, mTelefonoView, mDirecionView;
    private Retrofit retrofit;
    private View mFormView, mProgressView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt","vacio");
            id=getArguments().getString("id", "vacio");
            nombre=getArguments().getString("nombre", "vacio");
            telefono=getArguments().getString("telefono", "vacio");
            direccion=getArguments().getString("direccion", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_update_finca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNombreView=view.findViewById(R.id.nombre);
        mTelefonoView=view.findViewById(R.id.telefono);
        mDirecionView=view.findViewById(R.id.direccion);
        Button mGuardarView = view.findViewById(R.id.guardar);

        mNombreView.setText(nombre);
        mTelefonoView.setText(telefono);
        mDirecionView.setText(direccion);

        mGuardarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarfinca();
            }
        });

        mFormView=view.findViewById(R.id.form);
        mProgressView=view.findViewById(R.id.progress);

        retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void guardarfinca(){
        mNombreView.setError(null);
        mTelefonoView.setError(null);
        mDirecionView.setError(null);

        String nombre = mNombreView.getText().toString();
        String telefono = mTelefonoView.getText().toString();
        String direccion = mDirecionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nombre)){
            mNombreView.setError(getString(R.string.error_field_required));
            focusView=mNombreView;
            cancel=true;
        }

        if (TextUtils.isEmpty(telefono)){
            mTelefonoView.setError(getString(R.string.error_field_required));
            focusView=mTelefonoView;
            cancel=true;
        }

        if (TextUtils.isEmpty(direccion)){
            mDirecionView.setError(getString(R.string.error_field_required));
            focusView=mDirecionView;
            cancel=true;
        }

        if (cancel) {
            focusView.requestFocus();
        }else {
            showProgress(true);
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.updateFinca(new updateFincaBody(id,nombre,telefono,direccion,jwt));

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
