package com.example.cesar.agrosmart.admin.asociar;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.adapter.ListaEquipoAsociarAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.asociar.AddAsociarUsuarioANDEquipoBody;
import com.example.cesar.agrosmart.apiBody.asociar.CambiarEstadoElemento;
import com.example.cesar.agrosmart.apiBody.asociar.ReadAsociarUsuarioANDEquipoBody;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.interfaces.OnItemClickListener;
import com.example.cesar.agrosmart.interfaces.OnItemClickListenerState;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.equipos.AsociarEquipo;
import com.example.cesar.agrosmart.models.equipos.Equipos;
import com.example.cesar.agrosmart.models.equipos.ReadAsociarEquipoRespuesta;
import com.example.cesar.agrosmart.models.equipos.ReadEquiposRespuesta;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class adminAsociarEquipo extends Fragment {

    private static final String TAG = "asociar";

    private String idfincaparcela, jwt, parcela;
    private Retrofit retrofit;
    private ListaEquipoAsociarAdapter listaEquipoAsociarAdapter;
    private AsociarEquipoListAdapter asociarEquipoListAdapter;
    FloatingActionButton addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idfincaparcela=getArguments().getString("id", "vacio");
            parcela=getArguments().getString("parcela", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_admin_asociar_equipo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButton=view.findViewById(R.id.addEquipos);
        TextView mParcelaView = view.findViewById(R.id.parcela);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaEquipoAsociarAdapter=new ListaEquipoAsociarAdapter(getContext(),jwt);
        recyclerView.setAdapter(listaEquipoAsociarAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(),1 );
        recyclerView.setLayoutManager(layoutManager);
        mParcelaView.setText(parcela);
        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        listaEquipoAsociarAdapter.setOnItemClickListenerState(new OnItemClickListenerState() {
            @Override
            public void OnClick(int position, String id, String estado) {
                cambiarEstado(id,estado);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
    }

    private void cambiarEstado(String id, String estado){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.cambiarEstado(new CambiarEstadoElemento(id,estado,jwt));

        respuestaCall.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
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
                obtenerdatos();
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {

            }
        });
    }

    private void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View f=inflater.inflate(R.layout.admin_dialog_list_equipos,null);
        RecyclerView li=f.findViewById(R.id.recyclerView);
        li.setLayoutManager(new LinearLayoutManager(getActivity()));
        SearchView searchView=(SearchView) f.findViewById(R.id.search);
        searchView.setQueryHint("Buscar");

        asociarEquipoListAdapter=new AsociarEquipoListAdapter();
        li.setAdapter(asociarEquipoListAdapter);
        obtenerdatosequipos();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                asociarEquipoListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                asociarEquipoListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        builder.setView(f);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        asociarEquipoListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClicked(int position, String id) {
                guardarEquipos(id);
                alertDialog.dismiss();
            }
        });
    }

    private void guardarEquipos(String id){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.addAsociarEquipos(new AddAsociarUsuarioANDEquipoBody(idfincaparcela,id,"0",jwt));
        respuestaCall.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
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
                obtenerdatos();
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
            }
        });
    }

    private void obtenerdatosequipos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadEquiposRespuesta> readEquiposRespuestaCall=service.obtenerEquipos(new jwtOnlyBody(jwt));
        readEquiposRespuestaCall.enqueue(new Callback<ReadEquiposRespuesta>() {
            @Override
            public void onResponse(Call<ReadEquiposRespuesta> call, Response<ReadEquiposRespuesta> response) {
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
                ReadEquiposRespuesta readEquiposRespuesta=response.body();
                ArrayList<Equipos> listaEquipo=readEquiposRespuesta.getRecords();
                asociarEquipoListAdapter.adicionarListaEquipos(listaEquipo);
            }

            @Override
            public void onFailure(Call<ReadEquiposRespuesta> call, Throwable t) {

            }
        });
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadAsociarEquipoRespuesta> readAsociarEquipoRespuestaCall=service.readAsociarEquipo(new ReadAsociarUsuarioANDEquipoBody(idfincaparcela,jwt));

        readAsociarEquipoRespuestaCall.enqueue(new Callback<ReadAsociarEquipoRespuesta>() {
            @Override
            public void onResponse(Call<ReadAsociarEquipoRespuesta> call, Response<ReadAsociarEquipoRespuesta> response) {
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
                ReadAsociarEquipoRespuesta readAsociarEquipoRespuesta=response.body();
                ArrayList<AsociarEquipo> listaAsociarEquipo=readAsociarEquipoRespuesta.getRecords();
                listaEquipoAsociarAdapter.adicionarListaAsociarEquipo(listaAsociarEquipo);
            }

            @Override
            public void onFailure(Call<ReadAsociarEquipoRespuesta> call, Throwable t) {

            }
        });
    }

    private void showMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listaEquipoAsociarAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaEquipoAsociarAdapter.getFilter().filter(newText);
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
