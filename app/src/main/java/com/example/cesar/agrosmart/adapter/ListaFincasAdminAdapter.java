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

public class ListaFincasAdminAdapter extends RecyclerView.Adapter<ListaFincasAdminAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private Context context;

    public ListaFincasAdminAdapter(Context context){
        this.context=context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_fincas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fincas F = dataset.get(position);
        holder.nombre.setText(F.getNombre());
        holder.telefono.setText(F.getTelefono());
        holder.direccion.setText(F.getDireccion());
        holder.id = F.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaFincas(ArrayList<Fincas> listaFincas){
        dataset.addAll(listaFincas);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre, telefono, direccion;
        private Button borrar;
        private String id;

        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombre);
            telefono = itemView.findViewById(R.id.telefono);
            direccion = itemView.findViewById(R.id.direccion);
            borrar = itemView.findViewById(R.id.borrarFincas);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
