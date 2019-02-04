package com.example.cesar.agrosmart.admin.asociar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.interfaces.OnItemClickListener;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AsociarUsuarioListAdapter extends RecyclerView.Adapter<AsociarUsuarioListAdapter.ViewHolder> {

    private ArrayList<Usuarios> dataset;
    private ArrayList<Usuarios> datasetFiltered;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public AsociarUsuarioListAdapter() {
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public AsociarUsuarioListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_dialog_usuario,parent,false);
        return new ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AsociarUsuarioListAdapter.ViewHolder holder, int position) {
        Usuarios U=datasetFiltered.get(position);
        holder.mUsuarioView.setText(U.getNombre()+" "+U.getApellido());
        holder.mEmailView.setText(U.getEmail());
        switch (U.getNivel()){
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

        holder.id=U.getId();
        holder.position=holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaUsuarios(ArrayList<Usuarios> listaUsuarios){
        dataset.addAll(listaUsuarios);
        datasetFiltered=dataset;
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
                    ArrayList<Usuarios> filteredList = new ArrayList<>();
                    for(Usuarios row : dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getApellido().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getEmail().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<Usuarios>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG="recycleview";

        private TextView mUsuarioView, mEmailView, mNivelView;
        private String id;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            mUsuarioView=itemView.findViewById(R.id.usuario);
            mEmailView=itemView.findViewById(R.id.email);
            mNivelView=itemView.findViewById(R.id.nivel);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION){
                        onItemClickListener.OnClicked(position,id);
                    }
                }
            });

            retrofit=new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

    }
}
