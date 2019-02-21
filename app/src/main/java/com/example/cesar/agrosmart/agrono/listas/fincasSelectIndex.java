package com.example.cesar.agrosmart.agrono.listas;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.cesar.agrosmart.MainActivity;
import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.Adapter.ListaFincasIndexAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.idANDjwtBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.fincas.Fincas;
import com.example.cesar.agrosmart.models.fincas.ReadFincasRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fincasSelectIndex extends Fragment {

    private static final String TAG= "indexAgronomo";

    private String jwt, idUsuario;
    private Retrofit retrofit;
    private ListaFincasIndexAdapter listaFincasIndexAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() !=null){
            jwt=getArguments().getString("jwt", "vacio");
            idUsuario=getArguments().getString("id", "vacio");
        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.fincas));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.fincas));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_select_fincas_index, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaFincasIndexAdapter=new ListaFincasIndexAdapter(jwt, getContext(), idUsuario);
        recyclerView.setAdapter(listaFincasIndexAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadFincasRespuesta> readFincasRespuestaCall=service.readFincasUsuario(new idANDjwtBody(idUsuario, jwt));
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
                listaFincasIndexAdapter.adicionarListaFincas(listaFincas);
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
                listaFincasIndexAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaFincasIndexAdapter.getFilter().filter(newText);
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
