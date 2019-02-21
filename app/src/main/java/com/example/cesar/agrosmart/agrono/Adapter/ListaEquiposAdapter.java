package com.example.cesar.agrosmart.agrono.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.datos.datos;
import com.example.cesar.agrosmart.models.equipos.AsociarEquipo;

import java.util.ArrayList;

public class ListaEquiposAdapter extends RecyclerView.Adapter<ListaEquiposAdapter.ViewHolder> {

    private ArrayList<AsociarEquipo> dataset;
    private ArrayList<AsociarEquipo> datasetFiltered;
    private String jwt, Ubicacion;
    private Context context;

    public ListaEquiposAdapter(String jwt, String Ubicacion, Context context) {
        this.jwt = jwt;
        this.Ubicacion = Ubicacion;
        this.context = context;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agronomo_item_select_equipo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsociarEquipo P= datasetFiltered.get(position);
        holder.equipo=P.getEquipo();
        holder.mNombreView.setText(holder.equipo);
        holder.mTipoView.setText(P.getTipo());
        holder.mDescripcionView.setText(P.getDescripcion());
        holder.id=P.getId();
        holder.mIdEquipoView.setText(holder.id);
        switch (P.getEstado()){
            case "0":{
                holder.mEstadoView.setText(R.string.bloqueado);
                holder.item.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }break;
            case "1":{
                holder.mEstadoView.setText(R.string.activo);
                holder.item.setCardBackgroundColor(context.getResources().getColor(R.color.blanco));
            }
        }

    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaEquipos(ArrayList<AsociarEquipo> listaEquipo){
        dataset.addAll(listaEquipo);
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

        private TextView mNombreView, mIdEquipoView, mEstadoView, mTipoView, mDescripcionView;
        private String id, equipo;
        private CardView item;

        public ViewHolder(View itemView) {
            super(itemView);
            mNombreView=itemView.findViewById(R.id.nombre);
            mIdEquipoView=itemView.findViewById(R.id.idEquipo);
            mTipoView=itemView.findViewById(R.id.tipo);
            mEstadoView=itemView.findViewById(R.id.estado);
            mDescripcionView=itemView.findViewById(R.id.descripcion);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("idEquipo", id);
                    bundle.putString("Ubicacion", Ubicacion+" / "+equipo);
                    Fragment fragment=new datos();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}