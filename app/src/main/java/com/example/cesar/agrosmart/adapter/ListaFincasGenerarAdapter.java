package com.example.cesar.agrosmart.adapter;

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
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.add.addParcelas;
import com.example.cesar.agrosmart.admin.admin.adminParcela;
import com.example.cesar.agrosmart.models.fincas.Fincas;

import java.util.ArrayList;

public class ListaFincasGenerarAdapter extends RecyclerView.Adapter<ListaFincasGenerarAdapter.ViewHolder> {

    private ArrayList<Fincas> dataset;
    private Context context;
    private String jwt;

    public ListaFincasGenerarAdapter(Context context, String jwt){
        this.context = context;
        dataset = new ArrayList<>();
        this.jwt = jwt;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fincas_lista_get_id,parent,false);
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
        private String id;
        private View item;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            telefono = itemView.findViewById(R.id.telefono);
            direccion = itemView.findViewById(R.id.direccion);
            item = itemView.findViewById(R.id.item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("id", id);
                    Fragment fragment = new adminParcela();
                    FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }
}
