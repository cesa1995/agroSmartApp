package com.example.cesar.agrosmart.admin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.asociar.adminAsociarParcela;
import com.example.cesar.agrosmart.models.fincas.Fincas;

import java.util.ArrayList;

public class ListaFincasIndexAdapter extends RecyclerView.Adapter<ListaFincasIndexAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private ArrayList<Fincas> datasetFiltered;
    private String jwt;
    private Context context;

    public ListaFincasIndexAdapter(Context context, String jwt){
        this.context = context;
        this.jwt = jwt;
        dataset = new ArrayList<>();
        datasetFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_index_finca,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fincas f = datasetFiltered.get(position);
        holder.finca=f.getNombre();
        holder.fincaNombre.setText(holder.finca);
        holder.fincaTelefono.setText(f.getTelefono());
        holder.fincaDireccion.setText(f.getDireccion());
        holder.id = f.getId();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaFincas(ArrayList<Fincas> listaFincas){
        dataset.addAll(listaFincas);
        datasetFiltered = dataset;
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView fincaNombre, fincaTelefono, fincaDireccion;
        private String id, finca;
        private View item;

        public ViewHolder(View itemView){
            super(itemView);

            fincaNombre = itemView.findViewById(R.id.fincaNonmbre);
            fincaTelefono = itemView.findViewById(R.id.fincaTelefono);
            fincaDireccion = itemView.findViewById(R.id.fincaDireccion);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("idfinca", id);
                    bundle.putString("jwt", jwt);
                    bundle.putString("finca", finca);
                    Fragment fragment = new adminAsociarParcela();
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
            
        }

    }
}
