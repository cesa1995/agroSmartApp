package com.example.cesar.agrosmart.agrono.comentarios;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.Adapter.ListaMensajesComentariosAdapter;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.comentarios.createComentariosBody;
import com.example.cesar.agrosmart.apiBody.comentarios.deleteComentarioBody;
import com.example.cesar.agrosmart.apiBody.comentarios.readComentariosBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


public class agronomoComentarios extends Fragment {

    private static final String TAG="addcomentarios";

    private RecyclerView recyclerView;
    private EditText escribirMensaje;
    private String ubicacion, usuario, jwt, idparcela;
    private int pag;
    TextView mUbicacionView, mEventosView;
    private Retrofit retrofit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton delete;

    private ListaMensajesComentariosAdapter listaMensajesComentariosAdapter;
    private static final String URL = "http://3.16.180.219:3000";
    private Socket mSocket;
    {
        try {
            mSocket= IO.socket(URL);
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ubicacion=getArguments().getString("Ubicacion", "vacio");
            jwt=getArguments().getString("jwt", "vacio");
            idparcela=getArguments().getString("idParcela", "vacio");
        }
        SharedPreferences preferences = getActivity().getSharedPreferences("SESSION_USUARIO", MODE_PRIVATE);
        usuario = preferences.getString("nombre", null);
        mSocket.on("chat:typing", onTyping);
        mSocket.on("chat:mensaje", onNewMessage);
        mSocket.connect();
        mSocket.emit("chat:channel", ubicacion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_comentarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUbicacionView = view.findViewById(R.id.ubicacion);
        recyclerView=view.findViewById(R.id.recyclerView);
        escribirMensaje=view.findViewById(R.id.escribirMensaje);
        Button enviar = view.findViewById(R.id.enviar);
        mUbicacionView.setText(ubicacion);
        mEventosView=view.findViewById(R.id.eventos);
        listaMensajesComentariosAdapter=new ListaMensajesComentariosAdapter(getContext());
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listaMensajesComentariosAdapter);
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        delete=view.findViewById(R.id.delete);
        pag=1;

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarData();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leer(String.valueOf(pag));
            }
        });

        escribirMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSocket.emit("chat:typing", usuario+" esta escribiendo.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje=escribirMensaje.getText().toString().trim();
                if (TextUtils.isEmpty(mensaje)){
                    return;
                }
                JSONObject mensajeJson=new JSONObject();
                try {
                    mensajeJson.put("username", usuario);
                    mensajeJson.put("message", mensaje);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                escribirMensaje.setText("");
                mSocket.emit("chat:mensaje", mensajeJson);
                mSocket.emit("chat:typing", "");
                guardarMensaje(mensaje, getTime(), usuario);
            }
        });

        listaMensajesComentariosAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        retrofit=new Retrofit.Builder()
                .baseUrl("http://3.16.180.219/agroSmart/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        leer(String.valueOf(pag));
    }

    private void eliminarData(){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.deleteMensajes(new deleteComentarioBody(idparcela, jwt));
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
                Respuesta respuesta=response.body();
                showMessage(respuesta.getMessage());
                listaMensajesComentariosAdapter.deleteMensajes();
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {

            }
        });
    }

    private void leer(final String pagi){
        ApiService service=retrofit.create(ApiService.class);
        Call<ReadMensajesRespuesta> readMensajesRespuestaCall=service.readMensaje(new readComentariosBody(jwt,idparcela,"20", pagi));
        readMensajesRespuestaCall.enqueue(new Callback<ReadMensajesRespuesta>() {
            @Override
            public void onResponse(Call<ReadMensajesRespuesta> call, Response<ReadMensajesRespuesta> response) {
                swipeRefreshLayout.setRefreshing(false);
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
                    swipeRefreshLayout.setEnabled(false);
                    showMessage(error);
                    return;
                }
                ReadMensajesRespuesta readMensajesRespuesta=response.body();
                ArrayList<Mensaje> listMensaje=readMensajesRespuesta.getRecords();
                listaMensajesComentariosAdapter.adicionarMensajes(listMensaje);
                pag++;
            }

            @Override
            public void onFailure(Call<ReadMensajesRespuesta> call, Throwable t) {

            }
        });
    }

    private void guardarMensaje(String comentario, String fecha, String usuario){
        ApiService service=retrofit.create(ApiService.class);
        Call<Respuesta> respuestaCall=service.createComentario(new createComentariosBody(jwt, comentario, idparcela, usuario, fecha));
        Log.d("comentarios", jwt+" "+comentario+" "+ubicacion+" "+usuario+" "+fecha);
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
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {

            }
        });
    }

    private void showMessage(String error){
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    public Emitter.Listener onTyping=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEventosView.setText(args[0].toString());
                    }
                });
            }catch (RuntimeException e){
                Log.d("runtime", "runtime problema");
            }
        }
    };

    public Emitter.Listener onNewMessage=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                        try {
                            username = data.getString("username");
                            message = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        listaMensajesComentariosAdapter.addMensaje(new Mensaje(message, username, getTime()));
                    }
                });
            }catch (RuntimeException e){
                Log.d("runtime", "runtime problema");
            }
        }
    };

    public String getTime(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    private void setScrollbar(){
        recyclerView.scrollToPosition(listaMensajesComentariosAdapter.getItemCount()-1);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
