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
import com.example.cesar.agrosmart.models.equipos.Equipos;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AsociarEquipoListAdapter extends RecyclerView.Adapter<AsociarEquipoListAdapter.ViewHolder> {

    private ArrayList<Equipos> dataset;
    private ArrayList<Equipos> datasetFiltered;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public AsociarEquipoListAdapter() {
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public AsociarEquipoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_dialog_equipo,parent,false);
        return new ViewHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AsociarEquipoListAdapter.ViewHolder holder, int position) {
        Equipos E=datasetFiltered.get(position);
        holder.mTipoView.setText(E.getDevicetype());
        holder.mDescripcionView.setText(E.getDescripcion());
        holder.mEquiposView.setText(E.getNombre());
        holder.id=E.getId();
        holder.position=holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaEquipos(ArrayList<Equipos> listaEquipos){
        dataset.addAll(listaEquipos);
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
                    ArrayList<Equipos> filteredList = new ArrayList<>();
                    for(Equipos row : dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getDevicetype().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getDescripcion().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<Equipos>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG="recycleview";

        private TextView mEquiposView, mTipoView, mDescripcionView;
        private String id;
        private int position;
        Retrofit retrofit;
        private View item;

        public ViewHolder(View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);

            mDescripcionView=itemView.findViewById(R.id.descripcion);
            mEquiposView=itemView.findViewById(R.id.nombre);
            mTipoView=itemView.findViewById(R.id.devicetype);
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
                    .baseUrl("http://3.16.180.219/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
    }
}
