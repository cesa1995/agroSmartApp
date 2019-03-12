package com.example.cesar.agrosmart.agrono.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.tareas.agronomoTareaAdd;
import com.example.cesar.agrosmart.models.tareas.tareas;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ListaTareaDoneAdapter extends RecyclerView.Adapter<ListaTareaDoneAdapter.ViewHolder>{
    private ArrayList<tareas> dataset;
    private ArrayList<tareas> datasetFiltered;
    private Context context;
    private String jwt, ubicacion;

    public ListaTareaDoneAdapter(Context context, String jwt, String ubicacion) {
        this.context = context;
        this.jwt = jwt;
        this.ubicacion=ubicacion;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ListaTareaDoneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agronomo_item_tarea_done, parent, false);
        return new ListaTareaDoneAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaTareaDoneAdapter.ViewHolder holder, int position) {
        tareas T=datasetFiltered.get(position);
        holder.mTareaView.setText(T.getTarea());
        String inicio="Desde: "+T.getInicio();
        holder.mInicioView.setText(inicio);
        String fin="Hasta: "+T.getFin();
        holder.mFinView.setText(fin);
        holder.id=T.getId();
        holder.tarea=T.getTarea();
        String[] desde= T.getInicio().split(" ");
        holder.Finicio=desde[0];
        holder.Hinicio=desde[1];
        String[] hasta= T.getFin().split(" ");
        holder.Ffin=hasta[0];
        holder.Hfin=hasta[1];
    }

    public void adicionarListaTareas(ArrayList<tareas> listaTareas){
        if (!dataset.isEmpty()){
            dataset.clear();
            datasetFiltered.clear();
        }
        dataset.addAll(listaTareas);
        datasetFiltered=dataset;
        notifyDataSetChanged();
    }

    public String getID(int position){
        return datasetFiltered.get(position).getId();
    }

    public void removeData(int position) {
        try {
            dataset.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, dataset.size());
        }catch (IndexOutOfBoundsException e){
            Log.d("error", "eliminar");
        }
    }

    public void addTarea(tareas ListTarea){
        if (ListTarea.getEstado().equals("1")) {
            dataset.add(ListTarea);
            notifyDataSetChanged();
            notifyItemInserted(dataset.size()-1);
        }
    }

    public void updateData(String id, String tarea, String inicio, String fin){
        for (tareas row:dataset){
            if (row.getId().toLowerCase().contains(id.toLowerCase())){
                row.setTarea(tarea);
                row.setInicio(inicio);
                row.setFin(fin);
                notifyDataSetChanged();
            }
        }
    }

    public void removeDataSocket(String id){
        int count=0;
        try {
            for (tareas row : dataset) {
                if (row.getId().toLowerCase().contains(id.toLowerCase())) {
                    dataset.remove(count);
                    notifyItemRemoved(count);
                    notifyItemRangeRemoved(count, dataset.size());
                }
                count++;
            }
        }catch (ConcurrentModificationException e){
            Log.d("error", "eliminando");
        }
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if (charString.isEmpty()){
                    datasetFiltered=dataset;
                }else{
                    ArrayList<tareas> filteredList=new ArrayList<>();
                    for(tareas row : dataset){
                        if (row.getTarea().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getInicio().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getFin().toLowerCase().contains(charString.toLowerCase())){
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
                datasetFiltered=(ArrayList<tareas>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTareaView, mInicioView, mFinView;
        private String id, tarea, Finicio, Hinicio, Ffin, Hfin;
        private CardView item;

        public ViewHolder(View itemView) {
            super(itemView);

            mTareaView=itemView.findViewById(R.id.tarea);
            mFinView=itemView.findViewById(R.id.fin);
            mInicioView=itemView.findViewById(R.id.inicio);
            item=itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("Ubicacion", ubicacion);
                    bundle.putString("jwt", jwt);
                    bundle.putString("id", id);
                    bundle.putString("tarea", tarea);
                    bundle.putString("Finicio", Finicio);
                    bundle.putString("Hinicio", Hinicio);
                    bundle.putString("Ffin", Ffin);
                    bundle.putString("Hfin", Hfin);
                    bundle.putString("tipo", "1");
                    Fragment fragment=new agronomoTareaAdd();
                    FragmentTransaction transaction=((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_tareas,fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}
