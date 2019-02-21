package com.example.cesar.agrosmart.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import com.example.cesar.agrosmart.admin.asociar.adminAsociarEquipo;
import com.example.cesar.agrosmart.admin.asociar.adminAsociarUsuario;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.idANDjwtBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.parcelas.AsociarParcela;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaParcelaAsociarAdapter extends RecyclerView.Adapter<ListaParcelaAsociarAdapter.ViewHolder> {

    private ArrayList<AsociarParcela> dataset;
    private ArrayList<AsociarParcela> datasetFiltered;
    private Context context;
    private Activity activity;
    private String jwt;

    public ListaParcelaAsociarAdapter(Context context, String jwt, Activity activity) {
        this.context = context;
        this.activity=activity;
        this.jwt = jwt;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_asociar_parcela,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsociarParcela A = datasetFiltered.get(position);
        holder.parcela=A.getNombre();
        holder.mNickView.setText(A.getNick());
        holder.mTipoView.setText(A.getTipo());
        holder.mParcelaView.setText(holder.parcela);
        holder.mFincaView.setText(A.getFinca());
        holder.id=A.getId();
        holder.position=holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaAsociarParcela(ArrayList<AsociarParcela> listaAsociarParcela){
        if (!datasetFiltered.isEmpty()){
            dataset.clear();
            datasetFiltered.clear();
        }
        dataset.addAll(listaAsociarParcela);
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
                    ArrayList<AsociarParcela> filteredList=new ArrayList<>();
                    for(AsociarParcela row : dataset){
                        if (row.getFinca().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getNombre().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getTipo().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<AsociarParcela>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public static final String TAG="recycleview";

        private TextView mFincaView, mParcelaView, mTipoView, mNickView;
        private String id, parcela;
        private Button borrar;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView) {
            super(itemView);

            mFincaView=itemView.findViewById(R.id.finca);
            mParcelaView=itemView.findViewById(R.id.parcela);
            mTipoView=itemView.findViewById(R.id.tipo);
            mNickView=itemView.findViewById(R.id.nick);
            borrar=itemView.findViewById(R.id.borrarParcela);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectelemento();
                }
            });

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarParcela();
                }
            });

            retrofit=new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        private void selectelemento(){
            final AlertDialog.Builder builder=new AlertDialog.Builder(activity);
            LayoutInflater inflater=activity.getLayoutInflater();
            View f=inflater.inflate(R.layout.admin_dialog_selecionar_elemento,null);
            Button equipos = f.findViewById(R.id.equipos);
            Button usuarios = f.findViewById(R.id.usuarios);
            builder.setTitle("¿Que desea agregar?");
            builder.setView(f);
            builder.create();
            final AlertDialog alertDialog=builder.show();

            equipos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("jwt", jwt);
                    bundle.putString("parcela", parcela);
                    Fragment fragment = new adminAsociarEquipo();
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main,fragment).addToBackStack(null);
                    transaction.commit();
                    alertDialog.dismiss();
                }
            });

            usuarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("jwt", jwt);
                    bundle.putString("parcela", parcela);
                    Fragment fragment = new adminAsociarUsuario();
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main,fragment).addToBackStack(null);
                    transaction.commit();
                    alertDialog.dismiss();
                }
            });

        }

        private void borrarParcela(){
            ApiService service=retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall=service.rmAsociarParcela(new idANDjwtBody(id,jwt));

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
