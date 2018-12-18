package com.example.cesar.agrosmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.models.fincas.Equipos;

import java.util.ArrayList;

public class ListaEquiposAdminAdapter extends RecyclerView.Adapter<ListaEquiposAdminAdapter.ViewHolder>{

    private ArrayList<Equipos> dataset;
    private Context context;

    public ListaEquiposAdminAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_equipos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipos E = dataset.get(position);
        holder.nombre.setText(E.getNombre());
        holder.typedevice.setText(E.getDevicetype());
        holder.descripcion.setText(E.getDescripcion());
        holder.id = E.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaEquipos(ArrayList<Equipos> listaEquipos){
        dataset.addAll(listaEquipos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre, typedevice, descripcion;
        private Button borrar;
        private String id;

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            typedevice = itemView.findViewById(R.id.devicetype);
            descripcion = itemView.findViewById(R.id.descripcion);
            borrar = itemView.findViewById(R.id.borrarEquipo);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
