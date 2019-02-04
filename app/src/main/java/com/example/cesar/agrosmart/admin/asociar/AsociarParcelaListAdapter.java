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
import com.example.cesar.agrosmart.models.parcelas.Parcelas;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AsociarParcelaListAdapter extends RecyclerView.Adapter<AsociarParcelaListAdapter.ViewHolder> {
    private ArrayList<Parcelas> dataset;
    private ArrayList<Parcelas> datasetFiltered;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AsociarParcelaListAdapter() {
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_dialog_parcela,parent,false);
        return new ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Parcelas P = datasetFiltered.get(position);
        holder.id = P.getId();
        holder.position=holder.getAdapterPosition();
        holder.mNombreView.setText(P.getNombre());
        holder.mTipoView.setText(P.getTipo());
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaParcelas(ArrayList<Parcelas> listaParcelas){
        dataset.addAll(listaParcelas);
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public static final String TAG = "recycleview";

        private TextView mNombreView, mTipoView;
        private String id;
        private int position;
        Retrofit retrofit;
        private View item;


        public ViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            mNombreView = itemView.findViewById(R.id.nombre);
            mTipoView = itemView.findViewById(R.id.tipo);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.OnClicked(position,id);
                    }

                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.107/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }
}
