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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.adapter.ListaFincasAdminAdapter;
import com.example.cesar.agrosmart.admin.add.addFincas;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.fincas.Fincas;
import com.example.cesar.agrosmart.models.fincas.ReadFincasRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class adminFincas extends Fragment {

    private static final String TAG = "fincas";

    private Retrofit retrofit;

    private String jwt;

    private ListaFincasAdminAdapter listaFincasAdminAdapter;

    FloatingActionButton addfincas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() !=null){
            jwt = getArguments().getString("jwt", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_admin_fincas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addfincas = view.findViewById(R.id.addFinca);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        listaFincasAdminAdapter = new ListaFincasAdminAdapter(getContext(), jwt);
        recyclerView.setAdapter(listaFincasAdminAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        addfincas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("jwt",jwt);
                Fragment fragment = new addFincas();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                transaction.commit();
            }
        });

    }

    private void obtenerdatos(){
        ApiService service = retrofit.create(ApiService.class);
        Call<ReadFincasRespuesta> readFincasRespuestaCall = service.obtenerFincas(new jwtOnlyBody(jwt));

        readFincasRespuestaCall.enqueue(new Callback<ReadFincasRespuesta>() {
            @Override
            public void onResponse(Call<ReadFincasRespuesta> call, Response<ReadFincasRespuesta> response) {
                if (!response.isSuccessful()){
                    String error = "Ha ocurrido un error. Contacte el admistrador";
                    if (response.errorBody().contentType().subtype().equals("json")){
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        error=apiError.getMessage();
                        Log.e(TAG, error);
                    }else{
                        try {
                            Log.e(TAG, response.errorBody().toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                    return;
                }
                ReadFincasRespuesta readFincasRespuesta = response.body();
                ArrayList<Fincas> listaFincas = readFincasRespuesta.getRecords();
                listaFincasAdminAdapter.adicionarListaFincas(listaFincas);
            }

            @Override
            public void onFailure(Call<ReadFincasRespuesta> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listaFincasAdminAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaFincasAdminAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
