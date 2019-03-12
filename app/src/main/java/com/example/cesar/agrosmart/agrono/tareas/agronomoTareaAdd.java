package com.example.cesar.agrosmart.agrono.tareas;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.tareas.addTareaBody;
import com.example.cesar.agrosmart.apiBody.tareas.updateTareaBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class agronomoTareaAdd extends Fragment implements View.OnClickListener{

    public static final String TAG = "addTarea";

    private String jwt, idparcela, ubicacion, estado, Finicio, Ffin, Hinicio, Hfin, tarea, id;
    private EditText mTareaView;
    private TextView fechaInicio, horaInicio, fechaFin, horaFin;
    private Button guardar;
    private View mFormView, mProgressView;
    private Retrofit retrofit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idparcela=getArguments().getString("idParcela", "vacio");
            ubicacion=getArguments().getString("Ubicacion", "vacio");
            //modificar
            id=getArguments().getString("id",null);
            tarea=getArguments().getString("tarea",null);
            Finicio=getArguments().getString("Finicio",null);
            Hinicio=getArguments().getString("Hinicio",null);
            Ffin=getArguments().getString("Ffin", null);
            Hfin=getArguments().getString("Hfin", null);
            estado=getArguments().getString("tipo", null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_tarea_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView=view.findViewById(R.id.progress);
        mFormView=view.findViewById(R.id.form);
        mTareaView=view.findViewById(R.id.tarea);
        fechaFin=view.findViewById(R.id.fecha_fin);
        fechaInicio=view.findViewById(R.id.fecha_inicio);
        horaFin=view.findViewById(R.id.hora_fin);
        horaInicio=view.findViewById(R.id.hora_inicio);
        guardar=view.findViewById(R.id.guardar);

        fechaFin.setOnClickListener(this);
        fechaInicio.setOnClickListener(this);
        horaFin.setOnClickListener(this);
        horaInicio.setOnClickListener(this);
        guardar.setOnClickListener(this);

        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        update();
    }

    private void update(){
        if (estado.equals("1")) {
                mTareaView.setText(tarea);
                fechaInicio.setText(Finicio);
                horaInicio.setText(Hinicio);
                fechaFin.setText(Ffin);
                horaFin.setText(Hfin);
        }
    }

    private void dialog_fecha(final int chose){
        final Dialog builder=new Dialog(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View f=inflater.inflate(R.layout.agronomo_dialog_tarea_add_fecha,null);

        final DatePicker mFechaView=f.findViewById(R.id.fecha);
        Button guardar=f.findViewById(R.id.guardar);
        Button cancelar=f.findViewById(R.id.cancelar);
        builder.setContentView(f);
        builder.setTitle("Ingrese la Fecha:");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fecha;
                fecha=mFechaView.getYear()+"-"+mFechaView.getMonth()+"-"+mFechaView.getDayOfMonth();
                switch (chose){
                    case 0:{
                        fechaFin.setText(fecha);
                        Ffin=fecha;
                    }break;
                    case 1:{
                        fechaInicio.setText(fecha);
                        Finicio=fecha;
                    }break;
                }
                builder.dismiss();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();

    }

    private void dialog_hora(final int chose){
        final Dialog builder=new Dialog(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View f=inflater.inflate(R.layout.agronomo_dialog_tarea_add_hora,null);

        final TimePicker mHoraView=f.findViewById(R.id.time);
        Button guardar=f.findViewById(R.id.guardar);
        Button cancelar=f.findViewById(R.id.cancelar);
        builder.setContentView(f);
        builder.setTitle("Ingrese la Hora:");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String hora;
               String Ehora;
               int h=mHoraView.getCurrentHour();
               int m=mHoraView.getCurrentMinute();
               Ehora=h+":"+m;
               if (h>12) {
                   hora=(h-12)+":"+m+" p.m";
               }else{
                   hora=h+":"+m+" a.m";
               }
               switch (chose){
                   case 1:{
                       horaInicio.setText(hora);
                       Hinicio=Ehora;
                   }break;
                   case 0:{
                       horaFin.setText(hora);
                       Hfin=Ehora;
                   }break;
               }
               builder.dismiss();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fecha_inicio:{
                dialog_fecha(1);
            }break;
            case R.id.fecha_fin:{
                dialog_fecha(0);
            }break;
            case R.id.hora_inicio:{
                dialog_hora(1);
            }break;
            case R.id.hora_fin:{
                dialog_hora(0);
            }break;
            case R.id.guardar:{
                saveTarea();
            }break;
        }
    }

    private void saveTarea(){
        mTareaView.setError(null);
        fechaInicio.setText(null);
        fechaFin.setError(null);
        horaInicio.setError(null);
        horaFin.setError(null);

        String tarea=mTareaView.getText().toString();
        String desde=Finicio+" "+Hinicio;
        String hasta=Ffin+" "+Hfin;

        boolean cancel=false;
        View focusView=null;

        if (TextUtils.isEmpty(tarea)){
            mTareaView.setError(getText(R.string.error_field_required));
            focusView=mTareaView;
            cancel=true;
        }

        if (TextUtils.isEmpty(Ffin)){
            fechaFin.setError(getText(R.string.error_field_required));
            focusView=fechaFin;
            cancel=true;
        }
        if (TextUtils.isEmpty(Hfin)){
            horaFin.setError(getText(R.string.error_field_required));
            focusView=horaFin;
            cancel=true;
        }
        if (TextUtils.isEmpty(Finicio)){
            fechaInicio.setError(getText(R.string.error_field_required));
            focusView=fechaInicio;
            cancel=true;
        }
        if (TextUtils.isEmpty(Hinicio)){
            horaInicio.setError(getText(R.string.error_field_required));
            focusView=horaInicio;
            cancel=true;
        }

        if (cancel){
            focusView.requestFocus();
        }else {
            showProgress(true);
            switch (estado){
                case "0":{
                    create(desde,hasta, tarea);
                    Log.d("estado", estado);
                }break;
                case "1":{
                    updatedata(desde,hasta, tarea);
                    Log.d("estado", estado);
                }break;
            }
        }

    }

    private void updatedata(String desde, String hasta, String tarea){
                ApiService service = retrofit.create(ApiService.class);
                Call<Respuesta> respuestaCall = service.updateTarea(new updateTareaBody(id, tarea, desde, hasta, ubicacion, jwt));
                Log.d("update", id+" "+tarea+" "+desde+" "+hasta+" "+ubicacion+" "+jwt);
                respuestaCall.enqueue(new Callback<Respuesta>() {
                    @Override
                    public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                        showProgress(false);
                        if (!response.isSuccessful()) {
                            String error = "Ha ocurrido un error. Contacte al administrador";
                            if (response.errorBody().contentType().subtype().equals("json")) {
                                ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                                error = apiError.getMessage();
                                Log.e(TAG, error);
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().toString());
                                } catch (Exception e) {
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

                    }
                });
    }

    private void create(String desde, String hasta, String tarea){
                ApiService service = retrofit.create(ApiService.class);
                Call<Respuesta> respuestaCall = service.addTareas(new addTareaBody(tarea, idparcela, desde, hasta, "1", ubicacion, jwt));
                Log.d("create", tarea+" "+idparcela+" "+desde+" "+hasta+" "+ubicacion+" "+jwt);
                respuestaCall.enqueue(new Callback<Respuesta>() {
                    @Override
                    public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                        showProgress(false);
                        if (!response.isSuccessful()) {
                            String error = "Ha ocurrido un error. Contacte al administrador";
                            if (response.errorBody().contentType().subtype().equals("json")) {
                                ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                                error = apiError.getMessage();
                                Log.e(TAG, error);
                            } else {
                                try {
                                    Log.d(TAG, response.errorBody().toString());
                                } catch (Exception e) {
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

                    }
                });
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
