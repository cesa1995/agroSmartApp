package com.example.cesar.agrosmart.admin;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.adapter.ListaFincasIndexAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.models.fincas.Fincas;
import com.example.cesar.agrosmart.models.fincas.ReadFincasRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IndexAdmin extends Fragment {

    private static final String TAG = "fincas";
    private Retrofit retrofit;
    private String jwt;
    private ListaFincasIndexAdapter listaFincasIndexAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            jwt = getArguments().getString("jwt", "Vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_index_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaFincasIndexAdapter = new ListaFincasIndexAdapter(getContext());
        recyclerView.setAdapter(listaFincasIndexAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();
    }

    private void obtenerdatos(){
        ApiService service = retrofit.create(ApiService.class);
        Call<ReadFincasRespuesta> readFincasRespuestaCall = service.obtenerFincas(new jwtOnlyBody(jwt));
        readFincasRespuestaCall.enqueue(new Callback<ReadFincasRespuesta>() {
            @Override
            public void onResponse(Call<ReadFincasRespuesta> call, Response<ReadFincasRespuesta> response) {
                if (response.isSuccessful()){
                    ReadFincasRespuesta readFincasRespuesta = response.body();
                    ArrayList<Fincas> listaFincas = readFincasRespuesta.getRecords();
                    listaFincasIndexAdapter.adicionarListaFincas(listaFincas);
                }else{
                    Log.e(TAG, "onResponse: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<ReadFincasRespuesta> call, Throwable t) {
                Log.e(TAG, "onFature: "+ t.getMessage());
            }
        });
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
