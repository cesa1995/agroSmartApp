package com.example.cesar.agrosmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaUsuarioAdminAdapter extends RecyclerView.Adapter<ListaUsuarioAdminAdapter.ViewHolder> {

    private ArrayList<Usuarios> dataset;
    private Context context;
    private String jwt;

    public ListaUsuarioAdminAdapter(Context context, String jwt){
        this.context = context;
        this.jwt = jwt;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_usuarios,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuarios U = dataset.get(position);
        String nombreApellido = U.getNombre()+" "+U.getApellido();
        holder.nombreApellido.setText(nombreApellido);
        holder.email.setText(U.getEmail());
        switch (U.getNivel()){
            case "0":{
                holder.nivel.setText(R.string.administrador);
            }break;
            case "1":{
                holder.nivel.setText(R.string.agronomo);
            }break;
            case "2":{
                holder.nivel.setText(R.string.cliente);
            }break;
        }
        holder.id = U.getId();
        holder.position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaUsuarios(ArrayList<Usuarios> listaUsuarios){
        dataset.addAll(listaUsuarios);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public static final String TAG = "recycleview";

        private TextView nombreApellido, email, nivel;
        private Button borrar;
        private String id;
        private int position;
        Retrofit retrofit;

        public ViewHolder(View itemView){
            super(itemView);

            nombreApellido = itemView.findViewById(R.id.UsuarioNombreApellido);
            email = itemView.findViewById(R.id.UsuarioEmail);
            nivel = itemView.findViewById(R.id.UsuarioNivel);
            borrar = itemView.findViewById(R.id.borrarUsuario);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarUsuarios();
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        private void borrarUsuarios(){
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.eliminarUsuario(new deleteBody(id,jwt));

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
                    dataset.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position,dataset.size());
                    showMessage(respuesta.getMessage());
                }

                @Override
                public void onFailure(Call<Respuesta> call, Throwable t) {

                }
            });
        }

        private void showMessage(String message){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }
    }
}
