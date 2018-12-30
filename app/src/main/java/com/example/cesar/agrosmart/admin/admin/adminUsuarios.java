package com.example.cesar.agrosmart.admin.admin;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cesar.agrosmart.MainActivity;
import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.adapter.ListaUsuarioAdminAdapter;
import com.example.cesar.agrosmart.admin.add.addUsuarios;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.usuarios.ReadUsuariosRespuesta;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class adminUsuarios extends Fragment {

    private static final String TAG="usuarios";

    private Retrofit retrofit;

    private String jwt;

    private ListaUsuarioAdminAdapter listaUsuarioAdminAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){
            jwt = getArguments().getString("jwt", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_usuarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton add = view.findViewById(R.id.addUsuario);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        listaUsuarioAdminAdapter = new ListaUsuarioAdminAdapter(getContext(),jwt);
        recyclerView.setAdapter(listaUsuarioAdminAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("jwt",jwt);
                Fragment fragment = new addUsuarios();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                transaction.commit();
            }
        });


    }

    private void obtenerdatos(){
        ApiService service = retrofit.create(ApiService.class);
        Call<ReadUsuariosRespuesta> readUsuariosRespuestaCall = service.obtenerUsuarios(new jwtOnlyBody(jwt));

        readUsuariosRespuestaCall.enqueue(new Callback<ReadUsuariosRespuesta>() {
            @Override
            public void onResponse(Call<ReadUsuariosRespuesta> call, Response<ReadUsuariosRespuesta> response) {
                if (!response.isSuccessful()){
                    String error = "Ha ocurrido un error. Contacte el administrador";
                    if (response.errorBody().contentType().subtype().equals("json")){
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        error=apiError.getMessage();
                        Log.e(TAG,apiError.getMessage());
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
                ReadUsuariosRespuesta readUsuariosRespuesta = response.body();
                ArrayList<Usuarios> listaUsuarios = readUsuariosRespuesta.getRecords();
                listaUsuarioAdminAdapter.adicionarListaUsuarios(listaUsuarios);
            }

            @Override
            public void onFailure(Call<ReadUsuariosRespuesta> call, Throwable t) {

            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}