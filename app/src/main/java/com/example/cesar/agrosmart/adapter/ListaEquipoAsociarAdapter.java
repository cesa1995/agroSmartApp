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
import com.example.cesar.agrosmart.models.equipos.AsociarEquipo;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaEquipoAsociarAdapter extends RecyclerView.Adapter<ListaEquipoAsociarAdapter.ViewHolder> {

    private ArrayList<AsociarEquipo> dataset;
    private ArrayList<AsociarEquipo> datasetFiltered;
    private Context context;
    private String jwt;
    private OnItemClickListenerState onItemClickListenerState;

    public void setOnItemClickListenerState(OnItemClickListenerState onItemClickListenerState) {
        this.onItemClickListenerState = onItemClickListenerState;
    }

    public ListaEquipoAsociarAdapter(Context context, String jwt) {
        this.context = context;
        this.jwt = jwt;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_asociar_equipo, parent,false);
        return new ViewHolder(view, onItemClickListenerState);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsociarEquipo E=datasetFiltered.get(position);
        holder.mNombreView.setText(E.getEquipo());
        holder.mTipoView.setText(E.getTipo());
        holder.mDescripcionView.setText(E.getDescripcion());
        holder.id=E.getId();
        holder.position=holder.getAdapterPosition();
        holder.estado=E.getEstado();
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
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaAsociarEquipo(ArrayList<AsociarEquipo> listaAsociarEquipo){
        if (!datasetFiltered.isEmpty()){
            dataset.clear();
            datasetFiltered.clear();
        }
        dataset.addAll(listaAsociarEquipo);
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
                    ArrayList<AsociarEquipo> filteredList=new ArrayList<>();
                    for(AsociarEquipo row : dataset){
                        if (row.getEquipo().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getTipo().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getDescripcion().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<AsociarEquipo>) results.values;
                notifyDataSetChanged();
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG="recycleview";

        private TextView mNombreView, mTipoView, mDescripcionView, mEstadoView;
        private String id, estado;
        private Button borrar;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView, final OnItemClickListenerState onItemClickListenerState) {
            super(itemView);

            mNombreView=itemView.findViewById(R.id.nombre);
            mTipoView=itemView.findViewById(R.id.tipo);
            mDescripcionView=itemView.findViewById(R.id.descripcion);
            mEstadoView=itemView.findViewById(R.id.estado);
            borrar=itemView.findViewById(R.id.borrarEquipo);
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
                    borrarEquipos();
                }
            });

            retrofit=new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        private void borrarEquipos(){
            ApiService service=retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall=service.rmAsociarEquipo(new deleteBody(id,jwt));

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
