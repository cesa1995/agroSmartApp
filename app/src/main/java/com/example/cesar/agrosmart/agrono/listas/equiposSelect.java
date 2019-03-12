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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.MainActivity;
import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.Adapter.ListaEquiposAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.idANDjwtBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.equipos.AsociarEquipo;
import com.example.cesar.agrosmart.models.equipos.ReadAsociarEquipoRespuesta;
import com.example.cesar.agrosmart.models.parcelas.AsociarParcela;
import com.example.cesar.agrosmart.models.parcelas.ReadAsociarParcelaRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class equiposSelect extends Fragment {

    public static final String TAG="ListaEquipos";

    private String jwt, idParcela, ubicacion;
    private Retrofit retrofit;
    private ListaEquiposAdapter listaEquiposAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idParcela=getArguments().getString("idParcela", "vacio");
            ubicacion=getArguments().getString("Ubicacion", "vacio");
        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.equipo));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.equipo));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_select_equipos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaEquiposAdapter=new ListaEquiposAdapter(jwt, ubicacion, getContext());
        recyclerView.setAdapter(listaEquiposAdapter);

        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        TextView mUbicacionView=view.findViewById(R.id.ubicacion);

        mUbicacionView.setText(ubicacion);

        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadAsociarEquipoRespuesta> readAsociarEquipoRespuestaCall=service.readEquiposUsuarios(new idANDjwtBody(idParcela, jwt));
        readAsociarEquipoRespuestaCall.enqueue(new Callback<ReadAsociarEquipoRespuesta>() {
            @Override
            public void onResponse(Call<ReadAsociarEquipoRespuesta> call, Response<ReadAsociarEquipoRespuesta> response) {
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
                ReadAsociarEquipoRespuesta readAsociarEquipoRespuesta = response.body();
                ArrayList<AsociarEquipo> listaEquipos = readAsociarEquipoRespuesta.getRecords();
                listaEquiposAdapter.adicionarListaEquipos(listaEquipos);
            }

            @Override
            public void onFailure(Call<ReadAsociarEquipoRespuesta> call, Throwable t) {

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
                listaEquiposAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaEquiposAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
