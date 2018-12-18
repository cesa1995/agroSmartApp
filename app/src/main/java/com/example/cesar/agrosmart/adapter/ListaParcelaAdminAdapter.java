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
import com.example.cesar.agrosmart.models.fincas.Parcelas;

import java.util.ArrayList;

public class ListaParcelaAdminAdapter extends RecyclerView.Adapter<ListaParcelaAdminAdapter.ViewHolder> {

    private ArrayList<Parcelas> dataset;
    private Context context;

    public ListaParcelaAdminAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_parcelas,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Parcelas P = dataset.get(position);
        holder.nombre.setText(P.getNombre());
        holder.tipo.setText(P.getTipo());
        holder.finca.setText(P.getFinca());
        holder.id = P.getId();
        holder.idfinca = P.getIdfinca();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaParcelas(ArrayList<Parcelas> listaParcelas){
        dataset.addAll(listaParcelas);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre, tipo, finca;
        private String id, idfinca;
        private Button borrar;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            tipo = itemView.findViewById(R.id.tipo);
            finca = itemView.findViewById(R.id.finca);
            borrar = itemView.findViewById(R.id.borrarParcela);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, id+" : "+ idfinca,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
