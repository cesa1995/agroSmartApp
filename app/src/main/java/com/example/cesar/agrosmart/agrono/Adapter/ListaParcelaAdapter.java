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
import com.example.cesar.agrosmart.agrono.listas.dashboard;
import com.example.cesar.agrosmart.models.parcelas.AsociarParcela;

import java.util.ArrayList;

public class ListaParcelaAdapter extends RecyclerView.Adapter<ListaParcelaAdapter.ViewHolder> {
    private ArrayList<AsociarParcela> dataset;
    private ArrayList<AsociarParcela> datasetFiltered;
    private String jwt;
    private Context context;

    public ListaParcelaAdapter(String jwt, Context context) {
        this.jwt = jwt;
        this.context = context;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agronomo_item_select_parcela, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsociarParcela P=datasetFiltered.get(position);
        holder.mNickView.setText(P.getNick());
        holder.mTipoView.setText(P.getTipo());
        holder.mNombreView.setText(P.getNombre());
        holder.id=P.getId();
        holder.finca=P.getFinca();
        holder.parcela=P.getNombre();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaFincas(ArrayList<AsociarParcela> listaParcelas){
        dataset.addAll(listaParcelas);
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNickView, mNombreView, mTipoView;
        private String id, finca, parcela;
        private CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            mNickView=itemView.findViewById(R.id.nick);
            mNombreView=itemView.findViewById(R.id.nombre);
            mTipoView=itemView.findViewById(R.id.tipo);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("idParcela", id);
                    bundle.putString("Ubicacion", finca+" / "+parcela);
                    Fragment fragment=new dashboard();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction=((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}
