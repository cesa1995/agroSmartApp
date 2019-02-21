package com.example.cesar.agrosmart.agrono.Adapter;

import android.content.Context;
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
import com.example.cesar.agrosmart.agrono.listas.parcelasSelect;
import com.example.cesar.agrosmart.models.fincas.Fincas;

import java.util.ArrayList;

public class ListaFincasIndexAdapter extends RecyclerView.Adapter<ListaFincasIndexAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private ArrayList<Fincas> datasetFiltered;
    private String jwt, idUsuario;
    private Context context;

    public ListaFincasIndexAdapter(String jwt, Context context, String idUsuario) {
        this.jwt = jwt;
        this.context = context;
        this.idUsuario = idUsuario;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agronomo_item_select_finca_index, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fincas F=datasetFiltered.get(position);
        holder.finca=F.getNombre();
        holder.mFincaView.setText(holder.finca);
        holder.mDireccionView.setText(F.getDireccion());
        holder.id=F.getId();
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
                                row.getDireccion().toLowerCase().contains(charString.toLowerCase())||
                                row.getTelefono().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<Fincas>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mFincaView, mDireccionView;
        private String id, finca;
        private CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            mFincaView=itemView.findViewById(R.id.finca);
            mDireccionView=itemView.findViewById(R.id.direccion);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("idUsuario", idUsuario);
                    bundle.putString("idFinca", id);
                    bundle.putString("finca", finca);
                    bundle.putString("jwt", jwt);
                    Fragment fragment = new parcelasSelect();
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}
