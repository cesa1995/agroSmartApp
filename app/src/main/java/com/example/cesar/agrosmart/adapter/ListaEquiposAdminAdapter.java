package com.example.cesar.agrosmart.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.example.cesar.agrosmart.admin.update.UpdateEquipo;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.equipos.Equipos;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaEquiposAdminAdapter extends RecyclerView.Adapter<ListaEquiposAdminAdapter.ViewHolder>{

    private ArrayList<Equipos> dataset;
    private ArrayList<Equipos> datasetFiltered;
    private Context context;
    private String jwt;

    public ListaEquiposAdminAdapter(Context context, String jwt){
        this.context = context;
        this.jwt=jwt;
        dataset = new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_equipos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipos E = datasetFiltered.get(position);
        holder.nombre=E.getNombre();
        holder.tipo=E.getDevicetype();
        holder.descripcion=E.getDescripcion();
        holder.id = E.getId();
        holder.position = holder.getAdapterPosition();
        holder.mNombreView.setText(holder.nombre);
        holder.mTipoView.setText(holder.tipo);
        holder.mDescripcionView.setText(holder.descripcion);
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaEquipos(ArrayList<Equipos> listaEquipos){
        dataset.addAll(listaEquipos);
        datasetFiltered = dataset;
        notifyDataSetChanged();
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    datasetFiltered = dataset;
                }else{
                    ArrayList<Equipos> filteredList=new ArrayList<>();
                    for(Equipos row:dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase())||
                                row.getDevicetype().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<Equipos>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public static final String TAG = "recycleview";

        private TextView mNombreView, mTipoView, mDescripcionView;
        private Button borrar;
        private String id, nombre, tipo, descripcion;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView) {
            super(itemView);

            mNombreView = itemView.findViewById(R.id.nombre);
            mTipoView= itemView.findViewById(R.id.devicetype);
            mDescripcionView = itemView.findViewById(R.id.descripcion);
            borrar = itemView.findViewById(R.id.borrarEquipo);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("id", id);
                    bundle.putString("nombre", nombre);
                    bundle.putString("tipo", tipo);
                    bundle.putString("descripcion", descripcion);
                    Fragment fragment = new UpdateEquipo();
                    FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarEquipos();
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        private void borrarEquipos(){
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.eliminarEquipo(new deleteBody(id,jwt));

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
