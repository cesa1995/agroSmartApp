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
import com.example.cesar.agrosmart.admin.adapter.ListaParcelaAsociarAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.asociar.AddAsociarParcelaBody;
import com.example.cesar.agrosmart.apiBody.asociar.ReadAsociarParcelaBody;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.interfaces.OnItemClickListener;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.parcelas.AsociarParcela;
import com.example.cesar.agrosmart.models.parcelas.Parcelas;
import com.example.cesar.agrosmart.models.parcelas.ReadAsociarParcelaRespuesta;
import com.example.cesar.agrosmart.models.parcelas.ReadParcelasRespuesta;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class adminAsociarParcela extends Fragment {

    private static final String TAG = "asociar";

    private String idfinca, jwt, finca;
    private Retrofit retrofit;
    private ListaParcelaAsociarAdapter listaParcelaAsociarAdapter;
    private AsociarParcelaListAdapter asociarParcelaListAdapter;
    FloatingActionButton addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idfinca=getArguments().getString("idfinca", "vacio");
            finca=getArguments().getString("finca", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_admin_asociar_parcela, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButton=view.findViewById(R.id.addParcela);
        TextView mFincaView=view.findViewById(R.id.finca);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        listaParcelaAsociarAdapter=new ListaParcelaAsociarAdapter(getContext(),jwt, getActivity());
        recyclerView.setAdapter(listaParcelaAsociarAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        mFincaView.setText(finca);
        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
    }

    private void dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View f=inflater.inflate(R.layout.admin_dialog_list_parcelas,null);
        RecyclerView li=f.findViewById(R.id.recyclerView);
        li.setLayoutManager(new LinearLayoutManager(getActivity()));
        SearchView searchView=(SearchView) f.findViewById(R.id.search);
        searchView.setQueryHint("Buscar");

        asociarParcelaListAdapter=new AsociarParcelaListAdapter();
        li.setAdapter(asociarParcelaListAdapter);
        obtenerdatosparcelas();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                asociarParcelaListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                asociarParcelaListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        builder.setView(f);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        asociarParcelaListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClicked(int position, String id) {
                guardarParcela(id);
                alertDialog.dismiss();
            }
        });
    }

    private void guardarParcela(String id){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.addAsociarParcela(new AddAsociarParcelaBody(idfinca,id,jwt));

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

    private void obtenerdatosparcelas(){
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
                asociarParcelaListAdapter.adicionarListaParcelas(listaParcelas);
            }

            @Override
            public void onFailure(Call<ReadParcelasRespuesta> call, Throwable t) {

            }
        });
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadAsociarParcelaRespuesta> readAsociarParcelaRespuestaCall=service.readAsociarParcela(new ReadAsociarParcelaBody(idfinca, jwt));

        readAsociarParcelaRespuestaCall.enqueue(new Callback<ReadAsociarParcelaRespuesta>() {
            @Override
            public void onResponse(Call<ReadAsociarParcelaRespuesta> call, Response<ReadAsociarParcelaRespuesta> response) {
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
                ReadAsociarParcelaRespuesta readAsociarParcelaRespuesta=response.body();
                ArrayList<AsociarParcela> listaAsociarParcela =readAsociarParcelaRespuesta.getRecords();
                listaParcelaAsociarAdapter.adicionarListaAsociarParcela(listaAsociarParcela);
            }

            @Override
            public void onFailure(Call<ReadAsociarParcelaRespuesta> call, Throwable t) {

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
                listaParcelaAsociarAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaParcelaAsociarAdapter.getFilter().filter(newText);
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
