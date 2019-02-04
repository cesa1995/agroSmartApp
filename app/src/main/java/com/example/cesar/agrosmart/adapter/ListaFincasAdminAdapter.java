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
import com.example.cesar.agrosmart.admin.update.UpdateFinca;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.fincas.Fincas;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaFincasAdminAdapter extends RecyclerView.Adapter<ListaFincasAdminAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private ArrayList<Fincas> datasetFiltered;
    private Context context;
    private String jwt;

    public ListaFincasAdminAdapter(Context context, String jwt){
        this.context=context;
        this.jwt=jwt;
        datasetFiltered = new ArrayList<>();
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_admin_fincas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fincas F = datasetFiltered.get(position);
        holder.nombre=F.getNombre();
        holder.telefono=F.getTelefono();
        holder.direccion=F.getDireccion();
        holder.id = F.getId();
        holder.position = holder.getAdapterPosition();
        holder.mNombreView.setText(holder.nombre);
        holder.mTelefonoView.setText(holder.telefono);
        holder.mDireccionView.setText(holder.direccion);
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaFincas(ArrayList<Fincas> listaFincas){
        dataset.addAll(listaFincas);
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
                    ArrayList<Fincas> filteredList = new ArrayList<>();
                    for(Fincas row:dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase())||
                                row.getTelefono().toLowerCase().contains(charString.toLowerCase())||
                                row.getDireccion().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered= (ArrayList<Fincas>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "recycleview";

        private String id, nombre, telefono, direccion;
        private Button borrar;
        private TextView mNombreView, mTelefonoView, mDireccionView;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView) {
            super(itemView);

            mNombreView = itemView.findViewById(R.id.nombre);
            mTelefonoView = itemView.findViewById(R.id.telefono);
            mDireccionView = itemView.findViewById(R.id.direccion);
            borrar = itemView.findViewById(R.id.borrarFincas);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("id", id);
                    bundle.putString("nombre", nombre);
                    bundle.putString("telefono", telefono);
                    bundle.putString("direccion", direccion);
                    Fragment fragment = new UpdateFinca();
                    FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
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
            Call<Respuesta> respuestaCall = service.eliminarFinca(new deleteBody(id,jwt));

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
