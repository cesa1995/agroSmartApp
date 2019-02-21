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
import com.example.cesar.agrosmart.admin.adapter.ListaUsuarioAsociarAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.asociar.AddAsociarUsuarioANDEquipoBody;
import com.example.cesar.agrosmart.apiBody.asociar.CambiarEstadoElemento;
import com.example.cesar.agrosmart.apiBody.asociar.ReadAsociarUsuarioANDEquipoBody;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.interfaces.OnItemClickListener;
import com.example.cesar.agrosmart.interfaces.OnItemClickListenerState;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.AsociarUsuario;
import com.example.cesar.agrosmart.models.usuarios.ReadAsociarUsuarioRespuesta;
import com.example.cesar.agrosmart.models.usuarios.ReadUsuariosRespuesta;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class adminAsociarUsuario extends Fragment {

    private static final String TAG="asociar";

    private String idfincaparcela, jwt, parcela;
    private Retrofit retrofit;
    private ListaUsuarioAsociarAdapter listaUsuarioAsociarAdapter;
    private AsociarUsuarioListAdapter asociarUsuarioListAdapter;
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
        return inflater.inflate(R.layout.admin_fragment_admin_asociar_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButton=view.findViewById(R.id.addUsuario);
        TextView mParcelaView=view.findViewById(R.id.parcela);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaUsuarioAsociarAdapter=new ListaUsuarioAsociarAdapter(getContext(), jwt);
        recyclerView.setAdapter(listaUsuarioAsociarAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        mParcelaView.setText(parcela);
        retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.0.107/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        listaUsuarioAsociarAdapter.setOnItemClickListenerState(new OnItemClickListenerState() {
            @Override
            public void OnClick(int position, String id, String estado) {
                cambiarEstado(id, estado);
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
        View f=inflater.inflate(R.layout.admin_dialog_list_usuarios,null);
        RecyclerView li=f.findViewById(R.id.recyclerView);
        li.setLayoutManager(new LinearLayoutManager(getActivity()));
        SearchView searchView=(SearchView) f.findViewById(R.id.search);
        searchView.setQueryHint("Buscar");

        asociarUsuarioListAdapter=new AsociarUsuarioListAdapter();
        li.setAdapter(asociarUsuarioListAdapter);
        obtenerdatosusuarios();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                asociarUsuarioListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                asociarUsuarioListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        builder.setView(f);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        asociarUsuarioListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClicked(int position, String id) {
                guardarUsuario(id);
                alertDialog.dismiss();
            }
        });
    }

    private void guardarUsuario(String id){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.addAsociarUsuario(new AddAsociarUsuarioANDEquipoBody(idfincaparcela,id,"0",jwt));
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


    private void showMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void obtenerdatosusuarios(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadUsuariosRespuesta> readUsuariosRespuestaCall=service.obtenerUsuariosByNivel(new jwtOnlyBody(jwt));

        readUsuariosRespuestaCall.enqueue(new Callback<ReadUsuariosRespuesta>() {
            @Override
            public void onResponse(Call<ReadUsuariosRespuesta> call, Response<ReadUsuariosRespuesta> response) {
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
                ReadUsuariosRespuesta readUsuariosRespuesta=response.body();
                ArrayList<Usuarios> listaUsuarios=readUsuariosRespuesta.getRecords();
                asociarUsuarioListAdapter.adicionarListaUsuarios(listaUsuarios);
            }

            @Override
            public void onFailure(Call<ReadUsuariosRespuesta> call, Throwable t) {

            }
        });
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadAsociarUsuarioRespuesta> readAsociarUsuarioRespuestaCall=service.readAsociarUsuario(new ReadAsociarUsuarioANDEquipoBody(idfincaparcela,jwt));

        readAsociarUsuarioRespuestaCall.enqueue(new Callback<ReadAsociarUsuarioRespuesta>() {
            @Override
            public void onResponse(Call<ReadAsociarUsuarioRespuesta> call, Response<ReadAsociarUsuarioRespuesta> response) {
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
                ReadAsociarUsuarioRespuesta readAsociarUsuarioRespuesta=response.body();
                ArrayList<AsociarUsuario> listaAsociarUsuario=readAsociarUsuarioRespuesta.getRecords();
                listaUsuarioAsociarAdapter.adicionarListaAsociarUsuarios(listaAsociarUsuario);

            }

            @Override
            public void onFailure(Call<ReadAsociarUsuarioRespuesta> call, Throwable t) {

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
                listaUsuarioAsociarAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaUsuarioAsociarAdapter.getFilter().filter(newText);
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
