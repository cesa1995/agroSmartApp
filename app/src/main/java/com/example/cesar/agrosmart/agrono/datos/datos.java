package com.example.cesar.agrosmart.agrono.datos;

import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.cesar.agrosmart.agrono.Adapter.ListaDatosAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.idANDjwtBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.datos.ReadVariablesRespuesta;
import com.example.cesar.agrosmart.models.datos.variables;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class datos extends Fragment {

    public static final String TAG="variables";

    private String jwt, idEquipo, Ubicacion;
    private Retrofit retrofit;
    private ListaDatosAdapter listaDatosAdapter;
    private View mBrokerView, mTopicoView, mMensajeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idEquipo=getArguments().getString("idEquipo", "vacio");
            Ubicacion=getArguments().getString("Ubicacion", "vacio");
        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.datos));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.datos));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        listaDatosAdapter=new ListaDatosAdapter(jwt, idEquipo, getContext());
        recyclerView.setAdapter(listaDatosAdapter);

        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        TextView mUbicacionView=view.findViewById(R.id.ubicacion);
        mBrokerView=view.findViewById(R.id.broker);
        mTopicoView=view.findViewById(R.id.topico);
        mMensajeView=view.findViewById(R.id.mensaje);

        mUbicacionView.setText(Ubicacion);

        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerdatos();
    }

    private class mqttAsytask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String clientId = MqttClient.generateClientId();
            final MqttAndroidClient client;
            client = new MqttAndroidClient(getContext(), "tcp://3.16.180.219:1883", clientId);
            try {
                final IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        mBrokerView.setBackgroundResource(R.drawable.redondo_verde);
                        final String topic = "agroSmart/" + idEquipo;
                        final int qos = 1;
                        try {
                            IMqttToken subToken = client.subscribe(topic, qos);
                            subToken.setActionCallback(new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    // The message was publishe
                                    mTopicoView.setBackgroundResource(R.drawable.redondo_verde);
                                    client.setCallback(new MqttCallback() {
                                        @Override
                                        public void connectionLost(Throwable cause) {
                                            mMensajeView.setBackgroundResource(R.drawable.redondo_rojo);
                                            if(client.isConnected()){
                                                try {
                                                    client.subscribe(topic,qos);
                                                } catch (MqttException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                try {
                                                    client.connect();
                                                } catch (MqttException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                                            mMensajeView.setBackgroundResource(R.drawable.redondo_verde);
                                            listaDatosAdapter.dataMqtt(new String(message.getPayload()));
                                        }

                                        @Override
                                        public void deliveryComplete(IMqttDeliveryToken token) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken,
                                                      Throwable exception) {
                                    // The subscription could not be performed, maybe the user was not
                                    // authorized to subscribe on the specified topic e.g. using wildcards
                                    mTopicoView.setBackgroundResource(R.drawable.redondo_rojo);
                                }
                            });
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        mMensajeView.setBackgroundResource(R.drawable.redondo_rojo);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private void showMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadVariablesRespuesta> readVariablesRespuestaCall=service.readVariablesEquipo(new idANDjwtBody(idEquipo,jwt));
        readVariablesRespuestaCall.enqueue(new Callback<ReadVariablesRespuesta>() {
            @Override
            public void onResponse(Call<ReadVariablesRespuesta> call, Response<ReadVariablesRespuesta> response) {
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
                ReadVariablesRespuesta readVariablesRespuesta = response.body();
                ArrayList<variables> listaVariables = readVariablesRespuesta.getRecords();
                listaDatosAdapter.adicionarListaVariablesDatos(listaVariables);
                new mqttAsytask().execute();
            }

            @Override
            public void onFailure(Call<ReadVariablesRespuesta> call, Throwable t) {

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
                listaDatosAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listaDatosAdapter.getFilter().filter(newText);
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
