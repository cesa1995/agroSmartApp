package com.example.cesar.agrosmart.agrono.tareas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.Adapter.ListaTareaDoneAdapter;
import com.example.cesar.agrosmart.agrono.Adapter.ListaTareasTodoAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.tareas.readTareasBody;
import com.example.cesar.agrosmart.apiBody.tareas.rmBody;
import com.example.cesar.agrosmart.apiBody.tareas.updateStateBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.tareas.ReadTareasRespuesta;
import com.example.cesar.agrosmart.models.tareas.tareas;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class agronomoTareaDone extends Fragment {

    private static final String TAG="dont";

    private String jwt, idparcela, ubicacion, estado;
    private Retrofit retrofit;
    private ListaTareaDoneAdapter listaTareaDoneAdapter;
    private RecyclerView recyclerView;
    private RecyclerTouchListener touchListener;

    private static final String URL="http://3.16.180.219:3000";
    private Socket mSocket;
    {
        try{
            mSocket= IO.socket(URL);
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idparcela=getArguments().getString("idParcela", "vacio");
            ubicacion=getArguments().getString("Ubicacion", "vacio");
            estado=getArguments().getString("estado", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_tarea_done, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.recyclerView);
        listaTareaDoneAdapter =new ListaTareaDoneAdapter(getContext(),jwt,ubicacion);
        recyclerView.setAdapter(listaTareaDoneAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager=new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        touchListener=new RecyclerTouchListener(getActivity(), recyclerView);
        touchListener.setClickable(new RecyclerTouchListener.OnRowClickListener() {
            @Override
            public void onRowClicked(int position) {

            }

            @Override
            public void onIndependentViewClicked(int independentViewID, int position) {

            }
        }).setSwipeOptionViews(R.id.delete, R.id.done)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.delete:{
                                deleteTarea(position);
                            }break;
                        }
                    }
                });

        listaTareaDoneAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                setScrollbar();
            }
        });


        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (!mSocket.connected()){
            socketConnect();
        }
    }

    private void socketConnect(){
        JSONObject channel=new JSONObject();
        try {
            channel.put("channel", ubicacion);
        }catch (JSONException e){
            return;
        }
        Log.d("channel", channel.toString());
        mSocket.on("tarea:update",onUpdate);
        mSocket.on("tarea:delete",onDelete);
        mSocket.on("tarea:updateState",onUpdateState);
        mSocket.connect();
        mSocket.emit("chat:channel", channel);
        obtenerdatos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket.connected()) {
            mSocket.disconnect();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mSocket.connected()){
            socketConnect();
        }
        recyclerView.addOnItemTouchListener(touchListener);
    }

    public Emitter.Listener onUpdate=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data=(JSONObject) args[0];
                        String id;
                        String tarea;
                        String inicio;
                        String fin;
                        try{
                            id=data.getString("id");
                            tarea=data.getString("tarea");
                            inicio=data.getString("inicio");
                            fin=data.getString("fin");
                        }catch (JSONException e){
                            return;
                        }
                        listaTareaDoneAdapter.updateData(id,tarea,inicio,fin);
                        Log.d("Actualizar", "actualizar lista");
                    }
                });
            }catch (RuntimeException e){
                Log.d("runtime", "problema OnCreate");
            }
        }
    };
    public Emitter.Listener onDelete=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data=(JSONObject) args[0];
                        String id;
                        try {
                            id=data.getString("id");
                        }catch (JSONException e){
                            return;
                        }
                        listaTareaDoneAdapter.removeDataSocket(id);
                    }
                });
            }catch (RuntimeException e){
                Log.d("runtime", "problema OnCreate");
            }
        }
    };
    public Emitter.Listener onUpdateState=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data=(JSONObject) args[0];
                        String id;
                        String estado;
                        String tarea;
                        String inicio;
                        String fin;
                        try {
                            id=data.getString("id");
                            estado=data.getString("estado");
                            tarea=data.getString("tarea");
                            inicio=data.getString("inicio");
                            fin=data.getString("fin");
                        }catch (JSONException e){
                            return;
                        }
                        if(!estado.equals("0")) {
                            listaTareaDoneAdapter.removeDataSocket(id);
                        }else {
                            listaTareaDoneAdapter.addTarea(new tareas(id, tarea,inicio,fin,estado));
                        }
                    }
                });
            }catch (RuntimeException e){
                Log.d("runtime", "problema OnCreate");
            }
        }
    };

    private void setScrollbar(){
        recyclerView.scrollToPosition(listaTareaDoneAdapter.getItemCount()-1);
    }


    private void deleteTarea(final int position){
        String id= listaTareaDoneAdapter.getID(position);
        ApiService service = retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.rmTarea(new rmBody(id,ubicacion,jwt));
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
                listaTareaDoneAdapter.removeData(position);
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {

            }
        });
    }

    private void showMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    public String getTime(){
        Time time=new Time(Time.getCurrentTimezone());
        time.setToNow();
        return time.year+"-"+time.month+"-"+time.monthDay+" "+time.hour+":"+time.minute;
    }

    private void obtenerdatos(){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadTareasRespuesta> readTareasRespuestaCall=service.readTareas(new readTareasBody(idparcela,estado,getTime(),jwt));
        readTareasRespuestaCall.enqueue(new Callback<ReadTareasRespuesta>() {
            @Override
            public void onResponse(Call<ReadTareasRespuesta> call, Response<ReadTareasRespuesta> response) {
                if (!response.isSuccessful()) {
                    String error = "Ha ocurrido un error. Contacte el admistrador";
                    if (response.errorBody().contentType().subtype().equals("json")) {
                        ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                        error = apiError.getMessage();
                        Log.e(TAG, error);
                    } else {
                        try {
                            Log.e(TAG, response.errorBody().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                ReadTareasRespuesta readTareasRespuesta=response.body();
                ArrayList<tareas> listaTareas = readTareasRespuesta.getRecords();
                listaTareaDoneAdapter.adicionarListaTareas(listaTareas);
            }

            @Override
            public void onFailure(Call<ReadTareasRespuesta> call, Throwable t) {

            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
