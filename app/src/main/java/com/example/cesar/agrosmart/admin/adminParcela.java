package com.example.cesar.agrosmart.admin;

import android.content.Context;
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
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.adapter.ListaParcelaAdminAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.fincas.Parcelas;
import com.example.cesar.agrosmart.models.fincas.ReadParcelasRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class adminParcela extends Fragment {

    private static final String TAG = "parcelas";

    private Retrofit retrofit;

    private String jwt;

    private ListaParcelaAdminAdapter listaParcelaAdminAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            jwt = getArguments().getString("jwt", "vacio");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_parcela, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        listaParcelaAdminAdapter = new ListaParcelaAdminAdapter(getContext());
        recyclerView.setAdapter(listaParcelaAdminAdapter);
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
        Call<ReadParcelasRespuesta> readParcelasRespuestaCall = service.obtenerParcelas(new jwtOnlyBody(jwt));

        readParcelasRespuestaCall.enqueue(new Callback<ReadParcelasRespuesta>() {
            @Override
            public void onResponse(Call<ReadParcelasRespuesta> call, Response<ReadParcelasRespuesta> response) {
                if (!response.isSuccessful()){
                    String error = "Ha ocurrido un error. Contacte el administrador";
                    if (response.errorBody().contentType().subtype().equals("json")){
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        error=apiError.getMessage();
                        Log.e(TAG,error);
                    }else{
                        try {
                            Log.e(TAG,response.errorBody().toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                    return;
                }
                ReadParcelasRespuesta readParcelasRespuesta = response.body();
                ArrayList<Parcelas> listaParcelas = readParcelasRespuesta.getRecords();
                listaParcelaAdminAdapter.adicionarListaParcelas(listaParcelas);
            }

            @Override
            public void onFailure(Call<ReadParcelasRespuesta> call, Throwable t) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
