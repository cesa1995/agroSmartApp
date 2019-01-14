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
import com.example.cesar.agrosmart.admin.update.UpdateParcela;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.parcelas.Parcelas;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaParcelaAdminAdapter extends RecyclerView.Adapter<ListaParcelaAdminAdapter.ViewHolder> {

    private ArrayList<Parcelas> dataset;
    private ArrayList<Parcelas> datasetFiltered;
    private Context context;
    private String jwt;

    public ListaParcelaAdminAdapter(Context context, String jwt){
        this.context = context;
        this.jwt=jwt;
        dataset = new ArrayList<>();
        datasetFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_parcelas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Parcelas P = datasetFiltered.get(position);
        holder.nombre=P.getNombre();
        holder.tipo=P.getTipo();
        holder.id = P.getId();
        holder.position=holder.getAdapterPosition();
        holder.mNombreView.setText(holder.nombre);
        holder.mTipoView.setText(holder.tipo);
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaParcelas(ArrayList<Parcelas> listaParcelas){
        dataset.addAll(listaParcelas);
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
                    ArrayList<Parcelas> filteredList = new ArrayList<>();
                    for(Parcelas row : dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getTipo().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    datasetFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=datasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datasetFiltered=(ArrayList<Parcelas>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "recycleview";

        private TextView mNombreView, mTipoView;
        private String id, nombre, tipo;
        private Button borrar;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView) {
            super(itemView);
            mNombreView = itemView.findViewById(R.id.nombre);
            mTipoView = itemView.findViewById(R.id.tipo);
            borrar = itemView.findViewById(R.id.borrarParcela);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("jwt", jwt);
                    bundle.putString("nombre", nombre);
                    bundle.putString("tipo", tipo);
                    Fragment fragment = new UpdateParcela();
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   borrarParcelas();
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        private void borrarParcelas(){
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.eliminarParcela(new deleteBody(id,jwt));

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
