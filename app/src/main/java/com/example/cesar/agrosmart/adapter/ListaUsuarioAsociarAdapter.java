package com.example.cesar.agrosmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.interfaces.OnItemClickListenerState;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.AsociarUsuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaUsuarioAsociarAdapter extends RecyclerView.Adapter<ListaUsuarioAsociarAdapter.ViewHolder> {

    private ArrayList<AsociarUsuario> dataset;
    private ArrayList<AsociarUsuario> datasetFiltered;
    private Context context;
    private String jwt;
    private OnItemClickListenerState onItemClickListenerState;

    public void setOnItemClickListenerState(OnItemClickListenerState onItemClickListenerState){
        this.onItemClickListenerState=onItemClickListenerState;
    }

    public ListaUsuarioAsociarAdapter(Context context, String jwt) {
        this.context = context;
        this.jwt = jwt;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_asociar_usuario,parent,false);
        return new ViewHolder(view,onItemClickListenerState);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsociarUsuario A = datasetFiltered.get(position);
        holder.id=A.getId();
        holder.mUsuarioView.setText(A.getUsuario());
        holder.mEmailView.setText(A.getEmail());
        holder.estado=A.getEstado();
        switch (A.getNivel()){
            case "0":{
                holder.mNivelView.setText(R.string.administrador);
            }break;
            case "1":{
                holder.mNivelView.setText(R.string.agronomo);
            }break;
            case "2":{
                holder.mNivelView.setText(R.string.cliente);
            }break;
        }
        switch (holder.estado){
            case "0":{
                holder.mEstadoView.setText(R.string.bloqueado);
                holder.item.setBackgroundResource(R.drawable.list_select_background_b);
            }break;
            case "1":{
                holder.mEstadoView.setText(R.string.activo);
                holder.item.setBackgroundResource(R.drawable.list_select_background);
            }break;
        }
        holder.position=holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaAsociarUsuarios(ArrayList<AsociarUsuario> listaAsociarUsuarios){
        if (!datasetFiltered.isEmpty()){
            dataset.clear();
            datasetFiltered.clear();
        }
        dataset.addAll(listaAsociarUsuarios);
        datasetFiltered=dataset;
        notifyDataSetChanged();
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if (charString.isEmpty()){
                    datasetFiltered=dataset;
                }else{
                    ArrayList<AsociarUsuario> filteredList=new ArrayList<>();
                    for(AsociarUsuario row : dataset){
                        if (row.getEmail().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getUsuario().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    datasetFiltered=filteredList;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=datasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datasetFiltered=(ArrayList<AsociarUsuario>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG="recycleview";

        private TextView mUsuarioView, mEmailView, mNivelView, mEstadoView;
        private String id, estado;
        private Button borrar;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView, final OnItemClickListenerState onItemClickListenerState) {
            super(itemView);

            mUsuarioView=itemView.findViewById(R.id.usuario);
            mEmailView=itemView.findViewById(R.id.email);
            mNivelView=itemView.findViewById(R.id.nivel);
            mEstadoView=itemView.findViewById(R.id.estado);
            borrar=itemView.findViewById(R.id.borrarUsuario);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION){
                        onItemClickListenerState.OnClick(position,id,estado);
                    }
                }
            });

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarUsuario();
                }
            });

            retrofit=new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        private void borrarUsuario(){
            ApiService service=retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall=service.rmAsociarUsuario(new deleteBody(id,jwt));

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
