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
import com.example.cesar.agrosmart.models.fincas.Fincas;

import java.util.ArrayList;

public class ListaFincasIndexAdapter extends RecyclerView.Adapter<ListaFincasIndexAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private Context context;

    public ListaFincasIndexAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_index_finca,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fincas f = dataset.get(position);
        holder.fincaNombre.setText(f.getNombre());
        holder.fincaTelefono.setText(f.getTelefono());
        holder.fincaDireccion.setText(f.getDireccion());
        holder.id = f.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaFincas(ArrayList<Fincas> listaFincas){
        dataset.addAll(listaFincas);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView fincaNombre, fincaTelefono, fincaDireccion;
        private Button equipos, usuarios;
        private String id;

        public ViewHolder(View itemView){
            super(itemView);

            fincaNombre = itemView.findViewById(R.id.fincaNonmbre);
            fincaTelefono = itemView.findViewById(R.id.fincaTelefono);
            fincaDireccion = itemView.findViewById(R.id.fincaDireccion);

            equipos = itemView.findViewById(R.id.equipos);
            usuarios = itemView.findViewById(R.id.usuarios);

            equipos = itemView.findViewById(R.id.equipos);
            usuarios = itemView.findViewById(R.id.usuarios);

            equipos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,id,Toast.LENGTH_SHORT).show();
                }
            });

            usuarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,id,Toast.LENGTH_SHORT).show();

                }
            });
        }

    }
}
