package com.example.cesar.agrosmart.admin.admin;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.adapter.ListaEquiposAdminAdapter;
import com.example.cesar.agrosmart.admin.add.addEquipos;
import com.example.cesar.agrosmart.admin.add.addParcelas;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.equipos.Equipos;
import com.example.cesar.agrosmart.models.equipos.ReadEquiposRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class adminEquipos extends Fragment {

    private static final String TAG="equipos";

    private Retrofit retrofit;

    private String jwt;

    private ListaEquiposAdminAdapter listaEquiposAdminAdapter;

    FloatingActionButton addequipos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            jwt = getArguments().getString("jwt", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_equipos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addequipos = view.findViewById(R.id.addEquipos);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        listaEquiposAdminAdapter = new ListaEquiposAdminAdapter(getContext(),jwt);
        recyclerView.setAdapter(listaEquiposAdminAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        retrofit =new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        addequipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("jwt",jwt);
                Fragment fragment = new addEquipos();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void obtenerdatos(){
        ApiService service = retrofit.create(ApiService.class);
        Call<ReadEquiposRespuesta> readEquiposRespuestaCall = service.obtenerEquipos(new jwtOnlyBody(jwt));

        readEquiposRespuestaCall.enqueue(new Callback<ReadEquiposRespuesta>() {
            @Override
            public void onResponse(Call<ReadEquiposRespuesta> call, Response<ReadEquiposRespuesta> response) {
                String error = "Ha ocurrido un error. Contacte el administrador";
                if (!response.isSuccessful()){
                    if (response.errorBody().contentType().subtype().equals("json")){
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        error = apiError.getMessage();
                        Log.e(TAG, apiError.getMessage());
                    }else{
                        try{
                            Log.e(TAG,response.errorBody().toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                    return;
                }
                ReadEquiposRespuesta readEquiposRespuesta = response.body();
                ArrayList<Equipos> listaEquipos = readEquiposRespuesta.getRecords();
                listaEquiposAdminAdapter.adicionarListaEquipos(listaEquipos);
            }

            @Override
            public void onFailure(Call<ReadEquiposRespuesta> call, Throwable t) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}